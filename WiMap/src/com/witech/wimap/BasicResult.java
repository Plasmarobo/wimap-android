package com.witech.wimap;

import android.net.wifi.ScanResult;

public class BasicResult implements Comparable<BasicResult> {
	private int power;
	private int freq;
	private String ssid;
	private String uid;
	
	public BasicResult()
	{
		this.power = -100;
		this.ssid = "Error";
		this.uid = "Error";
		this.freq = 2400;
	}
	
	public BasicResult(int power, String ssid, String uid, int freq)
	{
		this.power = power;
		this.ssid = ssid;
		this.uid = uid;
		this.freq = freq;
	}
	
	public BasicResult(ScanResult result)
	{
		power = result.level;
		ssid = result.SSID;
		uid = result.BSSID;
		freq = result.frequency;
	}
	
	public int GetPower()
	{
		return this.power;
	}
	public int GetFreq()
	{
		return this.freq;
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
	
	void SetFreq(int freq)
	{
		this.freq = freq;
	}
	
	void SetSSID(String ssid)
	{
		this.ssid = ssid;
	}
	void SetUID(String uid)
	{
		this.uid = uid;
	}

	@Override
	public int compareTo(BasicResult another) {
		if(this.power > another.power)
			return -1;
		else if(this.power == another.power)
			return 0;
		else return 1;
	}
	
	public String toString()
	{
		return this.ssid + "|" + this.uid + "|" + this.power;
	}

	public void Merge(BasicResult br) {
		this.power += br.power;
		if(this.freq != br.freq)
		{
			//Throw Exception! But really we don't care!
		}
	}
	public BasicResult Average(double dividend)
	{
		if(dividend != 0)
		{
			this.power /= dividend;
		}
		return this;
	}
	public BasicResult CompensateForMisses(double misses)
	{
		if(misses > 0)
		{
			this.power += (-100*misses);
		}
		return this;
	}

	
}

