/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api;

import com.wimap.api.templates.BasicAPI;
import com.wimap.common.DistanceSample;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class DistanceSampleAPI extends BasicAPI
{
    public static final String router_id_tag = "router_id";
    public static final String power_tag = "power";
    public static final String distance_tag = "distance";
    public static final String timestamp_tag = "time";

    public String DISTANCE_ENDPOINT = "distancesamples";
    public String DISTANCE_FIELD = "sample";

    @Override
    public String GetEndpoint()
    {
        return DISTANCE_ENDPOINT;
    }

    @Override
    protected String GetAPIFieldName() {
        return DISTANCE_FIELD;
    }

    @Override
    protected boolean AddPushArguments(List<NameValuePair> arguments) {
        return false;
    }

    @Override
    protected boolean AddPullArguments(List<NameValuePair> arguments) {
        return false;
    }

    @Override
    protected boolean OnResult(HttpResponse response) {
        return false;
    }


}
