package com.wimap.services;

import com.wimap.math.Router;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Property of WiMap and Austen Higgins-Cassidy
 * No Reproduction or Use without explicit permission including
 * Signature of the CTO of WiMap
 * <p/>
 * Created by Austen on 4/26/2014.
 */
public class WiMapServiceScanFilter {
    protected Router source;
    protected final double spike_tolerance = 5;
    protected double weight_sum;
    protected Double last_derivative;
    protected Double last_weight;

    protected class WeightedPoint {
        public double value;
        public double weight;

        public WeightedPoint()
        {
            this.value = 0;
            this.weight = 1;
        }

        public WeightedPoint(double value)
        {
            this.value = value;
            this.weight = 1;
        }

        public WeightedPoint(double value, double weight)
        {
            this.weight = weight;
            this.value = value;
        }

        public WeightedPoint(WeightedPoint rhs)
        {
            this.weight = rhs.weight;
            this.value = rhs.value;
        }

        public double GetResult()
        {
            return this.weight * this.value;
        }

    }
    protected LinkedList<WeightedPoint> window;
    protected int length;

    public WiMapServiceScanFilter(Router src, int length)
    {
        this.source = src;
        this.length = length;
        this.window = new LinkedList<WeightedPoint>();
        for(int i = 0; i < length; ++i)
        {
            this.window.push(new WeightedPoint(0,0));
        }
        this.last_derivative = new Double(0);
        this.last_weight = new Double(0);
        this.weight_sum = length*(length+1)/2;
    }



    public double Filter(Double value)
    {
        this.window.removeFirst();
        Double derivative = value - this.window.getLast().value;

        if( Math.abs(derivative) > spike_tolerance)
        {
            //Patch the list
            last_weight = (last_derivative + this.window.getLast().value) / value;
        }else{
            last_weight = new Double(1);
        }

        WeightedPoint current = new WeightedPoint(value, last_weight);
        this.window.push(current);
        double age_weight = 0;
        double filtered_value = 0;
        for(int i = 0; i < length; ++i)
        {
            age_weight = i + 1;
            filtered_value = this.window.get(i).GetResult() * age_weight;
        }
        filtered_value /= this.weight_sum;
        return filtered_value;
    }

    public void SetWindowSize(int length)
    {
        if(length < this.length) {
            while(this.window.size() > length)
            {
                this.window.removeFirst();
            }
            this.length = length;
        }
        if(length > this.length) {
            while(this.window.size() < length)
            {
                this.window.push(new WeightedPoint(this.window.getLast()));
            }
            this.length = length;
        }
        this.weight_sum = length * (length + 1) / 2;
    }
}
