package com.example.huanghy.weixinhongbao.other;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import com.example.huanghy.weixinhongbao.R;

public class LauncherActivity extends Activity {

    private TextView tv;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        preferences = getSharedPreferences("mySentence", MODE_PRIVATE);
        String sentence = preferences.getString("myText", "");
        tv = (TextView) findViewById(R.id.tv);
        if (!"".equals(sentence)) {
            tv.setText(sentence);
        }else{
            //使用系统内置的语句，这里先写成静态的
            tv.setText("再多一点努力，就多一点成功");
        }
        int tvLength = tv.length();
        if (tvLength <= 18) {
            tv.setGravity(Gravity.CENTER);
        } else {
            tv.setGravity(Gravity.LEFT);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
