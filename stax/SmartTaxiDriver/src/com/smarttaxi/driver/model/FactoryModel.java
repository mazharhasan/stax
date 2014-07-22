package com.smarttaxi.driver.model;

import java.util.ArrayList;

import com.smarttaxi.driver.entities.Cab;
import com.smarttaxi.driver.entities.User;

public class FactoryModel {
	
	public static ArrayList<Cab> cabsList;
	public static User user;
	
	public static void setCabsList(ArrayList<Cab> cabsList) {
		FactoryModel.cabsList = cabsList;
	}
	
	public static ArrayList<Cab> getCabsList() {
		return FactoryModel.cabsList;
	}

	public static User getUser() {
		return user;
	}

	public static void setUser(User user) {
		FactoryModel.user = user;
	}
}
