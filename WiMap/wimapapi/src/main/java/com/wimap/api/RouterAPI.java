/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api;
import android.content.Context;

import com.wimap.api.templates.CachedAPI;
import com.wimap.common.APIObject;
import com.wimap.common.Router;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class RouterAPI extends CachedAPI {

	public static final String ROUTERS_ENDPOINT = "routers";
    public static final String ROUTERS_FIELD = "router";

    @Override
    public String GetEndpoint()
    {
        return ROUTERS_ENDPOINT;
    }

    @Override
    protected String GetAPIFieldName() {
        return ROUTERS_FIELD;
    }

    protected boolean AddPushArguments(List<NameValuePair> arguments){
        JSONArray router_array_json = new JSONArray();
        while(!queue.isEmpty())
        {
            APIObject r = queue.poll();
            if( r != null)
            {
                router_array_json.put(r.ToJSON());
            }
        }
        arguments.add(new BasicNameValuePair(GetAPIFieldName(), router_array_json.toString()));
        return true;
    }

    protected boolean AddPullArguments(List<NameValuePair> arguments){
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


	public RouterAPI(Context context)
	{
		this(context, new Integer(0), new ArrayList<Router>());
	}

	public RouterAPI(Context context, Integer progress, ArrayList<Router> local_list)
	{
		//Eventually pull a cache based on coarse/cached location
        boolean upToDate = true;
		//cache = new ArrayList<Router>();
        if(upToDate) {
            cache = (List<APIObject>)(List<?>)local_list;
        }else{
            Pull();
        }
	}


}
