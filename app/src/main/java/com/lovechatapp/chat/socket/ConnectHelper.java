package com.lovechatapp.chat.socket;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.lovechatapp.chat.BuildConfig;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.util.LogUtil;

/**
 * 单例连接类
 */
public class ConnectHelper {

    private ConnectThread connectThread;

    private volatile ServiceHandler mServiceHandler;

    private OnCommonListener<Boolean> onLineListener;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private static String socketUrl = BuildConfig.socketIp;

    /**
     * handler的检查消息
     */
    private final int CHECK = 0;

    /**
     * handler的连接消息
     */
    private final int CONNECT = 1;

    /**
     * 周期检查socket状态的间隔时间(毫秒)
     */
    private final long INTERVAL = 10 * 1000L;

    private static ConnectHelper connectHelper;

    public static ConnectHelper get() {
        if (connectHelper == null) {
            connectHelper = new ConnectHelper();
        }
        return connectHelper;
    }

    private ConnectHelper() {
    }

    public final synchronized void start() {
        if (connectThread != null || AppManager.getInstance().getUserInfo().t_id == 0) {
            return;
        }
        synchronized (ConnectHelper.class) {
            if (connectThread != null || AppManager.getInstance().getUserInfo().t_id == 0) {
                return;
            }
            LogUtil.i("start mina");
            connectThread = new ConnectThread("mina");
            connectThread.start();
            mServiceHandler = new ServiceHandler(connectThread.getLooper());
            mServiceHandler.sendEmptyMessage(CONNECT);
        }
    }

    public final synchronized void onDestroy() {
        synchronized (ConnectHelper.class) {
            if (connectThread != null) {
                connectThread.disConnection();
                connectThread.quitSafely();
                connectThread = null;
            }
            if (mServiceHandler != null) {
                mServiceHandler.removeCallbacksAndMessages(null);
                mServiceHandler = null;
            }
        }
    }

    public void setUrl(String url) {
        Log.d("-socket-", "setUrl: ");
        socketUrl = url;
        if (connectThread == null || connectThread.mManager == null) {
            return;
        }
        connectThread.mManager.setSocketUrl(url);
    }

    /**
     * 检查连接状态
     * 未连接则启动连接
     */
    public final void checkConnect() {
        if (!(ConnectHelper.get().isRunning()
                && ConnectHelper.get().isConnected())
                && AppManager.getInstance().getUserInfo().t_id != 0) {
            ConnectHelper.get().resConnect();
            LogUtil.i("checkConnect unconnected");
        }
    }

    /**
     * socket已连接，但后台识别为未登录则发送登录消息
     */
    public final void checkLogin() {
        if (isConnected()) {
            connectThread.mManager.sendLoginMsg();
        }
    }

    /**
     * 判断socket连接状态
     */
    public final boolean isConnected() {
        return connectThread != null && connectThread.mManager != null && connectThread.mManager.isConnected();
    }

    /**
     * 线程是否运行
     */
    public final boolean isRunning() {
        return connectThread != null && connectThread.isAlive() && !connectThread.isInterrupted() && mServiceHandler != null;
    }

    /**
     * 重新连接
     */
    public final void resConnect() {
        if (isConnected())
            return;
        synchronized (ConnectHelper.class) {
            if (isConnected())
                return;
            LogUtil.i("resConnect");
            try {
                if (!isRunning()) {
                    onDestroy();
                    start();
                } else {
                    mServiceHandler.sendEmptyMessage(CONNECT);
                }
            } catch (Exception e) {
                e.printStackTrace();
                onDestroy();
                start();
            }
        }
    }

    public final void setOnLineListener(OnCommonListener<Boolean> onLineListener) {
        this.onLineListener = onLineListener;
        notifyOnlineState();
    }

    public final void notifyOnlineState() {
        if (onLineListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (onLineListener != null) {
                        onLineListener.execute(isConnected());
                    }
                }
            });
        }
    }

    /**
     * 负责调用connect manager类来完成与服务器的连接
     */
    class ConnectThread extends HandlerThread {

        ConnectManager mManager;

        ConnectThread(String name) {
            super(name);

            //创建连接的管理类
            mManager = new ConnectManager(socketUrl) {

                @Override
                public void onConnectChanged() {
                    notifyOnlineState();
                }

                @Override
                public void onSessionOpened() {
                    /**
                     * 连接打开，发送登录消息
                     */
                    if (mServiceHandler != null) {
                        mServiceHandler.removeCallbacksAndMessages(null);
                        mServiceHandler.sendEmptyMessage(CHECK);
                    }
                }
            };
        }

        ConnectManager getManager() {
            return mManager;
        }

        /**
         * 断开连接
         */
        void disConnection() {
            mManager.disConnect();
        }
    }

    private final class ServiceHandler extends Handler {

        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**
                 * 周期（时间为：INTERVAL毫秒）执行，检查是否连上，是否登录
                 */
                case CHECK: {

                    removeMessages(CHECK);

                    try {
                        ConnectManager manager = connectThread.getManager();
                        if (manager != null) {
                            boolean isConnect = manager.isSocketConnect();
                            if (!isConnect) {
                                sendEmptyMessage(CONNECT);
                            } else {
//                                manager.checkLogin();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendEmptyMessageDelayed(CHECK, INTERVAL);
                    break;
                }
                case CONNECT: {

                    removeCallbacksAndMessages(null);

                    boolean isConnectSocket = false;
                    try {
                        ConnectManager manager = connectThread.getManager();
                        isConnectSocket = manager.isSocketConnect();
                        if (!isConnectSocket) {
                            connectThread.getManager().connect();
                        }
                        isConnectSocket = manager.isSocketConnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!isConnectSocket) {
                        sendEmptyMessageDelayed(CONNECT, ConnectManager.ConnectTimeOut);
                    } else {
                        sendEmptyMessageDelayed(CHECK, INTERVAL);
                    }
                    break;
                }
            }
        }
    }
}