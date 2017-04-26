package com.yyy.xxx.airspace;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by len on 2017. 3. 13..
 * ViewPagerAdapter
 */

public class ViewPageAdapter extends FragmentPagerAdapter {

    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // Returning the current tabs
        switch (position) {
            case 0:
                return BoardFragment.newInstance(null);
            case 1:
                return MapFragment.newInstance(null,null);
<<<<<<< HEAD
            case 2:
                return NotiFragment.newInstance(null,null);
=======
//            case 2:
//                return NotiFragment.newInstance(null,null);
>>>>>>>  - 쓸모없는 부분 정리 / 1차 완
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
