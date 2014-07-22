package com.smarttaxi.driver.adapters;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class JourneyRequestAdapter  extends FragmentStatePagerAdapter {

	

	public List<Fragment> joutneyFragmentCollection;

	public JourneyRequestAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.joutneyFragmentCollection = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return this.joutneyFragmentCollection.get(position);
	}

	@Override
	public int getCount() {
		return this.joutneyFragmentCollection.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "Page " + position;
	}

	@Override
	public int getItemPosition(Object object) {
		return FragmentStatePagerAdapter.POSITION_NONE;
	}

	/*
	 * @Override public void destroyItem(ViewGroup container, int position,
	 * Object object) { super.destroyItem(container, position, object);
	 * FragmentManager manager = ((Fragment)object).getFragmentManager();
	 * android.support.v4.app.FragmentTransaction trans =
	 * manager.beginTransaction();
	 * trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE); //
	 * trans.remove((Fragment)object); trans.commit(); }
	 */
}

