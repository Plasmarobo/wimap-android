/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api;

import com.wimap.api.templates.CachedAPI;
import com.wimap.common.APIObject;
import com.wimap.common.NavPoint;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class NavPointAPI extends CachedAPI {

    public static final String NAVPOINTS_ENDPOINT = "navpoints";
    public static final String NAVPOINTS_FIELD = "navpoint";

    public static final String NAVPOINTS_SITE_ID = "site_id";

    protected int current_site;
    protected int cached_site;

    public NavPointAPI()
    {
        super();
        current_site = 0;
    }

    public void SetSite(int site)
    {
        current_site = site;
        if(current_site != cached_site)
        {
            current_site = site;
            UpdateNow();
        }
    }

    @Override
    protected List<APIObject> JSONToCache(String json_str) throws JSONException {
        JSONArray navpoints_json = new JSONArray(json_str);
        cache = new ArrayList<APIObject>();
        while(navpoints_json.length() > 0)
        {
            NavPoint p = new NavPoint();
            p.FromJSONArray(navpoints_json);
            cache.add(p);
        }
        return cache;
    }

    @Override
    protected String GetEndpoint() {
        return NAVPOINTS_ENDPOINT;
    }

    @Override
    protected String GetAPIFieldName() {
        return NAVPOINTS_FIELD;
    }

    @Override
    protected boolean AddPullArguments(List<NameValuePair> arguments) {
        arguments.add(new BasicNameValuePair(NAVPOINTS_SITE_ID, Integer.toString(current_site)));
        return true;
    }
}
