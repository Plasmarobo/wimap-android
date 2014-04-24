package com.wimap.math;

import org.apache.commons.math3.fitting.CurveFitter;

public class Router {
	protected int id;
	protected double x;
	protected double y;
	protected double z;
	protected String ssid;
	protected String uid;
	protected double power;
	protected double freq;
	protected int site_id;
    protected double tx_power;
	protected final double c =  299792458.0;
	
	public Router()
	{
		id = 0;
		x = 0;
		y = 0;
		x = 0;
		site_id = 1;
		ssid = "MySetting";
		uid = "MyMAC";
		power = -90;
        tx_power = 75;
		freq = 2400;
	}
	public Router(double x, double y, double z, String ssid, String uid, double dBm, double freq)
	{
		this(x, y, z, ssid, uid, 1, dBm, freq);
	}

	public Router(double x, double y, double z, String ssid, String uid, int site_id, double dBm, double frequency, double tx_power)
    {
        this(x,y,z,ssid,uid,site_id,dBm,frequency);
        this.tx_power = tx_power;
    }
	public Router(double x, double y, double z, String ssid, String uid, int site_id, double dBm, double frequency)
	{
		this.id = 0;
		this.x = x;
		this.y = y;
		this.z = z;
		this.ssid = ssid;
		this.uid = uid;
		this.site_id = site_id;
		this.freq = frequency;
		this.power = dBm; //Distance initializer of 1
	}
	public int GetID() { return id;}
	public void SetID(int id) { this.id = id;}
	public void SetSiteID(int id) { this.site_id = id;}
	public double GetX() { return x;}
	public double GetY() { return y;}
	public double GetZ() { return z;}
	public String GetSSID() { return ssid;}
	public String GetUID() { return uid;}
	public int GetSiteID() {return site_id;}
	public double GetPower() { return power;}
	public double GetFreq() {return freq;}
    public double GetTxPower() {return tx_power;}
	
	public void SetX(double x) { this.x = x;}
	public void SetY(double y) { this.y = y;}
	public void SetZ(double z) { this.z = z;}
	public void SetSSID(String ssid) { this.ssid = ssid;}
	public void SetUID(String uid) { this.uid = uid;}
	public void SetPower(double power) { this.power = power;}
	public void SetFreq(double freq) {this.freq = freq;}
	public void SetPower(double power, double freq) {this.freq = freq; this.power = power;}
    public void SetTxPower(double power) { this.tx_power = power;}
	
	public double GetFSPLDistance(double dBm)
	{
		//Compute free space path loss
		//loss(dBm)= 20.0*log10(df) -27.55221678
		//double distance = scan.frequency*Math.pow(10.0,(scan.level+27.55221678)/20.0);
		//return Math.pow(10, ((27.55 - (20 * Math.log10(frequency)) - (dBm-30))/20));
		//return frequency*Math.pow(10.0,(power+27.55221678)/20.0); //mW
        double hz = this.freq *(10^6);

        double frequency_attenuation =  20*Math.log10(this.c / hz)-20*Math.log10(4*Math.PI);
        double jitter = 6*(Math.random()-0.5);
        double gtx = 2.3;
        double grx = 2.3;
        double effective_power = this.tx_power + gtx + grx - jitter;
        double n = 5;
        double multipath_compensation = (10*n);
        return Math.pow(10.0,((effective_power-dBm)/multipath_compensation));
    }

    public void FindTxPower(double [][] power_map)
    {

    }
	
	
}

