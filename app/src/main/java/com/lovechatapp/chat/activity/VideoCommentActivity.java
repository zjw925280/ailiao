package com.lovechatapp.chat.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseBean;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.AVChatBean;
import com.lovechatapp.chat.bean.LabelBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.DensityUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.OnItemClickListener;
import com.lovechatapp.chat.view.recycle.RecycleGridDivider;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   评论页面
 * 作者：
 * 创建时间：2018/8/15
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class VideoCommentActivity extends BaseActivity {

    @BindView(R.id.head_iv)
    ImageView headIv;

    @BindView(R.id.cost_tv)
    TextView costTv;

    @BindView(R.id.nick_tv)
    TextView nickTv;

    @BindView(R.id.star_rb)
    RatingBar mStarRb;

    @BindView(R.id.content_rv)
    RecyclerView recyclerView;

    private int mActorId;

    private AbsRecycleAdapter adapter;

    private AVChatBean avChatBean;

    private final List<LabelBean> mLabelBeans = new ArrayList<>();

    public static void start(AVChatBean avChatBean) {
        calculateGold(avChatBean, new OnCommonListener<Response>() {
            @Override
            public void execute(Response response) {
                Intent starter = new Intent(AppManager.getInstance(), VideoCommentActivity.class);
                starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                starter.putExtra("data", avChatBean);
                starter.putExtra("data2", response);
                AppManager.getInstance().startActivity(starter);
            }
        });
    }

    /**
     * 计算收益/消费
     */
    private static void calculateGold(AVChatBean avChatBean, OnCommonListener<Response> listener) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("roomId", avChatBean.roomId);
        paramMap.put("coverUserId", avChatBean.otherId);
        OkHttpUtils.post().url(ChatApi.getVideoComsumerInfo())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Response>>() {
            @Override
            public void onResponse(BaseResponse<Response> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    listener.execute(response.m_object);
                }
            }
        });
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_comment_layout);
    }

    @Override
    protected void onContentAdded() {

        setTitle(R.string.send_comment);

        avChatBean = (AVChatBean) getIntent().getSerializableExtra("data");
        Response response = (Response) getIntent().getSerializableExtra("data2");

        mActorId = avChatBean.otherId;
        loadImgNick(response);
        adapter = new AbsRecycleAdapter(
                new AbsRecycleAdapter.Type(R.layout.item_comment_label, LabelBean.class),
                new AbsRecycleAdapter.Type(R.layout.item_comment_menu, String.class)) {
            @Override
            public void convert(ViewHolder holder, Object t) {
                TextView textView = (TextView) holder.itemView;
                if (t.getClass() == String.class) {
                    textView.setText(t.toString());
                } else {
                    LabelBean bean = (LabelBean) t;
                    textView.setText(bean.t_label_name);
                    textView.setBackgroundResource(!bean.selected ?
                            R.drawable.corner5_stroke_gray_eb : R.drawable.corner5_stroke_main);
                }
            }
        };

        adapter.setOnItemClickListener(new OnItemClickListener() {

            private int getGoodCount() {
                int count = 0;
                for (LabelBean labelBean : mLabelBeans) {
                    if (labelBean.selected && labelBean.t_id != -1) {
                        count++;
                    }
                }
                return count;
            }

            private void clearCount(boolean good) {
                for (LabelBean labelBean : mLabelBeans) {
                    if (!good || labelBean.t_id == -1) {
                        labelBean.selected = false;
                    }
                }
            }

            @Override
            public void onItemClick(View view, Object obj, int position) {
                if (obj.getClass() == LabelBean.class) {
                    LabelBean bean = (LabelBean) obj;
                    if (bean.t_id == -1) {
                        clearCount(false);
                        bean.selected = true;
                        mStarRb.setRating(4);
                    } else {
                        if (!bean.selected && getGoodCount() >= 3) {
                            ToastUtil.INSTANCE.showToast("最多3个好评");
                            return;
                        }
                        mStarRb.setRating(5);
                        clearCount(true);
                        bean.selected = !bean.selected;
                    }
                    adapter.notifyDataSetChanged();
                }
            }

        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getBaseContext(), 4);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                Object obj = adapter.getData().get(i);
                if (obj.getClass() == LabelBean.class) {
                    return 1;
                }
                return gridLayoutManager.getSpanCount();
            }
        });
        recyclerView.addItemDecoration(new RecycleGridDivider(DensityUtil.dip2px(mContext, 6)));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        getLabelList();
    }

    private void loadImgNick(Response response) {
        nickTv.setText(response.t_nickName);
        Glide.with(this)
                .load(response.t_handImg)
                .transform(new CircleCrop())
                .into(headIv);
        costTv.setText(String.format(avChatBean.isActor() ? "收益%s约豆" : "消费%s约豆", response.t_room_gold));
        costTv.append("\n");
        costTv.append(String.format("通话时长: %s", response.roomTime));
    }

    /**
     * 获取标签列表
     * useType 1:编辑资料  2:评论列表
     */
    private void getLabelList() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", mActorId);
        paramMap.put("useType", 2);
        OkHttpUtils.post().url(ChatApi.GET_LABEL_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<LabelBean>>() {
            @Override
            public void onResponse(BaseListResponse<LabelBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    if (response.m_object != null && response.m_object.size() > 0) {
                        mLabelBeans.clear();
                        mLabelBeans.addAll(response.m_object);
                        List<Object> list = new ArrayList<>();
                        list.add("好评");
                        list.addAll(mLabelBeans);
                        list.add("差评");
                        LabelBean labelBean = new LabelBean();
                        labelBean.t_label_name = "不满意";
                        labelBean.t_id = -1;
                        mLabelBeans.add(labelBean);
                        list.add(labelBean);
                        adapter.setDatas(list);
                    }
                }
            }
        });
    }

    @OnClick({
            R.id.right_text,
            R.id.submit_tv,
            R.id.complain_tv
    })
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.submit_tv: {
                commentActor();
                break;
            }

            case R.id.complain_tv: {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                intent.putExtra(Constant.ACTOR_ID, mActorId);
                startActivity(intent);
                break;
            }

        }
    }

    /**
     * 评价主播
     */
    private void commentActor() {

        if (mLabelBeans.size() == 0) {
            getLabelList();
            ToastUtil.INSTANCE.showToast("获取数据中");
            return;
        }

        List<LabelBean> beans = new ArrayList<>();
        for (LabelBean mLabelBean : mLabelBeans) {
            if (mLabelBean.selected) {
                beans.add(mLabelBean);
            }
        }
        if (beans.size() == 0) {
            ToastUtil.INSTANCE.showToast("请选择评价");
            return;
        }

        if (beans.get(0).t_id == -1) {
            beans.clear();
        }

        //评分
        int number = (int) mStarRb.getRating();

        //评价标签
        StringBuilder labels = new StringBuilder();
        for (LabelBean bean : beans) {
            labels.append(bean.t_id).append(",");
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverCommUserId", mActorId);
        paramMap.put("commScore", number);
        paramMap.put("lables", TextUtils.isEmpty(labels) ? "" : labels.toString());
        OkHttpUtils.post().url(ChatApi.SAVE_COMMENT())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<String>>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.comment_success);
                    finish();
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.comment_fail);
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.comment_fail);
                dismissLoadingDialog();
            }
        });
    }

    static class Response extends BaseBean {
        public int t_room_gold;
        public String roomTime;
        public String t_handImg;
        public String t_nickName;
        public int t_score;
    }

}
