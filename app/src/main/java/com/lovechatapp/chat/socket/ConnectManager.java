package com.lovechatapp.chat.socket;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.bean.ChatUserInfo;
import com.lovechatapp.chat.socket.domain.Mid;
import com.lovechatapp.chat.socket.domain.SocketResponse;
import com.lovechatapp.chat.socket.domain.UserLoginReq;
import com.lovechatapp.chat.util.LogUtil;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 连接的管理类
 * Created by lyf on 2017/5/20.
 */

public class ConnectManager {

    String TAG = "-socket-";

    //连接失败后增加的重连时间（毫秒）
    private final long TimeOutUnit = 5000;

    //记录连接超时时间
    static long ConnectTimeOut = 0;

    //连接失败，最长延迟30分钟连接socket
    private final long MaxTimeOut = 30 * 60 * 1000;

    private Context mContext;
    private NioSocketConnector mConnection;
    private IoSession mSession;
    private InetSocketAddress mAddress;
    private String socketUrl;

    ConnectManager(String url) {
        socketUrl = url;
        this.mContext = AppManager.getInstance();
    }

    public void setSocketUrl(String url) {
        socketUrl = url;
        disConnect();
    }

    /**
     * 初始化连接对象
     * 每次连接时重新初始化
     */
    private void init() {

        //销毁连接对象
        if (mConnection != null) {
            mConnection.dispose();
            mConnection = null;
            mSession = null;
        }

        ConnectConfig mConfig = new ConnectConfig.Builder(AppManager.getInstance())
                .setIp(socketUrl)
                .setReadBufferSize(2048)
                .setConnectionTimeout(20 * 1000L)
                .build();

        mAddress = new InetSocketAddress(mConfig.getIp(), mConfig.getPort());
        //创建连接对象
        mConnection = new NioSocketConnector();
        //设置连接地址
        mConnection.setDefaultRemoteAddress(mAddress);
        mConnection.getSessionConfig().setReadBufferSize(mConfig.getReadBufferSize());
        //设置过滤
        mConnection.getFilterChain().addLast("logger", new LoggingFilter());
        mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(
                new ByteArrayCodecFactory(StandardCharsets.UTF_8)));//自定义解编码器
        //设置连接监听
        mConnection.setHandler(new DefaultHandler(mContext));
        //连接超时时间
        mConnection.setConnectTimeoutMillis(20 * 1000);

        //设置心跳
        DoKeepAliveFilter heartFilter = new DoKeepAliveFilter(new HeartBeatMessageFactory());
        heartFilter.setForwardEvent(true);
        mConnection.getFilterChain().addLast("heartbeat", heartFilter);
    }

    private class DefaultHandler extends IoHandlerAdapter {

        private Context context;

        DefaultHandler(Context context) {
            this.context = context;
        }

        /**
         * 连接成功时回调的方法
         */
        @Override
        public void sessionOpened(IoSession session) {
            LogUtil.i(TAG, "sessionOpened");
            mSession = session;
            sendLoginMsg();
            onSessionOpened();
        }

        @Override
        public void sessionClosed(IoSession session) {
            LogUtil.i(TAG, "sessionClosed");
            mSession = null;
            onConnectChanged();
        }

        /**
         * 接收到消息时回调的方法
         */
        @Override
        public void messageReceived(IoSession session, Object message) {
            if (context != null) {

                String content = message.toString().trim();

                if (!TextUtils.isEmpty(content)) {
                    try {

                        SocketResponse response = JSON.parseObject(content, SocketResponse.class);

                        //登录socket成功
                        if (response.mid == Mid.LOGIN_SUCCESS) {
                            LogUtil.i(TAG, "connect done");
                            ConnectTimeOut = 0;
                            onConnectChanged();
                            return;
                        }

                        //处理消息
                        SocketMessageManager.get().dispatchMessage(content);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 向服务器注册当前用户
     * 当与服务器连接成功时,将我们的session保存到我们的session manager类中,从而可以发送消息到服务器
     */
    void sendLoginMsg() {

        LogUtil.i(TAG, "sendLoginMsg");

        ChatUserInfo chatUserInfo = AppManager.getInstance().getUserInfo();
        if (mSession != null && chatUserInfo.t_id != 0) {
            UserLoginReq ui = new UserLoginReq();
            ui.setUserId(chatUserInfo.t_id);
            ui.setT_is_vip(chatUserInfo.t_is_vip);
            ui.setT_role(chatUserInfo.t_role);
            ui.setT_sex(chatUserInfo.t_sex);
            ui.setMid(30001);
            mSession.write(JSONObject.toJSONString(ui));
        }
    }

    /**
     * socket登录成功
     */
    public void onConnectChanged() {

    }

    public void onSessionOpened() {

    }

    /**
     * 连接服务器
     */
    void connect() {

        if (AppManager.getInstance().getUserInfo().t_id == 0) {
            LogUtil.i(TAG, "connect unavailable...");
            return;
        }

        LogUtil.i(TAG, "connect");
        onConnectChanged();
        try {
            init();
            ConnectFuture mConnectFuture = mConnection.connect();
            mConnectFuture.awaitUninterruptibly();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTimeOut();
    }

    /**
     * 非强制连接超时设置重连间隔时间
     */
    private void setTimeOut() {
        if (!isSocketConnect()) {
            ConnectTimeOut += TimeOutUnit;
            if (ConnectTimeOut >= MaxTimeOut) {
                ConnectTimeOut = MaxTimeOut;
            }
            LogUtil.i(TAG, "resetTimeOut: " + ConnectTimeOut);
        }
    }

    /**
     * 断开连接的方法
     */
    void disConnect() {
        if (mConnection != null) {
            mConnection.dispose();
            mConnection = null;
        }
        mSession = null;
        mAddress = null;
        mContext = null;
    }

    /**
     * socket是否连接
     */
    boolean isSocketConnect() {
        return mSession != null && mSession.isActive() && mSession.isConnected();
    }

    /**
     * socket是否连接并已登录
     */
    public final boolean isConnected() {
        return isSocketConnect();
    }

}