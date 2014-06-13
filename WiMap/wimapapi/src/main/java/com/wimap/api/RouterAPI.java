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
import com.wimap.common.Router;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RouterAPI extends CachedAPI {

	public static final String ROUTERS_ENDPOINT = "routers";
    public static final String ROUTERS_FIELD = "router";
    public static final String SITE_FIELD = "site_id";

    protected int current_site;

    @Override
    public String GetEndpoint()
    {
        return ROUTERS_ENDPOINT;
    }

    @Override
    protected String GetAPIFieldName() {
        return ROUTERS_FIELD;
    }

    @Override
    protected boolean AddPullArguments(List<NameValuePair> arguments) {
        arguments.add(new BasicNameValuePair(SITE_FIELD, Integer.toString(current_site)));
        return true;
    }


    public List<Router> Routers()
	{
		return (List<Router>)(List<?>) cache;
	}

    @Override
	public List<APIObject> JSONToCache(String json_str)
	{
		try {
			JSONArray routers_json = new JSONArray(json_str);
			cache = new ArrayList<APIObject>();
			while(routers_json.length() > 0)
            {
                Router r = new Router();
                r.FromJSONArray(routers_json);
                cache.add(r);
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

    public void SetSite(int site)
    {
        if(current_site != site)
        {
            current_site = site;
            Pull();
        }
    }

	public RouterAPI(Context context)
	{
		this(context, new Integer(0), new ArrayList<Router>());
	}

	public RouterAPI(Context context, Integer progress, ArrayList<Router> local_list)
	{
        super(context);
		//Eventually pull a cache based on coarse/cached location
        boolean upToDate = true;
        current_site = 0;
		//cache = new ArrayList<Router>();
        if(upToDate) {
            cache = (List<APIObject>)(List<?>)local_list;
        }else{
            Pull();
        }
	}


}
