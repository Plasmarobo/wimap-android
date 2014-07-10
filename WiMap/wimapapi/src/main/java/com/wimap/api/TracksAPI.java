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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.wimap.api.templates.CachedAPI;
import com.wimap.common.APIObject;
import com.wimap.common.Site;
import com.wimap.common.Track;
import com.wimap.common.math.Intersect;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TracksAPI extends CachedAPI {

	public static final String TRACKS_ENDPOINT = "tracks";
    public static final String TRACKS_FIELD = "tracks";

    private static List<Track> cache;

    protected List<APIObject> GetCache()
    {
        return (List<APIObject>)(List<?>) cache;
    }

    protected void PopulateCache(List<APIObject> data)
    {
        cache = new ArrayList<Track>();
        for(APIObject object : data)
        {
            cache.add((Track)object);
        }
    }

    public TracksAPI(Context c) {
        super(c);
    }

    @Override
    protected String GetEndpoint()
    {
        return TRACKS_ENDPOINT;
    }

    @Override
    protected boolean AddPullArguments(List<NameValuePair> arguments) {
        return false;
    }


    @Override
    protected String GetAPIFieldName() {
        return TRACKS_FIELD;
    }

    @Override
    protected APIObject ParseJSON(JSONObject obj) throws JSONException
    {
        Track t = new Track();
        t.FromJSON(obj);
        return t;
    }

    @Override
    protected String GetLocalDBName() {
        return "tracks.db";
    }

    @Override
    protected int GetLocalDBVersion() {
        return 1;
    }

    @Override
    protected String GetCreateSQL() {
        return "create table TRACKS " +
                " (id integer primary key, " +
                " latitude double not null, " +
                " longitude double not null, " +
                " x double not null, " +
                " y double not null, " +
                " z double not null, " +
                " x_confidence double not null, " +
                " y_confidence double not null, " +
                " z_confidence double not null, " +
                " timestamp text not null);";
    }

    @Override
    protected List<APIObject> LocalDBRead(SQLiteDatabase local_db) {
        Cursor cursor = local_db.query("TRACKS", new String[]{"*"}, null, null, null, null, null);
        if(cursor.getCount() < 1) return new ArrayList<APIObject>();
        List<APIObject> list = new ArrayList<APIObject>(cursor.getCount());
        do {
            Track t = new Track();
            t.id = cursor.getInt(0);

            t.latitude = cursor.getDouble(2);
            t.longitude = cursor.getDouble(3);
            t.location.x = cursor.getDouble(4);
            t.location.y = cursor.getDouble(5);
            t.location.z = cursor.getDouble(6);

            t.location.x_conf = cursor.getDouble(7);
            t.location.y_conf = cursor.getDouble(8);
            t.location.z_conf = cursor.getDouble(9);

            t.time = Date.valueOf(cursor.getString(10));

            list.add(t);
        }while(cursor.moveToNext());
        return list;
    }


    @Override
    protected boolean LocalDBWrite(SQLiteDatabase local_db, List<APIObject> src) {
        Iterator<Track> iterator = ((List<Track>)(List<?>)src).iterator();
        while(iterator.hasNext())
        {
            Track t = iterator.next();
            ContentValues cv = new ContentValues();
            cv.put("id", t.id);
            cv.put("latitude", t.latitude);
            cv.put("longitude", t.longitude);
            cv.put("x", t.location.x);
            cv.put("y", t.location.y);
            cv.put("z", t.location.z);
            cv.put("x_confidence", t.location.x_conf);
            cv.put("y_confidence", t.location.y_conf);
            cv.put("z_confidence", t.location.z_conf);
            cv.put("timestamp", t.time.toString());
            try {
                local_db.insert("TRACKS", null, cv);
            }catch(SQLiteException e){
                Log.e("Sqlite", e.getMessage());
                return false;
            }
        }
        return true;
    }
}
