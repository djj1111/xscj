package com.djj.xscj;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity{
    private EditText etUsername, etPassword;
    private CheckBox cbAutofill, cbSavename;
    private Button btStart;
    private String user, password;
    private boolean isfirst = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = (EditText) findViewById(R.id.et_user);
        etPassword = (EditText) findViewById(R.id.et_password);
        cbAutofill = (CheckBox) findViewById(R.id.checkBox2_autologin);
        cbSavename = (CheckBox) findViewById(R.id.checkBox_savepassword);
        btStart = (Button) findViewById(R.id.bt_signin);
        SharedPreferences sharedPreferences = getSharedPreferences("main",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        //用putString的方法保存数据
        ((MyApplication) getApplication()).setIp(sharedPreferences.getString("ip", "192.168.21.1"));
        ((MyApplication) getApplication()).setPort(sharedPreferences.getInt("port", 12702));
        user = sharedPreferences.getString("user", "");
        password = sharedPreferences.getString("password", "");
        isfirst = sharedPreferences.getBoolean("isfirst", true);
        cbAutofill.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!user.equals("")) etUsername.setText(user);
                    if (!password.equals("")) etPassword.setText(password);
                } else {
                    etUsername.setText("");
                    etPassword.setText("");
                }
            }
        });

        cbAutofill.setChecked(sharedPreferences.getBoolean("isauto", false));
        cbSavename.setChecked(sharedPreferences.getBoolean("issave", false));
        btStart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etUsername.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "用户名不能为空，请重试", Toast.LENGTH_SHORT).show();
                    etUsername.requestFocus();
                    return;
                }
                if (etPassword.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "密码不能为空，请重试", Toast.LENGTH_SHORT).show();
                    etPassword.requestFocus();
                    return;
                }

                //写入状态
                editor.putBoolean("issave", cbSavename.isChecked());

                if (cbSavename.isChecked()) {
                    editor.putBoolean("isauto", cbAutofill.isChecked());
                    editor.putString("user", etUsername.getText().toString());
                    editor.putString("password", etPassword.getText().toString());
                } else {
                    editor.remove("isauto");
                    editor.remove("user");
                    editor.remove("password");
                }

                editor.apply();
                ((MyApplication) getApplication()).setUsername(etUsername.getText().toString());
                ((MyApplication) getApplication()).setPassword(etPassword.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("isfirst", isfirst);
                intent.setClass(LoginActivity.this, MainActivity.class);//跳转到加载界面
                startActivity(intent);
                finish();
            }
        });


    }
}

