package com.lovechatapp.chat.util.permission;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CheckPermissionActivity extends AppCompatActivity {

    private int requestCode;

    //设计两个OnPermissionListener的原因是，有效缓解并发请求权限时，OnPermissionListener公用一个会引发回调错乱或没法回调的问题
    private static PermissionUtil.OnPermissionListener transferOnPermissionListener;//这个静态变量充当一个传递者的角色,传递给mOnPermissionListener变量后，就释放引用了

    private PermissionUtil.OnPermissionListener mOnPermissionListener;//这个才是我们每次权限请求使用的回调监听

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1像素的activity
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);

        //获取界面传过来的值
        getIntentData();
    }

    private void getIntentData() {
        //获取传递过来回调监听
        mOnPermissionListener = transferOnPermissionListener;
        transferOnPermissionListener = null;
        //传过来的需要申请的权限
        String[] permissions = getIntent().getStringArrayExtra("permissions");
        if (permissions != null && permissions.length > 0) {
            requestPermissions(permissions);
        } else {
            //手动报错提示
            throw new NullPointerException("申请的权限列表不能为空！");
        }
    }

    /**
     * 去申请所有权限
     *
     * @param permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions(String[] permissions) {
        Random random = new Random();
        requestCode = random.nextInt(1000);
        List<String> deniedPermissions = getDeniedPermissions(this, permissions);
        if (deniedPermissions.size() > 0) {
            //没有授权过，去申请一下
            requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
        } else {
            //都已经授权过了
            if (mOnPermissionListener != null)
                mOnPermissionListener.onPermissionGranted();
            if (!isFinishing()) {
                finish();
            }
        }
    }

    /**
     * 请求权限结果
     */
    public void requestPermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode != -1) {
            if (verifyPermissions(grantResults)) {
                //都授权了
                if (mOnPermissionListener != null)
                    mOnPermissionListener.onPermissionGranted();
            } else {
                //有一个未授权或者多个未授权
                if (mOnPermissionListener != null)
                    mOnPermissionListener.onPermissionDenied();
            }
        }
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //申请权限结果返回
        requestPermissionsResult(requestCode, grantResults);
    }

    /**
     * 获取请求权限中需要授权的权限,有的可能已经授权过了
     */
    public static List<String> getDeniedPermissions(Context context, String[] permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    /**
     * 验证所有权限是否都已经授权
     */
    private static boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOnPermissionListener = null;
    }

    /**
     * 启动activity，并带些必要参数过来
     *
     * @param context
     * @param permissions 申请权限列表
     * @param listener    结果回调
     */
    public static void start(Context context, String[] permissions, PermissionUtil.OnPermissionListener listener) {
        Intent intent = new Intent(context, CheckPermissionActivity.class);
        intent.putExtra("permissions", permissions);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        transferOnPermissionListener = listener;
    }
}