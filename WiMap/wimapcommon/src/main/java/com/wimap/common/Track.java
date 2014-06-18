/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.common;

import com.wimap.common.math.Intersect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Track implements APIObject {

    public static final String x_tag = "x";
    public static final String y_tag = "y";
    public static final String z_tag = "z";
    public static final String x_confidence_tag = "x_confidence";
    public static final String y_confidence_tag = "y_confidence";
    public static final String z_confidence_tag = "z_confidence";
    public static final String site_id_tag = "site_id";
    public static final String user_id_tag = "user_id";
    public static final String timestamp_tag = "timestamp";

    public Intersect location;
    public int site_id;
    public int user_id;
    public Date time;

    public Track()
    {
        location = null;
        site_id = -1;
        user_id = -1;
        time = new Date();
    }

    public Track(Intersect location)
    {
        this.location = location;
        this.site_id = -1;
        this.user_id = -1;
        time = new Date();
    }

    public Track(Intersect location, int site_id, int user_id)
    {
        this.location = location;
        this.site_id = site_id;
        this.user_id = user_id;
        time = new Date();
    }

    @Override
    public JSONObject ToJSON(){
        JSONObject entry = new JSONObject();
            entry.put(x_tag, this.location.x);
            entry.put(y_tag, this.location.y);
            entry.put(z_tag, this.location.z);
            entry.put(x_confidence_tag, this.location.x_conf);
            entry.put(y_confidence_tag, this.location.y_conf);
            entry.put(z_confidence_tag, this.location.z_conf);
            entry.put(site_id_tag, this.site_id);
            entry.put(user_id_tag, this.user_id);
            entry.put(timestamp_tag, this.time.toString());
        return entry;
    }

    @Override
    public boolean FromJSON(JSONObject json) throws JSONException {
        return false;
    }

    @Override
    public JSONObject FromJSONArray(JSONArray json) throws JSONException {
        return null;
    }
}
