/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api;

import android.util.Log;

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

    public class DistanceSample {
        public int router_id;
        public double power;
        public double distance;
        public boolean timestamped;
        public Date time;

        public DistanceSample()
        {
            router_id = -1;
            power = 0;
            distance = 0;
            timestamped = true;
            time = new Date();
        }
    }

    public static Queue<DistanceSample> point_buffer;
    public String DISTANCE_ENDPOINT = "distancesamples";

    public DistanceSampleAPI()
    {
        point_buffer = (Queue<DistanceSample>) new LinkedList<DistanceSample>();
    }

    public void AddPoint(DistanceSample sample)
    {
        point_buffer.add(sample);
    }

    public String GetEndpoint()
    {
        return DISTANCE_ENDPOINT;
    }

    @Override
    protected boolean AddPushArguments(List<NameValuePair> arguments) {
        JSONArray sample_array_json = new JSONArray();
        while(!this.point_buffer.isEmpty())
        {
            DistanceSample point = this.point_buffer.poll();
            if( point != null)
            {
                JSONObject sample_json = new JSONObject();
                if(point.router_id == -1)
                    return false;
                try {
                    sample_json.put(this.power_tag, point.power);
                    sample_json.put(this.distance_tag, point.distance);
                    sample_json.put(this.router_id_tag, point.router_id);
                    sample_json.put(this.timestamp_tag, point.time.getTime());

                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return false;
                }
                sample_array_json.put(sample_json);
            }
        }
        arguments.add(new BasicNameValuePair("samples", sample_array_json.toString()));
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


}
