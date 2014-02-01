package com.witech.wimap;


import java.util.List;

import com.witech.wimap.RadialDistance;

import Jama.Matrix;
import Jama.QRDecomposition;

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
		Matrix guess = new Matrix(3,1);
		Matrix j = new Matrix(L.size(), 3);
		Matrix delta_y = new Matrix(L.size(), 1, 0.0);
		Matrix delta_guess = new Matrix(3,1, 0.0);
		Matrix residuals; //Use this to figure out how "accurate" we are
		//Initial Guess
		
		guess.set(0, 0, x);
		guess.set(1, 0, y);
		guess.set(2, 0, z);
		double delta_guess_sum=150;
		while(delta_guess_sum > accuracy)
		{
			//Setup Matrices
			
			for(int i = 0; i < L.size(); ++i)
			{
				RadialDistance r = L.get(i);
				j.set(i, 0, deriv(guess.get(0,0), r.GetX()));
				j.set(i, 1, deriv(guess.get(1,0), r.GetY()));
				j.set(i, 2, deriv(guess.get(2,0), r.GetZ()));
				delta_y.set(i, 0, Math.pow(r.GetDistance(), 2) - funct(guess.get(0, 0), r.GetX(), guess.get(1, 0), r.GetY(), guess.get(2,0), r.GetZ()));
			}
			residuals = delta_y.minus(j.times(delta_guess));
			QRDecomposition qr = new QRDecomposition(j);
			Matrix QTdelta_y = qr.getQ().transpose().times(delta_y);
			Matrix Rn = qr.getR(); 
			//RnB = QTY
			delta_guess = QTdelta_y.solve(Rn);
			delta_guess = delta_guess.transpose();
			delta_guess.set(0, 0, 1/delta_guess.get(0,0));
			delta_guess.set(1, 0, 1/delta_guess.get(1,0));
			delta_guess.set(2, 0, 1/delta_guess.get(2,0));
			delta_guess_sum = Math.abs(delta_guess.get(0,0) + delta_guess.get(1,0) + delta_guess.get(2,0));
			guess.plusEquals(delta_guess);
		}
		this.x = guess.get(0, 0);
		this.y = guess.get(1, 0);
		this.z = guess.get(2, 0);
		
	}
	
	private double deriv(double a, double x)
	{
		return 2*(a-x);
	}
	private double funct(double a, double x, double b, double y, double c, double z)
	{
		return (Math.pow(a-x, 2) + Math.pow(b-y, 2) + Math.pow(c-z, 2));
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