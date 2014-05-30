package com.wimap.api;

import android.util.Log;

import com.wimap.api.HTTPInterface;
import com.wimap.common.math.Intersect;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TracksAPI implements HTTPInterface {
	//private static final String API_KEY = "";
	public static final String TRACKS_URI = "http://www.wimapnav.com/api/v1/tracks";
	static List<Intersect> buffer;
	
	public TracksAPI()
	{
		if(buffer == null)
			buffer = new ArrayList<Intersect>();
	}
	
	public static void CommitPoint(Intersect p)
	{
		buffer.add(p);
	}

	@Override
	public boolean PerformRequest(Integer progress) {
		HttpPost req = new HttpPost(TracksAPI.TRACKS_URI);
		List<NameValuePair> arguments = new ArrayList<NameValuePair>();
		JSONArray track_json = new JSONArray();
		//Integer max = buffer.size();
		Iterator<Intersect> it = buffer.iterator();
		while(it.hasNext())
		{
			JSONObject entry = new JSONObject();
			Intersect p = it.next();
			try {
				entry.put("x", p.x);
				entry.put("y", p.y);
				entry.put("z", p.z);
				entry.put("x_confidence", p.x_conf);
				entry.put("y_confidence", p.y_conf);
				entry.put("z_confidence", p.z_conf);
				entry.put("site_id", 0);
				entry.put("user_id", 0);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			track_json.put(entry);
		}
		buffer.clear();
		arguments.add(new BasicNameValuePair("track", track_json.toString()));
		Log.i("POST:", arguments.toString());
		try {
			req.setEntity(new UrlEncodedFormEntity(arguments));
		} catch (UnsupportedEncodingException e) {
			// 	TODO Auto-generated catch block
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
}
