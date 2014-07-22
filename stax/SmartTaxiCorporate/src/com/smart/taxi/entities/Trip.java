package com.smart.taxi.entities;

import java.lang.reflect.Type;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.smart.taxi.constants.APIConstants;


public class Trip implements Parcelable {

	String cabID;
	int childSeats;
	String journeyOption;
	int maxPassengers;
	int noOfBags;
	String paymentDetail;
	String paymentOptions;
	String pickupAddress;
	double pickupLat;
	double pickupLong;
	String pickupTime;
	String userID;
	String cabName;
	String cabImageUrl;

	public Trip() {

	}

	public Trip(Parcel source) {
		cabID = source.readString();
		journeyOption = source.readString();
		paymentDetail = source.readString();
		paymentOptions = source.readString();
		pickupAddress = source.readString();
		pickupTime = source.readString();
		userID = source.readString();
		cabName = source.readString();
		cabImageUrl = source.readString();

		childSeats = source.readInt();
		maxPassengers = source.readInt();
		noOfBags = source.readInt();

		pickupLat = source.readDouble();
		pickupLong = source.readDouble();		
	}

	public String getCabID() {
		return cabID;
	}

	public void setCabID(String cabID) {
		this.cabID = cabID;
	}

	public int getChildSeats() {
		return childSeats;
	}

	public void setChildSeats(int childSeats) {
		this.childSeats = childSeats;
	}

	public String getJourneyOption() {
		return journeyOption;
	}

	public void setJourneyOption(String journeyOption) {
		this.journeyOption = journeyOption;
	}

	public int getMaxPassengers() {
		return maxPassengers;
	}

	public void setMaxPassengers(int maxPassengers) {
		this.maxPassengers = maxPassengers;
	}

	public int getNoOfBags() {
		return noOfBags;
	}

	public void setNoOfBags(int noOfBags) {
		this.noOfBags = noOfBags;
	}

	public String getPaymentDetail() {
		return paymentDetail;
	}

	public void setPaymentDetail(String paymentDetail) {
		this.paymentDetail = paymentDetail;
	}

	public String getPaymentOptions() {
		return paymentOptions;
	}

	public void setPaymentOptions(String paymentOptions) {
		this.paymentOptions = paymentOptions;
	}

	public String getPickupAddress() {
		return pickupAddress;
	}

	public void setPickupAddress(String pickupAddress) {
		this.pickupAddress = pickupAddress;
	}

	public double getPickupLat() {
		return pickupLat;
	}

	public void setPickupLat(double pickupLat) {
		this.pickupLat = pickupLat;
	}

	public double getPickupLong() {
		return pickupLong;
	}

	public void setPickupLong(double pickupLong) {
		this.pickupLong = pickupLong;
	}

	public String getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(String pickupTime) {
		this.pickupTime = pickupTime;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public void setCabName(String name) {
		this.cabName = name;
	}
	
	public String getCabName() {
		return this.cabName;
	}
	
	public String getCabImageUrl() {
		return cabImageUrl;
	}

	public void setCabImageUrl(String cabImageUrl) {
		this.cabImageUrl = cabImageUrl;
	}

	public class TripSerializer implements JsonSerializer<Trip> {

		@Override
		public JsonElement serialize(Trip trip, Type arg1,
				JsonSerializationContext arg2) {
			JsonObject tripObject = new JsonObject();

			tripObject.addProperty(APIConstants.KEY_CAB_ID, trip.cabID);
			tripObject.addProperty(APIConstants.KEY_CHILD_SEATS,
					trip.childSeats);
			tripObject.addProperty(APIConstants.KEY_JOURNEY_OPTION,
					trip.journeyOption);
			tripObject.addProperty(APIConstants.KEY_MAX_NO_OF_PASSENGERS,
					trip.maxPassengers);
			tripObject.addProperty(APIConstants.KEY_NO_OF_BAGS, trip.noOfBags);
			tripObject.addProperty(APIConstants.KEY_PAYMENT_DETAIL,
					trip.paymentDetail);
			tripObject.addProperty(APIConstants.KEY_PAYMENT_OPTION,
					trip.paymentOptions);
			tripObject.addProperty(APIConstants.KEY_PICKUP_ADDRESS,
					trip.pickupAddress);
			tripObject.addProperty(APIConstants.KEY_PICKUP_LAT, trip.pickupLat);
			tripObject
					.addProperty(APIConstants.KEY_PICKUP_LNG, trip.pickupLong);
			tripObject.addProperty(APIConstants.KEY_PICKUP_TIME,
					trip.pickupTime);
			tripObject.addProperty(APIConstants.KEY_USER_ID, trip.userID);

			return tripObject;
		}

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(cabID);
		dest.writeString(journeyOption);
		dest.writeString(paymentDetail);
		dest.writeString(paymentOptions);
		dest.writeString(pickupAddress);
		dest.writeString(pickupTime);
		dest.writeString(userID);
		dest.writeString(cabName);
		dest.writeString(cabImageUrl);

		dest.writeInt(childSeats);
		dest.writeInt(maxPassengers);
		dest.writeInt(noOfBags);

		dest.writeDouble(pickupLat);
		dest.writeDouble(pickupLong);
	}

	public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {

		@Override
		public Trip createFromParcel(Parcel source) {
			return new Trip(source);
		}

		@Override
		public Trip[] newArray(int size) {
			return new Trip[size];
		}
	};

}
