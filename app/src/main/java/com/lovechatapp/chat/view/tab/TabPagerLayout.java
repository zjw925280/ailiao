package com.lovechatapp.chat.view.tab;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * 自定义页面标签父布局
 */
public class TabPagerLayout extends HorizontalScrollView implements
        ViewGroup.OnHierarchyChangeListener, View.OnClickListener {

    private int currentSelected = -1;

    private LinearLayout linearLayout;

    private ViewPager viewPager;

    public TabPagerLayout(Context context) {
        super(context);
        init();
    }

    public TabPagerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setScrollBarSize(0);
        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);
        setClipChildren(false);
        linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.LEFT);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        linearLayout.setOnHierarchyChangeListener(this);
        super.addView(linearLayout, layoutParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public final void init(ViewPager viewPager) {
        this.currentSelected = -1;
        this.viewPager = viewPager;
        this.viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setSelected(position);
            }
        });
        linearLayout.removeAllViews();
        if (!(viewPager.getAdapter() instanceof TabFragmentAdapter)) {
            return;
        }
        TabFragmentAdapter pagerAdapter = (TabFragmentAdapter) viewPager.getAdapter();
        int count = pagerAdapter.getCount();
        for (int i = 0; i < count; i++) {
            FragmentParam fragmentParam = pagerAdapter.getFragmentParam(i);
            TabPagerViewHolder viewHolder = fragmentParam.getViewHolder();
            if (viewHolder != null) {
                viewHolder.itemView.setTag(i);
                addView(viewHolder.itemView);
                viewHolder.init(fragmentParam.getName());
            }
        }
        setSelected(this.viewPager.getCurrentItem());
    }

    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {

    }

    @Override
    public void addView(View child) {
        linearLayout.addView(child);
    }

    public final void setGravity(int gravity) {
        linearLayout.setGravity(gravity);
    }

    public void setSelected(int selected) {
        if (currentSelected == selected || selected < 0) {
            return;
        }

        View selectedView = null;
        int childSize = linearLayout.getChildCount();
        for (int i = 0; i < childSize; i++) {
            View view = linearLayout.getChildAt(i);
            view.setSelected(i == selected);
            if (i == selected)
                selectedView = view;
        }
        viewPager.setCurrentItem(selected);
        if (!(viewPager.getAdapter() instanceof TabFragmentAdapter)) {
            return;
        }

        TabFragmentAdapter pagerAdapter = (TabFragmentAdapter) viewPager.getAdapter();
        if (currentSelected >= 0) {
            TabPagerViewHolder holderLast = pagerAdapter.getFragmentParam(currentSelected).getViewHolder();
            if (holderLast != null) {
                holderLast.unSelected();
            }
        }
        TabPagerViewHolder holderCurrent = pagerAdapter.getFragmentParam(selected).getViewHolder();
        if (holderCurrent != null) {
            holderCurrent.onSelected();
        }

        currentSelected = selected;

        final View finalSelectedView = selectedView;
        post(() -> {
            requestLayout();
            if (finalSelectedView != null) {
                smoothScrollTo(finalSelectedView.getLeft() + finalSelectedView.getWidth() / 2 - getWidth() / 2, 0);
            }
        });
    }

    @Override
    public void onChildViewAdded(View parent, View child) {
        if (child.getId() == NO_ID) {
            child.setId(View.generateViewId());
        }
        child.setOnClickListener(this);
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {

    }

    @Override
    public void onClick(View v) {
        int index = (int) v.getTag();
        TabFragmentAdapter pagerAdapter = (TabFragmentAdapter) viewPager.getAdapter();
        if (pagerAdapter != null) {
            pagerAdapter.getFragmentParam(index).getViewHolder().onClick();
        }
        setSelected(index);
    }

    public ViewGroup getViewGroup() {
        return linearLayout;
    }

    public ViewPager getContentVp() {
        return viewPager;
    }
}