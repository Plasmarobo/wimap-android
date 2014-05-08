package com.wimap.components;

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

public class BasicResult implements Comparable<BasicResult>, Parcelable {
	private double power;
	private double freq;
	private String ssid;
	private String uid;
    private Boolean calibrated;
	
	public BasicResult()
	{
		this.power = -100;
		this.ssid = "Error";
		this.uid = "Error";
		this.freq = 2400;
        this.calibrated = false;
	}
	public BasicResult(Parcel in)
	{
		this.power = in.readDouble();
		this.freq = in.readDouble();
		this.ssid = in.readString();
		this.uid = in.readString();
        this.calibrated = in.readInt() > 0;
	}

    public BasicResult(double power, String ssid, String uid, double freq, boolean cal_done)
    {
        this(power, ssid, uid, freq);
        this.calibrated = cal_done;
    }
	
	public BasicResult(double power, String ssid, String uid, double freq)
	{
		this.power = power;
		this.ssid = ssid;
		this.uid = uid;
		this.freq = freq;
        this.calibrated = false;
	}
	
	public BasicResult(ScanResult result)
	{
		power = result.level;
		ssid = result.SSID;
		uid = result.BSSID;
		freq = result.frequency;
        this.calibrated = false;
	}
	public BasicResult(BasicResult rhs)
	{
		this.power = rhs.power;
		this.ssid = rhs.ssid;
		this.uid = rhs.uid;
		this.freq = rhs.freq;
        this.calibrated = rhs.calibrated;
	}
	
	public double GetPower()
	{
		return this.power;
	}
	public double GetFreq()
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
    public boolean IsCalibrated() {return this.calibrated; }
	
	void SetPower(double power)
	{
		this.power = power;
	}

    void SetCalibrated(boolean calibrated) { this.calibrated = calibrated; }
	
	void SetFreq(double freq)
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
		return this.ssid + "|" + this.uid + "|" + this.power + "|" + this.freq + "|" + this.calibrated;
	}

	public void Merge(BasicResult br, double weight) {
		this.power += weight*br.power;
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
			this.power += (-100.0*misses);
		}
		return this;
	}
	public BasicResult CompensateForMiss()
	{
		this.power += (-100.0);
		return this;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeDouble(power);
		out.writeDouble(freq);
		out.writeString(ssid);
		out.writeString(uid);
        out.writeInt(calibrated ? 1 : 0);
		
	}
	public static final Parcelable.Creator<BasicResult> CREATOR = new Parcelable.Creator<BasicResult>() {
		public BasicResult createFromParcel(Parcel in) {
			return new BasicResult(in);
		}
		public BasicResult[] newArray(int size) {
			return new BasicResult[size];
		}
	};

	
}

