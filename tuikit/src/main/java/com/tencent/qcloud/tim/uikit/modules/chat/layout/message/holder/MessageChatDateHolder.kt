package com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder

import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.tencent.imsdk.TIMCustomElem
import com.tencent.qcloud.tim.uikit.BuildConfig
import com.tencent.qcloud.tim.uikit.R
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.StatusBean
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo
import com.tencent.qcloud.tim.uikit.utils.AjaxCallback
import com.tencent.qcloud.tim.uikit.utils.BaseResponse
import com.tencent.qcloud.tim.uikit.utils.DateTimeUtil
import com.tencent.qcloud.tim.uikit.utils.getPhoneMasked
import com.zhy.http.okhttp.OkHttpUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MessageChatDateHolder(itemView: View) : MessageContentHolder(itemView) {
    companion object {
        /**
         * 邀请状态——未接受
         */
        const val INVITE_TYPE_NEW = 0

        /**
         * 邀请状态——已接受
         */
        const val INVITE_TYPE_ACCEPTED = 1

        /**
         * 邀请状态——已拒绝
         */
        const val INVITE_TYPE_REFUSED = 2

        /**
         * 约会状态——已取消
         */
        const val INVITE_TYPE_CANCELED = 3

        /**
         * 约会状态——约会暗号已确认
         */
        const val INVITE_TYPE_CODE_VERIFY = 4

        /**
         * 约会状态——约会暗号自动确认
         */
        const val INVITE_TYPE_AUTO_VERIFY = 5

        /**
         * 约会状态——失效
         */
        const val INVITE_TYPE_INACTIVE = 7
    }

    private var mIsSelf = false
    override fun getVariableLayout() = R.layout.layout_chat_date_holder
    private lateinit var nameText: AppCompatTextView
    private lateinit var ageText: AppCompatTextView
    private lateinit var phoneText: AppCompatTextView
    private lateinit var cityText: AppCompatTextView
    private lateinit var timeText: AppCompatTextView
    private lateinit var remarkText: AppCompatTextView
    private lateinit var bgIv: AppCompatImageView
    private lateinit var statusIv: AppCompatImageView
    private lateinit var giftIv: AppCompatImageView
    private lateinit var nameIcon: AppCompatImageView
    private lateinit var sexIcon: AppCompatImageView
    private lateinit var sexBg: View
    private lateinit var phoneIcon: AppCompatImageView
    private lateinit var cityIcon: AppCompatImageView
    private lateinit var timeIcon: AppCompatImageView
    private lateinit var remarkIcon: AppCompatImageView
    private lateinit var refuseBtn: AppCompatTextView
    private lateinit var acceptBtn: AppCompatTextView
    private lateinit var dataJson: JSONObject

    override fun initVariableViews() {
        nameText = itemView.findViewById(R.id.textName)
        ageText = itemView.findViewById(R.id.ageText)
        phoneText = itemView.findViewById(R.id.textPhone)
        cityText = itemView.findViewById(R.id.textLocation)
        timeText = itemView.findViewById(R.id.textTime)
        remarkText = itemView.findViewById(R.id.textMark)
        bgIv = itemView.findViewById(R.id.dateBg)
        statusIv = itemView.findViewById(R.id.dateStatusImg)
        giftIv = itemView.findViewById(R.id.dateGiftImg)
        nameIcon = itemView.findViewById(R.id.iconName)
        sexIcon = itemView.findViewById(R.id.sexIcon)
        sexBg = itemView.findViewById(R.id.sexBg)
        phoneIcon = itemView.findViewById(R.id.iconPhone)
        cityIcon = itemView.findViewById(R.id.iconLocation)
        timeIcon = itemView.findViewById(R.id.iconTime)
        remarkIcon = itemView.findViewById(R.id.iconMark)
        refuseBtn = itemView.findViewById(R.id.btnRefuse)
        acceptBtn = itemView.findViewById(R.id.btnAccept)
    }

    override fun layoutVariableViews(msg: MessageInfo?, position: Int) {
        msg?.apply {
            mIsSelf = isSelf
            val ele = element as TIMCustomElem
            val json = String(ele.data)
            dataJson = JSONObject(json)
            setData(dataJson)
            getNewStatus()
        }
    }

    private fun getNewStatus() {
        val status = dataJson.optInt("appointmentStatus")
        setData(dataJson)
        if (status <= 1) {
            doGetNewStatus(dataJson.optInt("appointmentId"), dataJson.optInt("appointmentStatus")) {
                dataJson.remove("appointmentStatus")
                dataJson.put("appointmentStatus", it)
                setData(dataJson)
            }
        } else {
            setData(dataJson)
        }
    }

    private fun doGetNewStatus(dateId: Int, status: Int, goDataSet: (newStatus: Int) -> Unit) {
        val map = HashMap<String, Any>()
        map["appointmentId"] = dateId
        map["appointmentStatus"] = status
        OkHttpUtils.post().url("${BuildConfig.hostAddress}app/getAppointmentStatus.html")
            .addParams("param", getParamStr(map))
            .build().execute(object : AjaxCallback<BaseResponse<StatusBean>>() {
                override fun onResponse(response: BaseResponse<StatusBean>?, id: Int) {
                    response?.apply {
                        if (response.m_istatus == 1 && response.m_object != null) {
                            goDataSet.invoke(response.m_object.appointmentStatus)
                        } else {
                            goDataSet.invoke(status)
                        }
                    } ?: apply {
                        goDataSet.invoke(status)
                    }
                }
            })
    }

    /**
     * 反射根据参数集合[map]获取加密后的参数字符串的方法
     */
    private fun getParamStr(map: HashMap<String, Any>): String {
        val cls = Class.forName("com.lovechatapp.chat.util.ParamUtil")
        val constructor = cls.getConstructor()
        val method = cls.getDeclaredMethod("getParam", Map::class.java)
        method.isAccessible = true
        return method.invoke(constructor.newInstance(), map) as String

    }

    private fun setData(jObject: JSONObject) {
        Glide.with(itemView.context).load(jObject.optString("giftImg")).into(giftIv) //设置礼物图片
        timeText.text = DateTimeUtil.getNewChatTime(jObject.optLong("createTime"))//设置创建时间
        val dateStatus = jObject.optInt("appointmentStatus")
        //根据状态[dateStatus]设置背景的图片加载
        bgIv.background =
            ContextCompat.getDrawable(
                itemView.context,
                when (dateStatus) {
                    0 -> {
                        R.mipmap.bg_date_w
                    }
                    1 -> {
                        R.mipmap.bg_date_n
                    }
                    else -> {
                        R.mipmap.bg_date_s
                    }
                }
            )
        //根据状态[dateStatus]设置图标的显示
//        nameIcon.setImageLevel(dateStatus)
//        phoneIcon.setImageLevel(dateStatus)
//        cityIcon.setImageLevel(dateStatus)
//        timeIcon.setImageLevel(dateStatus)
//        remarkIcon.setImageLevel(dateStatus)
        //设置数据显示
        nameText.text = jObject.optString("inviterName")
        cityText.text = jObject.optString("appointmentAddress")
        val remark = jObject.optString("remarks")
        if (remark.isNotEmpty()) {
            remarkText.text = remark
            remarkIcon.visibility = View.VISIBLE
            remarkText.visibility = View.VISIBLE
        } else {
            remarkIcon.visibility = View.GONE
            remarkText.visibility = View.GONE
        }
        timeText.text =
            SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.CHINA).format(
                Date(
                    jObject.optLong("appointmentTime")
                )
            )
        ageText.text = jObject.optInt("inviterAge").toString()
        //设置性别显示
        sexIcon.setImageLevel(jObject.optInt("inviterSex"))
        sexIcon.isSelected = true
        //根据状态[dateStatus]显示或隐藏部分控件
        when (dateStatus) {
            0 -> {
                statusIv.visibility = View.GONE
                if (mIsSelf) {
                    acceptBtn.visibility = View.GONE
                    refuseBtn.visibility = View.GONE
                } else {
                    acceptBtn.visibility = View.VISIBLE
                    refuseBtn.visibility = View.VISIBLE
                }
                //同意或拒绝约会的按钮点击监听
                refuseBtn.setOnClickListener {
                    //拒绝邀请
                    onBtnClick(it)
                }

                acceptBtn.setOnClickListener {
                    //同意邀请
                    onBtnClick(it)
                }

            }
            else -> {
                statusIv.visibility = View.VISIBLE
                remarkIcon.visibility = View.GONE
                remarkText.visibility = View.GONE
                acceptBtn.visibility = View.GONE
                refuseBtn.visibility = View.GONE
            }
        }
        phoneText.text = when (dateStatus) {
            INVITE_TYPE_ACCEPTED, INVITE_TYPE_AUTO_VERIFY, INVITE_TYPE_CODE_VERIFY -> {
                jObject.optString("inviterPhone")
            }
            else -> {
                if (mIsSelf) {
                    jObject.optString("inviterPhone")
                } else {
                    jObject.optString("inviterPhone").getPhoneMasked()
                }
            }
        }
        if (statusIv.visibility == View.VISIBLE) {
            //如果状态标识显示控件显示，则设置图片
            statusIv.setImageLevel(dateStatus)
        }
    }

    private fun onBtnClick(view: View) {
        val status = if (view == acceptBtn) INVITE_TYPE_ACCEPTED else INVITE_TYPE_REFUSED
        val intent = Intent()
        intent.action = "operateDate"
        intent.putExtra("id", dataJson.optInt("appointmentId"))
        intent.putExtra("status", status)
        itemView.context.sendBroadcast(intent)
        CoroutineScope(Dispatchers.Main).launch {
            updateStatus()
        }
    }

    private suspend fun updateStatus() {
        delay(1000)
        getNewStatus()
    }
}