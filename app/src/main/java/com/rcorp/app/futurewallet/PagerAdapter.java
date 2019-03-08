package com.rcorp.app.futurewallet;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int noOfTabs;

    public PagerAdapter(FragmentManager fm, int noOfTabs)
    {
        super(fm);
        this.noOfTabs=noOfTabs;
    }
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:

                ConfirmedTransaction ct = new ConfirmedTransaction();
                return ct;
            case 1:
                PendingTransaction pt = new PendingTransaction();
                return pt;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
