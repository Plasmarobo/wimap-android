package com.witech.wimap;


import java.util.List;

import com.witech.wimap.RadialDistance;

import Jama.Matrix;

public class Intersect {
	private double x;
	private double y;
	private double z;
	
	public Intersect(List<RadialDistance> L)
	{
		//parameters mx, my, mz
		//variables r, x, y, z
		//function r^2 = x^2+y^2+z^2
		//Weight via 1/r^2
		//Guess
		if(L.size() >= 3)
		{
		double r;
		double wr = 0.0;
		for(int i = 0; i < L.size(); ++i)
		{
			r = 1/(Math.pow(L.get(i).GetDistance(),2.0));
			
			this.x += L.get(i).GetX()*r;
			this.y += L.get(i).GetY()*r;
			this.z += L.get(i).GetZ()*r;
			wr += r;
		}
		this.x = (this.x/L.size())/wr;
		this.y = (this.y/L.size())/wr;
		this.z = (this.z/L.size())/wr;
		//Perform nonlinear least squares algorithm
		
		Matrix A = new Matrix(3, L.size(), 0.0);
		Matrix B = new Matrix(L.size(), 1, 0.0);
		Matrix lambda = new Matrix(3,1, 0.0);
		lambda.set(0, 0, this.x);
		lambda.set(1, 0, this.y);
		lambda.set(2, 0, this.z);
		//K is the number of iterations before we settle
		for(int k = 0; k < 10; ++k)
		{
			//Populate with equations
			//Perform derivatives
			for(int i = 0; i < L.size(); ++i)
			{
				double a = L.get(i).GetX();
				double b = L.get(i).GetY();
				double c = L.get(i).GetZ();
				A.set(0, i, df(lambda.get(0, 0), a));
				A.set(1, i, df(lambda.get(1, 0), b));
				A.set(2, i, df(lambda.get(2, 0), c));
				B.set(i, 0, eq(lambda.get(0, 0), a, lambda.get(1, 0), b, lambda.get(2, 0), c, L.get(i).GetDistance(), 1/(Math.pow(L.get(i).GetDistance(),2))));
			}
			//if(A.det() == 0)
			//{
			//	return;
			//}
			Matrix Aprime = A.transpose().times(A);
			Matrix Bprime = A.transpose().times(B);
			lambda = lambda.plus(Aprime.solve(Bprime));
		}
		this.x = lambda.get(0, 0);
		this.y = lambda.get(1, 0);
		this.z = lambda.get(2, 0);
		}
	}
	
	private double df(double a, double x)
	{
		return 2*a*(a+x);
	}
	private double eq(double a, double x, double b, double y, double c, double z, double r, double w)
	{
		return Math.pow(w*(Math.pow(r, 2.0) - (Math.pow(x+a, 2) + Math.pow(y+b, 2) + Math.pow(z+c, 2))),2);
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
