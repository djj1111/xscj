package com.djj.xscj;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.djj.view.slidecutlistview.SlideCutListView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import static android.R.attr.port;
import static android.app.Activity.RESULT_OK;

/**
 * Created by djj on 2016/12/18.
 */

public class SecondFragment extends Fragment {
    private SlideCutListView mSlideCutListView;
    private ArrayAdapter<String> mAdapter;
    private LinkedList<String> mListItems;
    public SecondFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_do,container,false);
        mSlideCutListView = (SlideCutListView) view.findViewById(R.id.slidecutlistview_do);
        mSlideCutListView.setFrontResId(R.id.front_layout);
        mSlideCutListView.setBackgroundLeftResId(R.id.left_view);
        mSlideCutListView.setBackgroundRightResId(R.id.right_view);
        mSlideCutListView.setRemoveListener(new SlideCutListView.RemoveListener(){
            @Override
            public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
                //mAdapter.remove(mAdapter.getItem(position-1));
                //Log.d("asdfasfdasfasdfasd",":::"+Thread.currentThread().getId()+"    pso="+position);

                //mAdapter.notifyDataSetChanged();
                switch (direction) {
                    case RIGHT:
                        mRemoveItemListener.removeitem(0,mListItems.remove(position));
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "向右删除  "+ position, Toast.LENGTH_SHORT).show();
                        break;
                    case LEFT:
                        mRemoveItemListener.removeitem(1,mListItems.remove(position));
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "向左删除  "+ position, Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }
            }
        });

        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(mSlideCutListView);

        mListItems = new LinkedList<>();
        //mListItems.addAll(Arrays.asList(mStrings));

        mAdapter = new ArrayAdapter<>(getActivity(),R.layout.list_item,R.id.list_item, mListItems);

        /**
         * Add Sound Event Listener
         */
        /*SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(getActivity());
        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(PullToRefreshBase.State.REFRESHING, R.raw.refreshing_sound);*/
        //mListView.setOnPullEventListener(soundListener);

        // You can also just use setListAdapter(mAdapter) or
        // mPullToRefreshListView.setAdapter(mAdapter)
        mSlideCutListView.setAdapter(mAdapter);
        return view;
    }

    private class GetDataTask extends AsyncTask<Void, Void, LinkedList> {

        @Override
        protected LinkedList<String> doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Log.d("asdfasfdasfasdfasd",":::"+Thread.currentThread().getId());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mListItems;
        }

        @Override
        protected void onPostExecute(LinkedList result) {
            mListItems.addFirst(getTag()+"Added after refresh...");
            mAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            //mPullToRefreshListView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        menu.setHeaderTitle("Item: " + mListItems.get(info.position));
        //ListView
        menu.add("Item 1");
        menu.add("Item 2");
        menu.add("Item 3");
        menu.add("Item 4");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public void add(String s){
        mListItems.addLast(s);
    }
    public interface RemoveItemListener{
        void removeitem(int direction,Object o);
    }
    private RemoveItemListener mRemoveItemListener;
    public void setRemoveItemListener(RemoveItemListener listener){
        mRemoveItemListener=listener;
    }

    private static final int TYPE_FILE_IMAGE = -11, TYPE_FILE_VEDIO = -12, TYPE_FILE_AUDIO = -13,
            SYSTEM_CAMERA_REQUESTCODE=16,NETWORK_REQUESTCODE=17,
            UPDATESUCCESS=-21,UPDATEFAULT=-22;
    private int port = 12702;
    private Uri imageFileUri;
    private ArrayList<File> filelist = new ArrayList<>();

    //生成输出文件
    private File getOutFile(int fileType) {

        String storageState = Environment.getExternalStorageState();
        //Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM);
        if (Environment.MEDIA_REMOVED.equals(storageState)){
            Toast.makeText(getActivity().getApplicationContext(), "无内存卡", Toast.LENGTH_SHORT).show();
            return null;
        }

        /*File mediaStorageDir = new File (Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                ,"MyPictures");*/
        File mediaStorageDir = new File (Environment.getExternalStorageDirectory(),"waterwork");
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Toast.makeText(getActivity(), "创建文件存储路径目录失败", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        File file = new File(getFilePath(mediaStorageDir, fileType));

        return file;
    }
    //生成输出文件路径

    private String getFilePath(File mediaStorageDir, int fileType){
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        //file.separator 目录分隔符
        String filePath = mediaStorageDir.getPath() + File.separator;
        if (fileType == TYPE_FILE_IMAGE){
            filePath += ("IMG_" + imei + "_" + timeStamp + ".jpg");
        }else if (fileType == TYPE_FILE_VEDIO){
            filePath += ("VIDEO_" + imei + "_" + timeStamp + ".mp4");
        } else if (fileType == TYPE_FILE_AUDIO) {
            filePath += ("AUDIO_" + imei + "_" + timeStamp + ".aac");
        }else{
            return null;
        }
        return filePath;
    }

    private Uri getOutFileUri(int fileType) {
        return Uri.fromFile(getOutFile(fileType));
    }

    private String photofilepath;
    private void getphoto() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFileUri = getOutFileUri(TYPE_FILE_IMAGE);//得到一个File Uri
        photofilepath = imageFileUri.getPath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(intent, SYSTEM_CAMERA_REQUESTCODE);
    }
    private void update() {
        String ip = ((MainActivity)getActivity()).getIp();
        if (filelist.size() == 0) {
            Toast.makeText(getActivity(), "没有文件，请先拍照或录音！", Toast.LENGTH_LONG).show();
            return;
        }
        ArrayList<String> filepath = new ArrayList<>();
        for (File f : filelist) filepath.add(f.getPath());
        Bundle bundle = new Bundle();
        bundle.putString("ipaddress", ip);
        bundle.putInt("port", port);
        bundle.putStringArrayList("filepath", filepath);
        Intent intent=new Intent();
        intent.putExtra("bundle", bundle);
        intent.setClass(getActivity(),UpLoadingActivity.class);//跳转到加载界面
        startActivityForResult(intent,NETWORK_REQUESTCODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SYSTEM_CAMERA_REQUESTCODE)
            if (resultCode==RESULT_OK){
                //Toast.makeText(getActivity(),"拍照成功",Toast.LENGTH_SHORT).show();
                File file = new File(photofilepath);
                filelist.add(file);
            }else {
                //Toast.makeText(getActivity(),"拍照不成功",Toast.LENGTH_SHORT).show();
                photofilepath = null;
            }
        if (requestCode==NETWORK_REQUESTCODE)
            if (resultCode==UPDATESUCCESS){
                Iterator iter = filelist.iterator();
                //int filesize=filepath.size();
                while (iter.hasNext()) {
                    File file = (File) iter.next();
                    if (file.delete()) {
                        //
                        //hasfiles = false;
                        iter.remove();
                    } else {
                        Toast.makeText(getActivity(), "临时文件未能删除，文件路径：" + file.getPath(), Toast.LENGTH_SHORT).show();
                    }

                }
                Toast.makeText(getActivity(), "临时文件成功删除", Toast.LENGTH_SHORT).show();

            }else {
                Bundle bundle = data.getBundleExtra("bundle");
                ArrayList<String> filepath = bundle.getStringArrayList("filepath");
                Iterator iter = filelist.iterator();
                while (iter.hasNext()) {
                    File file = (File) iter.next();
                    String filepathone = file.getPath();
                    boolean del_flag = true;
                    for (String filep : filepath) {
                        if (filepathone.equals(filep)) {
                            del_flag = false;
                            break;
                        }
                    }
                    if (del_flag) {
                        if (file.delete()) {
                            //
                            //hasfiles = false;
                            iter.remove();
                        } else {
                            Toast.makeText(getActivity(), "临时文件未能删除，文件路径：" + file.getPath(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    /*if (!file.delete()){
                        //
                        //hasfiles = false;
                        Toast.makeText(getActivity(), "临时文件未能删除，文件路径：" + file.getPath(), Toast.LENGTH_SHORT).show()
                    }*/

                }
                Toast.makeText(getActivity(), "上传不完全成功,部分文件未删除", Toast.LENGTH_SHORT).show();
            }
}
