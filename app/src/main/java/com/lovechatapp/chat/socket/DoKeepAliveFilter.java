package com.lovechatapp.chat.socket;


import com.lovechatapp.chat.util.LogUtil;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.DefaultWriteRequest;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;

public class DoKeepAliveFilter extends IoFilterAdapter {
    private final AttributeKey WAITING_FOR_RESPONSE;
    private final AttributeKey IGNORE_READER_IDLE_ONCE;
    private final KeepAliveMessageFactory messageFactory;
    private final IdleStatus interestedIdleStatus;
    private volatile KeepAliveRequestTimeoutHandler requestTimeoutHandler;
    private volatile int requestInterval;
    private volatile int requestTimeout;
    private volatile boolean forwardEvent;

    public DoKeepAliveFilter(KeepAliveMessageFactory messageFactory) {
        this(messageFactory, IdleStatus.READER_IDLE, KeepAliveRequestTimeoutHandler.CLOSE);
    }

    public DoKeepAliveFilter(KeepAliveMessageFactory messageFactory, IdleStatus interestedIdleStatus) {
        this(messageFactory, interestedIdleStatus, KeepAliveRequestTimeoutHandler.CLOSE, 60, 30);
    }

    public DoKeepAliveFilter(KeepAliveMessageFactory messageFactory, KeepAliveRequestTimeoutHandler policy) {
        this(messageFactory, IdleStatus.READER_IDLE, policy, 60, 30);
    }

    public DoKeepAliveFilter(KeepAliveMessageFactory messageFactory, IdleStatus interestedIdleStatus, KeepAliveRequestTimeoutHandler policy) {
        this(messageFactory, interestedIdleStatus, policy, 60, 30);
    }

    public DoKeepAliveFilter(KeepAliveMessageFactory messageFactory, IdleStatus interestedIdleStatus, KeepAliveRequestTimeoutHandler policy, int keepAliveRequestInterval, int keepAliveRequestTimeout) {
        this.WAITING_FOR_RESPONSE = new AttributeKey(this.getClass(), "waitingForResponse");
        this.IGNORE_READER_IDLE_ONCE = new AttributeKey(this.getClass(), "ignoreReaderIdleOnce");
        if (messageFactory == null) {
            throw new IllegalArgumentException("messageFactory");
        } else if (interestedIdleStatus == null) {
            throw new IllegalArgumentException("interestedIdleStatus");
        } else if (policy == null) {
            throw new IllegalArgumentException("policy");
        } else {
            this.messageFactory = messageFactory;
            this.interestedIdleStatus = interestedIdleStatus;
            this.requestTimeoutHandler = policy;
            this.setRequestInterval(keepAliveRequestInterval);
            this.setRequestTimeout(keepAliveRequestTimeout);
        }
    }

    public IdleStatus getInterestedIdleStatus() {
        return this.interestedIdleStatus;
    }

    public KeepAliveRequestTimeoutHandler getRequestTimeoutHandler() {
        return this.requestTimeoutHandler;
    }

    public void setRequestTimeoutHandler(KeepAliveRequestTimeoutHandler timeoutHandler) {
        if (timeoutHandler == null) {
            throw new IllegalArgumentException("timeoutHandler");
        } else {
            this.requestTimeoutHandler = timeoutHandler;
        }
    }

    public int getRequestInterval() {
        return this.requestInterval;
    }

    public void setRequestInterval(int keepAliveRequestInterval) {
        if (keepAliveRequestInterval <= 0) {
            throw new IllegalArgumentException("keepAliveRequestInterval must be a positive integer: " + keepAliveRequestInterval);
        } else {
            this.requestInterval = keepAliveRequestInterval;
        }
    }

    public int getRequestTimeout() {
        return this.requestTimeout;
    }

    public void setRequestTimeout(int keepAliveRequestTimeout) {
        if (keepAliveRequestTimeout <= 0) {
            throw new IllegalArgumentException("keepAliveRequestTimeout must be a positive integer: " + keepAliveRequestTimeout);
        } else {
            this.requestTimeout = keepAliveRequestTimeout;
        }
    }

    public KeepAliveMessageFactory getMessageFactory() {
        return this.messageFactory;
    }

    public boolean isForwardEvent() {
        return this.forwardEvent;
    }

    public void setForwardEvent(boolean forwardEvent) {
        this.forwardEvent = forwardEvent;
    }

    public void onPreAdd(IoFilterChain parent, String name, NextFilter nextFilter) throws Exception {
        if (parent.contains(this)) {
            throw new IllegalArgumentException("You can't add the same filter instance more than once. Create another instance and add it.");
        }
    }

    public void onPostAdd(IoFilterChain parent, String name, NextFilter nextFilter) throws Exception {
        this.resetStatus(parent.getSession());
    }

    public void onPostRemove(IoFilterChain parent, String name, NextFilter nextFilter) throws Exception {
        this.resetStatus(parent.getSession());
    }

    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
        try {
            LogUtil.i("-socket-", "messageReceived:" + message);
            if (this.messageFactory.isRequest(session, message)) {
                Object pongMessage = this.messageFactory.getResponse(session, message);
                if (pongMessage != null) {
                    nextFilter.filterWrite(session, new DefaultWriteRequest(pongMessage));
                }
            }
//            if (this.messageFactory.isResponse(session, message)) {
//                  this.resetStatus(session);
//            }
        } finally {
            if (!this.isKeepAliveMessage(session, message)) {
                nextFilter.messageReceived(session, message);
            }
        }

    }

    public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        Object message = writeRequest.getMessage();
        if (!this.isKeepAliveMessage(session, message)) {
            nextFilter.messageSent(session, writeRequest);
        }

    }

    public void sessionIdle(NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
        if (status == this.interestedIdleStatus) {
//            if (!session.containsAttribute(this.WAITING_FOR_RESPONSE)) {
//                Object pingMessage = this.messageFactory.getRequest(session);
//                if (pingMessage != null) {
//                    nextFilter.filterWrite(session, new DefaultWriteRequest(pingMessage));
//                    if (this.getRequestTimeoutHandler() != KeepAliveRequestTimeoutHandler.DEAF_SPEAKER) {
//                        this.markStatus(session);
//                        if (this.interestedIdleStatus == IdleStatus.BOTH_IDLE) {
//                            session.setAttribute(this.IGNORE_READER_IDLE_ONCE);
//                        }
//                    } else {
//                        this.resetStatus(session);
//                    }
//                }
//            } else {
//                  this.handlePingTimeout(session);
//            }
            session.closeNow();
        } else if (status == IdleStatus.READER_IDLE && session.removeAttribute(this.IGNORE_READER_IDLE_ONCE) == null && session.containsAttribute(this.WAITING_FOR_RESPONSE)) {
            this.handlePingTimeout(session);
        }

        if (this.forwardEvent) {
            nextFilter.sessionIdle(session, status);
        }

    }

    private void handlePingTimeout(IoSession session) throws Exception {
        this.resetStatus(session);
        KeepAliveRequestTimeoutHandler handler = this.getRequestTimeoutHandler();
        if (handler != KeepAliveRequestTimeoutHandler.DEAF_SPEAKER) {
            handler.keepAliveRequestTimedOut(null, session);
        }
    }

    private void markStatus(IoSession session) {
        session.getConfig().setIdleTime(this.interestedIdleStatus, 0);
        session.getConfig().setReaderIdleTime(this.getRequestTimeout());
        session.setAttribute(this.WAITING_FOR_RESPONSE);
    }

    private void resetStatus(IoSession session) {
        session.getConfig().setReaderIdleTime(0);
        session.getConfig().setWriterIdleTime(0);
        session.getConfig().setIdleTime(this.interestedIdleStatus, this.getRequestInterval());
        session.removeAttribute(this.WAITING_FOR_RESPONSE);
    }

    private boolean isKeepAliveMessage(IoSession session, Object message) {
        return this.messageFactory.isRequest(session, message) || this.messageFactory.isResponse(session, message);
    }
}