package com.smarttaxi.driver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.smarttaxi.driver.R;



public class ImageSplashActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
         
        Thread logoTimer = new Thread() {
            public void run(){
                try{
                    int logoTimer = 0;
                    while(logoTimer < 500){
                        sleep(100);
                        logoTimer = logoTimer +100;
                    };
                    
                    
              
                    startActivity(new Intent("com.smarttaxi.driver.activities.first.activity"));
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
