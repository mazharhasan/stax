package com.smart.taxi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.smarttaxi.client.R;


/*
 NOTE: You have to give this app permission to access
 the internet!!! See the AndroidManifest.xml file
 */




public class MainActivity extends BaseActivity{
    

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy); 
        setContentView(R.layout.activity_main);
        launchApp();
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	Log.e("onResume", "App resumed");
    }

	private void launchApp() {
		
		Thread logoTimer = new Thread() {
            public void run(){
                try{
                    int logoTimer = 0;
                    while(logoTimer <= 3000){
                        sleep(1000);
                        logoTimer += 1000;
                    };
                    
                    
                    //startService(new Intent(getApplicationContext(), ServiceLocation.class));
                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                    intent.putExtra("to_courage", "booga booga booga");
                    startActivity(intent);
                   // startActivity(new Intent("com.smarttaxi.driver.circular.pagination.control"));
         
                    //startActivity(new Intent("com.smarttaxi.driver.activities.drawer.current.trip"));
                    
                   // startActivity(new Intent("com.smarttaxi.driver.circular.pager.activity"));
                    
                    
                    
                } 
                 
                catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                 
                finally{
                    finish();
                }
            }
        };
         
        logoTimer.start();
	}
    
    
}

