package com.lovechatapp.chat.helper;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Vibrator;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMMessage;

/**
 * IM铃声、振动
 */
public class RingVibrateManager {

    private static SoundPool mSoundPool;

    private static int mSoundId;

    private static long lastRingTime;

    private static long lastVibrateTime;

    private static boolean isInit = false;

    /**
     * 音频开关
     */
    private static boolean soundEnable = false;

    /**
     * 震动开关
     */
    private static boolean vibrateEnable = true;

    /**
     * 群消息音频开关
     */
    private static boolean soundGroupEnable = false;

    /**
     * 群消息震动开关
     */
    private static boolean vibrateGroupEnable = true;

    /**
     * 消息铃声
     */
    private static void onRing() {

        if (System.currentTimeMillis() - lastRingTime < 1000) {
            return;
        }
        lastRingTime = System.currentTimeMillis();

        try {

            if (mSoundPool == null) {
                //初始化SoundPool
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes aab = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build();
                    mSoundPool = new SoundPool.Builder()
                            .setMaxStreams(5)
                            .setAudioAttributes(aab)
                            .build();
                } else {
                    mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 8);
                }
                //设置资源加载监听
                mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        if (mSoundPool != null && mSoundId > 0) {
                            mSoundPool.play(mSoundId, 1, 1, 0, 0, 1);
                        }
                    }
                });
                //加载deep 音频文件
                mSoundId = mSoundPool.load(AppManager.getInstance(), R.raw.new_message, 1);
            } else {
                if (mSoundId > 0) {
                    mSoundPool.play(mSoundId, 1, 1, 0, 0, 1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 震动提醒
     */
    private static void onVibrate() {
        if (System.currentTimeMillis() - lastVibrateTime < 1000) {
            return;
        }
        lastVibrateTime = System.currentTimeMillis();
        Vibrator vibrator = (Vibrator) AppManager.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(400);
        }
    }

    public static void receiveMessage(TIMMessage timMessage) {

        if (!isInit) {
            syncSwitch();
        }

        TIMConversation timConversation = timMessage.getConversation();

        //振动
        if (timConversation.getType() == TIMConversationType.C2C
                && vibrateEnable) {
            onVibrate();
        } else if (timConversation.getType() == TIMConversationType.Group
                && vibrateGroupEnable) {
            if (IMHelper.isPublicGroup(timConversation.getPeer())) {
                onVibrate();
            }
        }

        //响铃
        if (timConversation.getType() == TIMConversationType.C2C
                && soundEnable) {
            onRing();
        } else if (timConversation.getType() == TIMConversationType.Group
                && soundGroupEnable) {
            if (IMHelper.isPublicGroup(timConversation.getPeer())) {
                onRing();
            }
        }

    }

    /**
     * 同步开关状态
     */
    public static void syncSwitch() {

        //消息提示音
        soundEnable = SharedPreferenceHelper.getTipSound(AppManager.getInstance());

        //群消息提示音
        soundGroupEnable = SharedPreferenceHelper.getTipSound2(AppManager.getInstance());

        //消息震动
        vibrateEnable = SharedPreferenceHelper.getTipVibrate(AppManager.getInstance());

        //群消息震动
        vibrateGroupEnable = SharedPreferenceHelper.getTipVibrate2(AppManager.getInstance());

        isInit = true;

    }

}