package com.lovechatapp.chat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.KeyValue;
import com.lovechatapp.chat.layoutmanager.PickerLayoutManager;
import com.lovechatapp.chat.util.JsonUtil;
import com.lovechatapp.chat.view.Picker;

import java.util.ArrayList;
import java.util.List;

public abstract class CityPickerDialog {

    private Context context;

    private Picker<List<String>> pickerProvincial;
    private Picker<String> pickerCity;

    public CityPickerDialog(Context context) {
        this.context = context;
    }

    public abstract void onSelected(String city, String city2);

    /**
     * 显示城市选择dialog
     */
    public void show() {
        final Dialog mDialog = new Dialog(context, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_city_picker_layout, null);
        setCityPickerDialogView(view, mDialog);
        mDialog.setContentView(view);
        Window window = mDialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    /**
     * 设置city picker dialog view
     */
    private void setCityPickerDialogView(View view, final Dialog mDialog) {

        //获取city数据
        String cityStr = JsonUtil.getJson(context, "city.json");
        JSONObject jsonObject = JSON.parseObject(cityStr);
        JSONArray provinces = jsonObject.getJSONArray("provinces");

        List<KeyValue<String, List<String>>> provincesList = new ArrayList<>();

        for (int i = 0; i < provinces.size(); i++) {
            JSONObject object = provinces.getJSONObject(i);
            String cityName = object.getString("name");

            KeyValue<String, List<String>> keyValue = new KeyValue<String, List<String>>(cityName, new ArrayList<String>());

            JSONArray jsonArray = object.getJSONArray("citys");

            for (int j = 0; j < jsonArray.size(); j++) {
                String secondName = (String) jsonArray.get(j);
                keyValue.getValue().add(secondName);
            }

            provincesList.add(keyValue);

        }

        //右边
        RecyclerView right_rv = view.findViewById(R.id.right_rv);
        pickerCity = new Picker<>(right_rv);

        //左边
        RecyclerView left_rv = view.findViewById(R.id.left_rv);
        pickerProvincial = new Picker<>(left_rv);
        pickerProvincial.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                pickerCity.setData(pickerProvincial.getSelectedValue());
            }
        });
        pickerProvincial.setListData(provincesList);
        pickerProvincial.onSelectedView(null, 0);
        //取消
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        //确定
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.dismiss();

                String selectedStr1 = pickerProvincial.getSelectedKey();
                String selectedStr2 = pickerCity.getSelectedValue();
                onSelected(selectedStr1, selectedStr2);
            }
        });
    }
}