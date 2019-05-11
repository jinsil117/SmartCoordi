package com.pickth.dddd.smartcoordination;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pickth.dddd.smartcoordination.cloth.ClothesFragment;

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
                CoordiFragment laundryFragment = new CoordiFragment();
                return laundryFragment;
            case 1:
                ClothesFragment clothesFragmentHE = new ClothesFragment();
                return clothesFragmentHE;
            case 2:
                LookbookFragment lookbookFragment = new LookbookFragment();
                return lookbookFragment;
            case 3:
                HistoryFragment_dev historyFragment = new HistoryFragment_dev();
                return historyFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}