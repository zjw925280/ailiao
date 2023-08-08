package com.lovechatapp.chat.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.lovechatapp.chat.R
import com.lovechatapp.chat.activity.CloseRankActivity
import com.lovechatapp.chat.activity.GiftPackActivity
import com.lovechatapp.chat.adapter.CloseGiftRecyclerAdapter
import com.lovechatapp.chat.adapter.InfoCommentRecyclerAdapter
import com.lovechatapp.chat.base.AppManager
import com.lovechatapp.chat.base.BaseListResponse
import com.lovechatapp.chat.base.BaseResponse
import com.lovechatapp.chat.base.LazyFragment
import com.lovechatapp.chat.bean.*
import com.lovechatapp.chat.constant.ChatApi
import com.lovechatapp.chat.constant.Constant
import com.lovechatapp.chat.databinding.DialogSeeWeChatNumberLayoutBinding
import com.lovechatapp.chat.databinding.FragmentPersonInfoOneLayoutBinding
import com.lovechatapp.chat.databinding.ItemTagUserInfoLayoutBinding
import com.lovechatapp.chat.helper.ChargeHelper
import com.lovechatapp.chat.helper.SharedPreferenceHelper
import com.lovechatapp.chat.net.AjaxCallback
import com.lovechatapp.chat.net.NetCode
import com.lovechatapp.chat.util.FileUtil
import com.lovechatapp.chat.util.ParamUtil
import com.lovechatapp.chat.util.ToastUtil
import com.zhy.http.okhttp.OkHttpUtils
import okhttp3.Call
import java.util.*

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播资料页下方资料Fragment One
 * 作者：
 * 创建时间：2018/6/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
@Suppress("UNCHECKED_CAST")
class PersonInfoOneFragment : LazyFragment(), View.OnClickListener {
    //ID
    private var mActorId = 0
    private lateinit var mActorInfoBean: ActorInfoBean<CoverUrlBean?, LabelBean, ChargeBean, InfoRoomBean?>
    private lateinit var mBinding: FragmentPersonInfoOneLayoutBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            FragmentPersonInfoOneLayoutBinding.inflate(LayoutInflater.from(requireActivity()))
        initView()
        return mBinding.root
    }

    /**
     * 初始化
     */
    private fun initView() {
        mBinding.commentRv.isNestedScrollingEnabled = false
        mBinding.closeRl.setOnClickListener(this)
        mBinding.giftRl.setOnClickListener(this)
        mBinding.seeWechatTv.setOnClickListener(this)
        mBinding.seePhoneTv.setOnClickListener(this)
        mIsViewPrepared = true
    }

    override fun onFirstVisibleToUser() {
        arguments?.apply {
            mActorId = getInt(Constant.ACTOR_ID)
            getIntimateAndGift(mActorId)
            getUserComment(mActorId)
            getSerializable("infoData")?.apply {
                mActorInfoBean =
                    this as ActorInfoBean<CoverUrlBean?, LabelBean, ChargeBean, InfoRoomBean?>
                loadData()
            }
        }
        mIsDataLoadCompleted = true
    }

    /**
     * 加载数据
     */
    fun loadData() {
        //城市
        mBinding.cityTv.text = mActorInfoBean.t_city
        //接听率
        mBinding.rateTv.text = mActorInfoBean.t_reception
        //体重
        val weight = mActorInfoBean.t_weight.toString() + getString(R.string.body_des)
        mBinding.weightTv.text = weight
        //身高
        val high = mActorInfoBean.t_height.toString() + getString(R.string.high_des)
        mBinding.highTv.text = high
        //ID
        val id = getString(R.string.chat_number_one) + mActorInfoBean.t_idcard
        mBinding.idCardTv.text = id
        //最后登录
        val lastTime = getString(R.string.last_time_des) + mActorInfoBean.t_login_time
        mBinding.lastTimeTv.text = lastTime
        if (TextUtils.isEmpty(mActorInfoBean.t_weixin) || mActorInfoBean.t_role < 1) {
            mBinding.weChatRl.visibility = View.GONE
        } else {
            mBinding.weChatRl.visibility = View.VISIBLE
            mBinding.weChatTv.text =
                getString(R.string.we_chat_num_des_one, mActorInfoBean.t_weixin)
            mBinding.seeWechatTv.visibility =
                if (mActorInfoBean.isWeixin == 1) View.GONE else View.VISIBLE
        }
        if (TextUtils.isEmpty(mActorInfoBean.t_phone) || mActorInfoBean.t_role < 1) {
            mBinding.phoneRl.visibility = View.GONE
        } else {
            mBinding.phoneRl.visibility = View.VISIBLE
            mBinding.phoneTv.text = getString(R.string.phone_num_one, mActorInfoBean.t_phone)
            mBinding.seePhoneTv.visibility =
                if (mActorInfoBean.isPhone == 1) View.GONE else View.VISIBLE
        }
        //自评形象
        val tagBeans = mActorInfoBean.lable
        if (tagBeans != null && tagBeans.size > 0) {
            setLabelView(tagBeans)
        }
    }

    /**
     * 获取亲密榜礼物柜
     */
    private fun getIntimateAndGift(actorId: Int) {
        val paramMap: MutableMap<String?, String?> = HashMap()
        paramMap["userId"] = actorId.toString()
        OkHttpUtils.post().url(ChatApi.GET_INITMATE_AND_GIFT())
            .addParams("param", ParamUtil.getParam(paramMap))
            .build()
            .execute(object : AjaxCallback<BaseResponse<IntimateBean<IntimateDetailBean>?>?>() {
                override fun onResponse(
                    response: BaseResponse<IntimateBean<IntimateDetailBean>?>?,
                    id: Int
                ) {
                    if (requireActivity().isFinishing) {
                        return
                    }
                    response?.apply {
                        if (m_istatus == NetCode.SUCCESS) {
                            val bean = m_object
                            bean?.apply {
                                val intimates = intimates
                                val gifts = gifts
                                //亲密榜
                                if (intimates != null && intimates.size > 0) {
                                    mBinding.closeTv.visibility = View.VISIBLE
                                    mBinding.closeRl.visibility = View.VISIBLE
                                    val intimate = 0 //亲密
                                    setRecyclerView(mBinding.closeRv, intimates, intimate)
                                    if (intimates.size >= 6) {
                                        mBinding.closeTv.visibility = View.VISIBLE
                                    }
                                } else {
                                    mBinding.closeTv.visibility = View.GONE
                                    mBinding.closeRl.visibility = View.GONE
                                }
                                //礼物柜
                                if (gifts != null && gifts.size > 0) {
                                    mBinding.giftTv.visibility = View.VISIBLE
                                    mBinding.giftRl.visibility = View.VISIBLE
                                    val gift = 1 //礼物
                                    setRecyclerView(mBinding.giftRv, gifts, gift)
                                    if (gifts.size >= 6) {
                                        mBinding.giftIv.visibility = View.VISIBLE
                                    }
                                } else {
                                    mBinding.giftTv.visibility = View.GONE
                                    mBinding.giftRl.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            })
    }

    /**
     * 设置RecyclerView
     */
    private fun setRecyclerView(
        recyclerView: RecyclerView,
        beans: List<IntimateDetailBean>,
        type: Int
    ) {
        val manager = GridLayoutManager(requireActivity(), 6)
        recyclerView.layoutManager = manager
        val adapter = CloseGiftRecyclerAdapter(requireActivity(), type)
        recyclerView.adapter = adapter
        adapter.loadData(beans)
        adapter.setOnItemClickListener { type1: Int ->
            if (type1 == 0) { //亲密
                if (mBinding.closeIv.visibility == View.VISIBLE) {
                    if (mActorId > 0) {
                        val intent = Intent(requireActivity(), CloseRankActivity::class.java)
                        intent.putExtra(Constant.ACTOR_ID, mActorId)
                        startActivity(intent)
                    }
                }
            } else {
                if (mBinding.giftIv.visibility == View.VISIBLE) {
                    if (mActorId > 0) {
                        val intent = Intent(requireActivity(), GiftPackActivity::class.java)
                        intent.putExtra(Constant.ACTOR_ID, mActorId)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    /**
     * 获取用户评价
     */
    private fun getUserComment(actorId: Int) {
        val paramMap: MutableMap<String?, String?> = HashMap()
        paramMap["userId"] = actorId.toString()
        OkHttpUtils.post().url(ChatApi.GET_EVALUATION_LIST())
            .addParams("param", ParamUtil.getParam(paramMap))
            .build().execute(object : AjaxCallback<BaseListResponse<CommentBean>?>() {
                override fun onResponse(response: BaseListResponse<CommentBean>?, id: Int) {
                    response?.apply {
                        if (m_istatus == NetCode.SUCCESS) {
                            val commentBeans = m_object
                            if (commentBeans != null && commentBeans.size > 0) {
                                val loadBeans: List<CommentBean> = if (commentBeans.size >= 10) {
                                    commentBeans.subList(0, 10)
                                } else {
                                    commentBeans
                                }
                                mBinding.noCommentTv.visibility = View.GONE
                                mBinding.commentRv.visibility = View.VISIBLE
                                val manager = LinearLayoutManager(requireActivity())
                                mBinding.commentRv.layoutManager = manager
                                val adapter = InfoCommentRecyclerAdapter(requireActivity())
                                mBinding.commentRv.adapter = adapter
                                adapter.loadData(loadBeans)
                            } else {
                                mBinding.noCommentTv.visibility = View.VISIBLE
                                mBinding.commentRv.visibility = View.INVISIBLE
                            }
                        }
                    } ?: apply {
                        mBinding.noCommentTv.visibility = View.VISIBLE
                        mBinding.commentRv.visibility = View.INVISIBLE
                    }

                }

                override fun onError(call: Call, e: Exception, id: Int) {
                    super.onError(call, e, id)
                    mBinding.noCommentTv.visibility = View.VISIBLE
                    mBinding.commentRv.visibility = View.INVISIBLE
                }
            })
    }

    /**
     * 设置标签View
     */
    private fun setLabelView(labelBeans: List<LabelBean>?) {
        //形象标签
        mBinding.selfTagLl.removeAllViews()
        val backs = intArrayOf(
            R.drawable.shape_tag_one,
            R.drawable.shape_tag_two,
            R.drawable.shape_tag_three
        )
        if (labelBeans != null && labelBeans.isNotEmpty()) {
            for (i in labelBeans.indices) {
                val tagBinding = ItemTagUserInfoLayoutBinding.inflate(LayoutInflater.from(context))
                tagBinding.contentTv.text = labelBeans[i].t_label_name
                val random = Random()
                val index = random.nextInt(backs.size)
                tagBinding.contentTv.setBackgroundResource(backs[index])
                if (i != 0) {
                    val params = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.leftMargin = 20
                    tagBinding.contentTv.layoutParams = params
                }
                mBinding.selfTagLl.addView(tagBinding.contentTv)
            }
            if (mBinding.selfTagLl.childCount > 0) {
                mBinding.selfTagLl.visibility = View.VISIBLE
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.close_rl -> {
                //亲密榜
                if (mActorId > 0) {
                    val intent = Intent(requireActivity(), CloseRankActivity::class.java)
                    intent.putExtra(Constant.ACTOR_ID, mActorId)
                    startActivity(intent)
                }
            }
            R.id.gift_rl -> {
                //礼物柜
                if (mActorId > 0) {
                    val intent = Intent(requireActivity(), GiftPackActivity::class.java)
                    intent.putExtra(Constant.ACTOR_ID, mActorId)
                    startActivity(intent)
                }
            }
            R.id.see_wechat_tv -> {
                //查看微信
                if (userSex == mActorInfoBean.t_sex) {
                    ToastUtil.showToast(requireActivity(), R.string.sex_can_not_communicate)
                    return
                }
                val weChat = 0
                showSeeWeChatRemindDialog(weChat)
            }
            R.id.see_phone_tv -> {
                //查看电话
                if (userSex == mActorInfoBean.t_sex) {
                    ToastUtil.showToast(requireActivity(), R.string.sex_can_not_communicate)
                    return
                }
                val phone = 1
                showSeeWeChatRemindDialog(phone)
            }
        }
    }

    /**
     * 显示查看微信号提醒
     */
    private fun showSeeWeChatRemindDialog(position: Int) {
        val binding = DialogSeeWeChatNumberLayoutBinding.inflate(LayoutInflater.from(context))
        val mDialog = Dialog(requireActivity(), R.style.DialogStyle_Dark_Background)
        setDialogView(binding, mDialog, position)
        mDialog.setContentView(binding.root)
        val outSize = Point()
        requireActivity().windowManager.defaultDisplay.getSize(outSize)
        val window = mDialog.window
        if (window != null) {
            val params = window.attributes
            params.width = outSize.x
            window.setGravity(Gravity.CENTER) // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false)
        if (!requireActivity().isFinishing) {
            mDialog.show()
        }
    }

    /**
     * 设置查看微信号提醒view
     */
    private fun setDialogView(
        binding: DialogSeeWeChatNumberLayoutBinding,
        mDialog: Dialog,
        position: Int
    ) {
        var cost = 0f
        val typeStr = if (position == 0) "微信号" else "手机号码"
        //描述
        if (mActorInfoBean.anchorSetup != null && mActorInfoBean.anchorSetup.size > 0) {
            val chargeBean = mActorInfoBean.anchorSetup[0]
            cost = if (position == 0) {
                chargeBean.t_weixin_gold
            } else {
                chargeBean.t_phone_gold
            }
            binding.seeDesTv.text = if (cost == 0f) {
                getString(R.string.info_block_des, typeStr)
            } else {
                getString(R.string.see_info_number_des, typeStr, FileUtil.parseFloatToString(cost))
            }
        } else {
            binding.seeDesTv.text = getString(R.string.info_block_des, typeStr)
        }

        //取消
        binding.cancelTv.setOnClickListener { mDialog.dismiss() }
        //确定
        binding.confirmTv.setOnClickListener {
            if (cost > 0) {
                seeWeChat(position)
            }
            mDialog.dismiss()
        }
    }

    /**
     * 查看微信号码
     */
    private fun seeWeChat(position: Int) {
        val url: String = if (position == 0) { //微信
            ChatApi.SEE_WEI_XIN_CONSUME()
        } else {
            ChatApi.SEE_PHONE_CONSUME()
        }
        val paramMap: MutableMap<String?, String?> = HashMap()
        paramMap["userId"] = userId
        paramMap["coverConsumeUserId"] = mActorId.toString()
        OkHttpUtils.post().url(url)
            .addParams("param", ParamUtil.getParam(paramMap))
            .build().execute(object : AjaxCallback<BaseResponse<String?>?>() {
                override fun onResponse(response: BaseResponse<String?>?, id: Int) {
                    if (requireActivity().isFinishing) {
                        return
                    }
                    response?.apply {
                        if (m_istatus == NetCode.SUCCESS || m_istatus == 2) {
                            val message = m_strMessage
                            if (!TextUtils.isEmpty(message)) {
                                ToastUtil.showToast(requireActivity(), message)
                            } else {
                                if (m_istatus == 2) {
                                    ToastUtil.showToast(requireActivity(), R.string.vip_free)
                                } else {
                                    ToastUtil.showToast(requireActivity(), R.string.pay_success)
                                }
                            }
                            if (position == 0) {//微信
                                mBinding.weChatTv.text =
                                    getString(R.string.we_chat_num_des_one, m_object)
                                mBinding.seeWechatTv.visibility = View.GONE
                            } else {
                                mBinding.phoneTv.text =
                                    getString(R.string.phone_num_one, m_object)
                                mBinding.seePhoneTv.visibility = View.GONE
                            }
                        } else if (m_istatus == -1) { //余额不足
                            ChargeHelper.showSetCoverDialog(requireActivity())
                        } else {
                            ToastUtil.showToast(requireActivity(), R.string.system_error)
                        }
                    } ?: apply {
                        ToastUtil.showToast(requireActivity(), R.string.system_error)
                    }
                }

                override fun onError(call: Call, e: Exception, id: Int) {
                    super.onError(call, e, id)
                    ToastUtil.showToast(requireActivity(), R.string.system_error)
                }
            })
    }

    /**
     * 获取用户性别
     */
    private val userSex: Int
        get() {
            if (AppManager.getInstance() != null) {
                val userInfo = AppManager.getInstance().userInfo
                val sex: Int = userInfo?.t_sex
                    ?: SharedPreferenceHelper.getAccountInfo(requireActivity().applicationContext).t_sex
                return if (sex != 2) sex else 0
            }
            return 0
        }

    /**
     * 获取UserId
     */
    private val userId: String
        get() {
            var sUserId = ""
            if (AppManager.getInstance() != null) {
                val userInfo = AppManager.getInstance().userInfo
                if (userInfo != null) {
                    val userId = userInfo.t_id
                    if (userId >= 0) {
                        sUserId = userId.toString()
                    }
                } else {
                    val id =
                        SharedPreferenceHelper.getAccountInfo(requireActivity().applicationContext).t_id
                    sUserId = id.toString()
                }
            }
            return sUserId
        }
}