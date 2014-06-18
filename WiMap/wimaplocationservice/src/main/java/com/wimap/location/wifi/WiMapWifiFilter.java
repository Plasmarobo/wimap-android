/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.location.wifi;

import android.util.Log;

import com.wimap.api.RouterAPI;
import com.wimap.common.Router;
import com.wimap.location.models.AndroidRouter;
import com.wimap.location.models.BasicResult;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class WiMapWifiFilter extends AndroidRouter {

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

    public WiMapWifiFilter(Router src, int length)
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

    public static HashMap<String, WiMapWifiFilter> GenerateFilters(RouterAPI api, int sample_count)
    {
        HashMap<String, WiMapWifiFilter> filters = new HashMap<String, WiMapWifiFilter>();
        List<AndroidRouter> routers = (List<AndroidRouter>)(List<?>)api.Routers();
        for(AndroidRouter rt : routers)
        {
            filters.put(rt.uid, new WiMapWifiFilter(rt, sample_count));
        }
        return filters;
    }
}
