package com.example.huanghy.weixinhongbao.setting;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.huanghy.weixinhongbao.R;

public class SentenceActivity extends Activity implements View.OnClickListener {

    private ImageView back;
    private Button setMySentence;
    private EditText myText;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence);
        back = (ImageView) findViewById(R.id.back);
        setMySentence = (Button) findViewById(R.id.setMySentence);
        myText = (EditText) findViewById(R.id.my_text);
        back.setOnClickListener(this);
        setMySentence.setOnClickListener(this);

        preferences = getSharedPreferences("mySentence",MODE_PRIVATE);
        String mySentence = preferences.getString("myText", "");
        myText.setText(mySentence);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setMySentence:
                editor = getSharedPreferences("mySentence", MODE_PRIVATE).edit();
                editor.putString("myText", myText.getText().toString());
                editor.commit();
                Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
