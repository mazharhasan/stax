package com.smarttaxi.driver.entities;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.smarttaxi.driver.entities.base.BaseWebServiceEntity;

public class DeviceRegister extends BaseWebServiceEntity {

	private String notificationMsg;

	public String getNotificationMsg() {
		return notificationMsg;
	}

	private void copy(Register reg) {
		this.notificationMsg = reg.bodyMsg;

	}

	@Override
	public void deserializeFromJSON(String json) {
		// TODO Auto-generated method stub

		Gson gson = new Gson();
		Register msg = gson.fromJson(json, Register.class);
		copy(msg);

	}

	@Override
	public void deserializeFromJSON(JsonElement json) {
		// TODO Auto-generated method stub

	}

	private class Register {

		@SerializedName("body")
		private String bodyMsg;

	}
}
