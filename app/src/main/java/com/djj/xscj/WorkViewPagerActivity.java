package com.djj.xscj;

import android.support.v4.app.FragmentActivity;

import com.djj.jazzyviewpager.JazzyViewPager;

import org.xutils.DbManager;

import java.util.ArrayList;

/**
 * Created by djj on 2016/12/4.
 */


public class WorkViewPagerActivity extends FragmentActivity {

    private ArrayList<TestTable> tablelist;
    private DbManager db;
    private MyFragmentStatePagerAdapter mFragmentStatePagerAdapter;
    private JazzyViewPager mViewPager;


    /*private void update() {
      *//*  if (t!=null) {
            try {
                db.delete(t);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }*//*
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

    }

    private void init() {
        if (tablelist.isEmpty()) {
            add();
        } else {
            for (TestTable t : tablelist) {
                MyFragment fragment = MyFragment.getInstances();
                Bundle b = new Bundle();
                b.putParcelable("setTestTable", t);
                fragment.setArguments(b);
                mFragmentStatePagerAdapter.addFragment(fragment);
            }
            mFragmentStatePagerAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dowork);
        x.view().inject(this);
        mViewPager = (JazzyViewPager) findViewById(R.id.viewpager);
        mViewPager.setTransitionEffect(JazzyViewPager.TransitionEffect.Tablet);
        db_init();
        mFragmentStatePagerAdapter = new MyFragmentStatePagerAdapter(getSupportFragmentManager(), mViewPager);
        mViewPager.setAdapter(mFragmentStatePagerAdapter);
        mViewPager.setPageMargin(30);
        init();
        *//*mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                *//**//*View view = mViewPager.getFocusedChild();
                MyFragment fragment = (MyFragment) mFragmentStatePagerAdapter.getCurrentFragmentbyrootview(view);
                int position1 = mFragmentStatePagerAdapter.getFramentposition(fragment);
                Toast.makeText(MyFragmentActivity.this, "onPageSelected选中了" + position1, Toast.LENGTH_SHORT).show();*//**//*
            }
        });*//*
        *//*mViewPager.setPageTransformer(true,new ViewPager.PageTransformer(){
            @Override
            public void transformPage(View page, float position) {

            }
        });*//*

    }

    private void db_init() {
        db = x.getDb(((MyApplication)getApplication()).getDaoConfig());
        try {
            tablelist = (ArrayList<TestTable>) db.findAll(TestTable.class);
            if (tablelist == null) tablelist = new ArrayList<>();
            if (!tablelist.isEmpty()) {
                for (TestTable t : tablelist) {
                    t.setIsadd(false);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }*/
}
