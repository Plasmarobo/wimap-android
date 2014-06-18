/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api.templates;

import android.util.Log;

import com.wimap.api.transport.AsyncHTTP;
import com.wimap.api.transport.HTTPInterface;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class BasicAPI{

    private static String API_ROOT = "http://wimapnav.com/api";
    private static final String API_VERSION = "v1";
    private static final String API_KEY = "dummy_key";
    private static final int API_TIMEOUT = 1000; //ms

    public BasicAPI(String url)
    {
        this.API_ROOT = url;
    }

    public BasicAPI() {};

    abstract protected String GetEndpoint();
    abstract protected String GetAPIFieldName();

    public static String GetUrl(String endpoint)
    {
        return API_ROOT + "/" + API_VERSION + "/" + endpoint;
    }

    abstract protected boolean AddPushArguments(List<NameValuePair> arguments);
    abstract protected boolean AddPullArguments(List<NameValuePair> arguments);
    abstract protected boolean OnResult(HttpResponse response);

    public HttpRequest FormPushRequest()
    {
        HttpPost req = new HttpPost(GetUrl(GetEndpoint()));
        List<NameValuePair> arguments = new ArrayList<NameValuePair>();
        this.AddPushArguments(arguments);
        arguments.add(new BasicNameValuePair("api_key", this.API_KEY));
        Log.i("POST:", arguments.toString());
        try {
            req.setEntity(new UrlEncodedFormEntity(arguments));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return req;
    }


    public HttpRequest FormPullRequest()
    {
        StringBuilder url = new StringBuilder(BasicAPI.GetUrl(this.GetEndpoint()));
        List<NameValuePair> arguments = new ArrayList<NameValuePair>();
        this.AddPullArguments(arguments);
        arguments.add(new BasicNameValuePair("api_key", this.API_KEY));
        String html_safe = URLEncodedUtils.format(arguments, "utf-8");
        url.append("?");
        url.append(html_safe);
        Log.i("GET:", url.toString());
        return new HttpGet(url.toString());
    }

    public void AsyncPush(){
        HTTPInterface async_push = new HTTPInterface()
        {
            @Override
            public boolean PerformRequest(Integer progress)
            {
                HttpResponse resp = Push();
                if(resp == null)
                   return false;
                else
                    OnResult(resp);
                return true;
            }
        };
        AsyncHTTP http_task = new AsyncHTTP();
        http_task.execute(async_push);
    }

    public void SyncPush(){
        HTTPInterface async_push = new HTTPInterface()
        {
            @Override
            public boolean PerformRequest(Integer progress)
            {
                HttpResponse resp = Push();
                if(resp == null)
                    return false;
                else
                    OnResult(resp);
                return true;
            }
        };
        AsyncHTTP http_task = new AsyncHTTP();
        http_task.execute(async_push);
        try {
            http_task.get(API_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void AsyncPull(){
        HTTPInterface async_pull = new HTTPInterface()
        {
            @Override
            public boolean PerformRequest(Integer progress)
            {
                HttpResponse resp = Pull();
                if(resp == null)
                    return false;
                else
                    OnResult(resp);
                return true;
            }
        };
        AsyncHTTP http_task = new AsyncHTTP();
        http_task.execute(async_pull);
    }
    public void SyncPull()
    {
        HTTPInterface async_pull = new HTTPInterface()
        {
            @Override
            public boolean PerformRequest(Integer progress)
            {
                HttpResponse resp = Pull();
                if(resp == null)
                    return false;
                else
                    OnResult(resp);
                return true;
            }
        };
        AsyncHTTP http_task = new AsyncHTTP();
        http_task.execute(async_pull);
        try {
            http_task.get(API_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    protected HttpResponse Push(){
        HttpPost req = (HttpPost) FormPushRequest();
        // Execute HTTP Post Request
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse resp = httpclient.execute(req);
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

    protected HttpResponse Pull(){
        HttpGet req = (HttpGet) FormPullRequest();
        // Execute HTTP Post Request
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse resp = httpclient.execute(req);
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



}
