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

public class Message implements APIObject {
    public static final String title_tag = "title";
    public static final String icon_tag = "icon";
    public static final String body_tag = "body";
    public static final String time_tag = "time";
    public static final String navpoint_tag = "navpoint_id";

    public String title;
    public String icon;
    public String body;
    public Date time;
    public int navpoint_id;


    @Override
    public JSONObject ToJSON(){
        JSONObject json = new JSONObject();
        json.put(title_tag,title);
        json.put(icon_tag,icon);
        json.put(body_tag, body);
        json.put(time_tag, time.getTime());
        json.put(navpoint_tag, navpoint_id);
        return json;
    }

    @Override
    public boolean FromJSON(JSONObject json) throws JSONException {
        title = json.getString(title_tag);
        icon = json.getString(icon_tag);
        body = json.getString(body_tag);
        time = new Date(json.getLong(time_tag));
        navpoint_id = json.getInt(navpoint_tag);
        return false;
    }

}
