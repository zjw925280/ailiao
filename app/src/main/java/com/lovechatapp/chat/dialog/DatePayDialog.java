package com.lovechatapp.chat.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.faceunity.fulivedemo.utils.ToastUtil;
import com.google.gson.Gson;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.DateCreateActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseBean;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.DateGiftBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.friendship.TIMFriend;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.modules.chat.C2CChatManagerKit;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.ImCustomMessage;
import com.tencent.qcloud.tim.uikit.modules.message.ImFilter;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

public class DatePayDialog extends Dialog implements View.OnClickListener{
    private final Activity activity;
    private final String inviteeId;
    private final String targetName;
    private final String address;
    private final String time;
    private final String phone;
    private final String mark;
    private TextView tv_zj;
    private TextView tv_df;
    public  static boolean isPay=true;
    private TextView tv_money;
    private final String chatid;
    private  DateGiftBean bean;
    /**是否已经请求创建约会接口并成功*/
    private boolean isCreateRequested = false;
    public DatePayDialog(@NonNull Activity context, String inviteeId,String chatid ,String targetName, String address, String time, String phone , String mark, DateGiftBean bean) {
        super(context);
        activity = context;
        this.inviteeId = inviteeId;
        this.targetName = targetName;
        this.address = address;
        this.time = time;
        this.phone = phone;
        this.mark = mark;
        this.bean = bean;
        this.chatid = chatid;

    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_dialog_pay_free);
        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setGravity(Gravity.CENTER);
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.setAttributes(lp);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        findViewById(R.id.image_dele).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        findViewById(R.id.tv_suer_pay).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Log.e("isPay","isPay="+isPay);

                if(isPay){
                    getDateId(time);
                }else {
                    createDate(isPay,address, time, mark, phone,Integer.parseInt(inviteeId));
                }
            }
        });
        tv_zj = findViewById(R.id.tv_zj);
        tv_df = findViewById(R.id.tv_df);
        tv_money = findViewById(R.id.tv_money);
        tv_money.setText(bean.getT_gift_gold()+"约豆");
        tv_zj.setOnClickListener(new View.OnClickListener() {//自付
            @Override
            public void onClick(View view) {
                tv_zj.setBackgroundResource(R.drawable.shape_bg_date_accept_btn);
                tv_df.setBackgroundResource(R.drawable.shape_date_refuse_button);
                tv_zj.setTextColor(Color.parseColor("#ffffff"));
                tv_df.setTextColor(Color.parseColor("#000000"));
                isPay=false;
            }
        });

        tv_df.setOnClickListener(new View.OnClickListener() {//他付
            @Override
            public void onClick(View view) {
                tv_df.setBackgroundResource(R.drawable.shape_bg_date_accept_btn);
                tv_zj.setBackgroundResource(R.drawable.shape_date_refuse_button);
                tv_df.setTextColor(Color.parseColor("#ffffff"));
                tv_zj.setTextColor(Color.parseColor("#000000"));
                isPay=true;
            }
        });
    }

    private void getDateId(String dateTime) {

        Map<String, Object> paramMap = new HashMap<>();
//        paramMap.put("inviterId",AppManager.getInstance().getUserInfo().t_id);
//        paramMap.put("inviteeId",chatid);
//        paramMap.put("giftId",bean.getT_gift_id());
//        paramMap.put("inviterPhone", phone);
          paramMap.put("appointmentTime", dateTime);
//        paramMap.put("appointmentAddress", address);
//        paramMap.put("remarks", mark);
//        Log.e("ralph", "params ========= " + paramMap);
//
        OkHttpUtils.post()
                .url(ChatApi.GET_DATE_ID())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject m_object = jsonObject.getJSONObject("m_object");
                            int invitationId = m_object.getInt("invitationId");
                            int status = m_object.getInt("status");
                            sendMessge(isPay,0,0,invitationId,status);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        Log.e("失败", "params ========= "+e.getMessage());

                    }


                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
public void sendMessge(boolean isPay,int appointmentId,int appointmentStatus,int invitationId,int invitationStatus){

    //如果从其他界面进入此界面，则在此界面发送消息
    //将数据转化成Json对象
    try {
        JSONObject json = new JSONObject();
        //添加消息的类型
            json.put("type", ImCustomMessage.Type_Date);
            json.put("appointmentAddress", address);//约会地址
            json.put("appointmentId", appointmentId);//约会id
            json.put("invitationID",invitationId);//女方约会id
            json.put("appointmentTime",time);//约会时间
            json.put("giftImg",bean.getT_gift_still_url());//约会礼物图片地址
//          json.put("gift_id",bean.getT_gift_id() );//约会礼物id
            json.put("giftId",bean.getT_gift_id() );//约会礼物id
            json.put("gift_number",1 );//约会礼物数量
            json.put("gold_number",bean.getT_gift_gold() );//约会礼物金币数量
            json.put("giftGold",bean.getT_gift_gold() );//约会礼物金币数量
            json.put("inviterAge",20 );//发起人年龄
            json.put("appointmentStatus",appointmentStatus );//约会邀请状态
            json.put("invitationStatus",invitationStatus );//女方发起他付约会邀请状态
            json.put("inviterName", AppManager.getInstance().getUserInfo().t_nickName );//发起人姓名
            json.put("inviterPhone",phone);//发起人手机
            json.put("sex", 0);//发起人性别
            json.put("inviterId", AppManager.getInstance().getUserInfo().t_id);//发起人id
            json.put("inviteeId", inviteeId);//对方id
            json.put("remarks", mark);//对方id
            json.put("self", false);//是否当前登录用户
            json.put("isCharge", isPay);//谁支付
        //将Json对象转换成自定义消息数据对象
        ImCustomMessage imCustomMessage = new Gson().fromJson(json.toString(), ImCustomMessage.class);
        //初始化消息封装对象
        MessageInfo info = new MessageInfo();
        //初始化即时通讯消息对象
        TIMMessage timMsg = new TIMMessage();
        //初始化自定义元素
        TIMCustomElem ele = new TIMCustomElem();
        //各种设置数据
        ele.setData(json.toString().getBytes());
        timMsg.addElement(ele);
        info.setSelf(false);
        info.setTIMMessage(timMsg);
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setElement(ele);
        info.setExtra(imCustomMessage);
        info.setMsgType(MessageInfo.MSG_TYPE_DATE);
        info.setPay(isPay);
        info.setFromUser(TIMManager.getInstance().getLoginUser());
        ChatInfo chatInfo = new ChatInfo();
        int i = Integer.parseInt(inviteeId)+10000;
        chatInfo.setId(String.valueOf(i));
        chatInfo.setChatName(targetName);
        chatInfo.setTopChat(false);
        //查询是否有备注
        TIMFriend timFriend = TIMFriendshipManager.getInstance().queryFriend(chatInfo.getId());
        if (timFriend != null && !TextUtils.isEmpty(timFriend.getRemark())) {
            chatInfo.setChatName(timFriend.getRemark());
        }
        //设置发送消息的对象
        C2CChatManagerKit.getInstance().setCurrentChatInfo(chatInfo);

        Log.e("消息体","是不是="+new Gson().toJson(info)+" 啥"+new Gson().toJson(chatInfo)+" ImCustomMessage="+new Gson().toJson(imCustomMessage));
        //发送消息
        C2CChatManagerKit.getInstance().sendMessage(info, false,new IUIKitCallBack(){
            @Override
            public void onSuccess(Object data) {
//                发生成功
                ToastUtil.showToast(activity,"发送成功");
                dismiss();
                activity.finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.showToast(activity, errMsg);
            }
        });
    } catch (JSONException e) {
        Log.e("什么鬼","什么鬼="+e.getMessage());
    }

}
    /**请求创建约会接口返回的结果字符串*/
    private String jsonStr = "";
    /**使用输入的地址[address]， 约会时间[time]， 备注[mark]创建约会的方法*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    private void createDate(boolean isPay,String address , String time,String  mark,String phone,int chatid) {

        if (isCreateRequested && !jsonStr.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                sendMessge(isPay,jsonObject.getInt("appointmentId"),jsonObject.getInt("appointmentStatus"),-1,-1);
                return;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("inviterId",AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("inviteeId",chatid);
        paramMap.put("giftId",bean.getT_gift_id());
        paramMap.put("inviterPhone", phone);
        paramMap.put("appointmentTime", time);
        paramMap.put("appointmentAddress", address);
        paramMap.put("remarks", mark);
        Log.e("ralph", "params ========= " + paramMap);

        OkHttpUtils.post()
                .url(ChatApi.createDate())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<BaseResponse<String>>() {
                    @Override
                    public void onResponse(BaseResponse<String> response, int id) {
                        Log.e("創建約會",""+new Gson().toJson(response));
                        if (response != null) {
                            switch (response.m_istatus) {
                                case NetCode.SUCCESS:
                                    isCreateRequested = true;
                                    jsonStr = response.m_object;
                                    try {
                                        JSONObject jsonObject = new JSONObject(jsonStr);
                                        sendMessge(isPay,jsonObject.getInt("appointmentId"),jsonObject.getInt("appointmentStatus"),-1,-1);
                                        break;
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                case -1:
                                    ToastUtil.showToast(activity,response.m_strMessage);
                                    break;
                                default:
                                    ToastUtil.showToast(activity,"发起约会失败，请稍后重试");
                                    break;
                            }
                        }
                    }
                });
    }


    @Override
    public void onClick(View view) {
        dismiss();
    }
}
