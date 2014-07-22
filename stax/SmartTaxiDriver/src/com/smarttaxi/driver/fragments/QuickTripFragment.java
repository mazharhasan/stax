package com.smarttaxi.driver.fragments;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.Fragment;
import android.view.View;

import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.interfaces.HttpResponseListener;

public class QuickTripFragment extends Fragment implements OnClickListener,
		android.view.View.OnClickListener, HttpResponseListener {

	@Override
	public void onResponse(CustomHttpResponse object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
