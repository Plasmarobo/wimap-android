/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.common;


import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Router implements APIObject {

    public static final String tag_x = "x";
    public static final String tag_y = "y";
    public static final String tag_z = "z";
    public static final String tag_ssid = "ssid";
    public static final String tag_uid = "uid";
    public static final String tag_power = "power";
    public static final String tag_freq = "frequency";
    public static final String tag_site_id = "site_id";
    public static final String tag_tx_power = "tx_power";

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
    public Router(Router rhs)
    {
        this(rhs.GetX(), rhs.GetY(), rhs.GetZ(), rhs.GetSSID(), rhs.GetUID(), rhs.GetSiteID(), rhs.GetPower(), rhs.GetFreq(), rhs.GetTxPower());
        this.id = rhs.GetID();
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

    public double GetFSPLRelativeDistance(double dBm, double ptx)
    {
        return GetFSPLDistance(dBm - this.power, ptx);
    }

    public double GetFSPLRelativeDistance(double dBm)
    {
        return GetFSPLRelativeDistance(dBm, this.tx_power);
    }
	
	public double GetFSPLDistance(double dBm, double ptx)
	{
		//Compute free space path loss
		//loss(dBm)= 20.0*log10(df) -27.55221678
		//double distance = scan.frequency*Math.pow(10.0,(scan.level+27.55221678)/20.0);
		//return Math.pow(10, ((27.55 - (20 * Math.log10(frequency)) - (dBm-30))/20));
		//return frequency*Math.pow(10.0,(power+27.55221678)/20.0); //mW

        //double hz = this.freq *(10^6);
        //double frequency_attenuation =  20*Math.log10(this.c / hz)-20*Math.log10(4*Math.PI);

        double n = 5;
        double multipath_compensation = (10*n);
        return Math.pow(10.0,((ptx-dBm)/multipath_compensation));
    }

    public double GetFSPLDistance_d(double dBm)
    {
        dBm = Math.abs(dBm);
        double exponent = (27.55 - (20 * Math.log10(this.freq) ) + dBm) / 20.0;
        return Math.pow(10.0, exponent);
    }


    public double GetFSPLDistance(double dBm)
    {
        return this.GetFSPLDistance(dBm, this.tx_power);
    }


    public double FindTxPower(ArrayList<Double> power)
    {
        double ptx = 75.0;
        double sq_error = 2500.0;
        double max_error = 0;
        double error = 2500;
        double iteration = 0;
        double delta = 100;
        double delta_limit = 0.0001;
        double adjustment_rate = 1.0/3.0;
        int last_adjustment = 1;
        while(delta > delta_limit) {
            if (iteration > 1) {
                delta = ptx;
                if (error > 0) {
                    if (last_adjustment == -1)
                        adjustment_rate = adjustment_rate / 2.0;

                    ptx += ptx * adjustment_rate;
                    last_adjustment = 1;
                } else {
                    if (last_adjustment == 1)
                        adjustment_rate = adjustment_rate / 2.0;

                    ptx -= ptx * adjustment_rate;
                    last_adjustment = -1;
                }
                delta = Math.pow(delta - ptx, 2.0);
            }
            error = 0;
            double err = 0;
            for(int i = 0; i < power.size(); ++i) {
                err = i - this.GetFSPLDistance(power.get(i), ptx);
                if (err > max_error)
                    max_error = err;

                error += err;
            }

            sq_error = Math.pow(error,2.0);
            iteration += 1;
        }
        this.tx_power = ptx;
        return ptx;
    }

    public double TrainTxPower(double data[][], int trials, int samples)
    {
        double results = 0;
        for(int i = 0; i < trials; ++i)
        {
            ArrayList<Double> trial = new ArrayList<Double>();
            for(int j = 0; j < samples; ++j)
            {
                trial.add(data[i][j]);
            }
            results += this.FindTxPower(trial);
        }
        return results/(double)trials;
    }


    @Override
    public JSONObject ToJSON() throws JSONException {
        JSONObject router_json = new JSONObject();
            router_json.put(tag_x, this.GetX());
            router_json.put(tag_y, this.GetY());
            router_json.put(tag_z, this.GetZ());
            router_json.put(tag_site_id, this.GetSiteID());
            router_json.put(tag_ssid, this.GetSSID());
            router_json.put(tag_uid, this.GetUID());
            router_json.put(tag_power, this.GetPower());
            router_json.put(tag_freq, this.GetFreq());
            router_json.put(tag_tx_power, this.GetTxPower());
        return router_json;
    }

    @Override
    public boolean FromJSON(JSONObject json) throws JSONException {

                this.x = json.getDouble(tag_x);
                this.y = json.getDouble(tag_y);
                this.z = json.getDouble(tag_z);
                this.ssid = json.getString(tag_ssid);
                this.uid = json.getString(tag_uid);
                this.site_id = json.getInt(tag_site_id);
                this.power = json.getDouble(tag_power);
                this.freq = json.getDouble(tag_freq);
                this.tx_power = json.getDouble((tag_tx_power));
                return true;
    }

    @Override
    public JSONObject FromJSONArray(JSONArray json) throws JSONException {
        JSONObject item = json.getJSONObject(0);
        json.remove(0);
        this.FromJSON(item);
        return item;
    }
}

