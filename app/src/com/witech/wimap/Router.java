package com.witech.wimap;

public class Router {
	private int id;
	private int x;
	private int y;
	private int z;
	private String ssid;
	private String uid;
	private int power;
	
	public Router()
	{
		id = 0;
		x = 0;
		y = 0;
		x = 0;
		ssid = "MySetting";
		uid = "MyMAC";
		power = -75;
	}
	
	public Router(int x, int y, int z, String ssid, String uid, int power)
	{
		this.id = 0;
		this.x = x;
		this.y = y;
		this.z = z;
		this.ssid = ssid;
		this.uid = uid;
		this.power = power;
	}
	public int GetID() { return id;}
	public void SetID(int id) { this.id = id;}
	
	public int GetX() { return x;}
	public int GetY() { return y;}
	public int GetZ() { return z;}
	public String GetSSID() { return ssid;}
	public String GetUID() { return uid;}
	public int GetPower() { return power;}
	
	public void SetX(int x) { this.x = x;}
	public void SetY(int y) { this.y = y;}
	public void SetZ(int z) { this.z = z;}
	public void SetSSID(String ssid) { this.ssid = ssid;}
	public void SetUID(String uid) { this.uid = uid;}
	public void SetPower(int power) { this.power = power;}
	
}

