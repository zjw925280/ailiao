package com.lovechatapp.chat.socket;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

public class HeartBeatMessageFactory implements KeepAliveMessageFactory {

    // 心跳请求包,同时也是服务器的心跳反馈包
    private final String heartRequest = "0x11";

    // 心跳请求反馈包
    private final String heartResponse = "01010";

    /**
     * 是否服务器心跳请求包
     */
    @Override
    public boolean isRequest(IoSession ioSession, Object o) {
        return heartRequest.equals(o);
    }

    @Override
    public boolean isResponse(IoSession ioSession, Object o) {
        return heartRequest.equals(o);
    }

    /**
     * 发送心跳请求包
     */
    @Override
    public Object getRequest(IoSession ioSession) {
        return heartResponse;
    }

    /**
     * 发送心跳反馈包
     */
    @Override
    public Object getResponse(IoSession ioSession, Object o) {
        return heartResponse;
    }
}