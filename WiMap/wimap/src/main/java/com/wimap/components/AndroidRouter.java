/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.components;

import android.net.wifi.ScanResult;

import com.wimap.common.Router;


public class AndroidRouter extends Router {
	
	public AndroidRouter()
	{
		super();
	}
    public AndroidRouter(Router rhs)
    {
        super(rhs);
    }

    public AndroidRouter(AndroidRouter rhs)
    {
        super(rhs);
    }
	public AndroidRouter(double x, double y, double z, ScanResult r)
	{
		this.id = 0;
		this.x = x;
		this.y = y;
		this.z = z;
		this.ssid = r.SSID;
		this.uid = r.BSSID;
		this.freq = r.frequency;
		this.power = r.level;//Distance initializer of 1
	}
    public AndroidRouter(double x, double y, double z, String ssid, String uid, double dBm, double freq)
    {
        super(x, y, z, ssid, uid, 1, dBm, freq);
    }

    public AndroidRouter(double x, double y, double z, String ssid, String uid, int site_id, double dBm, double frequency, double tx_power)
    {
        super(x,y,z,ssid,uid,site_id,dBm,frequency,tx_power);
    }

	public void PowerFromScan(ScanResult scan) 
	{ 
		this.power = scan.level;
		this.freq = scan.frequency;
	}

	public double GetComparativeDistance(BasicResult sr)
	{
		//Ratio between 1m power and measured power
		double relative_strength = Math.abs((sr.GetPower())-(this.power));
		//Should return aproximate distance in M
		
		//double exp = (relative_strength - 20*Math.log10(this.freq) + 27.55)/20.0;
	    //return 1+Math.pow(10.0, exp);
		return 1 + Math.pow(10, (relative_strength-2.45)/20)/this.freq;
	}
	public double GetAverageDistance(BasicResult sr)
	{
		double distance =(
				this.GetFSPLDistance(sr.GetPower())+
				this.GetComparativeDistance(sr)
				);
		return distance/2;
		//return this.GetComparativeDistance(sr);
	}
    public double GetDistance(BasicResult sr)
    {
        return this.GetFSPLDistance(sr.GetPower());
    }
    public double GetFSPLRelativeDistance(BasicResult sr) {return this.GetFSPLRelativeDistance(sr.GetPower()); }
	public String toString()
	{
		return this.uid + "|" + this.ssid + "|" + this.power + "|" + this.tx_power;
	}

    public BasicResult ToBasicResult()
    {
        return new BasicResult(this.power, this.GetSSID(), this.GetUID(), this.GetFreq());
    }

}
