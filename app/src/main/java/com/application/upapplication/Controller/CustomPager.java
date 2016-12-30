package com.application.upapplication.Controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.application.upapplication.Views.ChatListFragment;
import com.application.upapplication.Views.ProfileFragment;
import com.application.upapplication.Views.SwipeFragment;


/**
 * Created by user on 12/27/2016.
 */

public class CustomPager extends FragmentStatePagerAdapter {
    int tabCount;

    public CustomPager(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                ChatListFragment chatListFragment = new ChatListFragment();
                return chatListFragment;
            case 1:
                SwipeFragment swipeFragment = new SwipeFragment();
                return swipeFragment;
            case 2:
                ProfileFragment profileFragment = new ProfileFragment();
                return profileFragment;
            case 3:
                ProfileFragment profileFragment1 = new ProfileFragment();
                return profileFragment1;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
