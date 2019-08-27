package com.allever.security.photo.browser.ui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class FragmentPageAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;
    private List<String> mTitles;

    public FragmentPageAdapter(FragmentManager fm, List<Fragment> data) {
        super(fm);
        mFragments = data;
    }

    @Override
    public Fragment getItem(int arg0) {
        if (mFragments != null) {
            return mFragments.get(arg0);
        }
        return null;
    }

    @Override
    public int getCount() {
        if (mFragments != null) {
            return mFragments.size();
        }
        return 0;
    }

    public void setData(ArrayList<Fragment> data) {
        mFragments = data;
    }

    public List<Fragment> getData() {
        return mFragments;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null) {
            return mTitles.get(position);
        }
        return super.getPageTitle(position);
    }

    public void setTitles(List<String> titles) {
        mTitles = titles;
    }
}
