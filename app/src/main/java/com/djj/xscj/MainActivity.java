package com.djj.xscj;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener{
    private TextView top_menu_input;
    private TextView top_menu_do;
    private TextView top_menu_setup;
    private TextView top_menu_output;
    private String mIp="djj1111.gicp.net";

   //private FrameLayout ly_content;

    private FirstFragment f1;
    private FourthFragment f4;
    private ThirdFragment f2,f3;
    public String getIp(){
        return mIp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.top_menu);

        bindView();
        init(savedInstanceState);
    }

    private  void init(Bundle savedInstanceState){
        FragmentTransaction mtransaction = getFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            f1 = new FirstFragment();
            mtransaction.add(R.id.fragment_container,f1,"F_f1");
            f2 = new ThirdFragment();
            mtransaction.add(R.id.fragment_container,f2,"F_f2");
            f3 = new ThirdFragment();
            mtransaction.add(R.id.fragment_container,f3,"F_f3");
            f4 = new FourthFragment();
            f4.setFreshIpPort(f1);
            mtransaction.add(R.id.fragment_container,f4,"F_f4");
        } else {
            f1 = (FirstFragment) getFragmentManager().getFragment(savedInstanceState,"F_f1");
            f2 = (ThirdFragment) getFragmentManager().getFragment(savedInstanceState,"F_f2");
            f3 = (ThirdFragment) getFragmentManager().getFragment(savedInstanceState,"F_f3");
            f4 = (FourthFragment) getFragmentManager().getFragment(savedInstanceState, "F_f4");
        }
//        f1.setRemoveItemListener(new RemoveItemListener(){
//            @Override
//            public void removeitem(int direction,Object o) {
//                switch (direction){
//                    case 0:f2.add(o);
//                        break;
//                    case 1:f4.add(o);
//                        break;
//                }
//
//            }
//        });
//        f2.setRemoveItemListener(new SecondFragment.RemoveItemListener(){
//            @Override
//            public void removeitem(int direction,Object o) {
//                switch (direction){
//                    case 0:f3.add(o);
//                        break;
//                    case 1:f1.add(o);
//                        break;
//                }
//            }
//        });
//        f3.setRemoveItemListener(new SecondFragment.RemoveItemListener(){
//            @Override
//            public void removeitem(int direction,Object o) {
//                switch (direction){
//                    case 0:f4.add(o);
//                        break;
//                    case 1:f2.add(o);
//                        break;
//                }
//            }
//        });
//        f4.setRemoveItemListener(new RemoveItemListener(){
//            @Override
//            public void removeitem(int direction,Object o) {
//                switch (direction){
//                    case 0:f1.add(o);
//                        break;
//                    case 1:f3.add(o);
//                        break;
//                }
//            }
//        });
        hideAllFragment(mtransaction);
        mtransaction.commit();
        if (savedInstanceState!=null){
            top_menu_input.setSelected(savedInstanceState.getBoolean("top_menu_input"));
            top_menu_do.setSelected(savedInstanceState.getBoolean("top_menu_do"));
            top_menu_output.setSelected(savedInstanceState.getBoolean("top_menu_output"));
            top_menu_setup.setSelected(savedInstanceState.getBoolean("top_menu_setup"));
        } else {
            Intent intent = this.getIntent();
            if (intent.getBooleanExtra("isfirst", true)) {
                top_menu_setup.setSelected(true);
            } else {
                top_menu_input.setSelected(true);
            }
        }
        if (top_menu_input.isSelected()) onClick(top_menu_input);
        if (top_menu_do.isSelected()) onClick(top_menu_do);
        if (top_menu_output.isSelected()) onClick(top_menu_output);
        if (top_menu_setup.isSelected()) onClick(top_menu_setup);
    }


    //UI组件初始化与事件绑定
    private void bindView() {
        top_menu_input = (TextView)this.findViewById(R.id.topmenu_input);
        top_menu_do = (TextView)this.findViewById(R.id.topmenu_do);
        top_menu_output = (TextView)this.findViewById(R.id.topmenu_output);
        top_menu_setup = (TextView)this.findViewById(R.id.topmenu_setup);
        //ly_content = (FrameLayout) findViewById(R.id.fragment_container);

        top_menu_input.setOnClickListener(this);
        top_menu_setup.setOnClickListener(this);
        top_menu_output.setOnClickListener(this);
        top_menu_do.setOnClickListener(this);

    }

    //重置所有文本的选中状态
    public void selected(){
        top_menu_input.setSelected(false);
        top_menu_setup.setSelected(false);
        top_menu_do.setSelected(false);
        top_menu_output.setSelected(false);
    }

    //隐藏所有Fragment
    public void hideAllFragment(FragmentTransaction transaction){
        if(f1!=null){
            transaction.hide(f1);
        }
        if(f2!=null){
            transaction.hide(f2);
        }
        if(f3!=null){
            transaction.hide(f3);
        }
        if(f4!=null){
            transaction.hide(f4);
        }
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch(v.getId()){
            case R.id.topmenu_input:
                selected();
                top_menu_input.setSelected(true);
                transaction.show(f1);
                break;

            case R.id.topmenu_do:
                selected();
                top_menu_do.setSelected(true);
                transaction.show(f2);
                break;

            case R.id.topmenu_output:
                selected();
                top_menu_output.setSelected(true);
                transaction.show(f3);
                break;

            case R.id.topmenu_setup:
                selected();
                top_menu_setup.setSelected(true);
                transaction.show(f4);
                break;
        }
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getFragmentManager().putFragment(outState,"F_f1",f1);
        getFragmentManager().putFragment(outState,"F_f2",f2);
        getFragmentManager().putFragment(outState,"F_f3",f3);
        getFragmentManager().putFragment(outState,"F_f4",f4);
        outState.putBoolean("top_menu_input",top_menu_input.isSelected());
        outState.putBoolean("top_menu_do",top_menu_do.isSelected());
        outState.putBoolean("top_menu_output",top_menu_output.isSelected());
        outState.putBoolean("top_menu_setup",top_menu_setup.isSelected());
        /*Bundle b_f1=f1.getArguments();
        outState.putBundle("f1",b_f1);
        Bundle b_f2=f2.getArguments();
        outState.putBundle("f2",b_f2);
        Bundle b_f3=f3.getArguments();
        outState.putBundle("f3",b_f3);
        Bundle b_f4=f4.getArguments();
        outState.putBundle("f4",b_f4);*/
        super.onSaveInstanceState(outState);

    }



    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }*/
}

