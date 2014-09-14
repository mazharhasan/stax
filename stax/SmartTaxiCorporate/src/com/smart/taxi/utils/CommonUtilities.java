package com.smart.taxi.utils;
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


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;

import com.smart.taxi.constants.APIConstants;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 * {
 * curl --header "Authorization: key=AIzaSyATl58Nv4mL8nESKNUdHi_smEVOxAKhuVg" --header Content-Type:"application/json" https://android.googleapis.com/gcm/send  -d "{\"data\": {\"score\": \"5x1\",\"time\": \"15:10\"}, \"registration_ids\":[\"APA91bF5PRi5E5wGEhcXFSOKcn-VaHYohKCiDRTqd_Xl3BT_QA1GUaaplvzq8nb0DupATnEe0sho5ZsWG1Dz1p1wQY00YllEgSH3RxOhb0Qix4vfCGVJuTFOr15uA3KN68m7CbmvPEdNUQfn29MG5kqXcE6o8-CIJQ\"]}"
 */
public final class CommonUtilities {

    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    public static final String SERVER_URL = APIConstants.API_END_POINT;

    /**
     * Google API project id registered to use GCM.
     */
    public static final String SENDER_ID = "387001418175";
    
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    
    public static final String PROPERTY_REG_ID = "registration_id";
    
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final int BACKOFF_MILLI_SECONDS = 2000;

    /**
     * Tag used on log messages.
     */
    public static final String TAG = "GCMDemo";

    /**
     * Intent used to display a message in the screen.
     */
    public static final String DISPLAY_MESSAGE_ACTION =
            "com.smart.taxi.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    public static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
    
    public static Dialog displayAlert(Context context, String message, String title, String okLabel, String cancelLabel, boolean showClose)
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setCancelable(true);
    	builder.setPositiveButton((Utils.isEmptyOrNull(okLabel))?"Ok":okLabel, null);
    	if(showClose)
    		builder.setNegativeButton((Utils.isEmptyOrNull(cancelLabel))?"Cancel":cancelLabel, null);
    	builder.setTitle(title);
    	builder.setMessage(message);
    	Dialog dialog = builder.create();
    	dialog.show();
    	return dialog;
    }
    
    public static void hideSoftKeyboard(Activity activity) {
        if(activity.getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
