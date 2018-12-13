package com.simsimhan.promissu;

import com.simsimhan.promissu.promise.PromiseFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 2;

    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    @Nullable
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
            case 1:
                return PromiseFragment.newInstance(position, position == 0 ? "약속": "지난 약속");
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return position == 0 ? "약속": "지난 약속";
    }
}
