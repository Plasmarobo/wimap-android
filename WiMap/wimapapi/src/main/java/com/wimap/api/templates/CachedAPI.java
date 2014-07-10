/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api.templates;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wimap.common.APIObject;
import com.wimap.common.Site;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class CachedAPI extends BasicAPI {
    //If the remote database has the same sync key, we have the most up to date version of it
    //protected List<APIObject> cache;
    abstract protected List<APIObject> GetCache();
    abstract protected void PopulateCache(List<APIObject> cache);

    protected Queue<APIObject> queue;
    protected static final int queue_limit=64;
    protected SQLiteDatabase local_db;

    protected class CacheOpenHelper extends SQLiteOpenHelper
    {
        public CacheOpenHelper(Context context) {
            super(context, GetLocalDBName(), null, GetLocalDBVersion());
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            try {
                sqLiteDatabase.execSQL(GetCreateSQL());
            }catch(SQLiteException e){
                Log.e("Sqlite:", e.getMessage());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GetLocalDBName());
            onCreate(sqLiteDatabase);
        }
    }
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
        SQLiteDatabase local_db = GetLocalDatabase(c);
        if(local_db.isOpen()) {
            PopulateCache(LocalDBRead(local_db));
            local_db.close();
        } else return false;
        return true;
    }

    protected boolean SaveCache(Context c)
    {
        SQLiteDatabase local_db = GetLocalDatabase(c);
        if(local_db.isOpen())
        {
            LocalDBWrite(local_db, GetCache());
            local_db.close();
        }else return false;
        return true;

    }

    public void Push(APIObject item)
    {
        GetCache().add(item);
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

        JSONToCache(json_str);
        return GetCache();
    }

    abstract protected APIObject ParseJSON(JSONObject json) throws JSONException;

    protected List<APIObject> JSONToCache(String json_str)
    {
        PopulateCache(new ArrayList<APIObject>());
        try {
            JSONArray sites_json = new JSONArray(json_str);
            for(int index = 0; index < sites_json.length(); ++index)
            {
                JSONObject json = sites_json.getJSONObject(index);
                GetCache().add(this.ParseJSON(json));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return GetCache();
    }

    abstract protected String GetLocalDBName();
    abstract protected int GetLocalDBVersion();
    abstract protected String GetCreateSQL();

    protected SQLiteDatabase GetLocalDatabase(Context c)
    {
        CacheOpenHelper dbhelper = new CacheOpenHelper(c);
        return dbhelper.getWritableDatabase();
        //SQLiteDatabase db = dbhelper.getWritableDatabase();
        //if(db.getVersion() < this.GetLocalDBVersion())
        //{
        //    dbhelper.onUpgrade(db,db.getVersion(),this.GetLocalDBVersion());
        //}
        //return db;
    }

    abstract protected List<APIObject> LocalDBRead(SQLiteDatabase local_db);
    abstract protected boolean LocalDBWrite(SQLiteDatabase local_db, List<APIObject> src);




    public void Update()
    {
        AsyncPull();
    }

    public List<APIObject> UpdateNow() { //TODO: Check sync key
        SyncPull();
        return GetCache();
    }

    public boolean OnResult(HttpResponse response)
    {
        PopulateCache(ParseResponse(response));
        return GetCache() != null;
    }

    public void Flush(Context c)
    {
        SaveCache(c);
        SyncPush();
    }


}
