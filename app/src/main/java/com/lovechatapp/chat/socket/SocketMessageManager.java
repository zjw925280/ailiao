package com.lovechatapp.chat.socket;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.lovechatapp.chat.activity.AudioChatActivity;
import com.lovechatapp.chat.activity.CallingActivity;
import com.lovechatapp.chat.activity.WaitActorActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.bean.AVChatBean;
import com.lovechatapp.chat.bean.MultipleChatInfo;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AudioVideoRequester;
import com.lovechatapp.chat.socket.domain.Mid;
import com.lovechatapp.chat.socket.domain.SocketResponse;
import com.lovechatapp.chat.util.LogUtil;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * socket消息分发
 */
public class SocketMessageManager {

    static String TAG = "-socket-";

    private static SocketMessageManager socketMessageManager;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final SparseArray<List<OnCommonListener<SocketResponse>>> subscriptionArray = new SparseArray<>();

    private SocketMessageManager() {
        subscribe(new DefaultSubscription(), DefaultSubscription.Subscriptions);
    }

    public static SocketMessageManager get() {
        if (socketMessageManager == null) {
            socketMessageManager = new SocketMessageManager();
        }
        return socketMessageManager;
    }

    /**
     * 分发
     */
    final void dispatchMessage(final String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    SocketResponse response = JSON.parseObject(content, SocketResponse.class);
                    response.sourceData = content;
                    List<OnCommonListener<SocketResponse>> list = subscriptionArray.get(response.mid);
                    if (list != null) {
                        for (OnCommonListener<SocketResponse> onCommonListener : list) {
                            onCommonListener.execute(response);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 订阅
     */
    public final void subscribe(OnCommonListener<SocketResponse> response, int... types) {
        if (response == null || types == null || types.length == 0)
            return;
        for (int type : types) {
            List<OnCommonListener<SocketResponse>> list = subscriptionArray.get(type);
            if (list == null) {
                list = new ArrayList<>();
                subscriptionArray.put(type, list);
            }
            list.add(response);
        }
    }

    /**
     * 取消订阅
     */
    public final void unsubscribe(OnCommonListener<SocketResponse> response) {
        if (response == null)
            return;
        int size = subscriptionArray.size();
        for (int i = 0; i < size; i++) {
            List<OnCommonListener<SocketResponse>> list = subscriptionArray.valueAt(i);
            if (list != null) {
                list.remove(response);
            }
        }
    }

    /**
     * 默认订阅
     */
    static class DefaultSubscription implements OnCommonListener<SocketResponse> {

        static int[] Subscriptions = {

                //用户收到语音邀请
                Mid.anchorLinkUserToVoiceRes,

                //主播收到语音邀请
                Mid.onLineToVoiceRes,

                //主播收到视频邀请
                Mid.CHAT_LINK,

                //用户收到视频邀请
                Mid.USER_GET_INVITE,

                //封号
                Mid.BEAN_SUSPEND,

                //推送视频公告
                Mid.VIDEO_CHAT_START_HINT,

                //发送模拟消息
                Mid.SEND_VIRTUAL_MESSAGE,

                //1V2房间视频通知连线  用户呼叫主播
                Mid.video_user_to_anchor_onLineRes,

                //1V2房间语音通知连线  用户呼叫主播
                Mid.voice_user_to_anchor_onLineRes

        };

        @Override
        public void execute(SocketResponse response) {

            switch (response.mid) {

                case Mid.anchorLinkUserToVoiceRes:
                case Mid.onLineToVoiceRes: {
                    if (AppManager.getInstance().getActivityObserve().isWaitChatState()) {
                        return;
                    }
                    screenOn();
                    AVChatBean avChatBean = new AVChatBean();
                    avChatBean.chatType = AudioVideoRequester.CHAT_AUDIO;
                    avChatBean.roomId = response.roomId;
                    avChatBean.otherId = response.connectUserId;
                    avChatBean.coverRole = response.mid == Mid.anchorLinkUserToVoiceRes ? 1 : 0;
                    AudioChatActivity.startReceive(AppManager.getInstance(), avChatBean);
                    break;
                }

                case Mid.CHAT_LINK:
                case Mid.USER_GET_INVITE: {
                    if (AppManager.getInstance().getActivityObserve().isWaitChatState()) {
                        return;
                    }
                    screenOn();
                    AVChatBean avChatBean = new AVChatBean();
                    avChatBean.chatType = AudioVideoRequester.CHAT_VIDEO;
                    avChatBean.roomId = response.roomId;
                    avChatBean.otherId = response.connectUserId;
                    avChatBean.coverRole = response.mid == Mid.USER_GET_INVITE ? 1 : 0;
                    start1V1(avChatBean);
                    break;
                }

                case Mid.video_user_to_anchor_onLineRes:
                case Mid.voice_user_to_anchor_onLineRes:
                    if (AppManager.getInstance().getActivityObserve().isWaitChatState()) {
                        return;
                    }
                    screenOn();
                    start1V2(response);
                    break;

                case Mid.BEAN_SUSPEND:
                    AppManager.getInstance().exit(response.message, true);
                    break;

                case Mid.VIDEO_CHAT_START_HINT:

                    break;

                case Mid.SEND_VIRTUAL_MESSAGE:
                    String mTargetId = String.valueOf(10000 + response.activeUserId);
                    TIMTextElem textElem = new TIMTextElem();
                    textElem.setText(response.msgContent);
                    //构造一条消息并添加一个文本内容
                    TIMMessage msg = new TIMMessage();
                    if (msg.addElement(textElem) != 0) {
                        return;
                    }
                    TIMConversation mConversation = TIMManager.getInstance()
                            .getConversation(TIMConversationType.C2C, mTargetId);
                    if (mConversation != null) {
                        mConversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {
                            @Override
                            public void onError(int code, String desc) {
                                LogUtil.i("TIM 发送模拟消息e failed. code: " + code + " errmsg: " + desc);
                            }

                            @Override
                            public void onSuccess(TIMMessage msg) {
                                LogUtil.i("TIM 发送模拟消息 bitmap");
                            }
                        });
                    }
                    break;
            }

        }

        /**
         * 亮屏
         */
        private void screenOn() {
            try {
                PowerManager mPowerManager = (PowerManager) AppManager.getInstance().getSystemService(Service.POWER_SERVICE);
                boolean screenOn = mPowerManager.isInteractive();
                if (!screenOn) {
                    PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock(
                            PowerManager.ACQUIRE_CAUSES_WAKEUP |
                                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "app:bright");
                    mWakeLock.acquire(1000);
                    mWakeLock.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 启动1v1等待接听页面
         */
        private void start1V1(AVChatBean avChatBean) {
            Intent videoIntent = new Intent(AppManager.getInstance(), WaitActorActivity.class);
            videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            videoIntent.putExtra("bean", avChatBean);
            try {
                PendingIntent pendingIntent = PendingIntent.getActivity(AppManager.getInstance(),
                        UUID.randomUUID().hashCode(), videoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e2) {
                e2.printStackTrace();
            }
        }

        /**
         * 启动1v2等待接听页面
         * chatType 1:视频 2:语音
         */
        private void start1V2(SocketResponse response) {

            try {

                MultipleChatInfo multipleChatInfo = JSON.parseObject(response.sourceData, MultipleChatInfo.class);

                Intent videoIntent = new Intent(AppManager.getInstance(), CallingActivity.class);
                videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                videoIntent.putExtra("chatInfo", multipleChatInfo);

                PendingIntent pendingIntent = PendingIntent.getActivity(AppManager.getInstance(),
                        UUID.randomUUID().hashCode(), videoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntent.send();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}