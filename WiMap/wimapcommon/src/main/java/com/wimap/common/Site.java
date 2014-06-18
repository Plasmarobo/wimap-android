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

public class Site implements APIObject
{
    public static final String id_tag = "id";
    public static final String name_tag = "name";
    public static final String lat_tag = "lat";
    public static final String long_tag = "long";
    public static final String range_tag = "range";

    public int id;
    public String name;
    public double lattitude;
    public double longitude;
    public double range;

    public Site()
    {
        id = 0;
        name = "ERROR";
        lattitude = 0;
        longitude = 0;
        range = 0;
    }

    public Site(int id, String name)
    {
        this.id = id;
        this.name = name;
        lattitude = 0;
        longitude = 0;
        range = 0;
    }

    @Override
    public JSONObject ToJSON(){
        JSONObject item = new JSONObject();
        item.put(id_tag, id);
        item.put(name_tag, name);
        item.put(lat_tag, lattitude);
        item.put(long_tag, longitude);
        item.put(range_tag, range);
        return item;
    }

    @Override
    public boolean FromJSON(JSONObject json) throws JSONException {
        this.id = json.getInt(id_tag);
        this.name = json.getString(name_tag);
        this.lattitude = json.getDouble(lat_tag);
        this.longitude = json.getDouble(long_tag);
        this.range = json.getDouble(range_tag);
        return true;
    }

    @Override
    public JSONObject FromJSONArray(JSONArray json) throws JSONException {
        JSONObject item = json.getJSONObject(0);
        json.remove(0);
        this.FromJSON(item);
        return item;
    }
}
