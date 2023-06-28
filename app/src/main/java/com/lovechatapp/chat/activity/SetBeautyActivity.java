package com.lovechatapp.chat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.faceunity.fulivedemo.FUBeautyActivity;
import com.gyf.barlibrary.ImmersionBar;
import com.lovechatapp.chat.R;

public class SetBeautyActivity extends FUBeautyActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).statusBarDarkFont(true).navigationBarColor(R.color.black).init();
    }
}