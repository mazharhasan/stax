package com.smart.taxi.entities;

import com.google.gson.JsonObject;
import com.smart.taxi.helpers.JsonHelper;

public class CorporateInfo extends Object{
	private int id;
	private String address;
	private String email;
	private String licenseNo;
	private String name;
	private String phone_no;
	
	public CorporateInfo()
	{
		
	}
	
	public CorporateInfo(int id, String address, String email, String licenseNo, String name, String phone_no)
	{
		super();
		this.setId(id);
		this.setAddress(address);
		this.setEmail(email);
		this.setLicenseNo(licenseNo);
		this.setName(name);
		this.setPhone_no(phone_no);
		
	}
	
	public static CorporateInfo deserializeFromJson(JsonObject info)
	{
		CorporateInfo ci = new CorporateInfo();
		JsonObject rootObj = info.getAsJsonObject();

		ci.setId(JsonHelper.getInt(rootObj, "id"));
		ci.setName(JsonHelper.getString(rootObj, "name"));
		ci.setAddress(JsonHelper.getString(rootObj, "address"));
		ci.setEmail(JsonHelper.getString(rootObj, "email"));
		ci.setPhone_no(JsonHelper.getString(rootObj, "phone_no"));
		ci.setLicenseNo(JsonHelper.getString(rootObj, "license_no"));
		
		return ci;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone_no() {
		return phone_no;
	}

	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}
}
