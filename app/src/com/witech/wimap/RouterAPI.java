package com.witech.wimap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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

import android.util.Log;










public class RouterAPI {

	private final String API_KEY = "";
	
	public static final String tag_x = "x";
	public static final String tag_y = "y";
	public static final String tag_z = "z";
	public static final String tag_ssid = "ssid";
	public static final String tag_uid = "uid";
	public static final String tag_power = "power";
	public static final String tag_freq = "frequency";
	public static final String site_id = "site_id";
	
	//private HttpClient httpclient;
	public static final String ROUTERS_URI = "http://www.wimapnav.com/api/v1/routers";
	private static List<AndroidRouter> cache;
	
	public static HttpResponse PerformGet()
	{
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet req = new HttpGet(ROUTERS_URI);
		try {
			HttpResponse resp = httpclient.execute(req);
			if(resp.getStatusLine().getStatusCode() == 200)
			return resp;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static List<AndroidRouter> JsonToCache(String json_str)
	{
		try {
			JSONArray routers_json = new JSONArray(json_str);
			cache = new ArrayList<AndroidRouter>();
			for(int i = 0; i < routers_json.length(); ++i)
			{
				JSONObject router_json = routers_json.getJSONObject(i);
				AndroidRouter model = new AndroidRouter(
						router_json.getDouble(tag_x), 
						router_json.getDouble(tag_y),
						router_json.getDouble(tag_z),
						router_json.getString(tag_ssid),
						router_json.getString(tag_uid),
						router_json.getDouble(tag_power),
						router_json.getDouble(tag_freq));
				cache.add(model);
				}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cache;
	}
	
	public static List<AndroidRouter> JsonToCache(HttpResponse resp, Integer progress)
	{

		InputStream inputStream = null;
		String json_str = "";
		try{
			HttpEntity entity = resp.getEntity();
			Header h = resp.getFirstHeader("Content-length");
			Integer total = Integer.parseInt(h.getValue());
			Integer bytes_read = new Integer(0);
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
	public RouterAPI()
	{
		this(new Integer(0));
	}
	
	public RouterAPI(Integer progress)
	{
		//Eventually pull a cache based on coarse/cached location
		
	}
	
	public static boolean Store(Router r)
	{
		HttpPost req = new HttpPost(RouterAPI.ROUTERS_URI);
		List<NameValuePair> arguments = new ArrayList<NameValuePair>();
		JSONObject router_json = new JSONObject();
		
		try {
			router_json.put("x", r.GetX());
			router_json.put("y", r.GetY());
			router_json.put("z", r.GetZ());
			router_json.put("site_id", r.GetSiteID());
			router_json.put("ssid", r.GetSSID());
			router_json.put("uid", r.GetUID());
			router_json.put("power", r.GetPower());
			router_json.put("frequency", r.GetFreq());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		arguments.add(new BasicNameValuePair("router", router_json.toString()));
		Log.i("POST:", arguments.toString());
		try {
			req.setEntity(new UrlEncodedFormEntity(arguments));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Execute HTTP Post Request
        try {
        	HttpClient httpclient = new DefaultHttpClient();
			HttpResponse resp = httpclient.execute(req);
			return resp.getStatusLine().getStatusCode() == 200;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public List<AndroidRouter> getRoutersBySSID(String ssid)
	{
		return cache;
	}
	public List<AndroidRouter> getRoutersByVenue(String venue)
	{
		return cache;
	}
	public Router getRouterByUID(String uid)
	{
		return cache.get(0);
	}
	

}
