package com.lovechatapp.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.ActorInfoBean;
import com.lovechatapp.chat.bean.ChargeBean;
import com.lovechatapp.chat.bean.CoverUrlBean;
import com.lovechatapp.chat.bean.InfoRoomBean;
import com.lovechatapp.chat.bean.LabelBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.fragment.PersonInfoFragment;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.view.tab.FragmentParamBuilder;
import com.lovechatapp.chat.view.tab.TabFragmentAdapter;
import com.lovechatapp.chat.view.viewpager.YViewPager;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 用户&主播资料页
 */
public class PersonInfoActivity extends BaseActivity {

    @BindView(R.id.load_pb)
    ProgressBar loadPb;

    @BindView(R.id.error_btn)
    View errorBtn;

    int otherId;

    private ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> mActorInfoBean;

    public static void start(Context context, int otherId) {
        Intent starter = new Intent(context, PersonInfoActivity.class);
        starter.putExtra(Constant.ACTOR_ID, otherId);
        context.startActivity(starter);
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_actor_pager);
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);

        otherId = getIntent().getIntExtra(Constant.ACTOR_ID, otherId);

        errorBtn.setOnClickListener(v -> getActorInfo());

        getActorInfo();
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    /**
     * 获取数据后加载布局
     */
    private void initFragment() {

        mBaseContent.removeAllViews();
        mBaseContent.addView(inflate(R.layout.activity_userinfo_pager));

        YViewPager pagerVv = findViewById(R.id.pager_vv);

        TabFragmentAdapter tabFragmentAdapter = new TabFragmentAdapter(getSupportFragmentManager(), pagerVv);

//        if (TextUtils.isEmpty(mActorInfoBean.t_addres_url)) {

        tabFragmentAdapter.init(
                FragmentParamBuilder.create()
                        .withClazz(PersonInfoFragment.class)
                        .build()

        );
//        } else {
//
//            tabFragmentAdapter.init(
//
//                    FragmentParamBuilder.create()
//                            .withClazz(VideoPlayFragment.class)
//                            .build(),
//
//                    FragmentParamBuilder.create()
//                            .withClazz(PersonInfoFragment.class)
//                            .build()
//
//            );
//        }

        pagerVv.setAdapter(tabFragmentAdapter);
    }

    /**
     * 获取主播资料
     */
    private void getActorInfo() {

        loadPb.setVisibility(View.VISIBLE);
        errorBtn.setVisibility(View.GONE);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("coverUserId", otherId);
        OkHttpUtils.post().url(ChatApi.GET_ACTOR_INFO())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean>>>() {

            @Override
            public void onResponse(BaseResponse<ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean>> response, int id) {
                Log.e("主播数据","主播数据="+new Gson().toJson(response));
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    mActorInfoBean = response.m_object;
                    initFragment();
                } else {
                    onError(null, null, 0);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (isFinishing()) {
                    return;
                }
                loadPb.setVisibility(View.GONE);
                errorBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    public final ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> getBean() {
        return mActorInfoBean;
    }

    @OnClick(R.id.back_iv)
    public void onClick() {
        finish();
    }
}