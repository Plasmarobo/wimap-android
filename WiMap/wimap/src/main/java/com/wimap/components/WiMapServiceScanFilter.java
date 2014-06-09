/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.components;

import android.util.Log;

import com.wimap.api.RouterAPI;
import com.wimap.common.Router;
import com.wimap.services.WiMapService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Property of WiMap and Austen Higgins-Cassidy
 * No Reproduction or Use without explicit permission including
 * Signature of the CTO of WiMap
 *
 * Created by Austen on 4/26/2014.
 */
public class WiMapServiceScanFilter extends AndroidRouter {

    protected final double spike_tolerance = 5;
    protected double weight_sum;
    protected Double last_derivative;
    protected Double last_weight;
    protected Double next_value;

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
        super(src);
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

    public void filteredMerge(BasicResult R)
    {
        this.insertValue((double)R.GetPower());
    }

    public void insertValue(Double value)
    {
        this.next_value = value;
    }

    public double Filter()
    {
        this.window.removeLast();
        Double derivative = next_value - this.window.getLast().value;

        if( Math.abs(derivative) > spike_tolerance)
        {
            //Patch the list
            last_weight = (derivative + this.window.getLast().value) / next_value;
        }else{
            last_weight = new Double(1);
        }

        WeightedPoint current = new WeightedPoint(next_value, last_weight);
        this.window.push(current);
        double age_weight = 0;
        double filtered_value = 0;
        for(int i = 0; i < length; ++i)
        {
            age_weight = length - i;
            filtered_value += this.window.get(i).GetResult() * age_weight;
        }
        filtered_value /= this.weight_sum;
        Log.d("Filter", new Double(filtered_value).toString());
        this.power = filtered_value;
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

    public static HashMap<String, WiMapServiceScanFilter> GenerateFilters(RouterAPI api, int sample_count)
    {
        HashMap<String, WiMapServiceScanFilter> filters = new HashMap<String, WiMapServiceScanFilter>();
        List<AndroidRouter> routers = (List<AndroidRouter>)(List<?>)api.Routers();
        for(AndroidRouter rt : routers)
        {
            filters.put(rt.GetUID(), new WiMapServiceScanFilter(rt, sample_count));
        }
        return filters;
    }
}
