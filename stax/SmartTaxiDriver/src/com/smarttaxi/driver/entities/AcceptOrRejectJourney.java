package com.smarttaxi.driver.entities;

import java.lang.reflect.Type;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.entities.base.BaseWebServiceEntity;
import com.smarttaxi.driver.helpers.JsonHelper;

public class AcceptOrRejectJourney extends BaseWebServiceEntity {

	private String notificationMsg;

	public String getNotificationMsg() {
		return notificationMsg;
	}

	private void copy(AcceptRejectMsg accept) {
		this.notificationMsg = accept.bodyMsg;

	}

	@Override
	public void deserializeFromJSON(String json) {
		// TODO Auto-generated method stub

		Gson gson = new Gson();
		AcceptRejectMsg msg = gson.fromJson(json, AcceptRejectMsg.class);
		copy(msg);

	}

	@Override
	public void deserializeFromJSON(JsonElement json) {
		// TODO Auto-generated method stub

	}

	private class AcceptRejectMsg {

		@SerializedName("body")
		private String bodyMsg;

	}

}
