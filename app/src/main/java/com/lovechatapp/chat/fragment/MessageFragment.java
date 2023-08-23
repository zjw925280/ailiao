package com.lovechatapp.chat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.CallListActivity;
import com.lovechatapp.chat.activity.ImGroupActivity;
import com.lovechatapp.chat.activity.MainActivity;
import com.lovechatapp.chat.activity.SearchActivity;
import com.lovechatapp.chat.activity.SystemMessageActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.bean.MessageBean;
import com.lovechatapp.chat.bean.UnReadBean;
import com.lovechatapp.chat.bean.UnReadMessageBean;
import com.lovechatapp.chat.dialog.WarningDialog;
import com.lovechatapp.chat.helper.IMHelper;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.im.ImNotifyManager;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.util.CodeUtil;
import com.lovechatapp.chat.util.TimeUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.recycle.ListTypeAdapter;
import com.lovechatapp.chat.view.recycle.SwipeItemLayout;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.conversation.ConversationManager;
import com.tencent.imsdk.friendship.TIMFriend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：消息页面fragment
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class MessageFragment extends BaseFragment implements View.OnClickListener, TIMMessageListener, Runnable {

    //自动脚本给用户发消息公告弹窗
    private boolean isAlert;

    private List<String> topConversation;

    private Content content;

    private Header header;

    private TIMMessage timMessage;

    @Override
    protected int initLayout() {
        return R.layout.fragment_message_layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        topConversation = SharedPreferenceHelper.getTop(getActivity());
        getAllConversations();
        TIMManager.getInstance().addMessageListener(this);
        setNotification();
    }

    @Override
    public void onPause() {
        super.onPause();
        TIMManager.getInstance().removeMessageListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView clear_tv = findViewById(R.id.clear_tv);
        clear_tv.setOnClickListener(this);
        RecyclerView mContentRv = findViewById(R.id.content_rv);
        final SmartRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getAllConversations();
                refreshLayout.finishRefresh(700);
                IMHelper.checkLogin();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mContentRv.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getActivity()));
        mContentRv.setLayoutManager(linearLayoutManager);

        ListTypeAdapter adapter = new ListTypeAdapter(
                header = new Header(mContext),
                content = new Content(mContext));

        mContentRv.setAdapter(adapter);

        topConversation = SharedPreferenceHelper.getTop(getActivity());
        isAlert = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean("AlertAutoMsg", false);

        getAllConversations();
    }

    //---------------------------------文字消息--------------------------------

    /**
     * 获取所有会话消息
     */
    public void getAllConversations() {

        //消息列表
        List<MessageBean> messageBeans = new ArrayList<>();

        //用于查询好友资料
        List<String> peers = new ArrayList<>();

        SparseArray<Object> sparseArray = new SparseArray<>();

        List<TIMConversation> conversationList = ConversationManager.getInstance().getConversationList();

        List<MessageBean> topBeans = new ArrayList<>();

        timMessage = null;

        if (conversationList != null && conversationList.size() > 0) {

            for (TIMConversation timConversation : conversationList) {

                if (TextUtils.isEmpty(timConversation.getPeer())) {
                    continue;
                }

                //过滤群消息
                if (!TextUtils.isDigitsOnly(timConversation.getPeer())
                        || timConversation.getType() != TIMConversationType.C2C) {

                    //处理组会话
                    if (timConversation.getType() == TIMConversationType.Group
                            && IMHelper.isPublicGroup(timConversation.getPeer())) {
                        TIMMessage lastMsg = timConversation.getLastMsg();
                        if (lastMsg != null) {
                            if (timMessage == null) {
                                timMessage = lastMsg;
                            }
                            if (timMessage.timestamp() < lastMsg.timestamp()) {
                                timMessage = lastMsg;
                            }
                        }
                    }

                    if (timConversation.getType() == TIMConversationType.Group) {
                        if (timConversation.getLastMsg() != null) {
                            header.lastGroupMessage(timConversation.getLastMsg());
                        }
                    }

                    continue;
                }

                //过滤单聊同性
                if (IMHelper.filterC2CSex(timConversation)) {
                    continue;
                }

                //创建bean
                MessageBean recordBean = new MessageBean();

                recordBean.unReadCount = timConversation.getUnreadMessageNum();

                //最后一条消息
                TIMMessage lastMsg = timConversation.getLastMsg();
                if (lastMsg != null) {
                    recordBean.lastMessage = ImNotifyManager.getMessageContent(lastMsg);
                    recordBean.t_create_time = lastMsg.timestamp();
                }

                //对方peer
                String peer = timConversation.getPeer();
                int peerId = Integer.parseInt(peer);
                if (sparseArray.get(peerId, "") != null) {
                    recordBean.t_id = peer;
                    peers.add(peer);
                    recordBean.isTop = topConversation.contains(peer);
                    if (recordBean.isTop) {
                        topBeans.add(recordBean);
                    } else {
                        messageBeans.add(recordBean);
                    }
                    sparseArray.put(peerId, null);
                }
            }
            messageBeans.addAll(0, topBeans);
            if (messageBeans.size() > 0) {
                getOtherUserInfo(peers, messageBeans);
            } else {
                content.setData(messageBeans);
            }
        } else {
            content.setData(messageBeans);
        }
        header.lastGroupMessage(timMessage);
    }

    /**
     * 获取聊天人的信息
     */
    private void getOtherUserInfo(List<String> peers, final List<MessageBean> recordBeans) {

        Map<String, MessageBean> map = new HashMap<>();
        for (MessageBean mb : recordBeans) {
            map.put(mb.t_id, mb);
        }

        TIMFriendshipManager.getInstance().getUsersProfile(peers, true, new TIMValueCallBack<List<TIMUserProfile>>() {

            @Override
            public void onError(int i, String s) {
                if (!isAdded()) {
                    return;
                }
                content.setData(recordBeans);
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                if (!isAdded() || timUserProfiles == null || timUserProfiles.size() == 0) {
                    return;
                }
                for (TIMUserProfile timUserProfile : timUserProfiles) {
                    MessageBean recordBean = map.get(timUserProfile.getIdentifier());
                    if (recordBean != null) {
                        TIMFriend timFriend = TIMFriendshipManager.getInstance().queryFriend(timUserProfile.getIdentifier());
                        if (timFriend != null && !TextUtils.isEmpty(timFriend.getRemark())) {
                            recordBean.nickName = timFriend.getRemark();
                        } else {
                            recordBean.nickName = timUserProfile.getNickName();
                        }
                        recordBean.headImg = timUserProfile.getFaceUrl();
                    }
                }
                //刷新数据
                content.setData(recordBeans);
            }
        });
    }

    @Override
    public void onClick(View v) {
        //清空
        if (v.getId() == R.id.clear_tv) {
            clearAllMessage();
        }
    }

    /**
     * 清空
     */
    private void clearAllMessage() {
        try {
            //清除
            List<TIMConversation> conversationList = TIMManager.getInstance().getConversationList();
            if (conversationList != null && conversationList.size() > 0) {
                for (TIMConversation timConversation : conversationList) {
                    TIMManager.getInstance().deleteConversation(
                            TIMConversationType.C2C,
                            timConversation.getPeer());
                }
            }
            //清除红点
            ((MainActivity) mContext).resetRedPot();
            delayGetConversation();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.INSTANCE.showToast(getContext(), R.string.system_error);
        }
    }

    /**
     * 延迟获取
     */
    private void delayGetConversation() {
        if (getActivity() != null) {
            mContext.getWindow().getDecorView().removeCallbacks(this);
            mContext.getWindow().getDecorView().postDelayed(this, 500);
        }
    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        delayGetConversation();
        return false;
    }

    @Override
    public void run() {
        getAllConversations();
    }

    private boolean isIgnoreNotification;

    /**
     * 设置通知开关提示
     */
    private void setNotification() {
        if (isIgnoreNotification)
            return;
        ImNotifyManager.get().checkSwitch(getActivity(), new OnCommonListener<String>() {
            @Override
            public void execute(String s) {
                TextView textView = getView().findViewById(R.id.title_tv);
                getView().findViewById(R.id.ignore_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isIgnoreNotification = true;
                        findViewById(R.id.notify_ll).setVisibility(View.GONE);
                    }
                });
                getView().findViewById(R.id.open_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImNotifyManager.get().toNotifyManager();
                    }
                });
                textView.setText(s);
                findViewById(R.id.notify_ll).setVisibility(TextUtils.isEmpty(s) || isIgnoreNotification ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    protected void showChanged(boolean b) {
        if (b && isAlert) {
            try {
                new WarningDialog(getActivity()).show();
                isAlert = false;
                PreferenceManager
                        .getDefaultSharedPreferences(getActivity())
                        .edit().putBoolean("AlertAutoMsg", isAlert).apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class Content extends ListTypeAdapter.BindViewHolder {

        private final Activity context;

        public Content(Activity context) {
            super(R.layout.item_message_recycler_layout, true);
            this.context = context;
        }

        @Override
        protected void initViewHolder(ViewHolder viewHolder) {

            viewHolder.getView(R.id.delete).setOnClickListener(v -> {

                MessageBean bean = (MessageBean) viewHolder.getObject();
                ConversationManager.getInstance().deleteConversation(TIMConversationType.C2C, bean.t_id);
                data.remove(bean);
                getCommonAdapter().notifyDataSetChanged();

                //刷新count
                if (context.getClass() == MainActivity.class) {
                    ((MainActivity) context).dealUnReadCount();
                }
            });

            viewHolder.getView(R.id.content_ll).setOnClickListener(v -> {
                MessageBean bean = (MessageBean) viewHolder.getObject();
                String t_id = bean.t_id;
                if (!TextUtils.isEmpty(t_id) && TextUtils.isDigitsOnly(t_id)) {
                    int realId = Integer.parseInt(t_id) - 10000;
                    if (realId > 0) {
                        String nick = bean.nickName;
                        if (TextUtils.isEmpty(nick) || nick.contains("null")) {
                            nick = String.valueOf(bean.t_id);
                        }
                        IMHelper.toChat(context, nick, realId, -1);
                        //FloatingMessageManager.removeMessage(bean.t_id);
                    }
                }
            });

        }

        @Override
        protected void bindViewHolder(ViewHolder viewHolder, Object obj) {

            MessageBean bean = (MessageBean) obj;

            //最后消息
            viewHolder.<TextView>getView(R.id.content_tv).setText(bean.lastMessage);

            //头像
            Glide.with(context)
                    .load(bean.headImg)
                    .error(R.drawable.default_head_img)
                    .transform(new CircleCrop())
                    .into(viewHolder.<ImageView>getView(R.id.header_iv));

            //昵称
            String nickName = bean.nickName;
            if (!TextUtils.isEmpty(nickName) && !nickName.contains("null")) {
                viewHolder.<TextView>getView(R.id.title_tv).setText(nickName);
            } else {
                viewHolder.<TextView>getView(R.id.title_tv).setText(String.valueOf(bean.t_id));
            }

            viewHolder.itemView.setBackgroundColor(bean.isTop ? 0xfff0f0f0 : 0x00000000);

            //时间
            viewHolder.<TextView>getView(R.id.time_tv).setText(TimeUtil.getTimeStr(bean.t_create_time));

            //未读消息
            TextView mRedCountTv = viewHolder.getView(R.id.red_count_tv);
            if (bean.unReadCount > 0) {
                if (bean.unReadCount <= 99) {
                    mRedCountTv.setText(String.valueOf(bean.unReadCount));
                    mRedCountTv.setBackgroundResource(R.drawable.shape_unread_count_big_text_back);
                } else {
                    mRedCountTv.setText(R.string.nine_nine);
                    mRedCountTv.setBackgroundResource(R.drawable.shape_unread_count_nine_nine_text_back);
                }
                mRedCountTv.setVisibility(View.VISIBLE);
            } else {
                mRedCountTv.setVisibility(View.GONE);
            }

        }

    }

    static class Header extends ListTypeAdapter.BindViewHolder {

        Activity mContext;
        TextView mSysTv;
        TextView mSysCountTv;
        TextView mGroupCountTv;
        TextView mGroupTv;
        String myPeer;
        TIMMessage timMessage;

        public Header(Activity context) {
            super(R.layout.item_system_messgae_layout);
            this.mContext = context;
            myPeer = String.valueOf(AppManager.getInstance().getUserInfo().t_id + 10000);
        }

        @Override
        protected void initViewHolder(ViewHolder viewHolder) {

            mSysTv = viewHolder.getView(R.id.sys_tv);
            mSysCountTv = viewHolder.getView(R.id.sys_count_tv);
            mGroupTv = viewHolder.getView(R.id.group_tv);
            mGroupCountTv = viewHolder.getView(R.id.group_count_tv);

            //注册未读监听
            MainActivity activity = (MainActivity) mContext;
            activity.setUnReadBeanListener(this::bindData);

            //搜索
            viewHolder.getView(R.id.search_btn).setOnClickListener(v -> {
                Intent intent = new Intent(mContext, SearchActivity.class);
                mContext.startActivity(intent);
            });

            //系统消息
            viewHolder.getView(R.id.sys_btn).setOnClickListener(v -> {
                Intent intent = new Intent(mContext, SystemMessageActivity.class);
                mContext.startActivity(intent);
            });

            //群消息
            viewHolder.getView(R.id.group_btn).setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ImGroupActivity.class);
                mContext.startActivity(intent);
            });

            //我的通话
            viewHolder.getView(R.id.call_btn).setOnClickListener(v -> {
                Intent intent = new Intent(mContext, CallListActivity.class);
                mContext.startActivity(intent);
            });

            //跳转客服
            viewHolder.getView(R.id.serve_btn).setOnClickListener(v -> CodeUtil.jumpToQQ(mContext));

            lastGroupMessage(timMessage);
        }

        private void lastGroupMessage(TIMMessage timMessage) {
            this.timMessage = timMessage;
            if (mGroupTv != null) {
                if (timMessage != null) {
                    mGroupTv.setText(String.format("%s:%s",
                            timMessage.getConversation().getGroupName(),
                            ImNotifyManager.getGroupContent(timMessage)));
                } else {
                    mGroupTv.setText("最新消息");
                }
            }

        }

        private void bindData(UnReadBean<UnReadMessageBean> mSystemBean) {

            mGroupCountTv.setVisibility(View.GONE);

            //系统消息
            mSysCountTv.setVisibility(View.GONE);
            if (mSystemBean != null) {

                if (mSystemBean.groupCount > 0) {
                    mGroupCountTv.setText(mSystemBean.groupCount <= 99 ? String.valueOf(mSystemBean.groupCount) : "99+");
                    mGroupCountTv.setVisibility(View.VISIBLE);
                }

                if (mSystemBean.totalCount > 0) {
                    int count = mSystemBean.totalCount;
                    mSysCountTv.setText(count <= 99 ? String.valueOf(count) : "99+");
                    mSysCountTv.setBackgroundResource(count <= 99 ?
                            R.drawable.shape_unread_count_big_text_back :
                            R.drawable.shape_unread_count_nine_nine_text_back);
                    mSysCountTv.setVisibility(View.VISIBLE);
                }
                UnReadMessageBean messageBean = mSystemBean.data;
                if (messageBean != null && !TextUtils.isEmpty(messageBean.t_message_content)) {
                    mSysTv.setText(messageBean.t_message_content);
                } else {
                    mSysTv.setText(R.string.click_to_see);
                }
            } else {
                mSysTv.setText(R.string.click_to_see);
            }

        }

    }

}