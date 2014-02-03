package com.witech.wimap;


import java.util.List;


import org.apache.commons.math3.analysis.DifferentiableMultivariateVectorFunction;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunction;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunctionJacobian;
import org.apache.commons.math3.optim.nonlinear.vector.Target;
import org.apache.commons.math3.optim.nonlinear.vector.Weight;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;


public class Intersect {
	public double x;
	public double y;
	public double z;
	public double x_conf;
	public double y_conf;
	public double z_conf;
	
	public Intersect(List<RadialDistance> L, double x, double y, double z)
	{
		this(L, x, y, z, 1E-8);
	}
	
	public Intersect(List<RadialDistance> L, double x, double y, double z, double accuracy)
	{
		InitialGuess guess = new InitialGuess(new double[]{x,y,z});
		//SimpleBounds limits = new SimpleBounds(null, null);
		double r_vector[] = new double[L.size()];
		double w_vector[] = new double[L.size()];
		for(int i = 0; i < L.size(); ++i)
		{
			r_vector[i] = L.get(i).GetDistance();
			w_vector[i] = 1/Math.pow(r_vector[i],2);
		}
		Target target = new Target(r_vector);
		
		Weight weight = new Weight(w_vector);
		MultivariateVectorFunction sphere_function = new SphereFunction(L);
		ModelFunction sphere_model = new ModelFunction(sphere_function);
		MultivariateMatrixFunction sphere_jacobian = new SphereJacobian(L);
		ModelFunctionJacobian model_jacobian = new ModelFunctionJacobian(sphere_jacobian);
		//GaussNewtonOptimizer minimizer = new GaussNewtonOptimizer(new SimpleVectorValueChecker(0.01, 1));
		LevenbergMarquardtOptimizer minimizer = new LevenbergMarquardtOptimizer();
		PointVectorValuePair sln = minimizer.optimize(new MaxEval(1000000), guess, null, target,weight, sphere_model, model_jacobian);
		double solution[] = sln.getPoint();
		this.x = solution[0];
		this.y = solution[1];
		this.z = solution[2];
		double confidence[] = sln.getValue();
		this.x_conf = confidence[0];
		this.y_conf = confidence[1];
		this.z_conf = confidence[2];
	}
	private class SphereFunction implements DifferentiableMultivariateVectorFunction
	{
		protected List<RadialDistance> data;
		public SphereFunction(List<RadialDistance> data)
		{
			this.data = data;
		}
		@Override
		public double[] value(double[] point) throws IllegalArgumentException {
			//args x, xi, y, yi, z, zi
			double result[] = new double[data.size()];
			for(int i = 0; i < data.size(); ++i)
				{
				RadialDistance iter = data.get(i);
				result[i] = Math.sqrt(
					Math.pow(point[0]-iter.GetX(),2)+
					Math.pow(point[1]-iter.GetY(),2)+
					Math.pow(point[2]-iter.GetZ(),2)
					);
				}	
			return result;
		}
		@Override
		public MultivariateMatrixFunction jacobian() {
			return new SphereJacobian(data);
		}
		
	}
	private class SphereJacobian implements MultivariateMatrixFunction
	{
		protected List<RadialDistance> data;
		public SphereJacobian(List<RadialDistance> data)
		{
			this.data = data;
		}
		@Override
		public double[][] value(double[] args) throws IllegalArgumentException {
			double matrix [][] = new double[data.size()][3];
			for(int i = 0; i < data.size(); ++i)
			{
				RadialDistance iter = data.get(i);
				matrix[i][2] = matrix[i][1] = matrix[i][0] = 1/
						Math.sqrt(
								Math.pow(iter.GetX()-args[0], 2)+
								Math.pow(iter.GetY()-args[1], 2)+
								Math.pow(iter.GetZ()-args[2], 2)
								);
				matrix[i][0] = (args[0]-iter.GetX())*matrix[i][0];
				matrix[i][1] = (args[1]-iter.GetY())*matrix[i][1];
				matrix[i][2] = (args[2]-iter.GetZ())*matrix[i][2];
			}
			return matrix;
		}
	}
	

}
