package com.witech.wimap;

import android.net.wifi.ScanResult;

import com.witech.wimap.Router;


public class AndroidRouter extends Router {
	
	
	public AndroidRouter(double x, double y, double z, ScanResult r)
	{
		this.id = 0;
		this.x = x;
		this.y = y;
		this.z = z;
		this.ssid = r.SSID;
		this.uid = r.BSSID;
		this.power = (c*Math.pow(10,(r.level/20)))/(4*Math.PI*r.frequency);//Distance initializer of 1
	}
	public void PowerFromScan(ScanResult scan) 
	{ 
		this.power = (c*Math.pow(10,(scan.level/20)))/(4*Math.PI*scan.frequency);
	}
	public double GetComparativeDistance(ScanResult scan)
	{
		double sample = (c*Math.pow(10,(scan.level/20)))/(4*Math.PI*scan.frequency);//Distance initializer of 1
		//sample=power/r^2
		return Math.sqrt(power/sample); 
	}
	public double GetAverageDistance(ScanResult scan)
	{
		double distance =
		Router.GetFDSPLDistance(scan.level, this.freq)+
		this.GetComparativeDistance(scan);
		return distance/2;
	}

}
