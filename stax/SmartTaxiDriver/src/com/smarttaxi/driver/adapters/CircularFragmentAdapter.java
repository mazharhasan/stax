package com.smarttaxi.driver.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smarttaxi.driver.R;
import com.smarttaxi.driver.custom.design.TransparentPanel;
import com.smarttaxi.driver.entities.Pick;
import com.viewpagerindicator.IconPagerAdapter;


public class CircularFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter  {
	private static LayoutInflater mInflater;
	
	static TextView test;
	
	//global
	protected static final View[] CONTENT2 = new View[] { test};
    protected static final String[] CONTENT = new String[] { "This", "Is", "A", "Test","taha","umair" };
    protected static final int[] ICONS = new int[] {
			R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher
    };

    private int mCount = CONTENT.length;
    
    
    
	 /* Weather weather_data[] = new Weather[] { new Weather(R.drawable.logo, "Cloudy"), new Weather(R.drawable.logo,"Showers"), new Weather(R.drawable.logo, "Snow"), new
	  Weather(R.drawable.logo, "Storm"), new Weather(R.drawable.logo,
	  "Sunny") };
	 
	  WeatherAdapter adapter = new WeatherAdapter(this,
	  R.layout.listview_item_row, weather_data);*/
	// listView1.setAdapter(adapter);
    Pick req_collection[] = new Pick[] {new Pick("journeyid","userid","username","userImage","","","time","pickLoc","Addmessge") {
   	}};
     
     
   	
   	
   	
       
       public  void getPassengerRequestLayout(Activity activity)
       {
       	
       	
       	mInflater =LayoutInflater.from(activity);
       	
       	//LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
   		View v = mInflater.inflate(R.layout.passenger_request,null);
       	
   		TextView passengerImage = (TextView)v.findViewById(R.id.passengerImage);
   		TextView username = (TextView)v.findViewById(R.id.passengerName);
   		TextView pickAddress = (TextView)v.findViewById(R.id.pickupAddress);
   		TextView pickTime = (TextView)v.findViewById(R.id.pickupTime);
   		TextView additionalMsg = (TextView)v.findViewById(R.id.additionalMessage);
   		
       /*	popup = (TransparentPanel) v.findViewById(R.id.popup_window);


       	//  Start out with the popup initially hidden.
       	popup.setVisibility(View.GONE);
       	*/
       	
       	
       
        

       
           
         
           
   	}
	  
    

    public CircularFragmentAdapter(FragmentManager fm) {
        super(fm);
       
    }

    public Fragment getItem(int position) {
        return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    public CharSequence getPageTitle(int position) {
      return CircularFragmentAdapter.CONTENT[position % CONTENT.length];
    }

    @Override
    public int getIconResId(int index) {
      return ICONS[index % ICONS.length];
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}


