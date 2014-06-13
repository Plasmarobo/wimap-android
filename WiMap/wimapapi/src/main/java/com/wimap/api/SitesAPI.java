/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wimap.api.templates.CachedAPI;
import com.wimap.common.APIObject;
import com.wimap.common.Site;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SitesAPI extends CachedAPI {


    public static final String SITES_ENDPOINT = "sites";
    public static final String SITES_FIELD = "site";
    public static final String SITES_PULL_FIELD = "location";

    public static final String lat_tag = "lat";
    public static final String long_tag = "long";
    public static final String range_tag = "range";

    protected List<Site> buffer;

    public SitesAPI(Context c)
    {
        super(c);
    }

    @Override
    public String GetEndpoint()
    {
        return SITES_ENDPOINT;
    }

    @Override
    public String GetAPIFieldName()
    {
        return SITES_FIELD;
    }

    @Override
    protected List<APIObject> JSONToCache(String json_str) throws JSONException {
        try {
            JSONArray sites_json = new JSONArray(json_str);
            cache = new ArrayList<APIObject>();
            while(sites_json.length() > 0)
            {
                Site s = new Site();
                s.FromJSONArray(sites_json);
                cache.add(s);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return cache;
    }

    @Override
    protected String GetLocalDBName() {
        return null;
    }

    @Override
    protected int GetLocalDBVersion() {
        return 0;
    }

    @Override
    protected SQLiteDatabase GetLocalDatabase() {
        return null;
    }

    @Override
    protected List<APIObject> LocalDBRead(SQLiteDatabase local_db, List<APIObject> dest) {
        return null;
    }

    @Override
    protected List<APIObject> LocalDBWrite(SQLiteDatabase local_db, List<APIObject> dest) {
        return null;
    }


    @Override
    protected boolean AddPullArguments(List<NameValuePair> arguments) {
        JSONObject json = new JSONObject();
        try {
            json.put(lat_tag, 0.0);
            json.put(long_tag, 0.0);
            json.put(range_tag, 1.0);
        }catch(JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        arguments.add(new BasicNameValuePair(SITES_PULL_FIELD, json.toString()));
        return true;
    }






}
