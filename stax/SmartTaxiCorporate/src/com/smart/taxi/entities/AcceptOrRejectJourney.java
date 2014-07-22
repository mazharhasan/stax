package com.smart.taxi.entities;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.smart.taxi.entities.base.BaseWebServiceEntity;


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
