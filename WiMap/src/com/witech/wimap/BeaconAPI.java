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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class BeaconAPI implements HTTPInterface {
	protected String ssid;
	protected String uid;
	protected int BeaconId;
	
	
	public BeaconAPI(String ssid, String uid)
	{
		this.ssid = ssid;
		this.uid = uid;
		this.BeaconId = -1;
	}
	
	protected class DistanceSampleAPI implements HTTPInterface
	{
		public double power;
		public double distance;
		
		public DistanceSampleAPI(double power, double distance)
		{
			this.power = power;
			this.distance = distance;
		}

		@Override
		public boolean PerformRequest(Integer progress) {
			HttpPost req = new HttpPost(new String("http://plasmarobo.linuxd.org:3000/distance_samples.json"));
			List<NameValuePair> arguments = new ArrayList<NameValuePair>();
			JSONObject router_json = new JSONObject();
			if(BeaconId == -1)
				return false;
			try {
				router_json.put("power", this.power);
				router_json.put("distance", this.distance);
				router_json.put("beacon_id", BeaconId);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			arguments.add(new BasicNameValuePair("beacon", router_json.toString()));
			Log.i("POST:", arguments.toString());
			try {
				req.setEntity(new UrlEncodedFormEntity(arguments));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse resp;
			try {
				resp = httpclient.execute(req);
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
	
	public void CommitSample(double power, double distance)
	{
		AsyncHTTP http = new AsyncHTTP();
		http.execute(new DistanceSampleAPI(power, distance));
	}
	
	@Override
	public boolean PerformRequest(Integer progress) {
		HttpPost req = new HttpPost(new String("http://plasmarobo.linuxd.org:3000/beacons.json"));
		List<NameValuePair> arguments = new ArrayList<NameValuePair>();
		JSONObject router_json = new JSONObject();
		
		try {
			router_json.put("name", ssid);
			router_json.put("uid", uid);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		arguments.add(new BasicNameValuePair("beacon", router_json.toString()));
		Log.i("POST:", arguments.toString());
		try {
			req.setEntity(new UrlEncodedFormEntity(arguments));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Execute HTTP Post Request
		String json_str = "";
		InputStream inputStream = null;
        try {
        	HttpClient httpclient = new DefaultHttpClient();
			HttpResponse resp = httpclient.execute(req);
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
			JSONObject json;
			try {
				json = new JSONObject(json_str);
				this.BeaconId = json.getInt("id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
