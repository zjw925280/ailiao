package com.lovechatapp.chat.activity


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.lovechatapp.chat.base.AppManager
import com.lovechatapp.chat.base.BaseActivity
import com.lovechatapp.chat.base.BaseResponse
import com.lovechatapp.chat.bean.DateGiftBean
import com.lovechatapp.chat.constant.ChatApi
import com.lovechatapp.chat.databinding.ActivityDateCreateBinding
import com.lovechatapp.chat.dialog.BottomDateGiftDialog
import com.lovechatapp.chat.dialog.BottomDateGiftDialog.GiftSelectedListener
import com.lovechatapp.chat.dialog.DatePayDialog
import com.lovechatapp.chat.dialog.DateTimePickerDialog
import com.lovechatapp.chat.dialog.InputRemarkDialog
import com.lovechatapp.chat.ext.setClick
import com.lovechatapp.chat.helper.ImageLoadHelper
import com.lovechatapp.chat.net.AjaxCallback
import com.lovechatapp.chat.net.NetCode
import com.lovechatapp.chat.util.ParamUtil
import com.lovechatapp.chat.util.ToastUtil
import com.tencent.imsdk.TIMCustomElem
import com.tencent.imsdk.TIMFriendshipManager
import com.tencent.imsdk.TIMManager
import com.tencent.imsdk.TIMMessage
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack
import com.tencent.qcloud.tim.uikit.modules.chat.C2CChatManagerKit
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.ImCustomMessage
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo
import com.zhy.http.okhttp.OkHttpUtils
import org.json.JSONObject

/**
 * 发起约会界面
 */
class DateCreateActivity : BaseActivity() {


    /**初始化当前用户信息对象*/
    private val user = AppManager.getInstance().userInfo
    private lateinit var list: ArrayList<DateGiftBean>
    private lateinit var bean1:DateGiftBean
    private  var money=0
    private  var giftImg:String=""

    /**底部弹出的礼物选择弹框*/
    private var dialog: BottomDateGiftDialog? = null

    /**是否已经请求创建约会接口并成功*/
    private var isCreateRequested = false

    /**视图绑定器*/
    private lateinit var mBinding: ActivityDateCreateBinding


    override fun getContentView(): View {
        mBinding = ActivityDateCreateBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onContentAdded() {
        needHeader(true)
        title = "约会"
        setListeners()

    }

    /***/
    @SuppressLint("CutPasteId")
    private fun setListeners() {
        mBinding.textAddress.setClick {
            //点击写入地址,弹出输入地址的弹框
            object : InputRemarkDialog(this, "地址", "请输入约会地址") {
                override fun remark(text: String) {
                    //点击确认按钮，设置输入的内容
                    mBinding.textAddress.text = text
                }
            }.show()
        }

        mBinding.textPhone.setClick {
            object : InputRemarkDialog(this, "手机号码", "请输入手机号码", InputType.TYPE_CLASS_PHONE) {
                override fun remark(text: String) {
                    mBinding.textPhone.text = text
                }
            }.show()
        }
        mBinding.textTime.setClick {//点击日期条目
            //弹出选择日期和时间的弹框
            DateTimePickerDialog(this) { result ->
                //点击确认按钮后，设置显示选择的日期和时间
                mBinding.textTime.text = result
            }.show()
        }
        mBinding.giftBg.setClick { //点击选择礼物
            //初始化礼物弹框，向服务器请求礼物列表并显示弹框
            showGiftDialog()
        }
        mBinding.btnSubmit.setClick {//提交约会按钮点击
            val address =
                mBinding.textAddress.text.toString().trim()//获取输入的地址
            val phone = mBinding.textPhone.text.toString().trim()//获取手机号码
            val time = removeExtraSpaces(mBinding.textTime.text.toString().trim())//获取选择的日期时间

            val mark =
                mBinding.markEdt.text.toString().trim()//获取输入的备注
            if (giftIdSelected == -1) {//未选择礼物
                ToastUtil.showToast("请选择礼物")
                return@setClick
            }

            if (phone.isEmpty()) {//未输入手机号码
                ToastUtil.showToast("请填写手机号码")
                return@setClick
            }

            if (address.isEmpty()) {//未输入约会地址
                ToastUtil.showToast("请填写约会地点")
                return@setClick
            }
            if (time.isEmpty()) {//未选择日期时间
                ToastUtil.showToast("请选择约会时间")
                return@setClick
            }

            if (AppManager.getInstance().userInfo.t_sex==0){//女性发起约会
                DatePayDialog(mContext,intent.getStringExtra("targetId") ?: "",intent.getStringExtra("chatid")?:"",intent.getStringExtra("targetName")?:"",address,time,phone,mark,bean1).show()
                return@setClick
            }

            //创建约会
            createDate(address, time, mark, phone)
        }
    }
    fun removeExtraSpaces(input: String): String {
        val regex = "\\s+".toRegex()
        return input.replace(regex, " ")
    }
    /**已经选择的礼物id*/
    private var giftIdSelected = -1

    /**根据选择的礼物数据对象[bean]设置数据显示*/
    private fun selectedGift(bean: DateGiftBean) {
        giftIdSelected = bean.t_gift_id//记录选择的礼物id
        //显示礼物图片
        ImageLoadHelper.glideShowImageWithUrl(
            this,
            bean.t_gift_still_url,
            mBinding.giftImg
        )
    }

    /**请求服务器获取礼物列表数据*/
    private fun requestGiftList() {
        val paramMap: MutableMap<String?, String?> = HashMap()
        paramMap["userId"] = userId
        OkHttpUtils.post().url(ChatApi.getDateGiftList())
            .addParams("param", ParamUtil.getParam(paramMap))
            .build().execute(object : AjaxCallback<BaseResponse<ArrayList<DateGiftBean>>?>() {
                override fun onResponse(response: BaseResponse<ArrayList<DateGiftBean>>?, id: Int) {
                    if (this@DateCreateActivity.isFinishing) {
                        return
                    }
                    if (response != null && response.m_istatus == NetCode.SUCCESS) {
                        if (response.m_object != null) {
                            //获取数据成功，使用礼物数据初始化弹窗
                            initGiftDialog(response.m_object)
                        } else {
                            ToastUtil.showToast("暂时还没有礼物")
                        }
                    } else if (response != null) {
                        ToastUtil.showToast(response.m_strMessage)
                    }
                }
            })
    }

    /**初始化礼物弹框*/
    private fun initGiftDialog(list: ArrayList<DateGiftBean>) {
        //使用传递进来的数据初始化数据
        this.list=list
        if (list.isNotEmpty()) {
            list[0].isSelected = true
            dialog = BottomDateGiftDialog(this@DateCreateActivity, list)
            dialog?.setGiftSelectListener(object : GiftSelectedListener() {
                override fun onSelected(bean: DateGiftBean) {
                    bean1=bean
                    selectedGift(bean)
                }
            })
            dialog?.show()
        } else {
            ToastUtil.showToast("暂时还没有礼物")
        }
    }

    /**显示礼物弹框*/
    private fun showGiftDialog() {
        if (dialog != null) {//如果礼物弹框对象不为空，则直接弹出
            dialog!!.show()
        } else {//若为空，则请求礼物列表进行初始化
            requestGiftList()
        }
    }

    /**请求创建约会接口返回的结果字符串*/
    private var jsonStr = ""

    /**使用输入的地址[address]， 约会时间[time]， 备注[mark]创建约会的方法*/
    @SuppressLint("SimpleDateFormat")
     fun createDate(address: String, time: String, mark: String, phone: String) {
        showLoadingDialog()
        if (isCreateRequested && jsonStr.isNotEmpty()) {//如果已经请求过创建约会的服务器且成功，则直接发送消息
            sendMessage()
            return
        }
        //组装提交服务器的参数列表
        val paramMap: MutableMap<String, Any?> = HashMap()
        paramMap["inviterId"] = userId
        paramMap["inviteeId"] = intent.getStringExtra("targetId") ?: ""
        paramMap["giftId"] = giftIdSelected.toString()
        paramMap["inviterPhone"] = phone
        paramMap["appointmentTime"] = time
        paramMap["appointmentAddress"] = address
        paramMap["remarks"] = mark
        Log.e("ralph", "params ========= $paramMap")

        //创建请求
        OkHttpUtils.post()
            .url(ChatApi.createDate())
            .addParams("param", ParamUtil.getParam(paramMap))
            .build()
            .execute(object : AjaxCallback<BaseResponse<String>?>() {
                override fun onResponse(response: BaseResponse<String>?, id: Int) {
                    if (this@DateCreateActivity.isFinishing) {
                        return
                    }
                    dismissLoadingDialog()
                    response?.apply {
                        when (m_istatus) {
                            NetCode.SUCCESS -> {//请求成功
                                isCreateRequested = true //设置标识
                                //记录返回的数据
                                jsonStr = m_object

                                //调用发送消息的方法
                                sendMessage()
                            }
                            -1 -> {//请求出错，提示用户
                                ToastUtil.showToast(m_strMessage)
                            }
                            else -> {
                                ToastUtil.showToast("发起约会失败，请稍后重试")
                            }
                        }
                    }

                }
            })
    }

    /**发送消息的方法*/
     fun sendMessage() {
        if (jsonStr.isEmpty()) {//如果服务器返回的数据为空，则不发送消息
            return
        }
        if (intent.getBooleanExtra("isFromChat", false)) {
            //如果从聊天页面进入此界面，则将数据返回至聊天界面发送消息
            val outIntent = Intent()
            outIntent.putExtra("data", jsonStr)
            setResult(RESULT_OK, outIntent)
            this@DateCreateActivity.finish()
        } else {
            //如果从其他界面进入此界面，则在此界面发送消息
            //将数据转化成Json对象
            val json = JSONObject(jsonStr)
            //添加消息的类型
            json.put("type", ImCustomMessage.Type_Date)
            //将Json对象转换成自定义消息数据对象
            val imCustomMessage = Gson().fromJson(json.toString(), ImCustomMessage::class.java)
            //初始化消息封装对象
            val info = MessageInfo()
            //初始化即时通讯消息对象
            val timMsg = TIMMessage()
            //初始化自定义元素
            val ele = TIMCustomElem()
            //各种设置数据
            ele.data = json.toString().toByteArray()
            timMsg.addElement(ele)
            info.isSelf = true
            info.timMessage = timMsg
            info.msgTime = System.currentTimeMillis() / 1000
            info.element = ele
            info.extra = imCustomMessage
            info.msgType = MessageInfo.MSG_TYPE_DATE
            info.fromUser = TIMManager.getInstance().loginUser
            val chatInfo = ChatInfo()
            val targetId=  intent.getStringExtra("targetId") ?: ""
            val i: Int = targetId.toInt() + 10000
            chatInfo.id = i.toString()
            chatInfo.chatName = intent.getStringExtra("targetName") ?: ""
            chatInfo.isTopChat = false
            //查询是否有备注
            val timFriend = TIMFriendshipManager.getInstance().queryFriend(chatInfo.id)
            if (timFriend != null && !TextUtils.isEmpty(timFriend.remark)) {
                chatInfo.chatName = timFriend.remark
            }
            //设置发送消息的对象
            C2CChatManagerKit.getInstance().currentChatInfo = chatInfo
            Log.e("是不是这个","info=="+Gson().toJson(info)+" chatInfo="+Gson().toJson(chatInfo));
            //发送消息
            C2CChatManagerKit.getInstance().sendMessage(info, false, object : IUIKitCallBack {
                override fun onSuccess(data: Any?) {
                    Log.e("是不是这个","发送成功data=="+data);
                    //消息发送成功，退出界面
                    this@DateCreateActivity.finish()
                }

                override fun onError(module: String?, errCode: Int, errMsg: String?) {
                    //TODO 失败处理
                    ToastUtil.showToast(errMsg ?: "出错啦！")
                    Log.e("是不是这个","出错啦errMsg=="+errMsg+" module="+module)
                }
            })
        }
    }

    companion object {

        /**使用
         * 上下文对象[context]
         * 对方用户id[targetId],
         * 对方聊天id[chatId],
         * 对方的昵称[targetName]
         * 跳转到本界面的方法*/
        @JvmStatic
        fun startActivity(context: Context, targetId: String?,chatId: String, targetName: String?) {
            val intent = Intent(context, DateCreateActivity::class.java)
            intent.putExtra("targetId", targetId)
            intent.putExtra("targetName", targetName)
            intent.putExtra("chatId", chatId)
            intent.putExtra("isFromChat", false)
            context.startActivity(intent)
        }

        /**使用
         * Activity类对象[act]
         *  请求码[requestCode]
         *  对方用户id[targetId]
         *  对方的昵称[targetName]
         *  跳转到本界面并要求返回的方法*/
        @JvmStatic
        fun startActivityForResult(
            act: Activity,
            requestCode: Int,
            targetId: String?,
            targetName: String?
        ) {
            val intent = Intent(act, DateCreateActivity::class.java)
            intent.putExtra("targetId", targetId)
            intent.putExtra("targetName", targetName)
            intent.putExtra("isFromChat", true)
            act.startActivityForResult(intent, requestCode)
        }
    }
}