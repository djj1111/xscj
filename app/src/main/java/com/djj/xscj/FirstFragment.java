package com.djj.xscj;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.djj.view.slidecutlistview.SlideCutListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by djj on 2016/12/18.
 */

public class FirstFragment extends Fragment {
    private MyPullToRefreshListView mPullToRefreshListView;
    private SlideCutListView mSlideCutListView;
    private ArrayAdapter<ListTestTable> mAdapter;
    private ArrayList<ListTestTable> mListItems;
    public FirstFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input,container,false);
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
        mPullToRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Toast.makeText(getActivity(), "End of List!", Toast.LENGTH_SHORT).show();
            }
        });

        mSlideCutListView = (SlideCutListView) mPullToRefreshListView.getRefreshableView();
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
                        mRemoveItemListener.removeitem(0,mListItems.remove(position-1));
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "向右删除  "+ position, Toast.LENGTH_SHORT).show();
                        break;
                    case LEFT:
                        mRemoveItemListener.removeitem(1,mListItems.remove(position-1));
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
        if (savedInstanceState!=null){
            mListItems=savedInstanceState.getParcelableArrayList("mListItems");
        }
        if (mListItems==null)
        mListItems = new ArrayList<>();
        //mListItems.addAll(Arrays.asList(mStrings));

        mAdapter = new ArrayAdapter<>(getActivity(),R.layout.list_item,R.id.list_item, mListItems);

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

    private class GetDataTask extends AsyncTask<Void, Void, ArrayList> {

        @Override
        protected ArrayList<ListTestTable> doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mListItems;
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            ListTestTable listTestTablel=new ListTestTable();
            TestTable testTable=new TestTable();
            SimpleDateFormat    formatter    =   new SimpleDateFormat("yyyy年MM月dd日hh时mm分ss秒");
            Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
            String    str    =    formatter.format(curDate);
            testTable.setInputdate(str);
            listTestTablel.add(testTable);
            mListItems.add(0,listTestTablel);
            mAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            mPullToRefreshListView.onRefreshComplete();

            super.onPostExecute(result);
        }
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

    @Override
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
    /*private String[] mStrings;

    public void setStrings(String[] s){
        mStrings=s;
    }*/
    public void add(Object t){
        mListItems.add((ListTestTable)t);
    }
    public interface RemoveItemListener{
        void removeitem(int direction,Object o);
    }
    private RemoveItemListener mRemoveItemListener;
    public void setRemoveItemListener(RemoveItemListener listener){
        mRemoveItemListener=listener;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putStringArrayList("mListItems",new ArrayList<>(mListItems));
        outState.putParcelableArrayList("mListItems",mListItems);
    }


}
