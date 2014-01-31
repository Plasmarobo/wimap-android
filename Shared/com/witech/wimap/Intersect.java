package com.witech.wimap;


import java.util.List;

import com.witech.wimap.RadialDistance;

import Jama.Matrix;

public class Intersect {
	private double x;
	private double y;
	private double z;
	
	public Intersect(List<RadialDistance> L, double x, double y, double z)
	{
		Matrix guess = new Matrix(3,1);
		Matrix j = new Matrix(L.size(), 3);
		Matrix res = new Matrix(L.size(), 1);
		Matrix jt;
		//Initial Guess
		
		guess.set(0, 0, x);
		guess.set(1, 0, y);
		guess.set(2, 0, z);
		double r_square = 6.0;
		while(r_square > 5)
		{
			//Setup Matrices
			for(int i = 0; i < L.size(); ++i)
			{
				RadialDistance r = L.get(i);
				j.set(i, 0, derivative(guess.get(0,0), r.GetX()));
				j.set(i, 1, derivative(guess.get(1,0), r.GetY()));
				j.set(i, 2, derivative(guess.get(2,0), r.GetZ()));
				res.set(i, 0, residual(guess.get(0, 0), r.GetX(), guess.get(1, 0), r.GetY(), guess.get(2,0), r.GetZ(), r.GetDistance()));
			}
			//Perform Gaussian-Newton Elimination
			jt = j.transpose();
			Matrix inv = jt.times(j);
			if(inv.det() == 0)
				break;
			inv = inv.inverse();
			Matrix rr = inv.times(jt);
			rr = rr.times(res);
			guess.minusEquals(rr);
			r_square = 0.0;
			for(int i = 0; i < L.size(); ++i)
				r_square += Math.pow(residual(guess.get(0,0), L.get(i).GetX(), guess.get(1,0), L.get(i).GetY(), guess.get(2, 0), L.get(i).GetZ(), L.get(i).GetDistance()),2);
		}
		
	}
	
	private double derivative(double a, double x)
	{
		return 2*(x+a);
	}
	private double residual(double a, double x, double b, double y, double c, double z, double r)
	{
		return (Math.pow(r, 2.0) - (Math.pow(x+a, 2) + Math.pow(y+b, 2) + Math.pow(z+c, 2)));
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
