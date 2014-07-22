package com.smarttaxi.driver.helpers;

import android.os.CountDownTimer;
import android.widget.TextView;

public class TimerHelper extends CountDownTimer{
	 
	TextView remTime;
	public TimerHelper(long millisInFuture, long countDownInterval,TextView rem) {
        super(millisInFuture, countDownInterval);
        remTime = rem;
    }
	

    @Override
    public void onFinish() {
        System.out.println("Timer Completed.");
        //tv.setText("Timer Completed.");
    }

    @Override
    public void onTick(long millisUntilFinished) {
        //tv.setText((millisUntilFinished/1000)+"");
    	remTime.setText((millisUntilFinished/1000)+"");
        System.out.println("Timer  : " + (millisUntilFinished/1000));
    }
}
