package com.lovechatapp.chat.activity;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.MessageBean;
import com.lovechatapp.chat.helper.IMHelper;
import com.lovechatapp.chat.im.ImNotifyManager;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.util.TimeUtil;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.conversation.ConversationManager;
import com.tencent.imsdk.ext.group.TIMGroupDetailInfoResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 群会话
 */
public class ImGroupActivity extends BaseActivity implements TIMMessageListener {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.content_rv)
    RecyclerView recyclerView;

    private AbsRecycleAdapter adapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_refresh_list);
    }

    @Override
    protected void onContentAdded() {

        setTitle("群消息");

        TIMManager.getInstance().addMessageListener(this);

        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> updateConversation());

        adapter = new AbsRecycleAdapter() {

            @Override
            public void convert(ViewHolder holder, Object t) {
                MessageBean bean = (MessageBean) t;
                Glide.with(mContext)
                        .load(bean.headImg)
                        .error(R.drawable.message_group)
                        .transform(new CircleCrop())
                        .into(holder.<ImageView>getView(R.id.header_iv));
                holder.<TextView>getView(R.id.title_tv).setText(bean.nickName);
                holder.<TextView>getView(R.id.time_tv).setText(TimeUtil.getTimeStr(bean.t_create_time));
                holder.<TextView>getView(R.id.content_tv).setText(bean.lastMessage);

                //未读消息
                TextView mRedCountTv = holder.getView(R.id.red_count_tv);
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

            @Override
            public int getItemViewType(int position) {
                return R.layout.item_message_recycler_layout;
            }

            @Override
            public void setViewHolder(ViewHolder viewHolder) {
                viewHolder.getView(R.id.aa).setOnClickListener(v -> {
                    MessageBean bean = (MessageBean) getData().get(viewHolder.getRealPosition());
                    IMHelper.toGroup(mContext, bean.nickName, bean.t_id);
                });
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        IMHelper.syncGroup(new OnCommonListener<Object>() {
            @Override
            public void execute(Object o) {
                if (isFinishing()) {
                    return;
                }
                updateConversation();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateConversation();
    }

    private void updateConversation() {
        List<TIMConversation> conversationList = ConversationManager.getInstance().getConversationList();
        List<MessageBean> messageBeans = new ArrayList<>();
        List<String> peerList = new ArrayList<>();
        Map<String, MessageBean> messageBeanMap = new HashMap<>();
        for (TIMConversation timConversation : conversationList) {
            if (timConversation.getType() != TIMConversationType.Group) {
                continue;
            }
            if (IMHelper.isPublicGroup(timConversation.getPeer())) {
                peerList.add(timConversation.getPeer());
                MessageBean messageBean = new MessageBean();
                messageBeans.add(messageBean);
                messageBeanMap.put(timConversation.getPeer(), messageBean);
                messageBean.t_id = timConversation.getPeer();
                messageBean.nickName = timConversation.getGroupName();
                messageBean.unReadCount = timConversation.getUnreadMessageNum();
                //最后一条消息
                TIMMessage lastMsg = timConversation.getLastMsg();
                if (lastMsg != null) {
                    messageBean.lastMessage = ImNotifyManager.getGroupContent(lastMsg);
                    messageBean.t_create_time = lastMsg.timestamp();
                }
            }
        }
        TIMGroupManager.getInstance().getGroupInfo(peerList,
                new TIMValueCallBack<List<TIMGroupDetailInfoResult>>() {

                    @Override
                    public void onError(int i, String s) {

                    }

                    @Override
                    public void onSuccess(List<TIMGroupDetailInfoResult> timGroupDetailInfoResults) {
                        if (isFinishing()) {
                            return;
                        }
                        for (TIMGroupDetailInfoResult infoResult : timGroupDetailInfoResults) {
                            MessageBean bean = messageBeanMap.get(infoResult.getGroupId());
                            if (bean != null) {
                                bean.nickName = infoResult.getGroupName();
                                bean.headImg = infoResult.getFaceUrl();
                            }
                        }
                        adapter.setDatas(messageBeans);
                    }

                });
        smartRefreshLayout.finishRefresh(500);
    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        boolean haveGroup = false;
        for (TIMMessage timMessage : list) {
            if (timMessage.getConversation().getType() == TIMConversationType.Group) {
                haveGroup = true;
                break;
            }
        }
        if (haveGroup) {
            handler.removeCallbacks(messageRun);
            handler.postDelayed(messageRun, 500);
        }
        return false;
    }

    /**
     * 新消息更新会话
     */
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable messageRun = new Runnable() {
        @Override
        public void run() {
            if (isFinishing()) {
                return;
            }
            updateConversation();
        }
    };

    @Override
    protected void onDestroy() {
        TIMManager.getInstance().removeMessageListener(this);
        super.onDestroy();
    }
}