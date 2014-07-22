package com.smarttaxi.driver.custom.design;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.smarttaxi.driver.R;
import com.smarttaxi.driver.adapters.CircularFragmentAdapter;
import com.smarttaxi.driver.entities.Pick;
import com.viewpagerindicator.CirclePageIndicator;

public class CircularPaginationControl extends CircularPaginationBaseActivity {
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_circles);

        
    
        mAdapter = new CircularFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

        //We set this on the indicator, NOT the pager
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Toast.makeText(CircularPaginationControl.this, "Changed to page " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    
   
}