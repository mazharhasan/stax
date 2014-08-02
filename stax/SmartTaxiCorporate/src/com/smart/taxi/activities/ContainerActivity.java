/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smart.taxi.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.smart.taxi.BarcodeActivity;
import com.smart.taxi.GcmIntentService;
import com.smarttaxi.client.R;
import com.smart.taxi.adapters.LeftMenuAdapter;
import com.smart.taxi.components.CFTextView;
import com.smart.taxi.entities.TripDetails;
import com.smart.taxi.fragments.ChangeTipFragment;
import com.smart.taxi.fragments.CurrentTripFragment;
import com.smart.taxi.fragments.FindARideFragment;
import com.smart.taxi.fragments.ProfileFragment;
import com.smart.taxi.fragments.TripHistoryFragment;
import com.smart.taxi.fragments.TripRequestFragment;
import com.smart.taxi.preferences.PreferencesHandler;

/**
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
 */
public class ContainerActivity extends BaseActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mNavigationLabels;
	private LinearLayout mDrawer;
	public TripRequestFragment tripRequestRef;
	private LeftMenuAdapter leftMenuAdapter;
	private CFTextView mCurrentUserName;
	private static Fragment lastFragment;
	private static int lastIndex = 0;
	public static ContainerActivity container;
	public static boolean isRestoringCurrentTrip = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        container = this;
        setContentView(R.layout.activity_container);
        try{
        	registerReceiver(mHandleMessageReceiver, new IntentFilter(GcmIntentService.DISPLAY_MESSAGE_ACTION));
        }catch(Exception ex){}
        mTitle = mDrawerTitle = "Preparing...";
        mNavigationLabels = getResources().getStringArray(R.array.navigation_labels);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawer = (LinearLayout) findViewById(R.id.drawerLayout);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        //leftMenuAdapter = new LeftMenuAdapter(this);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNavigationLabels));
        /*mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.id.txtNavTitle, mNavigationLabels));*/
        //mDrawerList.setAdapter(leftMenuAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mCurrentUserName = (CFTextView) findViewById(R.id.txtCurrentUserName);
        if(SplashActivity.isLoggedIn())
        {
        	mCurrentUserName.setText("Welcome " + SplashActivity.loggedInUser.getFullName() + "!");
        }

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setIcon(R.drawable.btn_menu_normal);
        getActionBar().setTitle("Preparing...");
        
        

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        //mDrawerLayout.setDrawerListener(mDrawerToggle);

    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        	Log.e("bundle received as: ", bundle.getString("score"));
    	selectItem(lastIndex);
    }
    
    @Override
    public void onBackPressed() {
    	if(lastIndex != 0)
    	{
    		selectItem(0);
    	}else{
    		super.onBackPressed();
    	}
    }

    

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
       boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_websearch:
            // create intent to perform web search for this planet
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
            // catch event that there's no activity to handle intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	/*for(int i = 0; i < mNavigationLabels.length; i++)
        	{
        		if(i == position)
        		{
        			mDrawerList.getChildAt(i).setBackgroundColor(0xffc300);
        		}else{
        			mDrawerList.getChildAt(i).setBackgroundColor(0x111111);
        		}
        	}*/
            selectItem(position);
        }
    }

    public void selectItem(int position) {
    	Fragment fragment;
    	String tag = null;
    	if(lastIndex == position && lastFragment instanceof TripRequestFragment && SplashActivity.isTripRequested)
    	{
    		if(tripRequestRef != null)
    		{
    			tripRequestRef.reset(null);
    		}
    		return;
    	}
    	else {
    		if(position != 5)
    			lastIndex = position;
			switch (position) {
	    	case 0:
	    		if(SplashActivity.isTripRequested && SplashActivity.getTripNewDetails() != null)
	    		{
	    			fragment = new TripRequestFragment();
	    			tag = TripRequestFragment.TAG;
	    		}else{
		    		fragment = new FindARideFragment();
		    		tag = FindARideFragment.TAG;
	    		}
	    		break;
	    	case 1:
	    		fragment = new CurrentTripFragment();
	    		tag = CurrentTripFragment.TAG;
	    		break;
			case 2:
				fragment = new TripHistoryFragment();
				tag = TripHistoryFragment.TAG;
				break;
				
			case 3:
				fragment = new ProfileFragment();
				tag = ProfileFragment.TAG;
				break;
				
			case 4:
				fragment = new ChangeTipFragment();
				tag = ChangeTipFragment.TAG;
				break;
				
			case 5:
				lastIndex = 0;
				Intent intent = new Intent(this, BarcodeActivity.class);
				startActivity(intent);
				//finish();
				return;
				
			case 6:
				SplashActivity.doLogoutAction = true;
				SplashActivity.logout();
				lastIndex = 0;
				lastFragment = null;
				finish();
				return;

			default:
				mDrawerLayout.closeDrawer(mDrawer);
				return;
			}
	    	lastFragment = fragment;

			FragmentManager manager = getFragmentManager();
			
			manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_ENTER_MASK).replace(R.id.content_frame, fragment).addToBackStack(tag).commit();
			mDrawerList.setItemChecked(position, true);
	        setTitle(mNavigationLabels[position]);
	    	mDrawerLayout.closeDrawer(mDrawer);
		}
    	
    	
        // update the main content by replacing fragments
        /*Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mNavigationLabels[position]);
        mDrawerLayout.closeDrawer(mDrawerList);*/
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, Intent intent) {
			// String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			try {
				PreferencesHandler pf = new PreferencesHandler(getApplicationContext());
				int rid = intent.getExtras().getInt("rid");
				if(rid > 0)
				{
					NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					nManager.cancel(rid);
				}
				String journeyId = intent.getExtras().getString("journeyId");
				String status = intent.getExtras().getString("type");
				String message = intent.getExtras().getString("message");
				Log.e("inMessageHandler", message);
				if(status.equals("accept"))
				{
					if(tripRequestRef != null && tripRequestRef.txtTripStatus != null)
					{
						SplashActivity.getTripNewDetails().setTripStatus(TripDetails.ACCEPTED);
						pf.saveCurrentTrip(SplashActivity.getTripNewDetails());
						tripRequestRef.txtTripStatus.setText("Driver confirmed");
						tripRequestRef.startListeneingForDriverPosition();
					}
				}else if(status.equals("cancel"))
				{
					pf.clearTrip();
					tripRequestRef.stopListeningForDriverPosition();
					SplashActivity.isTripRequested = false;
	        		SplashActivity.setTripNewDetails(null);
	        		setResult(1, message, null);
	        		//setResultCode(1);
	        		finish();
				}else if(status.equals("beep"))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(ContainerActivity.this);
					builder.setTitle("Driver arrived")
						.setCancelable(false)
						.setPositiveButton("Ok", null)
						.setNegativeButton("Close",null);
					builder.create();
					builder.show();
					SplashActivity.getTripNewDetails().setTripStatus(TripDetails.ARRIVED);
					pf.saveCurrentTrip(SplashActivity.getTripNewDetails());
					tripRequestRef.stopListeningForDriverPosition();
				}else if(status.equals("reject"))
				{
					pf.clearTrip();
					if(SplashActivity.getTripNewDetails().isPostCheckedIn())
					{
						//TODO: show markers
					}else{
						tripRequestRef.stopListeningForDriverPosition();
						SplashActivity.isTripRequested = false;
		        		SplashActivity.setTripNewDetails(null);
		        		setResult(2, message, null);
		        		setResultCode(2);
		        		finish();
					}
				}else if(status.equals("complete"))
				{
					pf.clearTrip();
					tripRequestRef.stopListeningForDriverPosition();
					SplashActivity.isTripRequested = false;
	        		SplashActivity.setTripNewDetails(null);
	        		Intent invoiceActivityIntent = new Intent(container, InvoiceActivity.class);
	        		invoiceActivityIntent.putExtra("journeyId", journeyId);
	        		startActivity(invoiceActivityIntent);
				}
			}catch(Exception ex){
				
				Log.e("Exception", ex.toString());
				
			}
			
		}
	};
	
	@Override
	protected void onDestroy()
	{
		try{
			unregisterReceiver(mHandleMessageReceiver);
		}catch(Exception exception)
		{
			Log.i("Tag", "Register not removed");
		}
		super.onDestroy();
	}
}