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
		//Initial Guess
		int n = L.size();
		//Matrix A = new Matrix(n-1, 3, 0.0);
		//Matrix b = new Matrix(n-1, 1, 0.0);
		RealMatrix A = MatrixUtils.createRealMatrix(L.size(), 3);
		RealMatrix b = MatrixUtils.createRealMatrix(L.size(), 1);
		RadialDistance ref = L.get(0);
		RadialDistance iter;
		//Setup Least Squares Estimate
		
		for(int i = 1; i < n; ++i)
		{
			iter = L.get(i);
			
			
			b.setEntry(i, 0, (
						Math.pow(ref.GetDistance(), 2)
						-Math.pow(iter.GetDistance(), 2)
						-(
							Math.sqrt(
								Math.pow(iter.GetX()-ref.GetX(),2)
								+Math.pow(iter.GetY()-ref.GetY(), 2)
								+Math.pow(iter.GetZ()-ref.GetZ(), 2)
								)
						 )	
					)
			);
			double row[] = {
					iter.GetX()-ref.GetX(),
					iter.GetY()-ref.GetY(),
					iter.GetZ()-ref.GetZ()
					};
			A.setRow(i-1,row);
		}
		double guess_data[][] = {{x},{y},{z}};
		RealMatrix guess;// = MatrixUtils.createRealMatrix(guess_data);
		DecompositionSolver decomp;
		try {
			QRDecomposition sd = new QRDecomposition(A);
			decomp = sd.getSolver();
			guess = decomp.solve(b);
		}catch(SingularMatrixException ea){
			SingularValueDecomposition svd = new SingularValueDecomposition(A);
			decomp = svd.getSolver();
			try{
				guess = decomp.solve(b);
			}catch(SingularMatrixException ec){
				guess = MatrixUtils.createRealMatrix(guess_data);
			}
		}
		int iteration_limit = 100000;
		//Matrix R = guess;
		RealMatrix delta_y = MatrixUtils.createRealMatrix(L.size(), 1);
		RealMatrix J = MatrixUtils.createRealMatrix(L.size(), 3);
		for(int k = 0; k < iteration_limit; ++k)
		{
			//Setup
			for(int i = 0; i < L.size(); ++i)
			{
				iter = L.get(i);
				J.setRow(i, 
						new double[]{
					df(guess.getEntry(0, 0)-iter.GetX(), guess, iter),
					df(guess.getEntry(1, 0)-iter.GetY(), guess, iter),
					df(guess.getEntry(2, 0)-iter.GetZ(), guess, iter)
						});
				delta_y.setRow(i, new double[]{
					iter.GetDistance() - funct(guess, iter),	
				});
				
						
			}
			//Get next aproximate
			double ng_data[][] = {{0.0},{0.0},{0.0}};
			RealMatrix next_guess = MatrixUtils.createRealMatrix(ng_data);
			RealMatrix Qtdy;
			RealMatrix Rn;
			QRDecomposition qr = new QRDecomposition(J);
			Qtdy = qr.getQT().multiply(delta_y);
			Rn = qr.getR();
			try{
				DecompositionSolver solver = new QRDecomposition(Rn).getSolver();
				next_guess = solver.solve(Qtdy);
			}catch(SingularMatrixException ec)
			{
				DecompositionSolver solver = new SingularValueDecomposition(Rn).getSolver();
				next_guess = solver.solve(Qtdy);
			}
			double difference_sum = ((guess.getEntry(0, 0) - next_guess.getEntry(0, 0)) + (guess.getEntry(1, 0) - next_guess.getEntry(1, 0)) + (guess.getEntry(2,0) - next_guess.getEntry(2,0)));
			if(difference_sum < accuracy)
			{
				
				break;
			}
			guess = guess.add(next_guess);
		}
		this.x = guess.getEntry(0,0);
		this.y = guess.getEntry(1,0);
		this.z = guess.getEntry(2,0);
		
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
