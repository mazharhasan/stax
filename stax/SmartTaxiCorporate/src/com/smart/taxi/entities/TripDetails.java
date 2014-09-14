package com.smart.taxi.entities;

import com.google.android.gms.maps.model.LatLng;

public class TripDetails {
	public static final String ACCEPTED = "accepted";
	public static final String CANCELLED = "cancelled";
	public static final String REJECTED = "rejected";
	public static final String ARRIVED = "arrived";
	private String numChildSeats = "0";
	private String journeyOptions = "solo";
	private String numPassengers = "1";
	private String numBags = "0";
	private String pickupAddress = "";
	private String lat;
	private String lng;
	private String pikcupTime;
	private String userId;
	private String optionalMessage;
	private String journeyCorporateType;
	private String cabId;
	private boolean isPostCheckedIn = false;
	private String journeyId;
	private LatLng driverLatLng;
	private String status = "Pending...";
	private boolean isCustomTrip = false;
	private boolean isRequestedLater;
	private String token;
	
	public TripDetails()
	{
		
	}

	public String getNumChildSeats() {
		return numChildSeats;
	}

	public void setNumChildSeats(String numChildSeats) {
		this.numChildSeats = numChildSeats;
	}

	public String getJourneyOptions() {
		return journeyOptions;
	}

	public void setJourneyOptions(String journeyOptions) {
		this.journeyOptions = journeyOptions;
	}

	public String getNumBags() {
		return numBags;
	}

	public void setNumBags(String numBags) {
		this.numBags = numBags;
	}

	public String getPickupAddress() {
		return pickupAddress;
	}

	public void setPickupAddress(String pickupAddress) {
		this.pickupAddress = pickupAddress;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getPikcupTime() {
		return pikcupTime;
	}

	public void setPikcupTime(String pikcupTime) {
		this.pikcupTime = pikcupTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOptionalMessage() {
		return optionalMessage;
	}

	public void setOptionalMessage(String optionalMessage) {
		this.optionalMessage = optionalMessage;
	}

	public String getJourneyCorporateType() {
		return journeyCorporateType;
	}

	public void setJourneyCorporateType(String journeyCorporateType) {
		this.journeyCorporateType = journeyCorporateType;
	}

	public String getCabId() {
		return cabId;
	}

	public void setCabId(String cabId) {
		this.cabId = cabId;
	}

	public boolean isPostCheckedIn() {
		return isPostCheckedIn;
	}

	public void setPostCheckedIn(boolean isPostCheckedIn) {
		this.isPostCheckedIn = isPostCheckedIn;
	}

	public String getNumPassengers() {
		return numPassengers;
	}

	public void setNumPassengers(String numPassengers) {
		this.numPassengers = numPassengers;
	}

	public String getJourneyId() {
		return journeyId;
	}

	public void setJourneyId(String journeyId) {
		this.journeyId = journeyId;
	}

	public void setDriverLatLng(LatLng driverLatLng) {
		this.driverLatLng = driverLatLng;
		
	}
	
	public LatLng getDriverLatLng()
	{
		return this.driverLatLng;
	}

	public void setTripStatus(String status) {
		this.status = status;
	}
	
	public String getTripStatus()
	{
		return this.status;
	}

	public boolean isCustomTrip() {
		return isCustomTrip;
	}

	public void setCustomTrip(boolean isCustomTrip) {
		this.isCustomTrip = isCustomTrip;
	}

	public void setLaterRequest(boolean isLaterTrip) {
		this.setRequestedLater(isLaterTrip);
		
	}

	public boolean isRequestedLater() {
		return isRequestedLater;
	}

	public void setRequestedLater(boolean isRequestedLater) {
		this.isRequestedLater = isRequestedLater;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
