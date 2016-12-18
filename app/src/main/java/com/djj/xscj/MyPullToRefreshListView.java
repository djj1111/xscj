package com.djj.xscj;

import android.content.Context;
import android.util.AttributeSet;

import com.djj.view.slidecutlistview.SlideCutListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by djj on 2016/12/18.
 */

public class MyPullToRefreshListView extends PullToRefreshListView {
    public MyPullToRefreshListView(Context context) {
        super(context);
    }

    public MyPullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPullToRefreshListView(Context context, Mode mode) {
        super(context, mode);
    }

    public MyPullToRefreshListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    protected SlideCutListView createListView(Context context, AttributeSet attrs) {
        final SlideCutListView lv;
        lv = new SlideCutListView(context, attrs);
        return lv;
    }

    @Override
    protected SlideCutListView createRefreshableView(Context context, AttributeSet attrs) {
        SlideCutListView lv = createListView(context, attrs);

        // Set it to this so it can be used in ListActivity/ListFragment
        lv.setId(android.R.id.list);
        return lv;
    }
}
