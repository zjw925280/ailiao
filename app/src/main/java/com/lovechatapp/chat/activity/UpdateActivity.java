package com.lovechatapp.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.lovechatapp.chat.BuildConfig;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.bean.UpdateBean;
import com.lovechatapp.chat.util.ToastUtil;

public class UpdateActivity extends Activity {

    private UpdateBean updateBean;

    public static void start(UpdateBean updateBean) {

        //过滤正在启动的过程
        if (AppManager.getInstance().getActivityObserve().isSplash()) {
            return;
        }

        Intent starter = new Intent(AppManager.getInstance(), UpdateActivity.class);
        starter.putExtra("data", updateBean);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppManager.getInstance().startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this).statusBarDarkFont(true).navigationBarColor(R.color.black).init();

        setContentView(R.layout.dialog_update_layout);

        getWindow().setBackgroundDrawable(new ColorDrawable(0x3f000000));
        updateBean = (UpdateBean) getIntent().getSerializableExtra("data");

        //描述
        TextView des_tv = findViewById(R.id.des_tv);
        des_tv.setText(updateBean.t_version_depict);

        //版本
        TextView title_tv = findViewById(R.id.title_tv);
        String version = updateBean.t_version;
        String content;
        if (!TextUtils.isEmpty(version)) {
            content = getResources().getString(R.string.new_version_des_one) + version;
        } else {
            content = getString(R.string.new_version_des);
        }
        title_tv.setText(content);

        //更新
        final TextView update_tv = findViewById(R.id.update_tv);
        update_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(updateBean.t_download_url)) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(updateBean.t_download_url);
                        intent.setData(content_url);
                        startActivity(intent);
                    } catch (Exception e) {
                        ToastUtil.INSTANCE.showToast("跳转失败");
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(R.string.get_download_url_fail_one);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (BuildConfig.DEBUG) {
            super.onBackPressed();
        }
    }

}