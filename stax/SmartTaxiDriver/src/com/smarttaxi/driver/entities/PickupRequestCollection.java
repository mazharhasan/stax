package com.smarttaxi.driver.entities;

import java.util.ArrayList;

public class PickupRequestCollection {
	
	private ArrayList<PickupRequest> PickupCollection;
	
	public PickupRequestCollection() {
		// TODO Auto-generated constructor stub
		PickupCollection = new ArrayList<PickupRequest>();
	}
	
	public void AddPickRequest (PickupRequest pickup)
	{
		PickupCollection.add(pickup);
	}
	
	public void RemovePickRequest(int index)
	{
		PickupCollection.remove(index);
	}
	
	public PickupRequest GetPickRequest(int index)
	{
		return PickupCollection.get(index);
	}
	
	public int GetTotalRequest()
	{
		return PickupCollection.size();
	}

}
