package com.lovechatapp.chat.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lovechatapp.chat.adapter.CityPickerRecyclerAdapter;
import com.lovechatapp.chat.base.KeyValue;
import com.lovechatapp.chat.layoutmanager.PickerLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class Picker<T> implements PickerLayoutManager.OnSelectedViewListener {

    private RecyclerView recyclerView;
    private int selectedIndex = -1;
    private List<KeyValue<String, T>> data = new ArrayList<>();
    private List<String> beans = new ArrayList<>();
    private CityPickerRecyclerAdapter adapter;
    private PickerLayoutManager.OnSelectedViewListener onSelectedViewListener;

    public Picker(RecyclerView recyclerView) {

        this.recyclerView = recyclerView;

        adapter = new CityPickerRecyclerAdapter(recyclerView.getContext());

        PickerLayoutManager layoutManager = new PickerLayoutManager(
                recyclerView.getContext(),
                recyclerView,
                PickerLayoutManager.VERTICAL,
                false,
                5,
                0.3f,
                true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        layoutManager.setOnSelectedViewListener(this);
    }

    public void setListData(List<KeyValue<String, T>> data) {
        this.data.clear();
        this.data.addAll(data);
        beans.clear();
        for (KeyValue<String, T> datum : data) {
            beans.add(datum.getKey());
        }
        notifyChanged();
    }

    public void setData(List<T> list) {
        data.clear();
        beans.clear();
        if (list == null)
            return;
        for (int i = 0, l = list.size(); i < l; i++) {
            KeyValue<String, T> keyValue = createKeyValue(list.get(i), i);
            data.add(keyValue);
            beans.add(keyValue.getKey());
        }
        notifyChanged();
    }

    private void notifyChanged() {
        if (beans.size() > 0) {
            selectedIndex = 0;
            adapter.loadData(beans);
            recyclerView.scrollToPosition(selectedIndex);
            onSelectedView(null, selectedIndex);
        } else {
            selectedIndex = -1;
        }
    }

    /**
     * 重写更改显示的string
     *
     * @param t
     * @param position
     * @return
     */
    public KeyValue<String, T> createKeyValue(T t, int position) {
        if (t instanceof String) {
            return new KeyValue<>((String) t, t);
        }
        return new KeyValue<>("", t);
    }

    @Override
    public void onSelectedView(View view, int position) {
        selectedIndex = position;
        if (onSelectedViewListener != null)
            onSelectedViewListener.onSelectedView(view, position);
    }

    public void setOnSelectedViewListener(PickerLayoutManager.OnSelectedViewListener onSelectedViewListener) {
        this.onSelectedViewListener = onSelectedViewListener;
    }

    public T getSelectedValue() {
        if (selectedIndex < 0 || selectedIndex >= data.size())
            return null;
        return data.get(selectedIndex).getValue();
    }

    public String getSelectedKey() {
        if (selectedIndex < 0 || selectedIndex >= data.size())
            return null;
        return data.get(selectedIndex).getKey();
    }
}