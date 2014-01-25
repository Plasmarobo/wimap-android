package com.witech.wimap;

import android.net.wifi.ScanResult;

public class BasicResult {
	private int power;
	private String ssid;
	private String uid;
	
	public BasicResult(int power, String ssid, String uid)
	{
		this.power = power;
		this.ssid = ssid;
		this.uid = uid;
	}
	
	public BasicResult(ScanResult result)
	{
		power = result.level;
		ssid = result.SSID;
		uid = result.BSSID;
	}
	
	public int GetPower()
	{
		return this.power;
	}
	
	public String GetSSID()
	{
		return this.ssid;
	}
	public String GetUID()
	{
		return this.uid;
	}
	
	void SetPower(int power)
	{
		this.power = power;
	}
	
	void SetSSID(String ssid)
	{
		this.ssid = ssid;
	}
	void SetUID(String uid)
	{
		this.uid = uid;
	}
}
