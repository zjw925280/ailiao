package com.lovechatapp.chat.activity;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.GoldItemRecyclerAdapter;
import com.lovechatapp.chat.adapter.SigninDayFriendRecyclerAdapter;
import com.lovechatapp.chat.adapter.SigninDayRecyclerAdapter;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.SigninDayBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.dialog.FreeImDialog;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class SignInActivity extends BaseActivity {

    @BindView(R.id.tv_day_num)
    TextView tv_day_num;
    @BindView(R.id.rv_signin)
    RecyclerView rv_signin;
    @BindView(R.id.rv_signin_friend)
    RecyclerView rv_signin_friend;

    private SigninDayRecyclerAdapter signinDayRecyclerAdapter;
    private SigninDayFriendRecyclerAdapter signinDayFriendRecyclerAdapter;
    private   List<SigninDayBean> dayNumList=new ArrayList<>();
    private   List<String> dayFrinendNumList=new ArrayList<>();
    private int isSigin;

    @NotNull
    @Override
    protected View getContentView() {
        return  inflate(R.layout.activity_sign_in);
    }

    @Override
    protected void onContentAdded() {
        setTitle("签到日历");
        initRecyclerView(this);
    }


    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView(Context context) {
        for(int a=0;a<10;a++){
            if (a<5){
                isSigin=1;
            }else {
                isSigin=0;
            }
            dayNumList.add(new SigninDayBean(isSigin,"第"+a+"天","5"+a));
        }
        //设置签到RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,6,LinearLayoutManager.VERTICAL,false);
        rv_signin.setLayoutManager(gridLayoutManager);
        signinDayRecyclerAdapter = new SigninDayRecyclerAdapter(context);
        rv_signin.setAdapter(signinDayRecyclerAdapter);
        signinDayRecyclerAdapter.loadData(dayNumList);


        for(int a=0;a<10;a++){
            dayFrinendNumList.add("啥玩意"+a);
        }
        //设置已经签到好友RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rv_signin_friend.setLayoutManager(linearLayoutManager);
        signinDayFriendRecyclerAdapter = new SigninDayFriendRecyclerAdapter(context);
        rv_signin_friend.setAdapter(signinDayFriendRecyclerAdapter);
        signinDayFriendRecyclerAdapter.loadData(dayFrinendNumList);





    }

//    public void signIn() {
//        Map<String, Object> params = new HashMap<>();
//        params.put("userId", AppManager.getInstance().getUserInfo().t_id);
//        OkHttpUtils
//                .post()
//                .url(ChatApi.privateLetterNumber())
//                .addParams("param", ParamUtil.getParam(params))
//                .build()
//                .execute(new AjaxCallback<BaseResponse<FreeImDialog.FreeImBean>>() {
//                    @Override
//                    public void onResponse(BaseResponse<FreeImBean> response, int id) {
//                        if (activity == null
//                                || activity.isFinishing()
//                                || response == null
//                                || response.m_object == null) {
//                            return;
//                        }
//                        freeImBean = response.m_object;
//                        if (freeImBean.isCase || freeImBean.isGold) {
//                            super.show();
//                        }
//                    }
//                });
//    }
}
