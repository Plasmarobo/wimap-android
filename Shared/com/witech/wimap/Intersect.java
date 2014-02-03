package com.witech.wimap;


import java.util.List;

import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.linear.SingularValueDecomposition;
//import Jama.Matrix;
//import Jama.QRDecomposition;
//import Jama.SingularValueDecomposition;


public class Intersect {
	private double x;
	private double y;
	private double z;
	
	public Intersect(List<RadialDistance> L, double x, double y, double z)
	{
		this(L, x, y, z, 1E-8);
	}
	
	public Intersect(List<RadialDistance> L, double x, double y, double z, double accuracy)
	{
		
		
	}

	private double df(double value, RealMatrix guess, RadialDistance R)
	{
		return value/(Math.pow(R.GetX()-guess.getEntry(0, 0), 2)+Math.pow(R.GetY()-guess.getEntry(2,0), 2)+Math.pow(R.GetZ()-guess.getEntry(2,0), 2));
	}
	private double funct(RealMatrix xyz, RadialDistance R)
	{
		return funct(xyz.getEntry(0,0), xyz.getEntry(1,0), xyz.getEntry(2,0),R);
	}
	private double funct(double x, double y, double z, RadialDistance R)
	{
		return Math.sqrt((Math.pow(x-R.GetX(), 2) + Math.pow(y-R.GetY(), 2) + Math.pow(z-R.GetZ(), 2)));
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
