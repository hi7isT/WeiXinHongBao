package com.example.huanghy.weixinhongbao.hongbao;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.huanghy.weixinhongbao.R;
import com.example.huanghy.weixinhongbao.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class HongBaoActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout openFz,openTzl;
    List<String> imeis = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hong_bao);
        AddRights();
        for (String imei : imeis) {
            if (!imei.equals(AppUtil.getIMEI1(HongBaoActivity.this))) {
                Toast.makeText(this, "抱歉，身份认证失败，您没有使用权限!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        openFz = (LinearLayout) findViewById(R.id.openFz);
        openTzl = (LinearLayout) findViewById(R.id.openTzl);
        openFz.setOnClickListener(this);
        openTzl.setOnClickListener(this);
    }

    private void AddRights() {
        imeis.add("864106034586198");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        for (String imei : imeis) {
            if (!imei.equals(AppUtil.getIMEI1(HongBaoActivity.this))) {
                Toast.makeText(this, "抱歉，身份认证失败，您没有使用权限!", Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        switch (item.getItemId()) {
            case R.id.openFz:
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                break;
            case R.id.openTzl:
                gotoNotificationAccessSetting(HongBaoActivity.this);
                break;
//            case R.id.about://关于
//                startActivity(new Intent(HongBaoActivity.this,AboutActivity.class));
//                break;
        }
        return true;
    }


    /**
     * 打开 通知使用权页面
     * @param context
     * @return
     */
    private boolean gotoNotificationAccessSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch(ActivityNotFoundException e) {
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings","com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
                return true;
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openFz:
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                break;
            case R.id.openTzl:
                gotoNotificationAccessSetting(HongBaoActivity.this);
                break;
        }
    }
}
