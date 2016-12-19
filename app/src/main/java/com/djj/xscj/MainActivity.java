package com.djj.xscj;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.djj.xscj.FirstFragment.RemoveItemListener;

public class MainActivity extends Activity implements View.OnClickListener{
    private TextView top_menu_input;
    private TextView top_menu_do;
    private TextView top_menu_setup;
    private TextView top_menu_output;
    private String mIp="djj1111.gicp.net";

   //private FrameLayout ly_content;

    private FirstFragment f1,f4;
    private SecondFragment f2,f3;
    public String getIp(){
        return mIp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.top_menu);

        bindView();
        init();
    }

    private  void init(){
        FragmentTransaction mtransaction = getFragmentManager().beginTransaction();
        f1 = new FirstFragment();
        f2 = new SecondFragment();
        f3 = new SecondFragment();
        f4 = new FirstFragment();
        f1.setRemoveItemListener(new RemoveItemListener(){
            @Override
            public void removeitem(int direction,Object o) {
                switch (direction){
                    case 0:f2.add((String)o);
                        break;
                    case 1:f4.add((String)o);
                        break;
                }

            }
        });
        f2.setRemoveItemListener(new SecondFragment.RemoveItemListener(){
            @Override
            public void removeitem(int direction,Object o) {
                switch (direction){
                    case 0:f3.add((String)o);
                        break;
                    case 1:f1.add((String)o);
                        break;
                }
            }
        });
        f3.setRemoveItemListener(new SecondFragment.RemoveItemListener(){
            @Override
            public void removeitem(int direction,Object o) {
                switch (direction){
                    case 0:f4.add((String)o);
                        break;
                    case 1:f2.add((String)o);
                        break;
                }
            }
        });
        f4.setRemoveItemListener(new RemoveItemListener(){
            @Override
            public void removeitem(int direction,Object o) {
                switch (direction){
                    case 0:f1.add((String)o);
                        break;
                    case 1:f3.add((String)o);
                        break;
                }
            }
        });

        mtransaction.add(R.id.fragment_container,f1);
        mtransaction.add(R.id.fragment_container,f2);
        mtransaction.add(R.id.fragment_container,f3);
        mtransaction.add(R.id.fragment_container,f4);
        //hideAllFragment(transaction);
        mtransaction.commit();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

