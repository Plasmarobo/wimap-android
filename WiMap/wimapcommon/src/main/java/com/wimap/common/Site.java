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

    public int id;
    public String name;

    public Site()
    {
        id = 0;
        name = "ERROR";
    }

    public Site(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    @Override
    public JSONObject ToJSON() throws JSONException {
        JSONObject item = new JSONObject();
        item.put(id_tag, id);
        item.put(name_tag, name);
        return item;
    }

    @Override
    public boolean FromJSON(JSONObject json) throws JSONException {
        this.id = json.getInt(id_tag);
        this.name = json.getString(name_tag);
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
