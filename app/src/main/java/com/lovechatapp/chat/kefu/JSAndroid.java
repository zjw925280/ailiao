package com.lovechatapp.chat.kefu;

import android.app.Activity;
import android.webkit.JavascriptInterface;


public class JSAndroid {

    private Activity context;

    public JSAndroid(Activity context) {
        this.context = context;
    }

    private ConfigerManagner configerManagner;

    @JavascriptInterface
    public void openAndroid(String msg) {
        context.finish();
    }

    @JavascriptInterface
    public void writeData(String msg) {
        configerManagner = ConfigerManagner.getInstance(context);
        configerManagner.setString("js", msg);
    }

    @JavascriptInterface
    public String giveInformation(String msg) {
        configerManagner = ConfigerManagner.getInstance(context);
        return configerManagner.getString("js");
    }

}
