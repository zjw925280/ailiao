package com.lovechatapp.chat.view.tab;

import android.text.TextUtils;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.fragment.HomeCityFragment;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;

/**
 * 同城
 */
public class LabelCityViewHolder extends LabelViewHolder {

    private TextView textView;

    public LabelCityViewHolder(TabPagerLayout viewGroup) {
        super(viewGroup);
        textView = (TextView) itemView;
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                R.drawable.home_city_arrow_unselected,
                R.drawable.selector_bottom_indicator_red_trans);
    }

    @Override
    protected void init(String title) {
        super.init(title);
        String city = SharedPreferenceHelper.getCity(textView.getContext());
        if (!TextUtils.isEmpty(city)) {
            textView.setText(city);
        }
    }

    @Override
    protected void onSelected() {
        super.onSelected();
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                R.drawable.home_city_arrow_selected,
                R.drawable.selector_bottom_indicator_red_trans);
    }

    @Override
    protected void unSelected() {
        super.unSelected();
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                R.drawable.home_city_arrow_unselected,
                R.drawable.selector_bottom_indicator_red_trans);
    }

    public void showChanged(boolean b) {
        if (HomeCityFragment.city != null) {
            textView.setText(HomeCityFragment.city);
        }
    }
}