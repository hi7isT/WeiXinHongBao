//package com.example.huanghy.weixinhongbao;
//
//import android.app.Notification;
//import android.app.PendingIntent;
//import android.content.SharedPreferences;
//import android.os.Parcelable;
//import android.preference.PreferenceManager;
//import android.view.accessibility.AccessibilityEvent;
//import android.view.accessibility.AccessibilityNodeInfo;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;
//
///**
// * Created by huanghy on 2017/2/3.
// */
//
//public class Tss {
//
//    private AccessibilityNodeInfo rootNodeInfo;
//    private AccessibilityNodeInfo mReceiveNode, mUnpackNode;
//    private boolean mLuckyMoneyPicked, mLuckyMoneyReceived, mNeedUnpack;
//    private static final String GET_RED_PACKET = "领取红包";//
//    private static final String CHECK_RED_PACKET = "查看红包";//
//    private static final String RED_PACKET_PICKED = "手慢了，红包派完了";//
//    private static final String RED_PACKET_PICKED_DETAIL = "红包详情";//
//    private static final String RED_PACKET_NOTIFICATION = "[微信红包]";//
//    private String lastContentDescription = "";
//    public static Map<String, Boolean> watchedFlags = new HashMap<>();
//
//    public void onAccessibilityEvent(AccessibilityEvent event) {
//
//        this.rootNodeInfo = event.getSource();
//        if (rootNodeInfo == null) return;
//
//        mReceiveNode = null;
//        mUnpackNode = null;
//
//        checkNodeInfo();
//
//         /* 如果已经接收到红包并且还没有戳开 */
//        if (mLuckyMoneyReceived && !mLuckyMoneyPicked && (mReceiveNode != null)) {
//            AccessibilityNodeInfo cellNode = mReceiveNode;
//            cellNode.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            mLuckyMoneyReceived = false;
//            mLuckyMoneyPicked = true;
//        }
//
//         /* 如果戳开但还未领取 */
//        if (mNeedUnpack && (mUnpackNode != null)) {
//            AccessibilityNodeInfo cellNode = mUnpackNode;
//            cellNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            mNeedUnpack = false;
//        }
//
//    }
//
//    private void checkNodeInfo() {
//         /* 聊天会话窗口，遍历节点匹配“领取红包”和"查看红包",补充：查看红包，是指自己在群里发的红包，私发给他人的不能点击 */
//        List<AccessibilityNodeInfo> nodes1 = this.findAccessibilityNodeInfosByTexts(this.rootNodeInfo, new String[]{GET_RED_PACKET, CHECK_RED_PACKET});
//        if (!nodes1.isEmpty()) {
//            AccessibilityNodeInfo targetNode = nodes1.get(nodes1.size() - 1);
//            if ("android.widget.LinearLayout".equals(targetNode.getParent().getClassName())) {//避免被文字干扰导致外挂失效
//                if (this.signature.generateSignature(targetNode)) {
//                    mLuckyMoneyReceived = true;
//                    mReceiveNode = targetNode;
//                }
//            }
//            return;
//        }
//
//        List<AccessibilityNodeInfo> nodes2 = this.findAccessibilityNodeInfosByTexts(this.rootNodeInfo, new String[]{"拆红包"});
//        if (!nodes2.isEmpty()) {
//            for (AccessibilityNodeInfo nodeInfo : nodes2) {
//                if (nodeInfo.getClassName().equals("android.widget.Button"))
//                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }
//        }
//         /* 戳开红包，红包已被抢完，遍历节点匹配“已存入零钱”和“手慢了” */
//        if (mLuckyMoneyPicked) {
//            List<AccessibilityNodeInfo> nodes3 = this.findAccessibilityNodeInfosByTexts(this.rootNodeInfo, new String[]{RED_PACKET_PICKED, RED_PACKET_PICKED_DETAIL});
//            if (!nodes3.isEmpty()) {
//                if (rootNodeInfo.getChildCount() > 1) {
//                    L.d("RED_PACKET_PICKED!");
//                } else {
//                    if (!nodes3.get(0).getText().toString().equals(RED_PACKET_PICKED_DETAIL)) {
//                        AccessibilityNodeInfo targetNode = nodes3.get(nodes3.size() - 1);
//                    } else {
//                        L.d("this packet is myself!");
//                    }
//
//                }
//                mLuckyMoneyPicked = false;
//            }
//        }
//    }
//
//
//    private boolean watchList(AccessibilityEvent event) {
//        // Not a message
//        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || event.getSource() == null)
//            return false;
//
//        List<AccessibilityNodeInfo> nodes = event.getSource().findAccessibilityNodeInfosByText(RED_PACKET_NOTIFICATION);
//        if (!nodes.isEmpty()) {
//            AccessibilityNodeInfo nodeToClick = nodes.get(0);
//            CharSequence contentDescription = nodeToClick.getContentDescription();
//            if (contentDescription != null && !lastContentDescription.equals(contentDescription)) {
//                nodeToClick.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                lastContentDescription = contentDescription.toString();
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 批量化执行AccessibilityNodeInfo.findAccessibilityNodeInfosByText(text).
//     * 由于这个操作影响性能,将所有需要匹配的文字一起处理,尽早返回
//     *
//     * @param nodeInfo 窗口根节点
//     * @param texts    需要匹配的字符串们
//     * @return 匹配到的节点数组
//     */
//    private List<AccessibilityNodeInfo> findAccessibilityNodeInfosByTexts(AccessibilityNodeInfo nodeInfo, String[] texts) {
//        for (String text : texts) {
//            if (text == null) continue;
//
//            List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(text);
//
//            if (!nodes.isEmpty()) return nodes;
//        }
//        return new ArrayList<>();
//    }
//
//}
