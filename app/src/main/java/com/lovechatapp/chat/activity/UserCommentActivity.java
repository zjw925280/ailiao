package com.lovechatapp.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.CommentBean;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户评价
 */
public class UserCommentActivity extends BaseActivity {

    public static void start(Context context, String data) {
        Intent starter = new Intent(context, UserCommentActivity.class);
        starter.putExtra("data", data);
        context.startActivity(starter);
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_user_comment);
    }

    @Override
    protected void onContentAdded() {
        setTitle("用户印象");

        List<CommentBean> list = JSON.parseObject(
                getIntent().getStringExtra("data"),
                new TypeReference<List<CommentBean>>() {
                });
        List<String> strings = new ArrayList<>(list.size());
        for (CommentBean commentBean : list) {
            strings.add(commentBean.t_label_name + String.format("(%s)", commentBean.evaluationCount));
        }

        TagFlowLayout flowLayout = findViewById(R.id.flow_view);
        flowLayout.setAdapter(new TagAdapter<String>(strings) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_flow_comment, parent, false);
                tv.setText(s);
                return tv;
            }
        });
    }

}