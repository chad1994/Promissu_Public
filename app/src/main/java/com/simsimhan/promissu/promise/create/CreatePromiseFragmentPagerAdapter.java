package com.simsimhan.promissu.promise.create;

import com.simsimhan.promissu.promise.PromiseFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class CreatePromiseFragmentPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 3;

    public CreatePromiseFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    @Nullable
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
            case 1:
            case 2:
                return CreatePromiseFragment.newInstance(position, (String) getPageTitle(position));
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
        return (position +1 ) + " 단계";
    }
}
