/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api.templates;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wimap.common.APIObject;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class CachedAPI extends BasicAPI {
    //If the remote database has the same sync key, we have the most up to date version of it
    protected List<APIObject> cache;
    protected Queue<APIObject> queue;
    protected static final int queue_limit=64;
    protected SQLiteDatabase local_db;

    public CachedAPI(Context c)
    {
        LoadCache(c);
        queue = new LinkedList<APIObject>();
    }

    public CachedAPI(Context c, String url)
    {
        super(url);
        LoadCache(c);
        queue = new LinkedList<APIObject>();
        LoadCache(c);
    }

    protected boolean LoadCache(Context c)
    {
        SQLiteDatabase local_db = GetLocalDatabase();
        cache = new ArrayList<APIObject>();
        if(local_db.isOpen()) {
            LocalDBRead(local_db, cache);
            local_db.close();
        } else return false;
        return true;
    }

    protected boolean SaveCache(Context c)
    {
        SQLiteDatabase local_db = GetLocalDatabase();
        if(local_db.isOpen())
        {
            LocalDBWrite(local_db, cache);
            local_db.close();
        }else return false;
        return true;

    }

    public void Push(APIObject item)
    {
        cache.add(item);
        queue.add(item);

        if(queue.size() > queue_limit)
            AsyncPush();
    }
    @Override
    protected boolean AddPushArguments(List<NameValuePair> arguments)
    {
        JSONArray json_array = new JSONArray();
        while(!queue.isEmpty())
        {
            APIObject current = queue.poll();
            if( current != null)
            {
                json_array.put(current.ToJSON());
            }
        }
        arguments.add(new BasicNameValuePair(GetAPIFieldName(), json_array.toString()));
        return true;
    }


    protected List<APIObject> ParseResponse(HttpResponse response)
    {
        InputStream inputStream = null;
        String json_str = "";
        try{
            HttpEntity entity = response.getEntity();
            Header h = response.getFirstHeader("Content-length");
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
                //progress = 200*bytes_read/total;
            }
            json_str = sb.toString();
        } catch (Exception e) {
            // Oops
        }
        finally {
            try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
        }
        try {
            cache = JSONToCache(json_str);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return cache;
    }

    abstract protected List<APIObject> JSONToCache(String json_str) throws JSONException;

    abstract protected String GetLocalDBName();
    abstract protected int GetLocalDBVersion();
    abstract protected SQLiteDatabase GetLocalDatabase();
    abstract protected List<APIObject> LocalDBRead(SQLiteDatabase local_db, List<APIObject> dest);
    abstract protected List<APIObject> LocalDBWrite(SQLiteDatabase local_db, List<APIObject> dest);


    public List<APIObject> Pull()
    {
        //TODO: Check sync key
        cache = ParseResponse(SyncPull());
        return cache;
    }

    public void Update()
    {
        AsyncPull();
    }

    public void UpdateNow() { Pull(); }

    public boolean OnResult(HttpResponse response)
    {
        cache = ParseResponse(response);
        return cache != null;
    }

    public void Flush()
    {
        SyncPush();
    }


}
