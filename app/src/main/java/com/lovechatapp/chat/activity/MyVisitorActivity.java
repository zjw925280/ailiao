package com.lovechatapp.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.BrowedBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.glide.GlideCircleTransform;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.net.PageRequester;
import com.lovechatapp.chat.net.RefreshHandler;
import com.lovechatapp.chat.util.TimeUtil;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.ViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * 我的访客
 */
public class MyVisitorActivity extends BaseActivity {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private PageRequester<BrowedBean> pageRequester;
    private AbsRecycleAdapter adapter;

    public static void start(Context context, int count) {
        Intent starter = new Intent(context, MyVisitorActivity.class);
        starter.putExtra("count", count);
        context.startActivity(starter);
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_visitor);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.my_visitor);

        initRecycleView();

        pageRequester = new PageRequester<BrowedBean>() {
            @Override
            public void onSuccess(List<BrowedBean> list, boolean isRefresh) {
                if (list.size() == 10) {
                    list.remove(list.size() - 1);
                }
                list.add(0, new BrowedBean());
                adapter.setData(list, isRefresh);
            }
        };
        pageRequester.setApi(ChatApi.getCoverBrowseList());
        pageRequester.setOnPageDataListener(new PageRequester.SimplePageDataListener() {
            @Override
            public void onRefreshEnd() {
                if (refreshLayout != null)
                    refreshLayout.finishRefresh();
            }
        });
        refreshLayout.setOnRefreshListener(new RefreshHandler(pageRequester) {
        });
        pageRequester.onRefresh();
        refreshLayout.setEnableLoadMore(false);
    }

    private void initRecycleView() {
        final RecyclerView recyclerView = findViewById(R.id.content_rv);
        recyclerView.getItemAnimator().setChangeDuration(0);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? gridLayoutManager.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter = new AbsRecycleAdapter(new AbsRecycleAdapter.Type(R.layout.item_visitor, BrowedBean.class)) {

            int color = Color.parseColor("#F5D037");

            @Override
            public void convert(ViewHolder holder, Object t) {

                BrowedBean browedBean = (BrowedBean) t;

                TextView textView = holder.getView(R.id.text_tv);
                textView.setTextColor(getResources().getColor(R.color.gray_868686));

                if (holder.getRealPosition() == 0) {
                    textView.setTextColor(getResources().getColor(R.color.black));
                    String countStr = getIntent().getIntExtra("count", 0) + "";
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(String.format(getString(R.string.visitor_count), countStr));
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(color), 0, countStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    textView.setText(spannableStringBuilder);

                    ImageLoadHelper.glideShowCircleImageWithUrl(MyVisitorActivity.this,
                            AppManager.getInstance().getUserInfo().headUrl, holder.<ImageView>getView(R.id.head_iv), 200, 200);

                } else {
                    textView.setText(TimeUtil.getFormatYMD(browedBean.t_create_time * 1000));

                    Glide.with(mContext)
                            .load(browedBean.t_handImg)
                            .error(R.drawable.default_head_img)
                            .override(200, 200)
                            .transition(DrawableTransitionOptions.withCrossFade(1000))
                            .transform(
                                    new CenterCrop(),
                                    new BlurTransformation(25, 2),
                                    new GlideCircleTransform(mContext))
                            .into(holder.<ImageView>getView(R.id.head_iv));
                }
            }
        });
    }


    @OnClick(R.id.vip_tv)
    public void onClick(View view) {
        VipCenterActivity.start(this, true);
        finish();
    }
}