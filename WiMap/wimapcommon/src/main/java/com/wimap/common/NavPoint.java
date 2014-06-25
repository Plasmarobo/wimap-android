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

public class NavPoint implements APIObject {

    public static final String x_tag = "x";
    public static final String y_tag = "y";
    public static final String z_tag = "z";
    public static final String range_tag = "range";
    public static final String title_tag = "title";
    public static final String body_tag = "body";

    public double x;
    public double y;
    public double z;

    public double range;

    public String title;
    public String body;

    public NavPoint()
    {
        x = -1;
        y = -1;
        z = -1;

        range = -1;
        title = "Error";
        body = "error";
    }


    @Override
    public JSONObject ToJSON(){
        JSONObject json = new JSONObject();
        json.put(x_tag, x);
        json.put(y_tag, y);
        json.put(z_tag, z);
        json.put(range_tag, range);
        json.put(title_tag, title);
        json.put(body_tag, body);

        return json;
    }

    @Override
    public boolean FromJSON(JSONObject json) throws JSONException {
        x = json.getDouble(x_tag);
        y = json.getDouble(y_tag);
        z = json.getDouble(z_tag);
        range = json.getDouble(range_tag);
        title = json.getString(title_tag);
        body = json.getString(body_tag);
        return true;
    }

}
