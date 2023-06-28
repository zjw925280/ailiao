package com.lovechatapp.chat.im;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.PersonInfoActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.dialog.VipDialog;
import com.lovechatapp.chat.helper.ImageHelper;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.friendship.TIMFriend;
import com.tencent.qcloud.tim.uikit.base.BaseFragment;
import com.tencent.qcloud.tim.uikit.component.AudioPlayer;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.ChatLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.MessageLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfoUtil;
import com.zhihu.matisse.Matisse;

import java.util.List;

public class ChatGroupFragment extends BaseFragment {

    private View mBaseView;
    private ChatLayout mChatLayout;
    private ChatInfo mChatInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.chat_fragment, container, false);
        return mBaseView;
    }

    private void initView() {

        //从布局文件中获取聊天面板组件
        mChatLayout = mBaseView.findViewById(R.id.chat_layout);

        //单聊组件的默认UI和交互初始化
        mChatLayout.initDefault();

        mChatLayout.getInputLayout().disableAudioInput(true);

        //查询是否有备注
        TIMFriend timFriend = TIMFriendshipManager.getInstance().queryFriend(mChatInfo.getId());
        if (timFriend != null && !TextUtils.isEmpty(timFriend.getRemark())) {
            mChatInfo.setChatName(timFriend.getRemark());
        }

        //需要聊天的基本信息
        mChatLayout.setChatInfo(mChatInfo);

        //获取单聊面板的标题栏
        TitleBarLayout mTitleBar = mChatLayout.getTitleBar();

        //单聊面板标记栏返回按钮点击事件
        mTitleBar.setOnLeftClickListener(view -> requireActivity().finish());

        //消息长按事件、头像点击事件
        mChatLayout.getMessageLayout().setOnItemClickListener(new MessageLayout.OnItemClickListener() {
            @Override
            public void onMessageLongClick(View view, int position, MessageInfo messageInfo) {
                //因为adapter中第一条为加载条目，位置需减1
                mChatLayout.getMessageLayout().showItemPopMenu(position - 1, messageInfo, view);
            }

            @Override
            public void onUserIconClick(View view, int position, MessageInfo messageInfo) {
                if (null == messageInfo || messageInfo.isSelf()) {
                    return;
                }
                try {
                    int id = Integer.parseInt(messageInfo.getFromUser()) - 10000;
                    PersonInfoActivity.start(requireActivity(), id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ConversationManagerKit.getInstance().loadConversation(null);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mChatInfo = (ChatInfo) requireActivity().getIntent().getSerializableExtra(ImConstants.CHAT_INFO);
        if (mChatInfo == null) {
            return;
        }
        initView();

        ChatLayoutHelper helper = new ChatLayoutHelper(requireActivity());
        helper.customizeChatLayout(mChatLayout);
        initBtn();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!AppManager.getInstance().getUserInfo().isVip()) {
            mChatLayout.getInputLayout().mAudioInputSwitchButton.setOnClickListener(v -> new VipDialog(requireActivity(), "VIP用户才可发送语音消息").show());
        } else {
            mChatLayout.getInputLayout().mAudioInputSwitchButton.setOnClickListener(mChatLayout.getInputLayout());
        }
    }

    /**
     * 点击事件
     */
    private void initBtn() {

        if (getView() == null)
            return;

        //守护
        View protectBtn = getView().findViewById(com.tencent.qcloud.tim.uikit.R.id.protect_btn);
        protectBtn.setVisibility(View.GONE);

        //vip
        View vipBtn = getView().findViewById(com.tencent.qcloud.tim.uikit.R.id.vip_btn);
        vipBtn.setVisibility(View.GONE);

        //选择图片
        getView().findViewById(com.tencent.qcloud.tim.uikit.R.id.btn_picture).setOnClickListener(v -> ImageHelper.openPictureChoosePage(requireActivity(), Constant.REQUEST_CODE_CHOOSE));

        //开启相机
        getView().findViewById(com.tencent.qcloud.tim.uikit.R.id.btn_camera).setOnClickListener(
                v -> mChatLayout.getInputLayout().startCapture());

        //发起音视频
        getView().findViewById(com.tencent.qcloud.tim.uikit.R.id.btn_video).setVisibility(View.GONE);
        getView().findViewById(com.tencent.qcloud.tim.uikit.R.id.btn_audio).setVisibility(View.GONE);
        //发送礼物
        getView().findViewById(com.tencent.qcloud.tim.uikit.R.id.btn_gift).setVisibility(View.GONE);
        //发起约会
        getView().findViewById(com.tencent.qcloud.tim.uikit.R.id.btn_date).setVisibility(View.GONE);

        //拦截器
        mChatLayout.setCanSend(onSend -> onSend.canSend(true));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //图片选择回调
        if (requestCode == Constant.REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            List<Uri> mSelectedUris = Matisse.obtainResult(data);
            if (mSelectedUris.size() > 0) {
                MessageInfo info = MessageInfoUtil.buildImageMessage(mSelectedUris.get(0), true);
                mChatLayout.sendMessage(info, false);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AudioPlayer.getInstance().stopPlay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatLayout != null) {
            mChatLayout.exitChat();
        }
    }
}