package com.smarttaxi.driver.BAL;

import java.util.ArrayList;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.database.SqliteHelper;
import com.smarttaxi.driver.entities.Cab;
import com.smarttaxi.driver.entities.User;
import com.smarttaxi.driver.helpers.JsonHelper;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.preferences.PrefKeys;
import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.Applog;
import com.smarttaxi.driver.utils.Utils;

public class Journey {
	static PreferencesHandler prefernecesHandler;
	private static Context context;

	public Journey(Context context) {
		prefernecesHandler = new PreferencesHandler(context);
		Journey.context = context;
	}

	/****** SALIK *****/
	public void SaveJourney(String journey) {
		if (Utils.isEmptyOrNull(journey))
			return;

		prefernecesHandler.saveJourney(journey);

	}

	public void removeSavedJourney() {
		prefernecesHandler.removeJourney();

	}

}
