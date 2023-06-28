package com.lovechatapp.chat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.YearPickerRecyclerAdapter;
import com.lovechatapp.chat.layoutmanager.PickerLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 年月选择器
 */
public class YearMonthChooserDialog extends Dialog {

    private String mSelectYear;
    private String mSelectMonth;

    private int startYear;
    private int endYear;

    public YearMonthChooserDialog(@NonNull Context context) {
        super(context, R.style.DialogStyle_Dark_Background);

        Calendar calendar = Calendar.getInstance();
        endYear = calendar.get(Calendar.YEAR);
        startYear = endYear - 1;

        mSelectYear = endYear + "";
        mSelectMonth = (calendar.get(Calendar.MONTH) + 1) + "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_year_month_picker_layout);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(false);

        setDialogView();
    }

    /**
     * 设置city picker dialog view
     */
    private void setDialogView() {
        final List<String> years = new ArrayList<>();
        for (int i = startYear; i <= endYear; i++) {
            years.add(i + getContext().getString(R.string.year));
        }
        final List<String> month = new ArrayList<>();
        month.add("1月");
        month.add("2月");
        month.add("3月");
        month.add("4月");
        month.add("5月");
        month.add("6月");
        month.add("7月");
        month.add("8月");
        month.add("9月");
        month.add("10月");
        month.add("11月");
        month.add("12月");
        //左边
        YearPickerRecyclerAdapter leftAdapter = new YearPickerRecyclerAdapter(getContext());
        RecyclerView left_rv = findViewById(R.id.left_rv);
        PickerLayoutManager leftManager = new PickerLayoutManager(getContext(),
                left_rv, PickerLayoutManager.VERTICAL, false, 3, 0.3f, true);
        left_rv.setLayoutManager(leftManager);
        left_rv.setAdapter(leftAdapter);
        leftAdapter.loadData(years);

        //右边
        final YearPickerRecyclerAdapter rightAdapter = new YearPickerRecyclerAdapter(getContext());
        RecyclerView right_rv = findViewById(R.id.right_rv);
        PickerLayoutManager rightManager = new PickerLayoutManager(getContext(),
                right_rv, PickerLayoutManager.VERTICAL, false, 3, 0.3f, true);
        right_rv.setLayoutManager(rightManager);
        right_rv.setAdapter(rightAdapter);
        rightAdapter.loadData(month);

        ImageView close_iv = findViewById(R.id.close_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        leftManager.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                String select = years.get(position);
                String[] result = select.split(getContext().getResources().getString(R.string.year));
                if (result.length > 0) {
                    mSelectYear = result[0];
                }
            }
        });

        rightManager.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                String select = month.get(position);
                String[] result = select.split(getContext().getResources().getString(R.string.month));
                if (result.length > 0) {
                    mSelectMonth = result[0];
                }
            }
        });

        //选中当前年份
        left_rv.scrollToPosition(left_rv.getAdapter().getItemCount() - 1);
        //选中当前月份
        right_rv.scrollToPosition(Integer.parseInt(mSelectMonth) - 1);

        //确定
        TextView confirm_tv = findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                choose();
            }
        });
    }

    public final String getSelectMonth() {
        return mSelectMonth;
    }

    public final String getSelectYear() {
        return mSelectYear;
    }

    public void choose() {

    }
}