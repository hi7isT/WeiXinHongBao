package com.example.huanghy.weixinhongbao.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

/**
 * Created by huanghy on 2017/9/16.
 * App工具类
 */

public class AppUtil {

    /**
     * 获取版本号
     * @param context   当前应用的版本号
     * @return
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "无法获取版本号";
        }
    }

    /**
     * 获取IMEI号 全球移动设备身份识别码 是全球唯一的,即：“手机识别码”
     * @param context
     * @return
     */
    public static String getIMEI1(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }


}
