package com.lovechatapp.chat.fragment;

import android.util.Log;
import android.widget.TextView;

import com.lovechatapp.chat.dialog.CityPickerDialog;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.view.tab.TabPagerViewHolder;

/**
 * 同城fragment
 */
public class HomeCityFragment extends HomeContentFragment {

    public static String city = null;

    @Override
    public void bindTab(TabPagerViewHolder viewHolder) {
        super.bindTab(viewHolder);
        viewHolder.setOnClickListener(v -> {
            if (isShowing()) {
                showCityChooser();
            }
        });
    }

    @Override
    protected void beforeGetData() {
        if (city == null) {
            String localCity = SharedPreferenceHelper.getCity(getActivity());
            Log.e("city","localCity=="+localCity);
            requester.setParam("t_city", localCity);
        } else {
            Log.e("city","city=="+city);
            requester.setParam("t_city", city);
            city = null;
        }
    }

    private CityPickerDialog cityPickerDialog;

    private void showCityChooser() {
        if (cityPickerDialog == null) {
            cityPickerDialog = new CityPickerDialog(getActivity()) {
                @Override
                public void onSelected(String city, String city2) {
                    setCity(city2);
                }
            };
        }
        cityPickerDialog.show();
    }

    private void setCity(String city) {
        requester.setParam("t_city", city);
        requester.onRefresh();
        TextView textView = (TextView) tabPagerViewHolder.itemView;
        textView.setText(city);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (city != null) {
            setCity(city);
            city = null;
        }
    }
}