package com.lovechatapp.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.ProtectRankBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 守护榜
 */
public class RankProtectActivity extends BaseActivity {

    public static void start(Context context, int actorId) {
        Intent starter = new Intent(context, RankProtectActivity.class);
        starter.putExtra(Constant.ACTOR_ID, actorId);
        context.startActivity(starter);
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_rank_protect);
    }

    @Override
    protected void onContentAdded() {
        setTitle("守护榜");
        protectRv();
    }

    /**
     * 守护榜
     */
    private void protectRv() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("coverUserId", getIntent().getIntExtra(Constant.ACTOR_ID, 0));
        OkHttpUtils
                .post()
                .url(ChatApi.getUserGuardGiftList())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<BaseResponse<List<ProtectRankBean>>>() {
                    @Override
                    public void onResponse(BaseResponse<List<ProtectRankBean>> response, int id) {

                        if (isFinishing()
                                || response.m_object == null
                                || response.m_object.size() == 0) {
                            return;
                        }

                        List<ProtectRankBean> list = response.m_object;

                        AbsRecycleAdapter adapter = new AbsRecycleAdapter(
                                new AbsRecycleAdapter.Type(R.layout.item_rank_protect, ProtectRankBean.class)) {

                            @Override
                            public void convert(ViewHolder holder, Object t) {
                                ProtectRankBean bean = (ProtectRankBean) t;
                                holder.<TextView>getView(R.id.nick_tv).setText(bean.t_nickName);
                                holder.<TextView>getView(R.id.count_tv).setText(String.valueOf(bean.giftCount));
                                holder.<TextView>getView(R.id.rank_tv).setText(String.valueOf(holder.getRealPosition() + 1));
                                Glide.with(mContext)
                                        .load(bean.t_handImg)
                                        .transform(new CircleCrop())
                                        .into(holder.<ImageView>getView(R.id.head_iv));
                            }

                        };
                        adapter.setDatas(list);
                        RecyclerView recyclerView = findViewById(R.id.content_rv);
                        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        recyclerView.setAdapter(adapter);
                    }
                });
    }

}