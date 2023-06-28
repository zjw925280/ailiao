package com.lovechatapp.chat.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.lovechatapp.chat.activity.SystemMessageActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.socket.ConnectHelper;
import com.lovechatapp.chat.util.LogUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                LogUtil.i("[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
                if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                    dealMessage(bundle, context);
                } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                    LogUtil.i("用户点击打开了通知");
                    //打开自定义的Activity
                    Intent i = new Intent(context, SystemMessageActivity.class);
                    i.putExtras(bundle);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                    LogUtil.i("[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                    //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
                } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                    boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                    LogUtil.i("[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
                } else {
                    LogUtil.i("[MyReceiver] Unhandled intent - " + intent.getAction());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 利用极光推送的消息变化判断长连接状态
         */
        ConnectHelper.get().checkConnect();
    }

    /**
     * 处理自定义消息
     */
    private void dealMessage(Bundle bundle, Context context) {
        String customMessage = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        LogUtil.i("接收到推送下来的自定义消息: " + customMessage);
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                if (!TextUtils.isEmpty(extra)) {
                    try {
                        JSONObject json = new JSONObject(extra);
                        Iterator<String> it = json.keys();
                        while (it.hasNext()) {
                            String myKey = it.next();
                            if (myKey.equals("noticeType")) {
                                int result = json.getInt("noticeType");
                                if (result == 1) {
                                    //将接收到的消息利用广播发送出去
                                    String content = "{\"mid\":30006}";
                                    Intent intent = new Intent("com.lovechatapp.chat.socket");
                                    intent.putExtra("message", content);
                                    context.sendBroadcast(intent);

                                    AppManager.getInstance().exit(customMessage, true);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    LogUtil.i("This message has no Extra data");
                    continue;
                }
                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    LogUtil.i("Get message extra JSON error!");
                }
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }

}