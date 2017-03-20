package com.example.jeremy.androidwearheartrate.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.jeremy.androidwearheartrate.HealthFragment;
import com.example.jeremy.androidwearheartrate.SummaryFragment;
import com.example.jeremy.androidwearheartrate.VehicleFragment;

/**
 * Created by kevind on 2017-02-25.
 */

public class TabsPagerAdapter extends FragmentPagerAdapter{
    final int PAGE_COUNT = 3;
    private String tabs[] = new String[]{"Summary","Health","Vehicle"};
    private Context context;
    private Bundle mbundle;

    public TabsPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int index){
//        return PageFragment.newInstance(index+1);
        Log.d("myTest",Integer.toString(index));
        switch (index){
            case 0:
                SummaryFragment sTab = new SummaryFragment();
                mbundle = new Bundle();
                sTab.setArguments(mbundle);
                return sTab;
            case 1:
                HealthFragment hTab = new HealthFragment();
                hTab.setArguments(mbundle);
                return hTab;
            case 2:
                VehicleFragment vTab = new VehicleFragment();
                vTab.setArguments(mbundle);
                return vTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return PAGE_COUNT;
    }

    public CharSequence getPageTitle(int index){
        return tabs[index];
    }

}
