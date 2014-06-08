/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.common.math;

public class RadialDistance {
	private double x;
	private double y;
	private double z;
	private double d;
	public RadialDistance(float x, float y, float z, float d)
	{
		this.x = x;
		this.y = y;
		this.d = d;
	}
	
	public RadialDistance(double x, double y, double z, double d)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.d = d;
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
