package com.djj.xscj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by djj on 2016/11/6.
 */

public class UpLoadingActivity extends Activity {
    static Activity uploadactivity;
    private ProgressBar progressBar;
    private String ip;
    private ArrayList<String> filepath;
    private int port;
    private TextView textView;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //System.out.println("按下了back键   onBackPressed()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uploadactivity=this;
        Intent intent=this.getIntent();
        setContentView(R.layout.uploading);
        textView = (TextView) findViewById(R.id.textload);
        textView.setText(intent.getStringExtra("type"));

    }


}
