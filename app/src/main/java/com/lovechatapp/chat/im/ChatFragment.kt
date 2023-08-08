package com.lovechatapp.chat.im

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import com.google.gson.Gson
import com.lovechatapp.chat.activity.DateCreateActivity.Companion.startActivityForResult
import com.lovechatapp.chat.activity.PersonInfoActivity
import com.lovechatapp.chat.activity.ReportActivity
import com.lovechatapp.chat.activity.VipCenterActivity
import com.lovechatapp.chat.base.AppManager
import com.lovechatapp.chat.base.BaseResponse
import com.lovechatapp.chat.bean.*
import com.lovechatapp.chat.constant.ChatApi
import com.lovechatapp.chat.constant.Constant
import com.lovechatapp.chat.dialog.GiftDialog
import com.lovechatapp.chat.dialog.InputRemarkDialog
import com.lovechatapp.chat.dialog.ProtectDialog
import com.lovechatapp.chat.dialog.VipDialog
import com.lovechatapp.chat.helper.ChargeHelper
import com.lovechatapp.chat.helper.IMFilterHelper
import com.lovechatapp.chat.helper.ImageHelper
import com.lovechatapp.chat.helper.SharedPreferenceHelper
import com.lovechatapp.chat.net.*
import com.lovechatapp.chat.util.DensityUtil
import com.lovechatapp.chat.util.LogUtil
import com.lovechatapp.chat.util.ParamUtil
import com.lovechatapp.chat.util.ToastUtil
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import com.tencent.imsdk.*
import com.tencent.imsdk.friendship.TIMFriend
import com.tencent.qcloud.tim.uikit.base.BaseFragment
import com.tencent.qcloud.tim.uikit.base.ITitleBarLayout
import com.tencent.qcloud.tim.uikit.component.AudioPlayer
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout
import com.tencent.qcloud.tim.uikit.modules.chat.C2CChatManagerKit
import com.tencent.qcloud.tim.uikit.modules.chat.ChatLayout
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo
import com.tencent.qcloud.tim.uikit.modules.chat.interfaces.ISend
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.ImCustomMessage
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.MessageLayout
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfoUtil
import com.zhihu.matisse.Matisse
import com.zhy.http.okhttp.OkHttpUtils
import okhttp3.Call
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URL
import com.lovechatapp.chat.R
class ChatFragment : BaseFragment() {
    private lateinit var mBaseView: View
    private lateinit var mChatLayout: ChatLayout
    private lateinit var mChatInfo: ChatInfo
    private lateinit var mTitleBar: TitleBarLayout
    private lateinit var mGifSv: SVGAImageView
    private var isFollow = false
    private var topConversation: ArrayList<String> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBaseView = inflater.inflate(R.layout.chat_fragment, container, false)
        return mBaseView
    }

    override fun onStart() {
        super.onStart()
        initView()
    }
    @SuppressLint("SetTextI18n")
    private fun initView() {
        mGifSv = mBaseView.findViewById(R.id.gif_sv)

        //从布局文件中获取聊天面板组件
        mChatLayout = mBaseView.findViewById(R.id.chat_layout)

        //单聊组件的默认UI和交互初始化
        mChatLayout.initDefault()

        //查询是否有备注
        val timFriend = TIMFriendshipManager.getInstance().queryFriend(mChatInfo.id)
        if (timFriend != null && !TextUtils.isEmpty(timFriend.remark)) {
            mChatInfo.chatName = timFriend.remark
        }

        //需要聊天的基本信息
        mChatLayout.chatInfo = mChatInfo


        //设置文字过滤
        mChatLayout.inputLayout.setImFilter(IMFilterHelper.getInstance())

        //获取单聊面板的标题栏
        mTitleBar = mChatLayout.titleBar
        val paddingSize = DensityUtil.dip2px(requireActivity(), 6f)
        mTitleBar.rightIcon.setPadding(paddingSize, paddingSize, paddingSize, paddingSize)
        mTitleBar.setRightIcon(R.drawable.dian_black)

        //单聊面板标记栏返回按钮点击事件
        mTitleBar.setOnLeftClickListener { requireActivity().finish() }

        //更多
        mTitleBar.rightIcon.setOnClickListener { showPop() }

        //消息长按事件、头像点击事件
        mChatLayout.messageLayout.onItemClickListener =
            object : MessageLayout.OnItemClickListener {
                override fun onMessageLongClick(
                    view: View,
                    position: Int,
                    messageInfo: MessageInfo
                ) {
                    //因为adapter中第一条为加载条目，位置需减1
                    if (messageInfo.msgType != MessageInfo.MSG_TYPE_DATE) {
                        mChatLayout.messageLayout
                            .showItemPopMenu(position - 1, messageInfo, view)
                    }
                }

                override fun onUserIconClick(view: View, position: Int, messageInfo: MessageInfo) {
                    if (messageInfo.isSelf) {
                        return
                    }
                    if (mUserCenterBean == null) {
                        actorInfo
                        return
                    }
                    PersonInfoActivity.start(requireActivity(), actorId)
                }
            }
        mChatLayout.noticeLayout.alwaysShow(true)
        mChatLayout.noticeLayout.contentExtra.text =
            "任何以可线下约会见面为由要求打赏礼物或者添加微信、QQ等第三方工具发红包的均是骗子。"
        ConversationManagerKit.getInstance().loadConversation(null)

        //监听消息事件
        TIMManager.getInstance().addMessageListener(timMessageListener)
        topConversation.addAll(SharedPreferenceHelper.getTop(requireActivity()))
    }

    var timMessageListener = TIMMessageListener { list ->
        for (timMessage in list) {
            val conversation = timMessage.conversation
            if (conversation != null && conversation.type == TIMConversationType.C2C && mChatInfo.id == conversation.peer) {
                for (i in 0 until timMessage.elementCount) {
                    val elem = timMessage.getElement(i)
                    if (elem.type == TIMElemType.Custom) {
                        val customElem = elem as TIMCustomElem
                        val data = customElem.data
                        val json = String(data)
                        val bean = CustomMessageBean.parseBean(json)
                        if (bean != null) {
                            if (ImCustomMessage.Type_gift == bean.type) {
                                //礼物
                                LogUtil.i("接收到的礼物: " + bean.gift_name)
                                startGif(bean.gift_gif_url)
                            }
                        }
                    }
                }
            }
        }
        false
    }

    /**
     * PopWindow
     */
    private fun showPop() {
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(requireActivity()).inflate(R.layout.pop_chat_more, null)
        val mPopWindow = PopupWindow(v)
        mPopWindow.width = ViewGroup.LayoutParams.WRAP_CONTENT
        mPopWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT
        mPopWindow.isFocusable = true
        mPopWindow.isOutsideTouchable = true
        mPopWindow.showAsDropDown(mChatLayout.titleBar, 0, 0, Gravity.END)
        @SuppressLint("NonConstantResourceId") val clickListener =
            View.OnClickListener { v1: View ->
                when (v1.id) {
                    R.id.black_btn -> {
                        AlertDialog.Builder(requireActivity())
                            .setMessage(
                                String.format(
                                    getString(R.string.black_alert),
                                    mChatInfo.chatName
                                )
                            )
                            .setPositiveButton(R.string.confirm) { dialog: DialogInterface, _: Int ->
                                object : BlackRequester() {
                                    override fun onSuccess(
                                        response: BaseResponse<*>?,
                                        addToBlack: Boolean
                                    ) {
                                        ToastUtil.showToast(R.string.black_add_ok)
                                        dialog.dismiss()
                                    }
                                }.post(actorId, true)
                            }
                            .setNegativeButton(R.string.cancel) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                            .create().show()
                    }
                    R.id.top_btn -> {
                        val isTop = isTop
                        if (isTop) {
                            topConversation.remove(mChatInfo.id)
                        } else {
                            topConversation.add(mChatInfo.id)
                        }
                        SharedPreferenceHelper.setTop(requireActivity(), mChatInfo.id, isTop)
                    }
                    R.id.follow_btn -> {
                        val setFollow = !isFollow
                        object : FocusRequester() {
                            override fun onSuccess(response: BaseResponse<*>?, focus: Boolean) {
                                if (requireActivity().isFinishing) return
                                setFollow(setFollow)
                            }
                        }.focus(actorId, setFollow)
                    }
                    R.id.report_btn -> {
                        val intent = Intent(requireActivity(), ReportActivity::class.java)
                        intent.putExtra(Constant.ACTOR_ID, actorId)
                        startActivity(intent)
                    }
                    R.id.remark_btn -> {
                        object : InputRemarkDialog(requireActivity()) {
                            override fun remark(text: String) {
                                mTitleBar.setTitle(text, ITitleBarLayout.POSITION.MIDDLE)
                                val hashMap = HashMap<String, Any>()
                                hashMap[TIMFriend.TIM_FRIEND_PROFILE_TYPE_KEY_REMARK] = text
                                TIMFriendshipManager.getInstance()
                                    .modifyFriend(mChatInfo.id, hashMap, object : TIMCallBack {
                                        override fun onError(i: Int, s: String) {}
                                        override fun onSuccess() {}
                                    })
                            }
                        }.show()
                    }
                    R.id.person_btn -> {
                        PersonInfoActivity.start(requireActivity(), actorId)
                    }
                }
                mPopWindow.dismiss()
            }
        val vp = v.findViewById<ViewGroup>(R.id.pop_ll)
        for (i in 0 until vp.childCount) {
            vp.getChildAt(i).setOnClickListener(clickListener)
        }
        val followTv = v.findViewById<TextView>(R.id.follow_btn)
        followTv.text = if (isFollow) "取消关注" else "关注"
        val topTv = v.findViewById<TextView>(R.id.top_btn)
        topTv.text = if (isTop) "取消置顶" else "消息置顶"
    }

    private val isTop: Boolean
        get() = topConversation.contains(mChatInfo.id)

    /**
     * 开始GIF动画
     */
    private fun startGif(path: String) {
        if (!TextUtils.isEmpty(path)) {
            val parser = SVGAParser(requireActivity())
            try {
                val url = URL(path)
                parser.parse(url, object : SVGAParser.ParseCompletion {
                    override fun onComplete(videoItem: SVGAVideoEntity) {
                        if (requireActivity().isFinishing) return
                        val drawable = SVGADrawable(videoItem)
                        mGifSv.setImageDrawable(drawable)
                        mGifSv.startAnimation()
                    }

                    override fun onError() {}
                })
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mChatInfo = requireActivity().intent.getSerializableExtra(ImConstants.CHAT_INFO) as ChatInfo
        initView()

        // TODO 通过api设置ChatLayout各种属性的样例
        val helper = ChatLayoutHelper(requireActivity())
        helper.customizeChatLayout(mChatLayout)
        initBtn()
    }

    override fun onResume() {
        super.onResume()
        if (!AppManager.getInstance().userInfo.isVip) {
            mChatLayout.inputLayout.mAudioInputSwitchButton.setOnClickListener {
                VipDialog(
                    requireActivity(),
                    "VIP用户才可发送语音消息"
                ).show()
            }
        } else {
            mChatLayout.inputLayout.mAudioInputSwitchButton.setOnClickListener(mChatLayout.inputLayout)
        }
    }

    /**
     * 点击事件
     */
    private fun initBtn() {
        if (view == null) return
        actorInfo

        //守护
        val protectBtn = view!!.findViewById<View>(com.tencent.qcloud.tim.uikit.R.id.protect_btn)
        if (AppManager.getInstance().userInfo.isSexMan) {
            protectBtn.visibility = View.VISIBLE
            protectBtn.setOnClickListener {
                ProtectDialog(
                    requireActivity(),
                    actorId
                ).show()
            }
        } else {
            protectBtn.visibility = View.GONE
        }

        //vip
        val vipBtn = view!!.findViewById<View>(com.tencent.qcloud.tim.uikit.R.id.vip_btn)
        if (!AppManager.getInstance().userInfo.isVip) {
            vipBtn.visibility = View.VISIBLE
            vipBtn.setOnClickListener {
                VipCenterActivity.start(
                    requireActivity(),
                    false
                )
            }
        } else {
            vipBtn.visibility = View.GONE
        }

        //选择图片
        view!!.findViewById<View>(com.tencent.qcloud.tim.uikit.R.id.btn_picture)
            .setOnClickListener {
                if (!AppManager.getInstance().userInfo.isVip) {
                    VipDialog(requireActivity(), "VIP用户才能使用图片聊天功能").show()
                    return@setOnClickListener
                }
                ImageHelper.openPictureChoosePage(requireActivity(), Constant.REQUEST_CODE_CHOOSE)
            }
        val onClickListener = View.OnClickListener {
            if (mUserCenterBean != null) {
                //同性别不能交流
                if (mUserCenterBean!!.t_sex == getUserSex()) {
                    ToastUtil.showToast(requireActivity(), R.string.sex_can_not_communicate)
                    return@OnClickListener
                }
                //判断双方是不是都是用户
                if (mUserCenterBean!!.t_role == 0 && getUserRole() == 0) {
                    ToastUtil.showToast(requireActivity(), R.string.can_not_communicate)
                    return@OnClickListener
                }
                val audioVideoRequester = AudioVideoRequester(
                    requireActivity(),
                    mUserCenterBean!!.t_role == 1,
                    actorId
                )
                if (it.tag != null) {
                    val type = it.tag.toString()
                    if (ImCustomMessage.Call_Type_Video == type) {
                        audioVideoRequester.executeVideo()
                    } else {
                        audioVideoRequester.executeAudio()
                    }
                } else {
                    audioVideoRequester.execute()
                }
            } else {
                actorInfo
            }
        }

        //开启相机
        view!!.findViewById<View>(com.tencent.qcloud.tim.uikit.R.id.btn_camera)
            .setOnClickListener {
                if (!AppManager.getInstance().userInfo.isVip) {
                    VipDialog(requireActivity(), "VIP用户才能使用图片聊天功能").show()
                    return@setOnClickListener
                }
                mChatLayout.inputLayout.startCapture()
            }

        //发起音视频
        view!!.findViewById<View>(com.tencent.qcloud.tim.uikit.R.id.btn_video).tag =
            ImCustomMessage.Call_Type_Video
        view!!.findViewById<View>(com.tencent.qcloud.tim.uikit.R.id.btn_video)
            .setOnClickListener(onClickListener)
        view!!.findViewById<View>(com.tencent.qcloud.tim.uikit.R.id.btn_audio).tag =
            ImCustomMessage.Call_Type_Audio
        view!!.findViewById<View>(com.tencent.qcloud.tim.uikit.R.id.btn_audio)
            .setOnClickListener(onClickListener)

        //发送礼物
        view!!.findViewById<View>(com.tencent.qcloud.tim.uikit.R.id.btn_gift)
            .setOnClickListener { GiftDialog(requireActivity(), actorId).show() }

        //发起约会
        view!!.findViewById<View>(com.tencent.qcloud.tim.uikit.R.id.btn_date)
            .setOnClickListener {
                startActivityForResult(
                    requireActivity(),
                    Constant.REQUEST_CODE_CREATE_DATE,
                    actorId.toString(),
                    mChatInfo.chatName
                )
            }

        //拦截器
        mChatLayout.setCanSend(ISend { onSend ->
            if (mUserCenterBean == null) {
                actorInfo
                return@ISend
            }

            //同性别不能交流
            if (mUserCenterBean!!.t_sex == getUserSex()) {
                ToastUtil.showToast(requireActivity(), R.string.sex_can_not_communicate)
                return@ISend
            }
            //判断双方是不是都是用户
            if (mUserCenterBean!!.t_role == 0 && getUserRole() == 0) {
                ToastUtil.showToast(requireActivity(), R.string.can_not_communicate)
                return@ISend
            }
            val paramMap: MutableMap<String?, Any?> = HashMap()
            paramMap["userId"] = getUserId()
            paramMap["coverConsumeUserId"] = actorId
            OkHttpUtils.post().url(ChatApi.SEND_TEXT_CONSUME())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(object : AjaxCallback<BaseResponse<*>?>() {
                    override fun onResponse(response: BaseResponse<*>?, id: Int) {
                        response?.apply {
                            Log.e("不然怎么会这样","不然怎么会这样="+m_istatus)
                            when (m_istatus) {
                                NetCode.SUCCESS,
                                2 -> {
                                    //扣费成功或者是VIP用户
                                    onSend.canSend(true)
                                }
                                -1 -> {
                                    //余额不足
                                    ChargeHelper.showSetCoverDialog(requireActivity())
                                }
                                  3 -> {
                                    ToastUtil.showToast(requireActivity(), m_strMessage)
                                    onSend.canSend(true)
                                }
                                else -> {
                                    //其他错误直接提示
                                    ToastUtil.showToast(requireActivity(), m_strMessage)
                                }
                            }
                        }

                    }

                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                        ToastUtil.showToast(requireActivity(), R.string.system_error)
                    }
                })
        })
        setSubTitleBar()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        //图片选择回调
        if (requestCode == Constant.REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            val mSelectedUris = Matisse.obtainResult(data)
            if (mSelectedUris.size > 0) {
                val info = MessageInfoUtil.buildImageMessage(mSelectedUris[0], true)
                mChatLayout.sendMessage(info, false)
            }
        } else if (requestCode == Constant.REQUEST_CODE_CREATE_DATE && resultCode == Activity.RESULT_OK) {
            val jsonStr = data.getStringExtra("data") ?: ""
            if (jsonStr.isNotEmpty()) {
                Handler().postDelayed({
                    val json = JSONObject(jsonStr)
                    json.put("type", ImCustomMessage.Type_Date)
                    val imCustomMessage =
                        Gson().fromJson(json.toString(), ImCustomMessage::class.java)
                    val info = MessageInfo()
                    val timMsg = TIMMessage()
                    val ele = TIMCustomElem()
                    ele.data = json.toString().toByteArray()
                    timMsg.addElement(ele)
                    info.isSelf = true
                    info.timMessage = timMsg
                    info.msgTime = System.currentTimeMillis() / 1000
                    info.element = ele
                    info.extra = imCustomMessage
                    info.msgType = MessageInfo.MSG_TYPE_DATE
                    info.fromUser = TIMManager.getInstance().loginUser
                    C2CChatManagerKit.getInstance().sendMessage(info, false, null)
                }, 1000)
            }
        }
    }

    private var mUserCenterBean: AudioUserBean? = null

    /**
     * 获取对方信息
     * ++++++++++++++++接口更改++++++++++++++
     */
    private val actorInfo: Unit
        get() {
            val paramMap: MutableMap<String?, Any?> = HashMap()
            paramMap["userId"] = AppManager.getInstance().userInfo.t_id
            paramMap["coverUserId"] = actorId.toString()
            OkHttpUtils.post().url(ChatApi.getUserInfoById())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(object : AjaxCallback<BaseResponse<AudioUserBean?>?>() {
                    override fun onResponse(response: BaseResponse<AudioUserBean?>?, id: Int) {
                        if (response != null && response.m_istatus == NetCode.SUCCESS) {
                            val bean = response.m_object
                            if (bean != null) {
                                mUserCenterBean = bean
                            }
                        }
                    }
                })
        }
    //    /**
    //     * 非vip用户顶部收费提示
    //     */
    //    private void setSubTitleBar() {
    //        if (AppManager.getInstance().getUserInfo().t_is_vip != 0 && getUserRole() == 0) {
    //            Map<String, Object> paramMap = new HashMap<>();
    //            paramMap.put("userId", getUserId());
    //            paramMap.put("anchorId", getActorId());
    //            OkHttpUtils.post().url(ChatApi.GET_ACTOR_CHARGE_SETUP)
    //                    .addParams("param", ParamUtil.getParam(paramMap))
    //                    .build().execute(new AjaxCallback<BaseResponse<ChargeBean>>() {
    //                @Override
    //                public void onResponse(BaseResponse<ChargeBean> response, int id) {
    //                    if (requireActivity() == null) {
    //                        return;
    //                    }
    //                    if (response != null && response.m_istatus == NetCode.SUCCESS && response.m_object != null) {
    //                        ChargeBean bean = response.m_object;
    //                        int textGold = bean.t_text_gold;
    //                        if (textGold > 0) {
    //                            ViewGroup viewGroup = getView().findViewById(R.id.subtitle_bar);
    //                            View view = View.inflate(requireActivity(), R.layout.top_price_layout, viewGroup);
    //                            TextView textView = view.findViewById(R.id.first_tv);
    //                            textView.setText(String.format(getString(R.string.im_chat_price), textGold + getResources().getString(R.string.gold)));
    //                            getView().findViewById(R.id.vip_tv).setOnClickListener(new View.OnClickListener() {
    //                                @Override
    //                                public void onClick(View v) {
    //                                    Intent intent = new Intent(requireActivity(), VipCenterActivity.class);
    //                                    startActivity(intent);
    //                                }
    //                            });
    //                        }
    //                    }
    //                }
    //            });
    //        }
    //    }
    /**
     * 查看主播联系方式
     */
    private fun setSubTitleBar() {
        val paramMap: MutableMap<String?, Any?> = HashMap()
        paramMap["userId"] = getUserId()
        paramMap["coverUserId"] = actorId
        OkHttpUtils.post().url(ChatApi.GET_ACTOR_INFO())
            .addParams("param", ParamUtil.getParam(paramMap))
            .build().execute(object :
                AjaxCallback<BaseResponse<ActorInfoBean<CoverUrlBean?, LabelBean?, ChargeBean?, InfoRoomBean?>?>?>() {
                override fun onResponse(
                    response: BaseResponse<ActorInfoBean<CoverUrlBean?, LabelBean?, ChargeBean?, InfoRoomBean?>?>?,
                    id: Int
                ) {
                    if (requireActivity().isFinishing) {
                        return
                    }
                    if (response != null && response.m_istatus == NetCode.SUCCESS) {
                        if (response.m_object != null) {

                            //联系方式
//                        setContact(response.m_object);

                            //关注
                            setFollow(response.m_object!!.isFollow == 1)
                            mChatLayout.messageLayout.scrollToEnd()
                        }
                    }
                }
            })
    }

    /**
     * 设置关注icon
     */
    private fun setFollow(isFollow: Boolean) {
        this.isFollow = isFollow
        //        mChatLayout.getTitleBar().getRightIcon().setSelected(isFollow);
//        mChatLayout.getTitleBar().setRightIcon(isFollow ?
//                R.drawable.follow_selected : R.drawable.follow_unselected);
//        mChatLayout.getTitleBar().getRightIcon().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final boolean setFollow = !v.isSelected();
//                new FocusRequester() {
//                    @Override
//                    public void onSuccess(BaseResponse response, boolean focus) {
//                        if (requireActivity() == null || requireActivity().isFinishing())
//                            return;
//                        setFollow(setFollow);
//                    }
//                }.focus(getActorId(), setFollow);
//            }
//        });
    }

    //    /**
    //     * 查看联系方式
    //     */
    //    private void setContact(final ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> bean) {
    //        if (getView() == null)
    //            return;
    //
    //        ViewGroup viewGroup = getView().findViewById(R.id.subtitle_bar);
    //        viewGroup.removeAllViews();
    //
    //        View view = View.inflate(requireActivity(), R.layout.top_contact_layout, viewGroup);
    //
    //        View mWeixinTv = view.findViewById(R.id.weixin_tv);
    //        View mQqTv = view.findViewById(R.id.qq_tv);
    //        View mPhoneTv = view.findViewById(R.id.phone_tv);
    //
    //        mWeixinTv.setTag(0);
    //        mPhoneTv.setTag(1);
    //        mQqTv.setTag(2);
    //
    //        mWeixinTv.setOnClickListener(null);
    //        mPhoneTv.setOnClickListener(null);
    //        mQqTv.setOnClickListener(null);
    //
    //        View.OnClickListener lookClickListener = new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                new LookNumberDialog(requireActivity(), bean, (int) v.getTag(), getActorId()).show();
    //            }
    //        };
    //
    //        mWeixinTv.setAlpha(0.3f);
    //        mQqTv.setAlpha(0.3f);
    //        mPhoneTv.setAlpha(0.3f);
    //
    //        if (bean.anchorSetup != null && bean.anchorSetup.size() > 0) {
    //            ChargeBean chargeBean = bean.anchorSetup.get(0);
    //            if (chargeBean.t_weixin_gold != 0 && !TextUtils.isEmpty(bean.t_weixin)) {
    //                mWeixinTv.setAlpha(1f);
    //                mWeixinTv.setOnClickListener(lookClickListener);
    //            }
    //            if (chargeBean.t_qq_gold != 0 && !TextUtils.isEmpty(bean.t_qq)) {
    //                mQqTv.setAlpha(1f);
    //                mQqTv.setOnClickListener(lookClickListener);
    //            }
    //            if (chargeBean.t_phone_gold != 0 && !TextUtils.isEmpty(bean.t_phone)) {
    //                mPhoneTv.setAlpha(1f);
    //                mPhoneTv.setOnClickListener(lookClickListener);
    //            }
    //        }
    //    }
    val actorId: Int
        get() = (mChatInfo.id.toInt() - 10000)

    private fun getUserId(): Int {
        return AppManager.getInstance().userInfo.t_id
    }

    private fun getUserSex(): Int {
        return AppManager.getInstance().userInfo.t_sex
    }

    private fun getUserRole(): Int {
        return AppManager.getInstance().userInfo.t_role
    }

    override fun onPause() {
        super.onPause()
        AudioPlayer.getInstance().stopPlay()
    }

    override fun onDestroyView() {
        TIMManager.getInstance().removeMessageListener(timMessageListener)
        mGifSv.pauseAnimation()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mChatLayout.exitChat()
    }
}