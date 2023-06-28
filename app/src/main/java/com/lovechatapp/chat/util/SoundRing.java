package com.lovechatapp.chat.util;

import android.media.MediaPlayer;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;

/**
 * 呼叫/等待铃声
 */
public class SoundRing {

    private MediaPlayer mPlayer;

    private int audioId = R.raw.call_come;

    public SoundRing(int audioId) {
        this.audioId = audioId;
    }

    public SoundRing() {
    }

    /**
     * 播放音频
     */
    public final void start() {
        if (mPlayer != null && mPlayer.isPlaying())
            return;
        try {
            if (mPlayer == null) {
                mPlayer = MediaPlayer.create(AppManager.getInstance(), audioId);
                mPlayer.setLooping(true);
                mPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }
}