package com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.tencent.imsdk.TIMCustomElem
import com.tencent.qcloud.tim.uikit.BuildConfig
import com.tencent.qcloud.tim.uikit.R
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.StatusBean
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo
import com.tencent.qcloud.tim.uikit.utils.AjaxCallback
import com.tencent.qcloud.tim.uikit.utils.BaseResponse
import com.tencent.qcloud.tim.uikit.utils.DateTimeUtil
import com.tencent.qcloud.tim.uikit.utils.ToastUtil
import com.zhy.http.okhttp.OkHttpUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MessageChatDateHolder(itemView: View,private val context: Context) : MessageContentHolder(itemView) {
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

    private var status: Int=-1
    private var mIsSelf = false
    private var mpay = false
    private var dateStatus=-1
    var isYes = 0
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
    private  var appointmentId: Int = 0

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
           val json1= JSONObject(Gson().toJson(msg))
           val json2= json1.getJSONObject("extra")
            mpay=json2.optBoolean("isCharge")
            val ele = element as TIMCustomElem
            val json = String(ele.data)
            dataJson = JSONObject(json)
            Log.e("啥数值呢","啥数值呢"+Gson().toJson(msg)+" json="+Gson().toJson(json))
            setData(dataJson)
            getNewStatus()
        }
    }

    private fun getNewStatus() {
        var status1=-1
        Log.e("自付","mpay="+mpay)
        if (mpay){
            status1 = dataJson.optInt("invitationStatus")
        }else{
            status1 = dataJson.optInt("appointmentStatus")
        }
        setData(dataJson)
        if (status1 <= 1) {
            if (mpay){
                Log.e("自付","mpay="+mpay)
                getInvitation( dataJson.optInt("invitationID"),dataJson.optInt("invitationStatus")){
                        dataJson.remove("invitationStatus")
                        dataJson.put("invitationStatus", it)
                       setData(dataJson)
                }
            }else{
                Log.e("自付","mpay="+mpay)
                doGetNewStatus( dataJson.optInt("appointmentId"), dataJson.optInt("appointmentStatus")) {
                        dataJson.remove("appointmentStatus")
                        dataJson.put("appointmentStatus", it)
                    setData(dataJson)
            }
            }

        } else {
            Log.e("自付","status1>1")
            setData(dataJson)
        }
    }

    private fun doGetNewStatus(dateId: Int, status: Int, goDataSet: (newStatus: Int) -> Unit) {
            val map = HashMap<String, Any>()
            map["appointmentId"] = dateId
            map["appointmentStatus"] = status
           Log.e("自付","dateId="+dateId+" appointmentStatus="+status)
            OkHttpUtils.post().url("${BuildConfig.hostAddress}app/getAppointmentStatus.html")
                .addParams("param", getParamStr(map))
                .build().execute(object : AjaxCallback<BaseResponse<StatusBean>>() {
                    override fun onResponse(response: BaseResponse<StatusBean>?, id: Int) {
                        response?.apply {
                            if (response.m_istatus == 1 && response.m_object != null) {
                                goDataSet.invoke(response.m_object.appointmentStatus)                            } else {
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
        if (mpay){
            dateStatus = jObject.optInt("invitationStatus")
        }else{
            dateStatus = jObject.optInt("appointmentStatus")
        }

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
        Log.e("是不是时间戳","是不是时间戳="+jObject.optString("appointmentTime"))
        if (jObject.optString("appointmentTime").length==13){
            timeText.text = SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.CHINA).format(
                    Date(
                        jObject.optLong("appointmentTime")
                    )
                )
        }else if (jObject.optString("appointmentTime").length==10){
            timeText.text = SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.CHINA).format(
                Date(
                    jObject.optLong("appointmentTime")*1000
                )
            )
        }else{
            timeText.text = jObject.optString("appointmentTime")
        }


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
                    Log.e("自付","mpay="+mpay)
                    if (mpay){//拒绝邀请
//                        createDate(refuseBtn,dataJson.optString("appointmentAddress"),
//                            dataJson.optString("appointmentTime"),
//                            dataJson.optString("remarks"), dataJson.optString("inviterPhone"), dataJson.optInt("inviteeId"), dataJson.optInt("inviterId"),dataJson.optInt("giftId"),dataJson.optString("inviterName"),dataJson.optInt("inviterId").toString())
//                            dataJson.remove("self")
//                            dataJson.put("self", true)
//                            setData(dataJson,1)
                        isYes=1
                        Log.e("拒绝邀请","拒绝邀请="+Gson().toJson(dataJson));
                        upDataStu( dataJson.optInt("invitationID"),isYes)

                   }else{
                        //拒绝邀请
                        Log.e("自付","mpay拒絕="+mpay)
                        onBtnClick(it,0,0)
                    }

                }
                acceptBtn.setOnClickListener {
                    if (mpay){//接受并支付
                        showDialog(dataJson.optInt("giftGold").toString(),context)
                    }else{
                        //同意邀请
                        onBtnClick(it,0,0)
                    }
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
                    jObject.optString("inviterPhone")
                }
            }
        }
        if (statusIv.visibility == View.VISIBLE) {
            //如果状态标识显示控件显示，则设置图片
            statusIv.setImageLevel(dateStatus)
        }
    }

    private fun onBtnClick(view: View,a:Int,b:Int) {
        var status = if (view == acceptBtn) INVITE_TYPE_ACCEPTED else INVITE_TYPE_REFUSED
        if (a==1){
            appointmentId=b
        }else{
            appointmentId= dataJson.optInt("appointmentId")
        }
        val intent = Intent()
        intent.action = "operateDate"
        intent.putExtra("id",appointmentId )
        intent.putExtra("status", status)
        itemView.context.sendBroadcast(intent)
        CoroutineScope(Dispatchers.Main).launch {
            updateStatus()
        }
    }

    /**请求创建约会接口返回的结果字符串*/
    private var jsonStr = ""
    /**是否已经请求创建约会接口并成功*/
    private var isCreateRequested = false

    /**使用输入的地址[address]， 约会时间[time]， 备注[mark]创建约会的方法*/
    @SuppressLint("SimpleDateFormat")
    private fun createDate(view: View,address: String, time: String, mark: String, phone: String, inviterId: Int,inviteeId:Int,giftId:Int,targetName:String,chatid:String,invitationId:Int) {
        //组装提交服务器的参数列表
            val paramMap: HashMap<String, Any> = HashMap()
            paramMap["inviterId"] = inviterId
            paramMap["inviteeId"] = inviteeId
            paramMap["giftId"] = giftId.toString()
            paramMap["inviterPhone"] = phone
            paramMap["appointmentTime"] = time
            paramMap["appointmentAddress"] = address
            paramMap["remarks"] = mark
            paramMap["isDeley"] = true
            Log.e("ralph", "params ========= $paramMap")
            //创建请求
            OkHttpUtils.post()
                .url("${BuildConfig.hostAddress}app/addAppointment.html")
                .addParams("param",getParamStr(paramMap))
                .build()
                .execute(object : AjaxCallback<BaseResponse<MessageBean>?>() {
                    override fun onResponse(response: BaseResponse<MessageBean>?, id: Int) {
                        Log.e("进来了","进来了")
                        response?.apply {
                            when (m_istatus) {
                                1 -> {//请求成功
                                    isCreateRequested = true //设置标识

                                    Log.e("是不是要这个线","是不是要这个线");

                                    //记录返回的数据
                                    jsonStr = Gson().toJson(response.m_object)

                                    onBtnClick(view,1,response.m_object.appointmentId)
                                }
                                -1 -> {//请求出错，提示用户
                                    ToastUtil.toastLongMessage(m_strMessage)
                                }
                                else -> {
                                    ToastUtil.toastLongMessage("约会失败，请稍后重试")
                                }
                            }
                        }

                    }
                })


    }




    /**发送消息的方法*/
    private suspend fun updateStatus() {
        delay(1000)
        getNewStatus()
    }
    fun showDialog(giftGold:String,context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(context)

        // 设置对话框标题和消息
        alertDialogBuilder.setTitle("提示")
        alertDialogBuilder.setMessage("支付"+giftGold+"约豆才能约会哦!")

        // 设置关闭按钮
        alertDialogBuilder.setNegativeButton("关闭") { dialog, _ ->
            dialog.dismiss()
        }
        // 设置确认按钮
        alertDialogBuilder.setPositiveButton("支付") { dialog, _ ->
            isYes = 0
            upDataStu(dataJson.optInt("invitationID"),isYes)
            createDate(acceptBtn,dataJson.optString("appointmentAddress"),
                dataJson.optString("appointmentTime"),
                dataJson.optString("remarks"), dataJson.optString("inviterPhone"), dataJson.optInt("inviteeId"), dataJson.optInt("inviterId"),dataJson.optInt("giftId"),dataJson.optString("inviterName"),dataJson.optInt("inviterId").toString(),dataJson.optInt("invitationID"))
        }
        // 创建并显示对话框
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun getInvitation(dateId: Int, status: Int, goDataSet: (newStatus: Int) -> Unit) {
        val map = HashMap<String, Any>()
        Log.e("dateId是啥呢","dateId是啥呢="+dateId)
        map["invitationId"] = dateId
        OkHttpUtils.post().url("${BuildConfig.hostAddress}app/getInvitation.html")
            .addParams("param", getParamStr(map))
            .build().execute(object : AjaxCallback<BaseResponse<StatusBean>>() {
                override fun onResponse(response: BaseResponse<StatusBean>?, id: Int) {
                    Log.e("啥数据",""+Gson().toJson(response));
                    response?.apply {
                        if (response.m_istatus == 1 && response.m_object != null) {
                            goDataSet.invoke(response.m_object.status)
                        } else {
                            goDataSet.invoke(status)
                        }
                    } ?: apply {
                        goDataSet.invoke(status)
                    }
                }
            })

    }
  fun upDataStu( invitationId:Int,isYes:Int){
      if (isYes==0){
          status=1//接受
      }else {
          status=2//拒绝
      }
      val paramMap1: HashMap<String, Any> = HashMap()
      paramMap1.put("invitationId",invitationId)
      paramMap1.put("status",status)
      paramMap1.put("appointmentTime",dataJson.optString("appointmentTime"))
      Log.e("ralph", "params ========= $paramMap1")
      //创建请求
      OkHttpUtils.post()
          .url("${BuildConfig.hostAddress}app/updateInvitation.html")
          .addParams("param",getParamStr(paramMap1))
          .build()
          .execute(object : AjaxCallback<BaseResponse<String>?>() {
              override fun onResponse(response: BaseResponse<String>?, id: Int) {
                  Log.e("进来了","进来了"+Gson().toJson(response))

                  getInvitation( dataJson.optInt("invitationID"),dataJson.optInt("invitationStatus")){
                      dataJson.remove("invitationStatus")
                      dataJson.put("invitationStatus", it)
                      setData(dataJson)
                  }

//                    response?.apply {
//                        when (m_istatus) {
//                            1 -> {//请求成功
//                                isCreateRequested = true //设置标识
//                                Log.e("是不是要这个线","是不是要这个线");
//                                //记录返回的数据
//                                jsonStr = Gson().toJson(response.m_object)
//
//                                onBtnClick(view,1,response.m_object.appointmentId)
//                            }
//                            -1 -> {//请求出错，提示用户
//                                ToastUtil.toastLongMessage(m_strMessage)
//                            }
//                            else -> {
//                                ToastUtil.toastLongMessage("发起约会失败，请稍后重试")
//                            }
//                        }
//                    }

              }
          })
  }

}