package com.app.youcheng.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.app.youcheng.base.BaseFragment;

import java.util.List;

public class MyfragmentPagerAdpter extends FragmentPagerAdapter {
    private List<BaseFragment> fragments;
    private String[] tabTitles;

    public MyfragmentPagerAdpter(FragmentManager fm, List<BaseFragment> fragments, String[] tabTitles) {
        super(fm);
        this.fragments = fragments;
        this.tabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (tabTitles == null || tabTitles.length == 0) return super.getPageTitle(position);
        return tabTitles[position];
    }
}
