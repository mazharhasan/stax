package com.smarttaxi.driver.activities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.smarttaxi.driver.R;
import com.smarttaxi.driver.R.style;
import com.smarttaxi.driver.adapters.JourneyRequestAdapter;
import com.smarttaxi.driver.entities.PickupRequest;
import com.smarttaxi.driver.fragments.FragmentJourney;
import com.smarttaxi.driver.utils.Applog;
import com.viewpagerindicator.CirclePageIndicator;

public class JourneyRequestDialog extends DialogFragment implements
		ViewPagerItemNotify {
	
	private final String TAG = JourneyRequestDialog.this.getClass().getSimpleName();
	@Override
	public void dismissAllowingStateLoss() {
		// TODO Auto-generated method stub
		
		Applog.Error(TAG +"="+ "dismissAllowingStateLoss()");
		/*fragmentCollection=null;
		journeyRequest=null;
		requestPager=null;
		journeyRequestAdapter=null;
		notifyDialoge=null;
		mIndicator=null;*/
		super.dismissAllowingStateLoss();
		
		
	}

	public static int requestCounter;
	public static final int JOURNEY_REJECTION_QUEUE = 2;
	public static final int JOURNEY_ACCEPTED = 1;
	public static final int JOURNEY_REJECTED = 0;

	private ViewPagerDialogeNotifier notifyDialoge;
	private CirclePageIndicator mIndicator;
	// private ViewPagerItemNotify notifyItem;
	private ViewPager requestPager;

	private JourneyRequestAdapter journeyRequestAdapter;

	List<Fragment> fragmentCollection;
	PickupRequest journeyRequest;

	public void initiatePagerDialoge(PickupRequest request,
			ViewPagerDialogeNotifier _notify) {
		requestCounter = 0;
		this.notifyDialoge = _notify;
		this.journeyRequest = request;
		fragmentCollection = new ArrayList<Fragment>();
	}

	public void addItemInPagerDialoge(PickupRequest request) {

		this.journeyRequest = request;
		addNewItem(request);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(DialogFragment.STYLE_NORMAL, style.JourneyRequestTheme);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    //No call for super(). Bug on API Level > 11.
		//super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Applog.Error(TAG +"="+ "onCreateViewPager()");
		View view = inflater.inflate(R.layout.custom_request_pager, container);
		requestPager = (ViewPager) view.findViewById(R.id.request_pager);

		if (journeyRequest != null) {
			// pass data in getFragments main
			fragmentCollection = getFragments(journeyRequest);

			if (fragmentCollection != null)
				journeyRequestAdapter = new JourneyRequestAdapter(
						getChildFragmentManager(), fragmentCollection);

			requestPager.setAdapter(journeyRequestAdapter);
			mIndicator = (CirclePageIndicator) view
					.findViewById(R.id.page_indicator);
			mIndicator.setViewPager(requestPager);
			mIndicator.notifyDataSetChanged();

			getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		}
		return view;
	}

	private void addNewItem(PickupRequest request) {
		
		Applog.Error(TAG +"="+ "addNewItem()");
		fragmentCollection.add(FragmentJourney.newInstance(request,
				requestCounter++, request.getjourneyId(),this));
		journeyRequestAdapter.notifyDataSetChanged();
		mIndicator.notifyDataSetChanged();

		requestPager.refreshDrawableState();
		requestPager.invalidate();
	}

	public void removePage(int position, String journeyId) {

		if (fragmentCollection.size() == 1) {
			if (notifyDialoge != null) {
				notifyDialoge.rejectJourney(journeyId);
				notifyDialoge.closeDialog();
				return;
			}
		}

		else if (fragmentCollection.size() > 1) {
			int tempPosition = position;
			/*
			 * if(tempPosition>=mEntries.size()) { tempPosition
			 * =mEntries.size()-1; position =mEntries.size()-1; }
			 */

			if (tempPosition > 0)
				// if(mEntries.get(--tempPosition)!=null)
				requestPager.setCurrentItem(tempPosition, true);
			else
				requestPager.setCurrentItem(tempPosition, true);

			fragmentCollection.remove(position);
			journeyRequestAdapter.notifyDataSetChanged();
			mIndicator.notifyDataSetChanged();

			if(notifyDialoge!=null)
			notifyDialoge.rejectJourney(journeyId);

		}
		requestPager.refreshDrawableState();
		requestPager.invalidate();

	}
	
	

	private List<Fragment> getFragments(PickupRequest request) {
		// List<Fragment> fList = new ArrayList<Fragment>();

		// pass requestresponse in place of 1,2,3
		if (requestCounter == 0)
			fragmentCollection.add(FragmentJourney.newInstance(request,
					requestCounter++,request.getjourneyId(), this));

		/*
		 * fragmentCollection.add(FragmentJourney.newInstance("224", 1, this));
		 * fragmentCollection.add(FragmentJourney.newInstance("225", 2, this));
		 * fragmentCollection.add(FragmentJourney.newInstance("226", 3, this));
		 * fragmentCollection.add(FragmentJourney.newInstance("227", 4, this));
		 * fragmentCollection.add(FragmentJourney.newInstance("228", 5, this));
		 * fragmentCollection.add(FragmentJourney.newInstance("229", 6, this));
		 */

		return fragmentCollection;
	}

	@Override
	public void itemSelectedonPager(int actionCode, String journey_id,
			int itemPosition) {
		// TODO Auto-generated method stub

		int currentItemIndex = requestPager.getCurrentItem();
		switch (actionCode) {
		case JOURNEY_REJECTED:

			Applog.Error("JOURNEY_REJECTED");
			/*
			 * Toast.makeText( getActivity(), "Journey= " + journey_id +
			 * "of fr=" + currentItemIndex + " Rejecting",
			 * Toast.LENGTH_LONG).show();
			 */
			if (notifyDialoge != null) {
				// _notify.removeFragment(index);

				// removePage(index);
				removePage(currentItemIndex, journey_id);
			}
			break;

		case JOURNEY_ACCEPTED:
			/*
			 * Toast.makeText( getActivity(), "Journey= " + journey_id +
			 * "of fr=" + currentItemIndex + " accepting",
			 * Toast.LENGTH_LONG).show();
			 */
			
					
			if(fragmentCollection.size()==1)
			{
				if (notifyDialoge != null) {
					notifyDialoge.closeDialog();
					notifyDialoge.actionCompletedOnDialoge(journey_id);
					
				}
			}
			else
			{
				fragmentCollection.remove(currentItemIndex);
				if (notifyDialoge != null)
				{
					notifyDialoge.actionCompletedOnDialoge(journey_id);
					journeyRequestAdapter.notifyDataSetChanged();
					mIndicator.notifyDataSetChanged();
						/*if( fragmentCollection.size()>=1) 
				notifyDialoge.rejectsRemainingJourneys(fragmentCollection);*/
				}
			
			}
			
			break;

		case JOURNEY_REJECTION_QUEUE:
			/*
			 * Toast.makeText( getActivity(), "Journey= " + journey_id +
			 * "of fr=" + currentItemIndex + " accepting",
			 * Toast.LENGTH_LONG).show();
			 */

			Applog.Error("JOURNEY_REJECTION_QUEUE");
			// index
			removePage(itemPosition, journey_id);
			/*
			 * if(fragmentCollection.size() >1) {
			 * 
			 * if (notifyDialoge != null) {
			 * notifyDialoge.rejectJourney(journey_id);
			 * 
			 * }
			 * 
			 * }
			 */
			break;

		default:
			break;
		}
	}

}
