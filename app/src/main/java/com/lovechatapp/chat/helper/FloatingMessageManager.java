package com.lovechatapp.chat.helper;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.imuxuan.floatingview.FloatingView;
import com.lovechatapp.chat.activity.VideoChatActivity;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.AudioChatActivity;
import com.lovechatapp.chat.activity.PhoneLoginActivity;
import com.lovechatapp.chat.activity.RegisterActivity;
import com.lovechatapp.chat.activity.ScrollLoginActivity;
import com.lovechatapp.chat.activity.SplashActivity;
import com.lovechatapp.chat.activity.WaitActorActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.im.ChatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class FloatingMessageManager {

    /**
     * 设置不显示悬浮窗的Activity
     */
    private final static Class[] filter = new Class[]{
            SplashActivity.class,
            ChatActivity.class,
            AudioChatActivity.class,
            VideoChatActivity.class,
            WaitActorActivity.class,
            PhoneLoginActivity.class,
            RegisterActivity.class,
            ScrollLoginActivity.class
    };

    private static int actorId;
    private final static List<MessageInfo> userProfiles = new ArrayList<>();

    private static class MessageInfo {
        public String name;
        public String icon;
        public String message;
        public String t_id;
    }

    public static void init() {
        FloatingView.get().setLookClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageInfo messageInfo = (MessageInfo) FloatingView.get().getTag();
                if (messageInfo != null) {

                    ListIterator<MessageInfo> iterator = userProfiles.listIterator();
                    while (iterator.hasNext()) {
                        MessageInfo next = iterator.next();
                        if (!TextUtils.isEmpty(messageInfo.name) && messageInfo.name.equals(next.name)) {
                            iterator.remove();
                        }
                    }
                    removeMessage(messageInfo.name);
                    IMHelper.toChat(FloatingView.get().getActivity(), messageInfo.name, actorId, -1);
                }
            }
        });
        FloatingView.get().setHideListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMessage();
            }
        });
        AppManager.getInstance().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (isFilter(activity)) {
                    FloatingView.get().setVisible(false);
                    FloatingView.get().setEnnable(false);
                } else {
                    FloatingView.get().attach(activity);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (isFilter(activity)) {
                    FloatingView.get().setEnnable(true);
                    nextMessage();
                } else {
                    FloatingView.get().detach(activity);
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {
                /**
                 *判断是否可接收消息，处于过滤中的activity则不接收消息0
                 */
                if (!FloatingView.get().isEnnable()) {
                    return false;
                }
                TIMMessage newMessage = null;
                if (list != null && list.size() > 0) {
                    for (TIMMessage timMessage : list) {
                        TIMConversation conversation = timMessage.getConversation();
                        if (conversation != null
                                && conversation.getType() == TIMConversationType.C2C
                                && !TextUtils.isEmpty(conversation.getPeer())
                                && !"admin".equals(timMessage.getSender())) {
                            newMessage = timMessage;
                            break;
                        }
                    }
                }
                if (newMessage != null && AppManager.getInstance().getUserInfo().t_role != 1 && !"admin".equals(newMessage.getSender())) {
                    try {
                        final MessageInfo messageInfo = new MessageInfo();
                        for (int i = 0; i < newMessage.getElementCount(); i++) {
                            if (newMessage.getElement(i).getType() == TIMElemType.Text) {
                                messageInfo.message = ((TIMTextElem) newMessage.getElement(i)).getText();
                                messageInfo.t_id = newMessage.getConversation().getPeer();
                                break;
                            }
                        }
                        actorId = Integer.parseInt(newMessage.getSender()) - 10000;
                        if (actorId != AppManager.getInstance().getUserInfo().t_id) {
                            TIMFriendshipManager.getInstance().getUsersProfile(Arrays.asList(list.get(0).getSender()),
                                    false, new TIMValueCallBack<List<TIMUserProfile>>() {
                                        @Override
                                        public void onError(int i, String s) {
                                        }

                                        @Override
                                        public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                                            if (timUserProfiles != null && timUserProfiles.size() > 0) {
                                                TIMUserProfile timUserProfile = timUserProfiles.get(0);
                                                messageInfo.name = timUserProfile.getNickName();
                                                messageInfo.icon = timUserProfile.getFaceUrl();
                                                synchronized (userProfiles) {
                                                    userProfiles.add(messageInfo);
                                                    nextMessage();
                                                }
                                            }
                                        }
                                    });
                        }
                    } catch (Exception e) {

                    }
                }
                return false;
            }
        });
    }

    public static void removeMessage(String name) {
        synchronized (userProfiles) {
            ListIterator<MessageInfo> iterator = userProfiles.listIterator();
            while (iterator.hasNext()) {
                MessageInfo next = iterator.next();
                if (!TextUtils.isEmpty(name) && name.equals(next.t_id)) {
                    iterator.remove();
                }
            }
        }
    }

    private static boolean isFilter(Activity activity) {
        boolean ok = false;
        for (Class aClass : filter) {
            if (aClass == activity.getClass()) {
                ok = true;
                break;
            }
        }
        return ok;
    }

    /**
     * 轮询消息
     */
    private static void nextMessage() {
        if (userProfiles.size() > 0 && FloatingView.get().isEnnable() && !FloatingView.get().isVisible()) {
            synchronized (userProfiles) {
                if (userProfiles.size() > 0) {
                    MessageInfo messageInfo = userProfiles.get(0);
                    userProfiles.remove(0);
                    FloatingView.get().setVisible(true);
                    if (TextUtils.isEmpty(messageInfo.icon)) {
                        FloatingView.get().getView().setIconImage(R.drawable.default_head_img);
                    } else {
                        ImageLoadHelper.glideShowCircleImageWithUrl(AppManager.getInstance(),
                                messageInfo.icon,
                                FloatingView.get().getView().getIcon(),
                                100, 100);
                    }
                    FloatingView.get().setMessage(messageInfo.name, messageInfo.message);
                    FloatingView.get().setTag(messageInfo);
                }
            }
        }
    }
}