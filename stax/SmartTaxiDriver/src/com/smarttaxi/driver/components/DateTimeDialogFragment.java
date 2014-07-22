package com.smarttaxi.driver.components;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.smarttaxi.driver.R;
import com.smarttaxi.driver.interfaces.OnDateTimeSelectedListener;

@SuppressLint("ValidFragment")
public class DateTimeDialogFragment extends DialogFragment implements OnDateChangedListener, OnTimeChangedListener {
    // Define constants for date-time picker.
    public final static int DATE_PICKER = 1;
    public final static int TIME_PICKER = 2;
    public final static int DATE_TIME_PICKER = 3;
    
    AlertDialog alertDialog;

    // DatePicker reference
    private DatePicker datePicker;

    // TimePicker reference
    private TimePicker timePicker;

    // Calendar reference
    private Calendar mCalendar;

    // Define activity
    private Activity activity;

    // Define Dialog type
    private int DialogType;

    // Define Dialog view
    private View mView;
    
    OnDateTimeSelectedListener onDateTimeSelectedListener;
    
    
	public DateTimeDialogFragment() {
    	
    }

    // Constructor start
    public DateTimeDialogFragment(Activity activity) {
        this(activity, DATE_TIME_PICKER);
    }
    
    
    public void setOnDateTimeSelectedListener(
			OnDateTimeSelectedListener onDateTimeSelectedListener) {
		this.onDateTimeSelectedListener = onDateTimeSelectedListener;
	}

    

    public DateTimeDialogFragment(Activity activity, int DialogType) {
        this.activity = activity;
        this.DialogType = DialogType;

        // Inflate layout for the view
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = activity.getLayoutInflater();
        mView = inflater.inflate(R.layout.date_time_dialog, null);  

        // Grab a Calendar instance
        mCalendar = Calendar.getInstance();

        // Init date picker
        datePicker = (DatePicker) mView.findViewById(R.id.DatePicker);
        datePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);

        // Init time picker
        timePicker = (TimePicker) mView.findViewById(R.id.TimePicker);
//        timePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
//        timePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        
        // Set default Calendar and Time Style
        setIs24HourView(false);
        setCalendarViewShown(false);

        switch (DialogType) {
        case DATE_PICKER:
            timePicker.setVisibility(View.GONE);
            break;
        case TIME_PICKER:
            datePicker.setVisibility(View.GONE);
            break;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
    	if(alertDialog != null)
    		return alertDialog;
        Builder builder = new AlertDialog.Builder(activity);
        
        // Set the layout for the dialog
        builder.setView(mView);

        // Set title of dialog
        builder.setMessage("Set Date")
                // Set Ok button
                .setPositiveButton("Set",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               if(onDateTimeSelectedListener != null)
                            	   onDateTimeSelectedListener.onDateTimeSelected(getDateTimeMillis());
                            }
                        })
                // Set Cancel button
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                DateTimeDialogFragment.this.getDialog().cancel();
                            }
                        }); 

        // Create the AlertDialog object and return it
        alertDialog =  builder.create();
        return alertDialog;
    }
  
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        timePicker.setOnTimeChangedListener(this);
    }

    // Convenience wrapper for internal Calendar instance
    public int get(final int field) {
        return mCalendar.get(field);
    }

    // Convenience wrapper for internal Calendar instance
    public long getDateTimeMillis() {
        return mCalendar.getTimeInMillis();
    }

    // Convenience wrapper for internal TimePicker instance
    public void setIs24HourView(boolean is24HourView) {
        timePicker.setIs24HourView(is24HourView);
    }

    // Convenience wrapper for internal TimePicker instance
    public boolean is24HourView() {
        return timePicker.is24HourView();
    }

    // Convenience wrapper for internal DatePicker instance
    public void setCalendarViewShown(boolean calendarView) {
        //datePicker.setCalendarViewShown(calendarView);
    }

    // Convenience wrapper for internal DatePicker instance
    @SuppressLint("NewApi")
	public boolean CalendarViewShown() {
//    	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB )
//    		return datePicker.getCalendarViewShown();
    	return false;
    }

    // Convenience wrapper for internal DatePicker instance
    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        datePicker.updateDate(year, monthOfYear, dayOfMonth);
    }

    // Convenience wrapper for internal TimePicker instance
    public void updateTime(int currentHour, int currentMinute) {
        timePicker.setCurrentHour(currentHour);
        timePicker.setCurrentMinute(currentMinute);
    }

    public String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss Z", Locale.US);
        return sdf.format(mCalendar.getTime());
    }

    // Called every time the user changes DatePicker values
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // Update the internal Calendar instance
        mCalendar.set(year, monthOfYear, dayOfMonth, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
    }

    // Called every time the user changes TimePicker values
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        // Update the internal Calendar instance
        mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
    }
}
