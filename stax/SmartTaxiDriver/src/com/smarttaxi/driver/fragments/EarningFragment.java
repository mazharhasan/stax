package com.smarttaxi.driver.fragments;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.smarttaxi.driver.R;
import com.smarttaxi.driver.activities.EarningList;
import com.smarttaxi.driver.components.DateTimeDialogFragment;
import com.smarttaxi.driver.database.SqliteHelper;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.entities.User;

import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.interfaces.OnDateTimeSelectedListener;
import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.AppToast;
import com.smarttaxi.driver.utils.Applog;

public class EarningFragment extends Fragment implements OnClickListener,
		HttpResponseListener, OnDateTimeSelectedListener {

	private Dialog picker;
	private long userID;
	private SqliteHelper sqliteHelper;
	private static View rootView;
	private Button bttFrom, bttTo, bttCalculate;
	private String from, to;
	private static User user;
	private static Calendar calender;
	private final String LONG_FORMAT = "yyy-MM-dd hh:mm:ss";
	private int SELECT_FROM_DATE = 1, SELECT_TO_DATE = 2;
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater
				.inflate(R.layout.fragment_earning, container, false);

		sqliteHelper = new SqliteHelper(getActivity());
		calender = Calendar.getInstance();
		
		load(rootView);
		setDefaultDates();
		
		return rootView;
	}

	private void setDefaultDates() {
		to = formatCalenderDate(calender);
		
		calender.add(Calendar.DATE, -7);
		from = formatCalenderDate(calender);
		Applog.Debug("Default Dates : from " +from + " to " + to);
		
		bttFrom.setText(from);
		bttTo.setText(to);
	}
	private String formatCalenderDate(Calendar c){
		return  String.valueOf(DateFormat.format(LONG_FORMAT, c.getTimeInMillis()));
	}
	private String formatCalenderDate(long timeInMilliSeconds){
		return  String.valueOf(DateFormat.format(LONG_FORMAT, timeInMilliSeconds));
	}
	private void load(View rootView) {
		PreferencesHandler preferencesHandler = new PreferencesHandler(
				getActivity());
		userID = preferencesHandler.getUserID();
		user = sqliteHelper.getUser(userID);
		if (user == null)
			return;

		bttFrom = (Button) rootView.findViewById(R.id.bttFrom);
		bttTo = (Button) rootView.findViewById(R.id.bttTo);
		bttCalculate = (Button) rootView.findViewById(R.id.bttCalculate);

		bttFrom.setOnClickListener(this);
		bttTo.setOnClickListener(this);
		bttCalculate.setOnClickListener(this);
	}

	@Override
	public void onResponse(CustomHttpResponse object) {

	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub
		if (LoaderHelper.progressDialog != null) {
			if (LoaderHelper.isLoaderShowing()) {
				LoaderHelper.hideLoader();
				AppToast.ShowToast(getActivity(), exception.getMessage());
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bttFrom:
			dateSelectFor_TO  = false;
			onSelectDateTime();
			break;
		case R.id.bttTo:
			dateSelectFor_TO  = true;
			onSelectDateTime();
			break;
		case R.id.bttCalculate:
			Intent intent = new Intent(getActivity(), EarningList.class);
			intent.putExtra("from", from);
			intent.putExtra("to", to);
			intent.putExtra("driver_id", user != null ? user.getDriverID() : "0");
			getActivity().startActivity(intent);
			break;
		default:
			break;
		}
	}
	private DateTimeDialogFragment dateTimeDialogFragment;
	private boolean dateSelectFor_TO  = false;
	
	private void onSelectDateTime() {
		if(dateTimeDialogFragment == null) {
			dateTimeDialogFragment = new DateTimeDialogFragment(getActivity());
			dateTimeDialogFragment.setOnDateTimeSelectedListener(this);
		}
		dateTimeDialogFragment.show(getActivity().getSupportFragmentManager(), "timePicker");		
	}

	@Override
	public void onDateTimeSelected(long dateTimeInMillis) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(dateTimeInMillis);
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0);
		if(dateSelectFor_TO)
			to =  formatCalenderDate(c);
		else
			from = formatCalenderDate(c);
		Applog.Debug("FROM SELECTED : " + from + " TO " + to);
	}

}
