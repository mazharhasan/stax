package com.smart.taxi.activities;
/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static com.smart.taxi.utils.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.smart.taxi.utils.CommonUtilities.EXTRA_MESSAGE;
import static com.smart.taxi.utils.CommonUtilities.SENDER_ID;
import static com.smart.taxi.utils.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.google.android.gcm.GCMRegistrar;
import com.smart.taxi.R;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.interfaces.HttpResponseListener;
import com.smart.taxi.utils.ServerUtilities;

/**
 * Main UI for the demo app.
 */
public class DemoActivity extends Activity implements OnClickListener, HttpResponseListener {

    TextView mDisplay;
    AsyncTask<Void, Void, Void> mRegisterTask;
    String reg = "";
    EditText ids;
    Button btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkNotNull(SERVER_URL, "SERVER_URL");
        checkNotNull(SENDER_ID, "SENDER_ID");
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
        setContentView(R.layout.main);
        btn = (Button)findViewById(R.id.btn);
        ids = (EditText)findViewById(R.id.eid);
        btn.setOnClickListener(this);
        mDisplay = (TextView) findViewById(R.id.display);
        registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        reg = regId;
        Log.e("Registration id from server", regId);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                mDisplay.append(getString(R.string.already_registered) + "\n");
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
            	/*
            	 * APA91bGbVTAllVSqI74Tl70vpj6AipNOH5BuDYRYW_vgXhmu_oSxJ0zjLCxkh9Ozed78-m5Le1P80srouBjPlpLzX0xY-gU33fskC4UWfpD0aSLPI2BQSflXokMcQEdGo-bnJ4XbDQOgkM1n9s3-yn7N6j86l5spCmwxjCTGsRJ2K1Z6QLDe_8I
 					curl --header "Authorization: key=AIzaSyATl58Nv4mL8nESKNUdHi_smEVOxAKhuVg" --header Content-Type:"application/json" https://android.googleapis.com/gcm/send  -d "{\"registration_ids\":[\"APA91bGbVTAllVSqI74Tl70vpj6AipNOH5BuDYRYW_vgXhmu_oSxJ0zjLCxkh9Ozed78-m5Le1P80srouBjPlpLzX0xY-gU33fskC4UWfpD0aSLPI2BQSflXokMcQEdGo-bnJ4XbDQOgkM1n9s3-yn7N6j86l5spCmwxjCTGsRJ2K1Z6QLDe_8I\"]}"
            	 */
            	
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        ServerUtilities.register(context, regId);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            /*
             * Typically, an application registers automatically, so options
             * below are disabled. Uncomment them if you want to manually
             * register or unregister the device (you will also need to
             * uncomment the equivalent options on options_menu.xml).
             */
            /*
            case R.id.options_register:
                GCMRegistrar.register(this, SENDER_ID);
                return true;
            case R.id.options_unregister:
                GCMRegistrar.unregister(this);
                return true;
             */
            case R.id.options_clear:
                mDisplay.setText(null);
                return true;
            case R.id.options_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(this);
        super.onDestroy();
    }

    private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(
                    getString(R.string.error_config, name));
        }
    }

    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            mDisplay.append(newMessage + "\n");
        }
    };

	@Override
	public void onClick(View v) {
		mDisplay.append("pushing Reg Id to server");
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("device_type", "Android"));
		params.add(new BasicNameValuePair("user_id", ids.getText().toString()));
		params.add(new BasicNameValuePair("udid", reg));
		CustomHttpClass.runPostService(this, "add_udid.json", params, false, false);
		
	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		// TODO Auto-generated method stub
		if(object.getMethodName() == "add_udid.json")
		{
			mDisplay.append("Ready for receiving message");
		}
		
		
	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub
		
	}

}