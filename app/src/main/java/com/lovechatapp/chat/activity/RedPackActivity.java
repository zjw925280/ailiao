package com.lovechatapp.chat.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.ReceiveRedBean;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ImageLoadHelper;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：红包打开页面
 * 作者：
 * 创建时间：2018/6/25
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RedPackActivity extends BaseActivity {

    @BindView(R.id.head_iv)
    ImageView mHeadIv;
    @BindView(R.id.nick_one_tv)
    TextView mNickOneTv;
    @BindView(R.id.des_tv)
    TextView mDesTv;
    @BindView(R.id.gold_number_tv)
    TextView mGoldNumberTv;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_red_pack_layout);
    }

    @Override
    protected int getStatusBarColorResId() {
        return R.color.yellow_f55738;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        initData();
    }

    private void initData() {
        ReceiveRedBean bean = (ReceiveRedBean) getIntent().getSerializableExtra(Constant.RED_BEAN);
        if (bean != null) {
            //头像
            String handImg = bean.t_handImg;
            if (!TextUtils.isEmpty(handImg)) {
                ImageLoadHelper.glideShowCircleImageWithUrl(this, handImg, mHeadIv);
            } else {
                mHeadIv.setImageResource(R.mipmap.ic_launcher);
            }
            //昵称
            mNickOneTv.setText(bean.t_nickName);
            //内容
            mDesTv.setText(bean.t_redpacket_content);
            //约豆
            mGoldNumberTv.setText(String.valueOf(bean.t_redpacket_gold));
        }
    }

    @OnClick({R.id.back_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv: {//返回
                finish();
                break;
            }
        }
    }


}
