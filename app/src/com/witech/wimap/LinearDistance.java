package com.witech.wimap;

public class LinearDistance {
	private double x;
	private double y;
	private double z;
	private double d;
	public LinearDistance(float x, float y, float z, float d)
	{
		this.x = x;
		this.y = y;
		this.d = d;
	}
	
	public LinearDistance(int nominal, int valued, float attn, float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		double relative_strength = Math.abs(nominal)/Math.abs(valued);
		d = Math.sqrt(relative_strength);
	}
	
	public double GetDistance()
	{
		return this.d;
	}
	
	public double GetX()
	{
		return this.x;
	}
	
	public double GetY()
	{
		return this.y;
	}
	
	public double GetZ()
	{
		return this.z;
	}
	
}
