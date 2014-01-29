package com.witech.wimap;

import java.util.List;

import Jama.Matrix;

public class Intersect {
	private double x;
	private double y;
	private double z;
	
	Intersect(List<RadialDistance> L)
	{
		//parameters mx, my, mz
		//variables r, x, y, z
		//function r^2 = x^2+y^2+z^2
		//Weight via 1/r^2
		//Guess
		if(L.size() >= 3)
		{
		double r;
		for(int i = 0; i < L.size(); ++i)
		{
			r = 1/(Math.pow(L.get(i).GetDistance(),2.0));
	
			this.x += L.get(i).GetX()*r;
			this.y += L.get(i).GetY()*r;
			this.z += L.get(i).GetZ()*r;
		}
		this.x = this.x/L.size();
		this.y = this.y/L.size();
		this.z = this.z/L.size();
		//Perform nonlinear least squares algorithm
		
		Matrix A = new Matrix(3, L.size(), 0.0);
		Matrix B = new Matrix(1, L.size(), 0.0);
		Matrix lambda = new Matrix(3,1, 0.0);
		//K is the number of iterations before we settle
		for(int k = 0; k < 10; ++k)
		{
			lambda.set(0, 0, this.x);
			lambda.set(1, 0, this.y);
			lambda.set(2, 0, this.z);
			//Populate with equations
			//Perform derivatives
			for(int i = 0; i < L.size(); ++i)
			{
				//Derivatives
				double a = L.get(i).GetX();
				double b = L.get(i).GetY();
				double c = L.get(i).GetZ();
				A.set(0, i, df(lambda.get(0, 0), a));
				A.set(1, i, df(lambda.get(1, 0), b));
				A.set(2, i, df(lambda.get(2, 0), c));
				//r^2 equation
				B.set(0, i, eq(lambda.get(0, 0), a, lambda.get(1, 0), b, lambda.get(2, 0), c, L.get(i).GetDistance(), 1/(Math.pow(L.get(i).GetDistance(),2))));
			}
			Matrix Aprime = A.transpose().times(A);
			Matrix Bprime = A.transpose().times(B);
			lambda = Aprime.solve(Bprime);
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
