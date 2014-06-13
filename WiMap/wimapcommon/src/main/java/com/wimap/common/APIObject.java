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

public interface APIObject {

    public JSONObject ToJSON();
    public boolean FromJSON(JSONObject json) throws JSONException;
    public JSONObject FromJSONArray(JSONArray json) throws JSONException;
}
