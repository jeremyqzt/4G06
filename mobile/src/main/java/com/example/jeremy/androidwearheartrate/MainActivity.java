package com.example.jeremy.androidwearheartrate;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;


import com.example.jeremy.androidwearheartrate.adapter.TabsPagerAdapter;


public class MainActivity extends FragmentActivity {

    TabsPagerAdapter mAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get adapter
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), MainActivity.this);

        //get viewpager
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mAdapter);

        //attach tablayout to adapter
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

}