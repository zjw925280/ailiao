package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.helper.ShareUrlHelper;
import com.lovechatapp.chat.util.share.IShare;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.OnItemClickListener;
import com.lovechatapp.chat.view.recycle.ViewHolder;

import java.util.List;


/**
 * 分享dialog
 */
public class ShareDialog extends Dialog {

    private List<ShareInfo> list;
    private Activity activity;

    public ShareDialog(@NonNull Activity context, List<ShareInfo> list) {
        super(context, R.style.DialogStyle_Dark_Background);
        this.list = list;
        activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_share);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        RecyclerView recyclerView = findViewById(R.id.content_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        AbsRecycleAdapter adapter = new AbsRecycleAdapter(new AbsRecycleAdapter.Type(R.layout.item_share, ShareInfo.class)) {
            @Override
            public void convert(ViewHolder holder, Object t) {
                ShareInfo bean = (ShareInfo) t;
                holder.<TextView>getView(R.id.share_tv).setText(bean.name);
                holder.<TextView>getView(R.id.share_tv).setCompoundDrawablesWithIntrinsicBounds(0, bean.drawId, 0, 0);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setDatas(list);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object obj, int position) {
                ShareInfo bean = (ShareInfo) obj;
                bean.iShare.share(activity);
                dismiss();
            }
        });

        ShareUrlHelper.getShareUrl(null);
    }

    public static class ShareInfo {

        int drawId;
        String name;
        IShare iShare;

        public ShareInfo(int drawId, String name, IShare iShare) {
            this.drawId = drawId;
            this.name = name;
            this.iShare = iShare;
        }
    }

}