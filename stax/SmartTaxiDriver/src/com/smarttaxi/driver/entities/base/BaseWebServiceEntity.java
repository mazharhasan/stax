package com.smarttaxi.driver.entities.base;

import com.google.gson.JsonElement;

public abstract class BaseWebServiceEntity {
	
	public abstract void deserializeFromJSON(JsonElement json);
	public abstract void deserializeFromJSON(String json);
	//public abstract void deserializeFromJSON(JsonElement jsonElement, String key);
}
