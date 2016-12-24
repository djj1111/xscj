package com.djj.xscj;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.djj.view.slidecutlistview.SlideCutListView;

import java.util.ArrayList;

/**
 * Created by djj on 2016/12/18.
 */

public class SecondFragment extends Fragment {
    private SlideCutListView mSlideCutListView;
    private ArrayAdapter<ListTestTable> mAdapter;
    private ArrayList<ListTestTable> mListItems;
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
        mSlideCutListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("position",position);
                bundle.putParcelable("ListTestTable", mListItems.get(position));
                Intent intent=new Intent();
                intent.putExtra("bundle", bundle);
                intent.setClass(getActivity(),WorkViewPagerActivity.class);//跳转到加载界面
                startActivityForResult(intent,10);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            Bundle b=data.getExtras().getBundle("bundle");
            int position=b.getInt("position");
            ListTestTable t=b.getParcelable("ListTestTable");
            mListItems.set(position,t);
            mAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /* private class GetDataTask extends AsyncTask<Void, Void, ArrayList> {

        @Override
        protected ArrayList<ListTestTable> doInBackground(Void... params) {
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
        protected void onPostExecute(ArrayList result) {
            mListItems.add(0,getTag()+"Added after refresh...");
            mAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            //mPullToRefreshListView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }*/

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

    public void add(Object s){
        mListItems.add((ListTestTable) s);
    }
    public interface RemoveItemListener{
        void removeitem(int direction,Object o);
    }
    private RemoveItemListener mRemoveItemListener;
    public void setRemoveItemListener(RemoveItemListener listener){
        mRemoveItemListener=listener;
    }


    /*@Override
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
        if (savedInstanceState!=null)
        mListItems=new LinkedList<>(savedInstanceState.getStringArrayList("mlistItems"));
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mListItems",mListItems);
    }
}
