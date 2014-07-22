package com.smart.taxi.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.provider.Settings.Secure;
import android.util.TypedValue;

public class Utils {
	
	static String deviceID = "";
	static String osVersion = "";
	static String mobileAppVersion = "";

	public static boolean isEmptyOrNull(String string) {
		if(string == null)
			return true;
		
		return (string.trim().length()<=0);			
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isEmptyOrNull(ArrayList arrayList) {
		if(arrayList == null)
			return true;
		
		return (arrayList.size()<=0);		
	}
	
	public static String getFormatedTimeString() {
		  
		  String Day = getDay();
		  
		  SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy hh:mm aa");
		  String dateString = Day + ", " +
		    formatter.format(new Date(System.currentTimeMillis()));
		  
		  return dateString;
		  // TODO Auto-generated method stub
		  
		 }
	
	 private static String getDay() {
		  // TODO Auto-generated method stub
		  String weekDay = "";

		     Calendar c = Calendar.getInstance();
		     int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

		     switch(dayOfWeek) 
		     {
		     case Calendar.MONDAY :
		         weekDay = "Mon";
		         break;
		     case Calendar.TUESDAY:
		      weekDay = "Tue";
		      break;
		     case Calendar.WEDNESDAY :
		         weekDay = "Wed";
		         break;
		     case Calendar.THURSDAY:
		      weekDay = "Thur";
		      break;
		     case Calendar.FRIDAY :
		         weekDay = "Fri";
		         break;
		     case Calendar.SATURDAY:
		      weekDay = "Sat";
		      break;
		     case Calendar.SUNDAY :
		         weekDay = "Sun";
		         break;
		     }
		     
		     return weekDay;
		 }
	
	 public static String getAddressString(Double lat, Double lng, Context context) {
		  // TODO Auto-generated method stub
		  
		  String Address = "";
		  
		  Geocoder geocoder;
		  List<Address> addresses = new ArrayList<Address>();
		  geocoder = new Geocoder(context);
		  try {
		   addresses = geocoder.getFromLocation(lat, lng, 1);
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }

		  String address = addresses.get(0).getAddressLine(0);
		  String city = addresses.get(0).getAddressLine(1);
		  String country = addresses.get(0).getAddressLine(2);
		  
		  Address = address+ " " + city + ", " + country;
		  
		  return Address;
		 }
	 
	 
	public static String validateEmptyString(String string) {
		return Utils.validateEmptyString(string,"");
	}
	
	public static String validateEmptyString(String string,String defaultValue) {
		if(Utils.isEmptyOrNull(string)) 
			return defaultValue;
		
		return string;
	}
	
	public static String getDeviceID(Context context) {
		if(isEmptyOrNull(deviceID)) {
			deviceID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		}
		return deviceID;
	}
	
	public static String getOSVersion() {
		if(isEmptyOrNull(osVersion)) {
			osVersion = "Android " +  Build.VERSION.RELEASE;
		}
		return osVersion;
	}
	
	public static String getMobileAppVersion(Context context) {
		if(isEmptyOrNull(mobileAppVersion)) {		
			try {
				PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
				mobileAppVersion = pInfo.versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return mobileAppVersion;
	}
	
	public static int getPixelsFromDps(int dp, Context context) {
		Resources r = context.getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
	}
	
	public static String getFormattedDate(int secondsToAdd, String dateFormat) {
		long time = System.currentTimeMillis();
		time = time + (secondsToAdd*1000);
		Date date = new Date(time);
		SimpleDateFormat postFormater = new SimpleDateFormat(dateFormat); 
        return postFormater.format(date); 
	}
}
