/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public class DistanceSample implements APIObject{

    public static final String router_id_tag = "router_id";
    public static final String power_tag = "power";
    public static final String distance_tag = "distance";
    public static final String timestamped_tag = "timestamped";
    public static final String time_tag = "time";
    public static final String set_id_tag = "set_id";

    public int router_id;
    public double power;
    public double distance;
    public boolean timestamped;
    public Date time;
    public int set_id;

    public DistanceSample()
    {
        router_id = -1;
        power = 0;
        distance = 0;
        timestamped = true;
        time = new Date();
        set_id = -1;
    }

    @Override
    public JSONObject ToJSON(){
        JSONObject json = new JSONObject();
        json.put(router_id_tag, router_id);
        json.put(power_tag, power);
        json.put(distance_tag, distance);
        json.put(timestamped_tag, timestamped);
        json.put(time_tag, time.getTime());
        json.put(set_id_tag, set_id);
        return json;
    }

    @Override
    public boolean FromJSON(JSONObject json) throws JSONException {
        router_id = json.getInt(router_id_tag);
        power = json.getDouble(power_tag);
        distance = json.getDouble(distance_tag);
        timestamped = json.getBoolean(timestamped_tag);
        time = new Date(json.getLong(time_tag));
        set_id = json.getInt(set_id_tag);
        return true;
    }

}
