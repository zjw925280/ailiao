package com.lovechatapp.chat.socket;

import android.content.Context;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：连接的配置文件
 * 作者：lyf
 * 创建时间：2017/5/20.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ConnectConfig {

    private Context mContext;
    private String ip;
    private int port;
    private int readBufferSize; //缓存大小
    private long connectionTimeout;//连接超时时间

    public Context getContext() {
        return mContext;
    }

    String getIp() {
        return ip;
    }

    int getPort() {
        return port;
    }

    int getReadBufferSize() {
        return readBufferSize;
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public static class Builder {
        private Context mContext;
        private String ip = "";
        private int port = 12580;//12580
        private int readBufferSize = 10240; //缓存大小
        private long connectionTimeout = 30 * 1000L;//连接超时时间


        Builder(Context mContext) {
            this.mContext = mContext;
        }

        Builder setConnectionTimeout(long connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder setContext(Context mContext) {
            this.mContext = mContext;
            return this;
        }

        Builder setPort(int port) {
            this.port = port;
            return this;
        }

        Builder setReadBufferSize(int readBufferSize) {
            this.readBufferSize = readBufferSize;
            return this;
        }

        ConnectConfig build() {
            ConnectConfig connectConfig = new ConnectConfig();
            connectConfig.connectionTimeout = this.connectionTimeout;
            connectConfig.ip = this.ip;
            connectConfig.port = this.port;
            connectConfig.mContext = this.mContext;
            connectConfig.readBufferSize = this.readBufferSize;
            return connectConfig;
        }
    }

}
