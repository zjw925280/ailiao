package com.lovechatapp.chat.util.permission.floating.api;

import android.app.Activity;
import android.content.Intent;

/**
 * Copyright (C), 2015-2020
 * FileName: BgStart
 * Author: zx
 * Date: 2020/4/17 11:33
 * Description:
 */
public interface BgStart {

    public void startActivity(Activity context, Intent intent, String className);

    public void requestStartPermission(Activity activity, PermissionLisenter listener);

}