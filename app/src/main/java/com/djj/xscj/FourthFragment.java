package com.djj.xscj;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by djj on 2017/1/3.
 */

public class FourthFragment extends Fragment {
    private EditText et_ip, et_port;
    private Button bt_save;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //eturn super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_setup, container, false);
        et_ip = (EditText) view.findViewById(R.id.et_setupip);
        et_port = (EditText) view.findViewById(R.id.et_setupport);
        bt_save = (Button) view.findViewById(R.id.bt_savesetup);
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("main",
                Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        et_ip.setText(sharedPreferences.getString("ip", "192.168.21.1"));
        et_port.setText(String.valueOf(sharedPreferences.getInt("port", 12702)));
        bt_save.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("ip", et_ip.getText().toString());
                editor.putInt("port", Integer.parseInt(et_port.getText().toString()));
                editor.putBoolean("isfirst", false);
                editor.apply();
                ((MyApplication) getActivity().getApplication()).setIp(et_ip.getText().toString());
                ((MyApplication) getActivity().getApplication()).setPort(sharedPreferences.getInt("port", 12702));
            }
        });
        return view;
    }
}
