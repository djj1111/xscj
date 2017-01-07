package com.djj.xscj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.djj.jazzyviewpager.JazzyViewPager;

import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by djj on 2016/12/4.
 */


public class WorkViewPagerActivity extends FragmentActivity {

    private ListTestTable mtablelist;
    //private DbManager db;
    private MyFragmentStatePagerAdapter mFragmentStatePagerAdapter;
    private JazzyViewPager mViewPager;
    //上一级控件的位置
    private int position;
    private int total;
    private TextView textView;


    /*private void update() {
        if (t!=null) {
            try {
                db.delete(t);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        try {
            List<Fragment> listFragment = mFragmentStatePagerAdapter.getFragments();
            for (Fragment f : listFragment) {
                TestTable f_t = ((MyFragment) f).getmTestTable().getParcelable("getTestTable");
                for (TestTable t : tablelist) {
                    if (t.getSerialsnum() == f_t.getSerialsnum()) {
                        t = f_t;
                        Toast.makeText(MyFragmentActivity.this, "address=" + t.getAddress() + "isadd=" + t.getIsadd() + "isde=" + t.getIsdelete(), Toast.LENGTH_SHORT).show();
                        break;
                    }

                }
            }

            for (TestTable t : tablelist) {
                if (t.getIsadd() && !t.getIsdelete()) {
                    db.saveBindingId(t);
                } else if (t.getIsdelete() && !t.getIsadd()) {
                    db.delete(t);
                } else if (!t.getIsdelete() && !t.getIsadd()) {
                    db.update(t, "name", "address", "phone");
                }
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

    }*/

    private void init() {
        if (mtablelist==null){
            Intent intent = this.getIntent();
            Bundle bundle = intent.getBundleExtra("bundle");
            position=bundle.getInt("position");
            mtablelist= bundle.getParcelable("ListTestTable");
        }
        if (mtablelist.getListTestTable().isEmpty()) {
            Toast.makeText(WorkViewPagerActivity.this,"没有内容",Toast.LENGTH_SHORT).show();
        } else {
            for (TestTable t : mtablelist.getListTestTable()) {
                WorkFragment fragment = WorkFragment.getInstances();
                Bundle b = new Bundle();
                b.putParcelable("mTestTable", t);
                fragment.setArguments(b);
                mFragmentStatePagerAdapter.addFragment(fragment);
            }
            mFragmentStatePagerAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null) {
            mtablelist=savedInstanceState.getParcelable("mtablelist");
            position = savedInstanceState.getInt("position");
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dowork);
        x.view().inject(this);
        mViewPager = (JazzyViewPager) findViewById(R.id.viewpager);
        textView=(TextView) findViewById(R.id.tv_dowork_top);
        mViewPager.setTransitionEffect(JazzyViewPager.TransitionEffect.Tablet);
        //db_init();
        mFragmentStatePagerAdapter = new MyFragmentStatePagerAdapter(getSupportFragmentManager(), mViewPager);
        mViewPager.setAdapter(mFragmentStatePagerAdapter);
        mViewPager.setPageMargin(30);
        init();
        total=mtablelist.size();
        textView.setText("第"+(mViewPager.getCurrentItem()+1)+"页/共"+total+"页");
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                textView.setText("第"+(position+1)+"页/共"+total+"页");
            }
        });
        /*mViewPager.setPageTransformer(true,new ViewPager.PageTransformer(){
            @Override
            public void transformPage(View page, float position) {

            }
        });*/

    }

    @Override
    protected void onPause() {
        ArrayList<TestTable> testTables=mtablelist.getListTestTable();
        for (Fragment fragment : mFragmentStatePagerAdapter.getFragments()) {
            TestTable t = ((WorkFragment) fragment).getArguments().getParcelable("mTestTable");
            for (int pos = 0; pos < testTables.size(); pos++) {
                if (testTables.get(pos).getSerialsnum() == t.getSerialsnum()){
                    testTables.set(pos, t);
                }
            }
        }
        mtablelist.setListTestTable(testTables);
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("mtablelist",mtablelist);
        outState.putInt("position", position);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        bundle.putParcelable("ListTestTable", mtablelist);
        intent.putExtra("bundle", bundle);
        WorkViewPagerActivity.this.setResult(RESULT_OK, intent);
        WorkViewPagerActivity.this.finish();
        super.onBackPressed();
    }

    private void db_init() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        mtablelist= bundle.getParcelable("ListTestTable");
        //db = x.getDb(((MyApplication)getApplication()).getDaoConfig());
        /*try {
            mtablelist = (ArrayList<TestTable>) db.findAll(TestTable.class);
            if (tablelist == null) tablelist = new ArrayList<>();
            if (!tablelist.isEmpty()) {
                for (TestTable t : tablelist) {
                    t.setIsadd(false);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }*/
    }
}
