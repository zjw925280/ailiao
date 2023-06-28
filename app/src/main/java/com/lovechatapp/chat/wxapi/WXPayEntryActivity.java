package com.lovechatapp.chat.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ToastUtil;

/**
 * 微信支付回调
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID, true);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        /*
          0 支付成功
         -1 发生错误 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
         -2 用户取消 发生场景：用户不支付了，点击取消，返回APP。
         */
        LogUtil.i("支付返回,errCode=" + resp.errCode + "  原因: " + resp.errStr);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            // 根据返回码
            int code = resp.errCode;
            switch (code) {
                case 0:
                    // 去本地确认支付结果
                    LogUtil.i("支付成功");
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.pay_vip_success);
                    if (AppManager.getInstance().getIsWeChatForVip()) {//是充值VIP
                        LogUtil.i("是充值VIP");
                    } else {
                        LogUtil.i("是充值金币");
                        //发送广播关闭Login页面
                        Intent intent = new Intent(Constant.FINISH_CHARGE_PAGE);
                        sendBroadcast(intent);
                    }
                    finish();
                    break;
                case -2:
                    LogUtil.i("支付已取消");
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), "支付已取消");
                    finish();
                    break;
                default:
                    LogUtil.i("支付失败");
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), "支付失败");
                    finish();
                    break;
            }
        }
    }
}
