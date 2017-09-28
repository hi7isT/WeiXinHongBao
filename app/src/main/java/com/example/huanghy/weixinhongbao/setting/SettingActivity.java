package com.example.huanghy.weixinhongbao.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.huanghy.weixinhongbao.R;

public class SettingActivity extends Activity implements View.OnClickListener {

    private LinearLayout sentence;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sentence = (LinearLayout) findViewById(R.id.sentence);
        back = (ImageView) findViewById(R.id.back);
        sentence.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sentence:
                startActivity(new Intent(SettingActivity.this,SentenceActivity.class));
                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
