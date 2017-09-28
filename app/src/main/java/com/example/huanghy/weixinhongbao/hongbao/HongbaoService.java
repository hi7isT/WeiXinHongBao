package com.example.huanghy.weixinhongbao.hongbao;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

public class HongbaoService extends AccessibilityService {


    private static final String TAG = "HongbaoService";
    private static final String WEIXIN_HONGBAO = "[微信红包]";

    @Override
    protected void onServiceConnected() {
        Toast.makeText(this, "服务开启成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "服务关闭成功", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    /**
     * 锁屏时需要解锁
     */
    private void unlockScreen() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
        wakeLock.acquire();//唤醒屏幕

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("MyKeyguardLock");
        keyguardLock.disableKeyguard();//解锁
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();

        Log.d(TAG, "事件---->" + event);

        //通知栏事件
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            List<CharSequence> texts = event.getText();
            if (!texts.isEmpty()) {
                for (CharSequence t : texts) {
                    String text = String.valueOf(t);
                    if (text.contains(WEIXIN_HONGBAO)) {
                        unlockScreen();//当通知为“微信红包”时，才进行屏幕解锁。
                        openNotification(event);
                        break;
                    }
                }
            }
            //TODO 出现bug，可能出现连续点击聊天界面两个红包的情况
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            openEnvelope(event);
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            checkKey2();
        }
    }

    /**
     * 打开通知栏消息,跳转到红包所在的聊天窗口
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openNotification(AccessibilityEvent event) {
        if (event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification)) {
            return;
        }
        //以下是精华，将微信的通知栏消息打开
        Notification notification = (Notification) event.getParcelableData();
        PendingIntent pendingIntent = notification.contentIntent;
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openEnvelope(AccessibilityEvent event) {
        if (event.getClassName().toString().startsWith("com.tencent.mm.plugin.luckymoney.ui")) {
//        if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(event.getClassName())) {
            //点中了红包，下一步就是去拆红包
            checkKey1();
        } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName())) {
            //拆完红包后看详细的纪录界面
            //nonething
        } else if ("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {
            //在聊天界面,去点中红包
            checkKey2();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void checkKey1() {

        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("@com.tencent.mm:id/bp6");
        if (list != null && list.size() > 0) {
            AccessibilityNodeInfo n = list.get(0);//取出“开”，一般该list只有一个。
            while (n != null) {
                if (n.isClickable()) {
                    n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
                n = n.getParent();
            }
        }

    }

    /** 在聊天界面,去点中红包,在微信第一个tab窗口 则去点击[微信红包]恭喜发财，大吉大利*/
    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void checkKey2() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return;
        }
        //
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");
        if (list.isEmpty()) {
            list = nodeInfo.findAccessibilityNodeInfosByViewId("@com.tencent.mm:id/aje");
            for (AccessibilityNodeInfo n : list) {
                if (n.getText().toString().contains(WEIXIN_HONGBAO)) {
                    while (n != null) {
                        if (n.isClickable()) {
                            n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        }
                        n = n.getParent();
                    }
                }

            }
        } else {
            //最新的红包领起
            for (int i = list.size() - 1; i >= 0; i--) {
                AccessibilityNodeInfo parent = list.get(i).getParent();
                Log.i(TAG, "-->领取红包:" + parent);
                while (parent != null) {
                    if(parent.isClickable()){
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
                    parent = parent.getParent();
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    //----------------------------------old------------------------------------------------------------------

//    private static final String TAG = "HongbaoService";
//    private static final String WEIXIN_HONGBAO = "[微信红包]";
//    private List<AccessibilityNodeInfo> lingquHongbaoNode = new ArrayList<AccessibilityNodeInfo>();//"领取红包"的节点集合
//    private List<AccessibilityNodeInfo> chaikaiHongbaoNode = new ArrayList<AccessibilityNodeInfo>();//“开”的节点集合
//    private boolean lingquHongbaoCanClick;
//    private boolean chaikaiHongbaoCanClick;
//    private boolean tongzhiComing;
//    private int twice = 0;
//
//
//    /**
//     * 页面变化回调事件
//     * @param event
//     *              event.getEventType() 当前事件的类型;
//     *              event.getClassName() 当前类的名称;
//     *              event.getSource() 当前页面中的节点信息；
//     *              event.getPackageName() 事件源所在的包名
//     */
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//    @Override
//    public void onAccessibilityEvent(AccessibilityEvent event) {
//
//        int eventType = event.getEventType();
//        switch (eventType) {
//            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED://通知栏状态变化
//                handleNotification(event);
//                break;
//            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://窗口状态变化
//                AccessibilityNodeInfo rootNode = event.getSource();
//                if (rootNode != null) {
//                    lingquHongbaoNode.clear();
//                    chaikaiHongbaoNode.clear();
//                    recycle(rootNode);
//                    findChaikaiHongbao();
//                    if (lingquHongbaoCanClick && tongzhiComing) {
//                        int size = lingquHongbaoNode.size();
//                        if (size > 0) {
//                            AccessibilityNodeInfo cellNode = lingquHongbaoNode.get(size - 1);
//                            cellNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                            lingquHongbaoCanClick = false;
//                            tongzhiComing = false;
//                        }
//                    }
//
//                    if (chaikaiHongbaoCanClick) {
//                        int size = chaikaiHongbaoNode.size();
//                        if (size > 0) {
//                            AccessibilityNodeInfo cellNode = chaikaiHongbaoNode.get(size - 1);
//                            cellNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                            chaikaiHongbaoCanClick = false;
//                        }
//                    }
//                }
//                break;
//            case AccessibilityEvent.TYPE_VIEW_SCROLLED://每次在聊天界面中有新消息到来时都出触发该事件
////                twice++;
////                Toast.makeText(this, "twice:" + twice, Toast.LENGTH_SHORT).show();
////                //获取微信聊天窗口的最后一个节点，并判断是不是“领取红包”红包节点，如果是就点击。(补充：所有的红包节点的id为a4w.聊天窗口中，一条记录就是一个id为p的节点。)
////                AccessibilityNodeInfo rootNode2 = getRootInActiveWindow();
////                if (rootNode2 != null) {
////                    List<AccessibilityNodeInfo> list = rootNode2.findAccessibilityNodeInfosByViewId("@com.tencent.mm:id/p");
////                    Toast.makeText(this, "数量" + twice + ": " + "list.size():" + list.size(), Toast.LENGTH_SHORT).show();
////                    if(list != null && list.size() > 0){
////                        AccessibilityNodeInfo nodeLastOne = list.get(list.size() - 1);//最后一个id为p的节点（即最后一个节点）
////                        List<AccessibilityNodeInfo> hongbaoList = nodeLastOne.findAccessibilityNodeInfosByViewId("@com.tencent.mm:id/a4w");
////                        List<AccessibilityNodeInfo> lingquHongbaoList = nodeLastOne.findAccessibilityNodeInfosByText("领取红包");
////                        if (hongbaoList.get(0) != null && lingquHongbaoList.get(0) != null) {//如果最后一个节点是“领取红包”，且该节点是个红包节点
////                            if (hongbaoList.get(0).isClickable()) {
////                                Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
////                                hongbaoList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
////                            }
////                        }
////                    }
////
////                }
//                break;
//            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED://窗口内容变化
//                break;
//        }
//    }
//
//    /**
//     * 红包通知, 并跳转到"领取红包"的聊天窗口
//     *
//     * @param event
//     */
//    private void handleNotification(AccessibilityEvent event) {
//        List<CharSequence> texts = event.getText();
//        if (!texts.isEmpty()) {
//            for (CharSequence text : texts) {
//                String content = text.toString();
//                if (content.contains(WEIXIN_HONGBAO)) {
//                    unlockScreen();//当通知为“微信红包”时，才进行屏幕解锁。
//                    tongzhiComing = true;//当通知为“微信红包”时，才会设为true
//                    if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
//                        Notification notification = (Notification) event.getParcelableData();
//                        PendingIntent pendingIntent = notification.contentIntent;
//                        try {
//                            pendingIntent.send();
//                        } catch (PendingIntent.CanceledException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//            }
//        }
//    }
//
//    /**
//     * 遍历查找"领取红包"
//     *
//     * @param node
//     */
//    public void recycle(AccessibilityNodeInfo node) {
//        if (node.getChildCount() == 0) {
//            if (node.getText() != null) {
//                if ("领取红包".equals(node.getText().toString())) {
//                    //这里有一个问题需要注意，就是需要找到一个可以点击的View
//                    AccessibilityNodeInfo parent = node.getParent();
//                    while (parent != null) {
//                        Log.i("demo", "parent isClick:" + parent.isClickable());
//                        if (parent.isClickable()) {
//                            lingquHongbaoCanClick = true;
//                            lingquHongbaoNode.add(parent);
//                            break;//此处使用return，不会将递归返回，导致其他的“领取红包”也会放进集合，而你从后面开始往集合中装“领取红包”，也就是在聊天页面前面的“领取红包”反而在集合的后面，你再取集合最后面的进行点击就会出现，一直都是领第一个红包的情况出现。
//                        }
//                        parent = parent.getParent();
//                    }
//                }
//            }
//        } else {
//            for (int i = 0; i < node.getChildCount(); i++) {
//                if (node.getChild(i) != null) {
//                    recycle(node.getChild(i));
//                }
//            }
//        }
//    }
//
//    /**
//     * 找到“开”节点的可Clickable节点，把他保存到集合，并打开chaikaiisClicked开关;
//     */
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//    private void findChaikaiHongbao() {
//        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
//        if (nodeInfo != null) {
////            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("拆红包");
////            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("@com.tencent.mm:id/bi3");
//            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("@com.tencent.mm:id/bp6");
//            if (list == null && list.size() > 0) return;
//            for (AccessibilityNodeInfo item : list) {
//                if (item.isClickable()) {
//                    chaikaiHongbaoCanClick = true;
//                    chaikaiHongbaoNode.add(item);
//                }
//            }
//        }
//    }
//
//
//    /**
//     * 在服务开关开启时候执行
//     */
//    @Override
//    protected void onServiceConnected() {
//        Toast.makeText(this, "服务已开启", Toast.LENGTH_SHORT).show();
//    }
//
//    /**
//     * 在服务开关关闭时候执行
//     * @param intent
//     * @return
//     */
//    @Override
//    public boolean onUnbind(Intent intent) {
//        Toast.makeText(this, "服务已关闭", Toast.LENGTH_SHORT).show();
//        return super.onUnbind(intent);
//    }
//
//    @Override
//    /**
//     * 必须重写的方法：系统要中断此service返回的响应时会调用。在整个生命周期会被调用多次。
//     */
//    public void onInterrupt() {//解决办法：加入白名单
////        Toast.makeText(this, "服务被强制close", Toast.LENGTH_LONG).show();
//    }
//
//
//    /**
//     * 锁屏时需要解锁
//     */
//    private void unlockScreen() {
//        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
//        wakeLock.acquire();//唤醒屏幕
//
//        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("MyKeyguardLock");
//        keyguardLock.disableKeyguard();//解锁
//    }
}
