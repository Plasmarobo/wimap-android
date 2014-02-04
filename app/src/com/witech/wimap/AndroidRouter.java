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
	public double GetComparativeDistance(ScanResult scan)
	{
		//Ratio between 1m power and measured power
		double power_loss = this.power - scan.level;
		//Should return aproximate distance in M
		return Math.pow(10, ((27.55 - (20 * Math.log10(freq)) - (power_loss-30))/20));
	}
	public double GetAverageDistance(ScanResult scan)
	{
		double distance =(
				Router.GetFDSPLDistance(scan.level, this.freq)+
				this.GetComparativeDistance(scan)
				);
		return distance/2;
	}

}
