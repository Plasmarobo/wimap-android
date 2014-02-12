package com.witech.wimap;

import android.net.wifi.ScanResult;

import com.witech.wimap.Router;


public class AndroidRouter extends Router {
	
	public AndroidRouter()
	{
		super();
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
	public AndroidRouter(double double1, double double2, double double3,
			String string, String string2, double double4, double double5) {
		super(double1, double2, double3, string, string2, double4, double5);
	}
	public void PowerFromScan(ScanResult scan) 
	{ 
		this.power = scan.level;
		this.freq = scan.frequency;
	}
	public double GetComparativeDistance(BasicResult sr)
	{
		//Ratio between 1m power and measured power
		double relative_strength = (sr.GetPower()-30)-(this.power-30);
		//Should return aproximate distance in M
		
		double exp = (relative_strength - 20*Math.log10(this.freq) + 27.55)/20.0;
	    return 1+(1/Math.pow(10.0, exp));
	}
	public double GetAverageDistance(BasicResult sr)
	{
		//double distance =(
		//		Router.GetFDSPLDistance(sr.GetPower(), sr.GetFreq())+
		//		this.GetComparativeDistance(sr)
		//		);
		//return distance/2;
		return this.GetComparativeDistance(sr);
	}
	public String toString()
	{
		return this.uid + "|" + this.ssid + "|" + this.power;
	}

}
