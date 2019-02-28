package com.pickth.dddd.smartcoordination;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    // Count number of tabs
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        // Returning the current tabs
        switch (position) {
            case 0:
                ClothesFragment clothesFragment = new ClothesFragment();
                return clothesFragment;
            case 1:
                LookbookFragment lookbookFragment = new LookbookFragment();
                return lookbookFragment;
            case 2:
                HistoryFragment historyFragment = new HistoryFragment();
                return historyFragment;
            case 3:
                LaundryFragment laundryFragment = new LaundryFragment();
                return laundryFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}