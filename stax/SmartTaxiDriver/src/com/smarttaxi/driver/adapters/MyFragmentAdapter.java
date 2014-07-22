package com.smarttaxi.driver.adapters;

import com.smarttaxi.driver.custom.design.RequestDialogFragment;
import com.smarttaxi.driver.entities.PickupRequestCollection;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MyFragmentAdapter extends FragmentPagerAdapter {
	
	
	public static int count=2;
	private PickupRequestCollection pickupCollection;
	
	public MyFragmentAdapter(FragmentManager fm, PickupRequestCollection pickupcollection) {
		super(fm);
		this.pickupCollection = pickupcollection;
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return pickupCollection.GetTotalRequest();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		//return RequestDialogFragment.newInstance(position);
		return null;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		return super.instantiateItem(container, position);
	}
}

