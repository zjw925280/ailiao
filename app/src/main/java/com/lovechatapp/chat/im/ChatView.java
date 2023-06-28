package com.lovechatapp.chat.im;

/**
 * 聊天界面的接口
 */
public interface ChatView extends MvpView {

    /**
     * 发送文字消息
     */
    void sendText();

    /**
     * 正在发送
     */
    void sending();

    /**
     * 显示toast
     */
    void showToast(String msg);

    /**
     * 发红包
     */
    void sendRed();

    /**
     * 发送视频邀请
     */
    void sendVideo();
}
