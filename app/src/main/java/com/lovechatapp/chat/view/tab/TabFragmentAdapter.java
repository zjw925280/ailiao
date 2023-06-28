package com.lovechatapp.chat.view.tab;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.lovechatapp.chat.base.BaseFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabFragmentAdapter extends FragmentPagerAdapter {

    private final List<FragmentParam> fragmentParams;

    private final SparseArray<Fragment> fragmentMap;

    private final ViewPager viewPager;

    private int lastSelected = -1;

    public TabFragmentAdapter(FragmentManager fm, ViewPager viewPager) {
        super(fm);

        //更新adapter
        if (viewPager.getAdapter() != null && viewPager.getAdapter() instanceof TabFragmentAdapter) {
            TabFragmentAdapter adapter = (TabFragmentAdapter) viewPager.getAdapter();
            if (adapter.fragmentMap != null && adapter.fragmentMap.size() > 0) {
                FragmentTransaction transaction = fm.beginTransaction();
                for (int i = 0; i < adapter.fragmentParams.size(); i++) {
                    Fragment fragment = adapter.fragmentMap.get(i);
                    if (fragment != null) {
                        transaction.remove(fragment);
                    }
                }
                transaction.commitNow();
            }
        }

        this.fragmentMap = new SparseArray<>();
        this.fragmentParams = new ArrayList<>();
        this.viewPager = viewPager;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public long getItemId(int position) {
        return fragmentParams.get(position).hashCode();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragmentMap.get(position);
        if (fragment == null) {
            try {
                FragmentParam param = fragmentParams.get(position);
                fragment = param.getClazz().newInstance();
                if (param.getBundle() != null)
                    fragment.setArguments(param.getBundle());
                if (fragment instanceof BaseFragment) {
                    ((BaseFragment) fragment).bindTab(param.getViewHolder());
                }
                fragmentMap.put(position, fragment);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            if (fragment == null) {
                throw new RuntimeException(fragmentParams.get(position).getClazz() + " newInstance failed!");
            }
        }
        return fragment;
    }

    public FragmentParam getFragmentParam(int position) {
        return fragmentParams.get(position);
    }

    @Override
    public int getCount() {
        return fragmentParams.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentParams.get(position).getName();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    public Fragment getCurrentFragment() {
        return getFragment(viewPager.getCurrentItem());
    }

    public Fragment getFragment(Class clazz) {
        for (int i = 0; i < fragmentParams.size(); i++) {
            if (fragmentParams.get(i).getClazz() == clazz)
                return getFragment(i);
        }
        return null;
    }

    public Fragment getFragment(int position) {
        return getItem(position);
    }

    private void initViewPager() {
        viewPager.setAdapter(this);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setSelected(position);
            }
        });
    }

    private void setSelected(int position) {

        if (lastSelected == position)
            return;

        Fragment fragment = getFragment(position);

        if (lastSelected >= 0) {
            Fragment lastFragment = getFragment(lastSelected);
            if (lastFragment instanceof BaseFragment) {
                ((BaseFragment) lastFragment).setSelected(false);
            }
        }

        if (fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).setSelected(true);
        }
        lastSelected = position;
        viewPager.setCurrentItem(lastSelected);
    }

    /**
     * 加载fragment
     */
    public void init(int defaultSelected, FragmentParam... fragmentParams) {
        if (fragmentParams != null) {
            init(defaultSelected, Arrays.asList(fragmentParams));
        }
    }

    public void init(int defaultSelected, List<FragmentParam> list) {
        if (list != null) {
            this.fragmentParams.addAll(list);
        }
        initViewPager();
        setSelected(defaultSelected);
    }

    public void init(FragmentParam... fragmentParams) {
        init(0, fragmentParams);
    }
}