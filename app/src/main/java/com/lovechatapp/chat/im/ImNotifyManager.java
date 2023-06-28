package com.lovechatapp.chat.im;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.ImCustomMessage;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.helper.ActivityRegister;
import com.lovechatapp.chat.listener.OnCommonListener;

import java.util.Arrays;
import java.util.List;

/**
 * Im通知管理器
 */
public class ImNotifyManager implements TIMMessageListener, OnCommonListener<Boolean> {

    private static ImNotifyManager notifyManager;
    private boolean notifyEnable;
    private NotificationManager notificationManager;
    private final SparseArray notifyIds = new SparseArray();
    private final String channelId;
    private final String channelName;

    private ImNotifyManager() {
//        setNotifyEnable(true);
        channelName = "IM消息";
        channelId = AppManager.getInstance().getPackageName() + ".im";
        getNotifyMgr();
    }

    public static ImNotifyManager get() {
        if (notifyManager == null) {
            notifyManager = new ImNotifyManager();
        }
        return notifyManager;
    }

    public final void setNotifyEnable(boolean notifyEnable) {
        this.notifyEnable = notifyEnable;
        if (!notifyEnable) {
            cancelImNotify();
        }
    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        if (notifyEnable && list != null && list.size() > 0) {
            for (TIMMessage timMessage : list) {
                comeMessage(timMessage);
            }
        }
        return false;
    }

    /**
     * app前后台状态监听
     *
     * @param isBackground
     */
    @Override
    public void execute(Boolean isBackground) {
        setNotifyEnable(isBackground);
    }

    private void getNotifyMgr() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) AppManager.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
            ActivityRegister.get().add(this);
        }
    }

    /**
     * 获取头像
     *
     * @param peer
     * @param listener
     */
    private void getUserFace(String peer, final OnCommonListener<Bitmap> listener) {
        TIMFriendshipManager.getInstance().getUsersProfile(
                Arrays.asList(peer),
                false,
                new TIMValueCallBack<List<TIMUserProfile>>() {

                    @Override
                    public void onError(int i, String s) {
                        listener.execute(null);
                    }

                    @Override
                    public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                        if (timUserProfiles != null && timUserProfiles.size() > 0) {
                            TIMUserProfile timUserProfile = timUserProfiles.get(0);
                            String url = timUserProfile.getFaceUrl();
                            if (TextUtils.isEmpty(url)) {
                                listener.execute(null);
                            } else {
                                Glide.with(AppManager.getInstance())
                                        .asBitmap()
                                        .load(url)
//                                        .transform(new GlideCircleTransform(AppManager.getInstance()))
                                        .into(new SimpleTarget<Bitmap>() {

                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                listener.execute(resource);
                                            }

                                            @Override
                                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                listener.execute(null);
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    /**
     * 新消息通知
     */
    private void comeMessage(final TIMMessage timMessage) {
        //过滤个人消息
        int userId = AppManager.getInstance().getUserInfo().t_id;
        if (userId == 0 || String.valueOf(userId + 10000).equals(timMessage.getSender())) {
            return;
        }
        getNotifyMgr();
        synchronized (notifyIds) {
            int notifyId = 101;
            try {
                notifyId = Integer.parseInt(timMessage.getSender());
            } catch (Exception e) {
                e.printStackTrace();
            }
            notifyIds.put(notifyId, null);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            final int finalNotifyId = notifyId;
            getUserFace(timMessage.getSender(), new OnCommonListener<Bitmap>() {
                @Override
                public void execute(Bitmap bitmap) {
                    PendingIntent pendingIntent = createPendingIntent(timMessage);
                    NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(AppManager.getInstance())
                            .setContentTitle(timMessage.getSenderNickname())
                            .setContentText(getMessageContent(timMessage))
                            .setContentIntent(pendingIntent)
                            .setChannelId(channelId)
                            .setAutoCancel(true)
                            .setTicker(AppManager.getInstance().getApplicationInfo().name)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setLargeIcon(bitmap == null ?
                                    BitmapFactory.decodeResource(AppManager.getInstance().getResources(), R.drawable.default_head_img)
                                    : bitmap)
                            .setSmallIcon(R.drawable.detail_text);
                    Notification notification = mNotifyBuilder.build();
                    notificationManager.notify(finalNotifyId, notification);
                }
            });
        }
    }

    /**
     * 跳转页面
     */
    private PendingIntent createPendingIntent(TIMMessage timMessage) {
        if (AppManager.getInstance().getUserInfo().t_id == 0) {
            notificationManager.cancelAll();
            return null;
        }
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(TIMConversationType.C2C);
        chatInfo.setId(timMessage.getSender());
        String chatName = timMessage.getSenderNickname();
        chatInfo.setChatName(chatName);
        Intent intent = new Intent(AppManager.getInstance(), ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ImConstants.CHAT_INFO, chatInfo);
        return PendingIntent.getActivity(AppManager.getInstance(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 清除Im通知消息
     */
    public final void cancelImNotify() {
        getNotifyMgr();
        synchronized (notifyIds) {
            for (int i = 0; i < notifyIds.size(); i++) {
                int key = notifyIds.keyAt(i);
                notificationManager.cancel(key);
            }
            notifyIds.clear();
        }
    }

    /**
     * 获取最后一条消息内容
     */
    public static String getMessageContent(TIMMessage timMessage) {
        if (timMessage != null && timMessage.getElementCount() > 0) {
            //获取第一个ELEM
            TIMElem elem = timMessage.getElement(0);
            if (elem.getType() == TIMElemType.Text) {
                TIMTextElem textElem = (TIMTextElem) elem;
                return textElem.getText();
            } else if (elem.getType() == TIMElemType.Custom) {
                TIMCustomElem customElem = (TIMCustomElem) elem;
                return parseCustomMessage(customElem);
            } else if (elem.getType() == TIMElemType.Image) {
                return "[图片]";
            } else if (elem.getType() == TIMElemType.Sound) {
                return "[语音]";
            } else if (elem.getType() == TIMElemType.Video) {
                return "[视频]";
            } else if (elem.getType() == TIMElemType.File) {
                return "[文件]";
            }
        }
        return "";
    }

    public static String getGroupContent(TIMMessage timMessage) {
        if (timMessage != null && timMessage.getElementCount() > 0) {
            TIMElem elem = timMessage.getElement(0);
            if (elem.getType() == TIMElemType.GroupTips
                    || elem.getType() == TIMElemType.GroupSystem) {
                return "系统消息";
            }
        }
        return getMessageContent(timMessage);
    }

    /**
     * 解析自定义消息,返回礼物名称
     */
    private static String parseCustomMessage(TIMCustomElem customElem) {
        try {
            byte[] data = customElem.getData();
            String json = new String(data);
            json = json.replace("serverSend&&", "{");
            ImCustomMessage customMessageBean = JSON.parseObject(json, ImCustomMessage.class);
            if (customMessageBean != null) {
                return customMessageBean.getImType();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 检查通知开关
     */
    public final void checkSwitch(Activity context, OnCommonListener<String> onCommonListener) {
        boolean isNotifyEnable = NotificationManagerCompat.from(context).areNotificationsEnabled();
        boolean isChannelEnable = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
                channel = notificationManager.getNotificationChannel(channelId);
            }
            isChannelEnable = channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
        }
        if (onCommonListener != null) {
            String result = null;
            if (!isNotifyEnable) {
                result = "打开通知，及时接收消息";
            } else if (!isChannelEnable) {
                result = "打开" + channelName + "通知，及时接收消息";
            }
            onCommonListener.execute(result);
        }
    }

    /**
     * 进入通知栏设置
     */
    public final void toNotifyManager() {
        Context context = AppManager.getInstance();
        Intent intent = new Intent();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}