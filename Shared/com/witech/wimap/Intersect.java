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
		RealMatrix AtA = A.transpose().multiply(A);
		double guess_data[][] = {{0.0},{0.0},{0.0}};
		RealMatrix guess = MatrixUtils.createRealMatrix(guess_data);
		DecompositionSolver decomp;
		try {
			QRDecomposition sd = new QRDecomposition(AtA);
			decomp = sd.getSolver();
			guess = decomp.getInverse().multiply(A.transpose().multiply(b));
		}catch(SingularMatrixException ea)
		{
			QRDecomposition sd = new QRDecomposition(A);
			decomp = sd.getSolver();
			try{
				guess = decomp.solve(b);
				//guess = qr.getR().solve(qr.getQ().transpose().times(b));
			}catch(SingularMatrixException eb)
			{
				SingularValueDecomposition svd = new SingularValueDecomposition(A);
				decomp = svd.getSolver();
				try{
					guess = decomp.solve(b);
				}catch(SingularMatrixException ec){
					
				}
			}
		
		}
		int iteration_limit = 100000;
		//Matrix R = guess;
		double jtjstore[][]={{0.0, 0.0, 0.0},{0.0, 0.0, 0.0},{0.0, 0.0, 0.0}};
		RealMatrix JtJ = MatrixUtils.createRealMatrix(jtjstore);
		double jtfstore[][]={{0.0},{0.0},{0.0}};
		RealMatrix Jtf = MatrixUtils.createRealMatrix(jtfstore);
		for(int k = 0; k < iteration_limit; ++k)
		{
			//Setup
			for(int i = 0; i < L.size(); ++i)
			{
				iter = L.get(i);
				double field[][] = 
					{
						{
							df(Math.pow(guess.getEntry(0,0)-iter.GetX(),2), guess, iter),
							df((guess.getEntry(0,0)-iter.GetX())*(guess.getEntry(1, 0)-iter.GetY()), guess, iter),
							df((guess.getEntry(0,0)-iter.GetX())*(guess.getEntry(2,0)-iter.GetZ()), guess, iter),
						},
						{
							df((guess.getEntry(0,0)-iter.GetX())*(guess.getEntry(1, 0)-iter.GetY()), guess, iter),
							df(Math.pow(guess.getEntry(1,0)-iter.GetY(),2), guess, iter),
							df((guess.getEntry(1,0)-iter.GetY())*(guess.getEntry(2,0)-iter.GetZ()), guess, iter),
						},
						{	
							df((guess.getEntry(0,0)-iter.GetX())*(guess.getEntry(2,0)-iter.GetZ()), guess, iter),
							df((guess.getEntry(1,0)-iter.GetY())*(guess.getEntry(2,0)-iter.GetZ()), guess, iter),
							df(Math.pow(guess.getEntry(2,0)-iter.GetZ(),2), guess, iter),
						},
					};
				
				JtJ.add(JtJ.add(MatrixUtils.createRealMatrix(field)));
				double f[][] =
					{
						{df((guess.getEntry(0,0)-iter.GetX())*funct(guess,iter), guess, iter)},
						{df((guess.getEntry(1,0)-iter.GetY())*funct(guess,iter), guess, iter)},
						{df((guess.getEntry(2,0)-iter.GetZ())*funct(guess,iter), guess, iter)}
					};
				Jtf.add(Jtf.add(MatrixUtils.createRealMatrix(f)));		
			}
			//Get next aproximate
			double r_data[][] = {{0.0},{0.0},{0.0}};
			RealMatrix R = MatrixUtils.createRealMatrix(r_data);
			
			QRDecomposition qr = new QRDecomposition(JtJ);
			try{
				R = guess.subtract(qr.getSolver().getInverse().multiply(Jtf));
			}catch(SingularMatrixException ec)
			{
				
			}
			double difference_sum = ((guess.getEntry(0, 0) - R.getEntry(0, 0)) + (guess.getEntry(1, 0) - R.getEntry(1, 0)) + (guess.getEntry(2,0) - R.getEntry(2,0)));
			if(difference_sum < accuracy)
			{
				break;
			}
			guess = R;
		}
		this.x = guess.getEntry(0,0);
		this.y = guess.getEntry(1,0);
		this.z = guess.getEntry(2,0);
		
	}

	private double df(double value, RealMatrix guess, RadialDistance R)
	{
		return value/Math.pow(funct(guess, R) + R.GetDistance(), 2);
	}
	private double funct(RealMatrix xyz, RadialDistance R)
	{
		return funct(xyz.getEntry(0,0), xyz.getEntry(1,0), xyz.getEntry(2,0),R);
	}
	private double funct(double x, double y, double z, RadialDistance R)
	{
		return Math.sqrt((Math.pow(x-R.GetX(), 2) + Math.pow(y-R.GetY(), 2) + Math.pow(z-R.GetZ(), 2)))-R.GetDistance();
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
