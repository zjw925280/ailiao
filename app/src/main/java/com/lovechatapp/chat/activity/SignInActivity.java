package com.lovechatapp.chat.activity;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.SigninDayFriendRecyclerAdapter;
import com.lovechatapp.chat.adapter.SigninDayRecyclerAdapter;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.SiginBean;
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
import okhttp3.Call;

public class SignInActivity extends BaseActivity {

    @BindView(R.id.tv_day_num)
    TextView tv_day_num;
    @BindView(R.id.rv_signin)
    RecyclerView rv_signin;
    @BindView(R.id.rv_signin_friend)
    RecyclerView rv_signin_friend;
    @BindView(R.id.tv_day)
    TextView tv_day;

    private SigninDayRecyclerAdapter signinDayRecyclerAdapter;
    private SigninDayFriendRecyclerAdapter signinDayFriendRecyclerAdapter;
    private   List<SigninDayBean.RowsBean> dayNumList=new ArrayList<>();
    private   List<SiginBean.SiginInRecordBean> dayFriendNumList=new ArrayList<>();

    @NotNull
    @Override
    protected View getContentView() {
        return  inflate(R.layout.activity_sign_in);
    }

    @Override
    protected void onContentAdded() {
        setTitle("签到日历");
        initRecyclerView(this);
        signIn();
    }


    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView(Context context) {
        //设置签到RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,6,LinearLayoutManager.VERTICAL,false);
        rv_signin.setLayoutManager(gridLayoutManager);
        signinDayRecyclerAdapter = new SigninDayRecyclerAdapter(context,dayNumList);
        rv_signin.setAdapter(signinDayRecyclerAdapter);

        //设置已经签到好友RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rv_signin_friend.setLayoutManager(linearLayoutManager);
        signinDayFriendRecyclerAdapter = new SigninDayFriendRecyclerAdapter(context,dayFriendNumList);
        rv_signin_friend.setAdapter(signinDayFriendRecyclerAdapter);
        rv_signin_friend.setNestedScrollingEnabled(false);
    }
    public void signIn() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", AppManager.getInstance().getUserInfo().t_id);
        Log.e("签到","请求参数=="+new Gson().toJson(params)+" url=="+ChatApi.SIGIN_NOW());
        OkHttpUtils
                .post()
                .url(ChatApi.SIGIN_NOW())
                .addParams("param", ParamUtil.getParam(params))
                .build()
                .execute(new AjaxCallback<BaseResponse<String>>() {
                    @Override
                    public void onResponse(BaseResponse<String> response, int id) {
                        dayNumList.clear();
                        Log.e("签到","返回数据=="+response.m_object);
                        SiginBean siginBean = new Gson().fromJson(response.m_object, SiginBean.class);
                        tv_day.setText(siginBean.getDay()+"");
                        List<SigninDayBean.RowsBean> rows = siginBean.getSignInList();
                        for (int a=0;a<rows.size();a++){
                            if (rows.get(a).getDay()<=siginBean.getDay()){
                                rows.get(a).setSignIn(true);
                            }else {
                                rows.get(a).setSignIn(false);
                            }
                        }
                        tv_day_num.setText(rows.get(siginBean.getDay() - 1).getGold()+"");
                        dayNumList.addAll(rows);
                        signinDayRecyclerAdapter.notifyDataSetChanged();
                        List<SiginBean.SiginInRecordBean> signInRecord =siginBean.getSignInRecord();
                        for (int b=0;b<20;b++){
                            SiginBean.SiginInRecordBean siginInRecordBean = signInRecord.get(b);
                            dayFriendNumList.add(siginInRecordBean);
                        }
                        signinDayFriendRecyclerAdapter.notifyDataSetChanged();
                    }
                });

//        ToastUtil.INSTANCE.showToast(ChatApi.SIGNIN_LIST());
//        Map<String, Object> params1 = new HashMap<>();
//        params1.put("userId", AppManager.getInstance().getUserInfo().t_id);
//        OkHttpUtils
//                .post()
//                .url(ChatApi.SIGNIN_LIST())
//                .addParams("param", ParamUtil.getParam(params1))
//                .build()
//                .execute(new AjaxCallback<BaseResponse<SigninDayBean>>() {
//                    @Override
//                    public void onResponse(BaseResponse<SigninDayBean> response, int id) {
//                        m_object = response.m_object;
//                        tv_day.setText(m_object.getDay()+"");
//                        List<SigninDayBean.RowsBean> rows = m_object.getRows();
//                        tv_day_num.setText(rows.get(m_object.getDay() - 1).getGold()+"");
//                        signinDayRecyclerAdapter.loadData(rows);
//                    }
//                });

    }



}
