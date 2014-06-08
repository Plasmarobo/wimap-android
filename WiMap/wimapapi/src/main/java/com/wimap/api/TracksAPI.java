/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class TracksAPI extends BasicAPI {

	public static final String TRACK_ENDPOINT = "tracks";
    public static final String x_tag = "x";
    public static final String y_tag = "y";
    public static final String z_tag = "z";
    public static final String x_confidence_tag = "x_confidence";
    public static final String y_confidence_tag = "y_confidence";
    public static final String z_confidence_tag = "z_confidence";
    public static final String site_id_tag = "site_id";
    public static final String user_id_tag = "user_id";


    @Override
    protected boolean AddPushArguments(List<NameValuePair> arguments) {
        JSONArray track_json = new JSONArray();
        //Integer max = buffer.size();
        Iterator<Track> it = buffer.iterator();
        while(it.hasNext())
        {
            JSONObject entry = new JSONObject();
            Track point = it.next();
            try {
                entry.put(x_tag, point.location.x);
                entry.put(y_tag, point.location.y);
                entry.put(z_tag, point.location.z);
                entry.put(x_confidence_tag, point.location.x_conf);
                entry.put(y_confidence_tag, point.location.y_conf);
                entry.put(z_confidence_tag, point.location.z_conf);
                entry.put(site_id_tag, point.site_id);
                entry.put(user_id_tag, point.user_id);
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return false;
            }
            track_json.put(entry);
        }
        buffer.clear();
        arguments.add(new BasicNameValuePair("track", track_json.toString()));
        return true;
    }

    @Override
    protected boolean AddPullArguments(List<NameValuePair> arguments) {
        return false;
    }

    @Override
    protected boolean OnResult(HttpResponse response) {
        return false;
    }

    public class Track {
        public Intersect location;
        public int site_id;
        public int user_id;

        public Track()
        {
            location = null;
            site_id = -1;
            user_id = -1;
        }

        public Track(Intersect location)
        {
            this.location = location;
            this.site_id = -1;
            this.user_id = -1;
        }

        public Track(Intersect location, int site_id, int user_id)
        {
            this.location = location;
            this.site_id = site_id;
            this.user_id = user_id;
        }
    }

    private Queue<Track> buffer;

	public TracksAPI()
	{
		buffer = new LinkedList<Track>();
	}
	
	public void AddPoint(Intersect p)
	{
		buffer.add(new Track(p));
	}

}
