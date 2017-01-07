package com.djj.xscj;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.djj.view.slidecutlistview.SlideCutListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by djj on 2016/12/18.
 */

public class FirstFragment extends Fragment implements FourthFragment.FreshIpPort {
    private final String appname = "xscj";
    private MyPullToRefreshListView mPullToRefreshListView;
    private SlideCutListView mSlideCutListView;
    private ArrayAdapter<ListTestTable> mAdapter;
    private ArrayList<ListTestTable> mListItems;
    private String username, password, ip;
    private int port;
    private DbManager db;
    //private RemoveItemListener mRemoveItemListener;

    @Override
    public void setIpPort(String ip, int port) {
        this.port = port;
        this.ip = ip;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = x.getDb(((MyApplication) getActivity().getApplication()).getDaoConfig());
        username = ((MyApplication) getActivity().getApplication()).getUsername();
        password = ((MyApplication) getActivity().getApplication()).getPassword();
        ip = ((MyApplication) getActivity().getApplication()).getIp();
        port = ((MyApplication) getActivity().getApplication()).getPort();
        refresh();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input, container, false);
        mPullToRefreshListView = (MyPullToRefreshListView) view.findViewById(R.id.pull_refresh_input);
        // Set a listener to be invoked when the list should be refreshed.
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });

        // Add an end-of-list listener
        /*mPullToRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Toast.makeText(getActivity(), "End of List!", Toast.LENGTH_SHORT).show();
            }
        });*/

        mSlideCutListView = (SlideCutListView) mPullToRefreshListView.getRefreshableView();
        mSlideCutListView.setFrontResId(R.id.front_layout);
        mSlideCutListView.setBackgroundLeftResId(R.id.left_view);
        mSlideCutListView.setBackgroundRightResId(R.id.right_view);
        mSlideCutListView.setRemoveListener(new SlideCutListView.RemoveListener() {
            @Override
            public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
                switch (direction) {
                    case RIGHT:
                        Intent intent_upload = new Intent();
                        intent_upload.putExtra("type","正在上传任务");
                        intent_upload.setClass(getActivity(), UpLoadingActivity.class);//跳转到加载界面
                        startActivity(intent_upload);
                        new uploadDataTask().execute(mListItems.get(position - 1));
                        break;
                    case LEFT:
                        dialog(position-1);
                        break;
                    default:
                        break;
                }

            }
        });
        mSlideCutListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putParcelable("ListTestTable", mListItems.get(position - 1));
                Intent intent = new Intent();
                intent.putExtra("bundle", bundle);
                intent.setClass(getActivity(), WorkViewPagerActivity.class);//跳转到加载界面
                startActivityForResult(intent, 10);
            }
        });


        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(mSlideCutListView);
        if (savedInstanceState != null) {
            mListItems = savedInstanceState.getParcelableArrayList("mListItems");
        }
        if (mListItems == null)
            mListItems = new ArrayList<>();
        //mListItems.addAll(Arrays.asList(mStrings));

        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, R.id.list_item, mListItems);

        /**
         * Add Sound Event Listener
         */
        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(getActivity());
        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(PullToRefreshBase.State.REFRESHING, R.raw.refreshing_sound);
        mPullToRefreshListView.setOnPullEventListener(soundListener);

        // You can also just use setListAdapter(mAdapter) or
        // mPullToRefreshListView.setAdapter(mAdapter)
        mSlideCutListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            /*Bundle b = data.getExtras().getBundle("bundle");
            int position = b.getInt("position");
            ListTestTable t = b.getParcelable("ListTestTable");
            mListItems.set(position - 1, t);
            try {
                ArrayList<TestTable> testTables = t.getListTestTable();
                db.update(testTables);
                ArrayList<FilepPathTable> filepPathTables = new ArrayList<>();
                for (TestTable table : testTables) {
                    ArrayList<String> filepath = table.getfilepath();
                    for (String s : filepath) {
                        if (db.selector(FilepPathTable.class).where("filepath", "=", s).findFirst()!=null)
                            continue;
                        FilepPathTable filepPathTable = new FilepPathTable();
                        filepPathTable.setmId(table.getid());
                        filepPathTable.setFilepath(s);
                        filepPathTables.add(filepPathTable);
                    }
                }
                db.save(filepPathTables);
            } catch (DbException e) {
                e.printStackTrace();
            }*/
            refresh();
            mAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_MANUAL_REFRESH, 0, "Manual Refresh");
        menu.add(0, MENU_DISABLE_SCROLL, 1,
                mPullToRefreshListView.isScrollingWhileRefreshingEnabled() ? "Disable Scrolling while Refreshing"
                        : "Enable Scrolling while Refreshing");
        menu.add(0, MENU_SET_MODE, 0, mPullToRefreshListView.getMode() == PullToRefreshBase.Mode.BOTH ? "Change to MODE_PULL_DOWN"
                : "Change to MODE_PULL_BOTH");
        menu.add(0, MENU_DEMO, 0, "Demo");
        return super.onCreateOptionsMenu(menu);
    }*/
    private int downloaddata() {
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        Socket socket = new Socket();
        int count = 0;
        try {
            socket.connect((new InetSocketAddress(ip, port)), 3000);
            socket.setSoTimeout(100000);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            out.writeUTF(appname);
            out.flush();
            if (in.readUTF().equals("application pass")) {
                out.writeUTF(username);
                out.writeUTF(password);
                out.flush();
                if (in.readUTF().equals("user pass")) {
                    out.writeUTF("downloaddata");
                    out.flush();
                    int size = in.readInt();
                    //ArrayList<TestTable> testTables;
                    for (int i = 0; i < size; i++) {
                        TestTable table = new TestTable();
                        table.setid(in.readInt());
                        table.setInputdate(in.readUTF());
                        table.setUser(in.readUTF());
                        table.setNum(in.readUTF());
                        table.setCnum(in.readUTF());
                        table.setName(in.readUTF());
                        table.setAddress(in.readUTF());
                        table.setCellphone(in.readUTF());
                        table.setPhone(in.readUTF());
                        table.setYear(in.readUTF());
                        table.setMonth(in.readUTF());
                        table.setMoney(in.readUTF());
                        if (db.selector(TestTable.class).where("id", "=", table.getid()).findFirst() == null) {
                            table.setImei(imei);
                            db.save(table);
                            count++;
                        }
                    }
                    if (refresh()) {
                        if (count > 0) {
                            out.writeUTF("downloaddata pass");
                        } else {
                            out.writeUTF("no new item has downloaded");
                        }
                        out.flush();
                        return count;
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "refresh错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "非法用户", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "非法程序", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            out.writeUTF("clients error,downloaddata failed");
            out.flush();
            if (!socket.isClosed()) {
                in.close();
                out.close();
            }
        } catch (DbException e) {
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "sqlite错误", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return -1;
    }

    private boolean refresh() {
        try {
            ArrayList<TestTable> res = (ArrayList<TestTable>) db.selector(TestTable.class).where("user", "=", username).orderBy("inputdate").findAll();
            //意数据库不存在返回null,数据库存在findall不返回NULL，findfirst可返回NULL
            mListItems.clear();
            if (res == null) return true;
            if (res.isEmpty()) {
                return true;
            }
            String inputtime = res.get(0).getInputdate();
            ListTestTable listTestTable = new ListTestTable();
            for (TestTable table : res) {
                if (!table.getInputdate().equals(inputtime)) {
                    mListItems.add(listTestTable);
                    inputtime = table.getInputdate();
                    listTestTable = new ListTestTable();
                }
                table.setFilenums(0);
                //注意数据库不存在返回null,数据库存在findall不返回NULL，findfirst可返回NULL
                ArrayList<FilepPathTable> filepPathTables = (ArrayList<FilepPathTable>) db.selector(FilepPathTable.class).where("mid", "=", table.getid()).findAll();
                if (filepPathTables != null) {
                    if (!filepPathTables.isEmpty()) {
                        for (FilepPathTable pathTable : filepPathTables) {
                            table.addfilepath(pathTable.getFilepath());
                        }
                    }
                }
                listTestTable.add(table);
            }
            mListItems.add(listTestTable);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }

    }

    private int uploaddata(ListTestTable listTestTable) {
        ArrayList<TestTable> uploadlist = listTestTable.getListTestTable();
        int prepare_count = listTestTable.gethasfileitemnums();
        if (prepare_count <= 0) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "没有文件可以上传，请先完成任务", Toast.LENGTH_SHORT).show();
                }
            });
            //防止上传activity未打开，造成空指针
            try {
                Thread.sleep(500);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            return 0;
        }
        Socket socket = new Socket();
        int count = 0;
        try {
            socket.connect((new InetSocketAddress(ip, port)), 3000);
            socket.setSoTimeout(100000);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            out.writeUTF(appname);
            out.flush();
            if (in.readUTF().equals("application pass")) {
                out.writeUTF(username);
                out.writeUTF(password);
                out.flush();
                if (in.readUTF().equals("user pass")) {
                    out.writeUTF("uploaddata");
                    out.flush();
                    out.writeInt(prepare_count);
                    out.flush();
                    //ArrayList<TestTable> testTables;
                    for (TestTable table : uploadlist) {
                        if (table.getFilenums() <= 0) continue;
                        out.writeInt(table.getid());
                        out.writeUTF(table.getUser());
                        out.writeUTF(table.getImei());
                        int filenums = table.getFilenums();
                        out.writeInt(filenums);
                        out.flush();
                        ArrayList<File> files = new ArrayList<>();
                        for (int i = 0; i < filenums; i++) {
                            File file = new File(table.getfilepath().get(i));
                            if (!file.exists()) {
                                Toast.makeText(getActivity(), "没有找到文件", Toast.LENGTH_SHORT).show();
                                return -1;
                            }
                            String[] path = file.getAbsolutePath().split(File.separator);
                            out.writeUTF(path[path.length - 1]);
                            out.flush();
                            int filelength = (int) file.length();
                            out.writeInt(filelength);
                            out.flush();
                            FileInputStream fin = new FileInputStream(file);
                            int length = 0;
                            int size = 8 * 1024;
                            byte[] sendBytes = new byte[size];
                            while ((length = fin.read(sendBytes, 0, size)) > 0) {
                                out.write(sendBytes, 0, length);
                                out.flush();
                            }
                            fin.close();
                            files.add(file);
                        }
                        if (in.readUTF().equals("update item success")) {
                            for (File f : files) {
                                f.delete();
                            }
                            files.clear();
                            db.delete(table);
                            //ArrayList<FilepPathTable> filepPathTables=(ArrayList<FilepPathTable>)db.selector(FilepPathTable.class).where("id","=",table.getid()).findAll();
                            db.delete(FilepPathTable.class, WhereBuilder.b("mid", "=", table.getid()));
                            count++;
                        } else {
                            return -1;
                        }

                    }
                    if (refresh()) {
                        if (count > 0) {
                            out.writeUTF("uploaddata pass");
                        } else {
                            out.writeUTF("no item has uploaded");
                        }
                        out.flush();
                        return count;
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "refresh错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "非法用户", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "非法程序", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            out.writeUTF("clients error,uploaddata failed");
            out.flush();
            if (!socket.isClosed()) {
                in.close();
                out.close();
            }
        } catch (DbException e) {
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "sqlite错误", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                }
            });
        }
        refresh();
        return -1;
    }

    private int canceldata(ListTestTable listTestTable){
        ArrayList<TestTable> uploadlist = listTestTable.getListTestTable();
        int prepare_count = listTestTable.gethasfileitemnums();
        if (prepare_count > 0) {
            Toast.makeText(getActivity(), "文件将全部删除", Toast.LENGTH_SHORT).show();
            //return 0;
        }
        prepare_count=uploadlist.size();
        Socket socket = new Socket();
        int count = 0;
        try {
            socket.connect((new InetSocketAddress(ip, port)), 3000);
            socket.setSoTimeout(100000);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            out.writeUTF(appname);
            out.flush();
            if (in.readUTF().equals("application pass")) {
                out.writeUTF(username);
                out.writeUTF(password);
                out.flush();
                if (in.readUTF().equals("user pass")) {
                    out.writeUTF("canceldata");
                    out.flush();
                    out.writeInt(prepare_count);
                    out.flush();
                    //ArrayList<TestTable> testTables;
                    for (TestTable table : uploadlist) {
                        out.writeInt(table.getid());
                        out.writeUTF(table.getImei());
                        out.flush();
                    }
                    if(in.readUTF().equals("canceldata success")){
                        for (TestTable table : uploadlist) {
                            int filenums = table.getFilenums();
                            for (int i = 0; i < filenums; i++) {
                                File file = new File(table.getfilepath().get(i));
                                if (file.exists()) {
                                    file.delete();
                                }
                                db.delete(FilepPathTable.class,WhereBuilder.b("filepath","=",table.getfilepath().get(i)));
                            }
                            db.delete(TestTable.class,WhereBuilder.b("id","=",table.getid()));
                            count++;
                        }
                    }
                    if (refresh()) {
                        return count;
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "refresh错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "非法用户", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "非法程序", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            out.writeUTF("clients error,uploaddata failed");
            out.flush();
            if (!socket.isClosed()) {
                in.close();
                out.close();
            }
        } catch (DbException e) {
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "sqlite错误", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                }
            });
        }
        refresh();
        return -1;
    }

    private void dialog(final int position){
        //先new出一个监听器，设置好监听
        DialogInterface.OnClickListener dialogOnclicListener=new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case Dialog.BUTTON_POSITIVE:
                        Intent intent_cancel = new Intent();
                        intent_cancel.putExtra("type","正在取消任务");
                        intent_cancel.setClass(getActivity(), UpLoadingActivity.class);//跳转到加载界面
                        startActivity(intent_cancel);
                        new cancelDataTask().execute(mListItems.get(position));
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        //dialog参数设置
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());  //先得到构造器
        builder.setTitle("警告"); //设置标题
        builder.setMessage("是否确认取消任务，所有文件将被删除?"); //设置内容
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("确认",dialogOnclicListener);
        builder.setNegativeButton("取消", dialogOnclicListener);
        builder.setCancelable(false);
        builder.create().show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        //menu.setHeaderTitle("项目: " + mListItems.get(info.position - 1));
        //ListView
        menu.setHeaderTitle("任务菜单");
        menu.add("下载任务");
        menu.add("上传任务");
        menu.add("取消任务");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getTitle().toString()) {
            case "下载任务":
                new GetDataTask().execute();
                break;
            case "上传任务":
                Intent intent_upload = new Intent();
                intent_upload.putExtra("type", "正在上传任务");
                intent_upload.setClass(getActivity(), UpLoadingActivity.class);//跳转到加载界面
                startActivity(intent_upload);
                new uploadDataTask().execute(mListItems.get(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position - 1));
                break;
            case "取消任务":
                dialog(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position - 1);
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void add(Object t) {
        mListItems.add((ListTestTable) t);
    }

    /*public void setRemoveItemListener(RemoveItemListener listener) {
        mRemoveItemListener = listener;
    }*/

   /* @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem disableItem = menu.findItem(MENU_DISABLE_SCROLL);
        disableItem
                .setTitle(mPullToRefreshListView.isScrollingWhileRefreshingEnabled() ? "Disable Scrolling while Refreshing"
                        : "Enable Scrolling while Refreshing");

        MenuItem setModeItem = menu.findItem(MENU_SET_MODE);
        setModeItem.setTitle(mPullToRefreshListView.getMode() == PullToRefreshBase.Mode.BOTH ? "Change to MODE_FROM_START"
                : "Change to MODE_PULL_BOTH");

        return super.onPrepareOptionsMenu(menu);
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case MENU_MANUAL_REFRESH:
                new MainActivity.GetDataTask().execute();
                mPullToRefreshListView.setRefreshing(false);
                break;
            case MENU_DISABLE_SCROLL:
                mPullToRefreshListView.setScrollingWhileRefreshingEnabled(!mPullToRefreshListView
                        .isScrollingWhileRefreshingEnabled());
                break;
            case MENU_SET_MODE:
                mPullToRefreshListView.setMode(mPullToRefreshListView.getMode() == PullToRefreshBase.Mode.BOTH ? PullToRefreshBase.Mode.PULL_FROM_START
                        : PullToRefreshBase.Mode.BOTH);
                break;
            case MENU_DEMO:
                mPullToRefreshListView.demo();
                break;
        }

        return super.onOptionsItemSelected(item);

    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putStringArrayList("mListItems",new ArrayList<>(mListItems));
        outState.putParcelableArrayList("mListItems", mListItems);
    }

    /*public interface RemoveItemListener {
        void removeitem(int direction, Object o);
    }*/

    private class GetDataTask extends AsyncTask<Void, Void, ArrayList> {

        @Override
        protected ArrayList<ListTestTable> doInBackground(Void... params) {
            downloaddata();
            return mListItems;
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            mAdapter.notifyDataSetChanged();
            // Call onRefreshComplete when the list has been refreshed.
            mPullToRefreshListView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }

    private class uploadDataTask extends AsyncTask<ListTestTable, Void, ArrayList> {

        @Override
        protected ArrayList<ListTestTable> doInBackground(ListTestTable... params) {
            uploaddata(params[0]);
            return mListItems;
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            UpLoadingActivity.uploadactivity.finish();
            mAdapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }
    }

   /* @Override
    protected void onSaveState(Bundle outState) {
        outState.putStringArrayList("mlistItems",new ArrayList<String>(mListItems));
        super.onSaveState(outState);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        mListItems=new LinkedList<>(savedInstanceState.getStringArrayList("mlistItems"));
    }*/

    /*@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState!=null){
            Log.w("asdfasdfasdfasf",""+savedInstanceState.getStringArrayList("mListItems").toString());
            mListItems=new LinkedList<>(savedInstanceState.getStringArrayList("mListItems"));
        }

    }*/

    private class cancelDataTask extends AsyncTask<ListTestTable, Void, ArrayList> {

        @Override
        protected ArrayList<ListTestTable> doInBackground(ListTestTable... params) {
            canceldata(params[0]);
            return mListItems;
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            UpLoadingActivity.uploadactivity.finish();
            mAdapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }
    }


}
