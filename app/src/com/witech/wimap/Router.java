package com.witech.wimap;

import android.net.wifi.ScanResult;

public class Router {
	private int id;
	private double x;
	private double y;
	private double z;
	private String ssid;
	private String uid;
	private double power;
	private final double c =  299792458.0;
	
	public Router()
	{
		id = 0;
		x = 0;
		y = 0;
		x = 0;
		ssid = "MySetting";
		uid = "MyMAC";
		power = 10^(-75/20);
	}
	
	public Router(double x, double y, double z, String ssid, String uid, ScanResult close)
	{
		this.id = 0;
		this.x = x;
		this.y = y;
		this.z = z;
		this.ssid = ssid;
		this.uid = uid;
		this.power = (c*Math.pow(10,(close.level/20)))/(4*Math.PI*close.frequency);//Distance initializer of 1
	}
	public Router(double x, double y, double z, String ssid, String uid, double level, double frequency)
	{
		this.id = 0;
		this.x = x;
		this.y = y;
		this.z = z;
		this.ssid = ssid;
		this.uid = uid;
		this.power = (c*Math.pow(10,(level/20)))/(4*Math.PI*frequency);//Distance initializer of 1
	}
	public int GetID() { return id;}
	public void SetID(int id) { this.id = id;}
	
	public double GetX() { return x;}
	public double GetY() { return y;}
	public double GetZ() { return z;}
	public String GetSSID() { return ssid;}
	public String GetUID() { return uid;}
	public double GetPower() { return power;}
	
	public void SetX(double x) { this.x = x;}
	public void SetY(double y) { this.y = y;}
	public void SetZ(double z) { this.z = z;}
	public void SetSSID(String ssid) { this.ssid = ssid;}
	public void SetUID(String uid) { this.uid = uid;}
	public void SetPower(double power) { this.power = power;}
	public void SetPower(double power, double freq) {this.power = (c*Math.pow(10,(power/20)))/(4*Math.PI*freq);}
	public void PowerFromScan(ScanResult scan) { this.power = (c*Math.pow(10,(scan.level/20)))/(4*Math.PI*scan.frequency);}
	
	public double GetFDSPLDistance(ScanResult scan)
	{
		//Compute free space path loss
		//loss(dBm)= 20.0*log10(df) -27.55221678
		//double distance = scan.frequency*Math.pow(10.0,(scan.level+27.55221678)/20.0);
		return scan.frequency*Math.pow(10.0,(scan.level+27.55221678)/20.0); //mW
	}
	public double GetComparativeDistance(ScanResult scan)
	{
		double sample = (c*Math.pow(10,(scan.level/20)))/(4*Math.PI*scan.frequency);//Distance initializer of 1
		//sample=power/r^2
		return Math.sqrt(power/sample); 
	}
	
}

