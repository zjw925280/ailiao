package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.MansionUserInfoBean;
import com.lovechatapp.chat.bean.MultipleChatInfo;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.PageRequester;
import com.lovechatapp.chat.net.RefreshHandler;
import com.lovechatapp.chat.net.RefreshPageListener;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 邀请加入1v2
 */
public class Invite1v2Dialog extends Dialog implements View.OnClickListener {

    private Activity activity;

    private PageRequester<MansionUserInfoBean> requester;

    protected SmartRefreshLayout mRefreshLayout;

    private MultipleChatInfo chatInfo;

    public Invite1v2Dialog(@NonNull Activity context, MultipleChatInfo chatInfo) {
        super(context);
        activity = context;
        this.chatInfo = chatInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_mansion_invite);

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        win.setGravity(Gravity.BOTTOM);
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.setAttributes(lp);

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        findViewById(R.id.cancel_btn).setOnClickListener(this);

        mRefreshLayout = findViewById(R.id.refreshLayout);

        RecyclerView recyclerView = findViewById(R.id.content_rv);

        final String[] onlineText = {"在线", "忙碌", "离线"};

        final int[] bgResourceId = {
                R.drawable.corner_solid_green,
                R.drawable.corner_solid_red,
                R.drawable.corner_solid_gray86};

        final AbsRecycleAdapter adapter = new AbsRecycleAdapter(
                new AbsRecycleAdapter.Type(R.layout.item_multiple_room_invite, MansionUserInfoBean.class)) {

            @Override
            public void convert(ViewHolder holder, Object t) {
                MansionUserInfoBean bean = (MansionUserInfoBean) t;
                Glide.with(getContext())
                        .load(bean.t_handImg)
                        .transform(new CircleCrop())
                        .into(holder.<ImageView>getView(R.id.head_iv));
                holder.<TextView>getView(R.id.nick_name).setText(bean.t_nickName);
                holder.<TextView>getView(R.id.online_tv).setText(onlineText[bean.t_onLine]);
                holder.<TextView>getView(R.id.online_tv).setBackgroundResource(bgResourceId[bean.t_onLine]);
                holder.<TextView>getView(R.id.invite_btn).setBackgroundResource(
                        bean.isInvited ? R.drawable.corner_solid_main : R.drawable.corner_solid_red);
                holder.<TextView>getView(R.id.invite_btn).setText(bean.isInvited ? "已邀请" : "邀请");
            }

            @Override
            public void setViewHolder(final ViewHolder viewHolder) {
                viewHolder.getView(R.id.invite_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MansionUserInfoBean bean = (MansionUserInfoBean) getData().get(viewHolder.getRealPosition());
                        if (!bean.isInvited) {
                            invite(bean);
                        }
                    }
                });
            }

        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        final TextView emptyView = findViewById(R.id.empty_tv);

        requester = new PageRequester<MansionUserInfoBean>() {

            @Override
            public void onSuccess(List<MansionUserInfoBean> list, boolean isRefresh) {
                if (!isShowing()) {
                    return;
                }
                adapter.setData(list, isRefresh);
                if (adapter.getData() == null || adapter.getData().size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                emptyView.setVisibility(View.GONE);
            }
        };
        requester.setApi(ChatApi.getMansionHouseVideoList());
        requester.setParam("mansionRoomId", chatInfo.mansionRoomId);
        requester.setParam("mansionId", chatInfo.mansionId);
        requester.setOnPageDataListener(new RefreshPageListener(mRefreshLayout));

        mRefreshLayout.setOnRefreshListener(new RefreshHandler(requester));
        mRefreshLayout.setOnLoadMoreListener(new RefreshHandler(requester));

        final EditText editText = findViewById(R.id.search_et);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                requester.setParam("searchUserId", s.toString().trim());
            }

        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    mRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRefreshLayout.autoRefresh();
                        }
                    }, 500);
                    return true;
                }
                return false;
            }
        });

        requester.onRefresh();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel_btn) {
            dismiss();
        }
    }

    /**
     * 府邸房间用户呼叫主播
     * chatType 1:视频 2:语音
     * mansionRoomId 当前多人房间号Id
     * coverLinkUserId 主播ID
     */
    private void invite(final MansionUserInfoBean bean) {
        final ProgressBar progressBar = findViewById(R.id.progress_bar);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("coverLinkUserId", bean.t_id);
        paramMap.put("mansionRoomId", chatInfo.mansionRoomId);
        paramMap.put("chatType", chatInfo.chatType);
        OkHttpUtils.post().url(ChatApi.launchMansionVideoChat())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Integer>>() {

            @Override
            public void onResponse(BaseResponse<Integer> response, int id) {
                if (!isShowing() || response == null) {
                    return;
                }
                if (response.m_istatus == 1) {
                    ToastUtil.INSTANCE.showToast("邀请已发送");
                    bean.isInvited = true;
                    RecyclerView recyclerView = findViewById(R.id.content_rv);
                    if (recyclerView.getAdapter() != null) {
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(response.m_strMessage);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (!isShowing()) {
                    return;
                }
                ToastUtil.INSTANCE.showToast("邀请失败");
            }

            @Override
            public void onBefore(Request request, int id) {
                if (!isShowing()) {
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAfter(int id) {
                if (!isShowing()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}