/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api;
import android.content.Context;
import android.util.Log;

import com.wimap.common.Router;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


public class RouterAPI extends BasicAPI {

	public static final String tag_x = "x";
	public static final String tag_y = "y";
	public static final String tag_z = "z";
	public static final String tag_ssid = "ssid";
	public static final String tag_uid = "uid";
	public static final String tag_power = "power";
	public static final String tag_freq = "frequency";
	public static final String tag_site_id = "site_id";
    public static final String tag_tx_power = "tx_power";

	public static final String ROUTERS_ENDPOINT = "routers";

    @Override
    public String GetEndpoint()
    {
        return ROUTERS_ENDPOINT;
    }
	private static Queue<Router> router_buffer;
    private static List<Router> cache;

    protected boolean AddPushArguments(List<NameValuePair> arguments){
        JSONArray router_array_json = new JSONArray();
        while(!router_buffer.isEmpty())
        {
            Router r = router_buffer.poll();
            if( r != null)
            {
                JSONObject router_json = new JSONObject();
                try {
                    router_json.put(tag_x, r.GetX());
                    router_json.put(tag_y, r.GetY());
                    router_json.put(tag_z, r.GetZ());
                    router_json.put(tag_site_id, r.GetSiteID());
                    router_json.put(tag_ssid, r.GetSSID());
                    router_json.put(tag_uid, r.GetUID());
                    router_json.put(tag_power, r.GetPower());
                    router_json.put(tag_freq, r.GetFreq());
                    router_json.put(tag_tx_power, r.GetTxPower());
                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return false;
                }
                router_array_json.put(router_json);
            }
        }
        arguments.add(new BasicNameValuePair("routers", router_array_json.toString()));
        return true;
    }

    protected boolean AddPullArguments(List<NameValuePair> arguments){
        return true;
    }
    protected boolean OnResult(HttpResponse response){
        JsonToCache(response, new Integer(0));
        return true;
    }

	public List<Router> Routers()
	{
		return cache;
	}

	public static List<Router> JsonToCache(String json_str)
	{
		try {
			JSONArray routers_json = new JSONArray(json_str);
			cache = new ArrayList<Router>();
			for(int i = 0; i < routers_json.length(); ++i)
			{
				JSONObject router_json = routers_json.getJSONObject(i);
				Router model = new Router(
						router_json.getDouble(tag_x),
						router_json.getDouble(tag_y),
						router_json.getDouble(tag_z),
						router_json.getString(tag_ssid),
						router_json.getString(tag_uid),
                        router_json.getInt(tag_site_id),
						router_json.getDouble(tag_power),
						router_json.getDouble(tag_freq),
                        router_json.getDouble((tag_tx_power)));
				cache.add(model);
				}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cache;
	}

	public static List<Router> JsonToCache(HttpResponse resp, Integer progress)
	{

		InputStream inputStream = null;
		String json_str = "";
		try{
			HttpEntity entity = resp.getEntity();
			Header h = resp.getFirstHeader("Content-length");
			Integer total = Integer.parseInt(h.getValue());
			Integer bytes_read = Integer.valueOf(0);
			inputStream = entity.getContent();
			// json is UTF-8 by default
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
				bytes_read = line.length();
				progress = 200*bytes_read/total;
			}
			json_str = sb.toString();
		} catch (Exception e) {
			// Oops
		}
		finally {
			try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
		}
		return JsonToCache(json_str);

	}
	protected void JsonToRouter(HttpResponse resp)
	{

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
            cache = local_list;
        }else{
            JsonToCache(SyncPull(), progress);
        }
	}

	public boolean Store(Router r)
	{
        router_buffer.add(r);
        return true;
	}
	public List<Router> getRoutersBySSID(String ssid)
	{
		return cache;
	}
	public List<Router> getRoutersByVenue(String venue)
	{
		return cache;
	}
	public Router getRouterByUID(String uid)
	{
		return cache.get(0);
	}


}
