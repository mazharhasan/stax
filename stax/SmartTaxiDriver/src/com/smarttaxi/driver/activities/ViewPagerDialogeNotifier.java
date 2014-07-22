package com.smarttaxi.driver.activities;

import java.util.List;

import android.support.v4.app.Fragment;

public interface ViewPagerDialogeNotifier {

	public void actionCompletedOnDialoge(String acceptedJourneyId);
	public void rejectJourney(String journeyId);
	//public void rejectsRemainingJourneys(List<Fragment>  otherFragments);
	public void closeDialog();
}
