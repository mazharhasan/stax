package com.smart.taxi.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smart.taxi.utils.Utils;

public class JsonHelper {
	
	public static JsonObject parseToJsonObject(String jsonString) {
		JsonParser parser = new JsonParser();
		return (JsonObject)parser.parse(jsonString);
	}
	
	public static String getString(JsonObject jsonObject,String key) {
		return getString(jsonObject, key, "");
	}
	
	public static String getString(JsonObject jsonObject,String key,String defaultValue) {
		if(jsonObject == null)
			return defaultValue;
		JsonElement jsonElement = jsonObject.get(key);
		if(jsonElement.isJsonNull()) {
			return defaultValue;
		}		
		return Utils.validateEmptyString(jsonElement.getAsString(),defaultValue);		
	}
	
	public static int getInt(JsonObject jsonObject,String key) {
		return getInt(jsonObject, key, 0);	
	}
	
	public static int getInt(JsonObject jsonObject,String key, int defaultValue) {
		if(jsonObject == null)
			return defaultValue;
		JsonElement jsonElement = jsonObject.get(key);
		if(jsonElement.isJsonNull()) {
			return defaultValue;
		}		
		return jsonElement.getAsInt();	
	}
	
	public static boolean getBoolean(JsonObject jsonObject,String key) {
		return getBoolean(jsonObject, key, false);
	}
	
	public static boolean getBoolean(JsonObject jsonObject,String key, boolean defaultValue) {
		if(jsonObject == null)
			return defaultValue;
		JsonElement jsonElement = jsonObject.get(key);
		if(jsonElement.isJsonNull()) {
			return defaultValue;
		}		
		return jsonElement.getAsBoolean();
	}
	
	public static double getDouble(JsonObject jsonObject,String key) {
		return getDouble(jsonObject, key, 0.0);
	}
	
	public static double getDouble(JsonObject jsonObject,String key, double defaultValue) {
		if(jsonObject == null)
			return defaultValue;
		JsonElement jsonElement = jsonObject.get(key);
		if(jsonElement.isJsonNull()) {
			return defaultValue;
		}		
		return jsonElement.getAsDouble();
	}
	
	public static JsonObject getJsonObject(JsonObject jsonObject, String key) {
		if(jsonObject == null)
			return null;
		JsonElement jsonElement = jsonObject.get(key);
		if(jsonElement.isJsonNull()) {
			return null;
		}		
		return jsonElement.getAsJsonObject();
	}

	public static JsonArray getJsonArray(JsonObject jsonObject, String key){

		if(jsonObject == null)
			return null;
		JsonElement jsonElement = jsonObject.get(key);
		if(jsonElement.isJsonNull()) {
			return null;
		}		
		return jsonElement.getAsJsonArray();
	}
}
