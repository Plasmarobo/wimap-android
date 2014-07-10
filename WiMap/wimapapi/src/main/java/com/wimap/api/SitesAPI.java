/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wimap.api.templates.CachedAPI;
import com.wimap.common.APIObject;
import com.wimap.common.Site;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SitesAPI extends CachedAPI {

    public static final String SITES_ENDPOINT = "sites";
    public static final String SITES_FIELD = "site";
    public static final String SITES_PULL_FIELD = "location";

    public static final String lat_tag = "lat";
    public static final String long_tag = "long";
    public static final String range_tag = "range";

    public SitesAPI(Context c)
    {
        super(c);
    }

    @Override
    public String GetEndpoint()
    {
        return SITES_ENDPOINT;
    }

    @Override
    public String GetAPIFieldName()
    {
        return SITES_FIELD;
    }

    @Override
    protected String GetLocalDBName() {
        return "sites.db";
    }

    @Override
    protected int GetLocalDBVersion() {
        return 1;
    }


    @Override
    protected List<APIObject> LocalDBRead(SQLiteDatabase local_db) {
        Cursor cursor = local_db.query("SITES", new String[]{"*"}, null, null, null, null, null);
        if(cursor.getCount() < 1) return new ArrayList<APIObject>();
        List<APIObject> list = new ArrayList<APIObject>(cursor.getCount());
        do {
            Site s = new Site();
            s.id = cursor.getInt(0);
            s.name = cursor.getString(1);
            s.latitude = cursor.getDouble(2);
            s.longitude = cursor.getDouble(3);
            s.range = cursor.getDouble(4);
            list.add(s);
        }while(cursor.moveToNext());
        return list;
    }

    @Override
    protected boolean LocalDBWrite(SQLiteDatabase local_db, List<APIObject> src) {
        Iterator<Site> iterator = ((List<Site>)(List<?>)src).iterator();
        while(iterator.hasNext())
        {
            Site s = iterator.next();
            ContentValues cv = new ContentValues();
            cv.put("id", s.id);
            cv.put("name", s.name);
            cv.put("lat", s.latitude);
            cv.put("long", s.longitude);
            cv.put("range", s.range);
            try {
                local_db.insert("SITES", null, cv);
            }catch(SQLiteException e){
                Log.e("Sqlite", e.getMessage());
                return false;
            }
        }
        return true;
    }

    public List<Site> Sites()
    {
        if(cache != null) {
            return new ArrayList<Site>((List<Site>)(List<?>)cache);
        }else{
            return new ArrayList<Site>();
        }
    }

    @Override
    protected APIObject ParseJSON(JSONObject obj) throws JSONException
    {
        Site s = new Site();
        s.FromJSON(obj);
        return s;
    }


    @Override
    protected boolean AddPullArguments(List<NameValuePair> arguments) {
        JSONObject json = new JSONObject();
        try {
            json.put(lat_tag, 0.0);
            json.put(long_tag, 0.0);
            json.put(range_tag, 1.0);
        }catch(JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        arguments.add(new BasicNameValuePair(SITES_PULL_FIELD, json.toString()));
        return true;
    }

    @Override
    protected String GetCreateSQL()
    {
        return "create table SITES " +
               " (id integer primary key, " +
               " name text not null, " +
               " lat double not null, " +
               " long double not null, " +
               " range double not null);";
    }

}
