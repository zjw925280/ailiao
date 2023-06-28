package com.lovechatapp.chat.util.permission.floating;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * Copyright (C), 2015-2020
 * FileName: SystemAlertWindow
 * Author: zx
 * Date: 2020/4/26 9:32
 * Description:
 */
public class SystemAlertWindow {
    private static final String MARK = Build.MANUFACTURER.toLowerCase();

    private Activity mSource;
    public static final int REQUEST_OVERLY=7562;

    public SystemAlertWindow(Activity source) {
        this.mSource = source;
    }

    public void start(int requestCode) {
        Intent intent;
        if (MARK.contains("meizu")) {
            intent = meiZuApi(mSource);
        } else {
            intent = defaultApi(mSource);
        }
        try {
            mSource.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            intent = appDetailsApi(mSource);
            mSource.startActivityForResult(intent, requestCode);
        }
    }

    private  Intent appDetailsApi(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }

    private  Intent defaultApi(Context context) {
        Intent intent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        }
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        if (hasActivity(context, intent)) {
            return intent;
        }

        return appDetailsApi(context);
    }

    private  Intent meiZuApi(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.putExtra("packageName", context.getPackageName());
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        if (hasActivity(context, intent)) {
            return intent;
        }

        return defaultApi(context);
    }

    private  boolean hasActivity(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }
}
