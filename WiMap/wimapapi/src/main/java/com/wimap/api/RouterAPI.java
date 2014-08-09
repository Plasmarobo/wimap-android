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
import com.wimap.common.Router;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class RouterAPI extends CachedAPI {

	public static final String ROUTERS_ENDPOINT = "routers";
    public static final String ROUTERS_FIELD = "router";
    public static final String SITE_FIELD = "site_id";

    private static List<Router> cache;

    protected List<APIObject> GetCache() {
        return (List<APIObject>)(List<?>)cache;
    }
    protected void PopulateCache(List<APIObject> data)
    {
        if(cache == null) {
            cache = new ArrayList<Router>();
            for (APIObject object : data) {
                cache.add((Router) object);
            }
        }
    }
    protected int current_site;

    @Override
    public String GetEndpoint()
    {
        return ROUTERS_ENDPOINT;
    }

    @Override
    protected String GetAPIFieldName() {
        return ROUTERS_FIELD;
    }

    @Override
    protected boolean AddPullArguments(List<NameValuePair> arguments) {
        arguments.add(new BasicNameValuePair(SITE_FIELD, Integer.toString(current_site)));
        return true;
    }


    public List<Router> Routers()
	{
		return cache;
	}
    public List<Router> FilterByUID(String uid)
    {
        List<Router> result = new ArrayList<Router>();
        Iterator<Router> i = this.Routers().iterator();
        while(i.hasNext()) {
            Router current = i.next();
            if(current.uid.equals(uid))
            {
                result.add(current);
            }
        }
        return result;
    }
    public Router FindByUID(String uid)
    {
        Iterator<Router> i = this.Routers().iterator();
        while(i.hasNext()) {
            Router current = i.next();
            if(current.uid.equals(uid))
            {
                return current;
            }
        }
        return null;
    }

    @Override
    protected APIObject ParseJSON(JSONObject obj) throws JSONException
    {
        Router r = new Router();
        r.FromJSON(obj);
        return r;
    }

    @Override
    protected String GetLocalDBName() {
        return "routers.db";
    }

    @Override
    protected int GetLocalDBVersion() {
        return 2;
    }

    @Override
    protected String GetCreateSQL() {
        return "create table ROUTERS " +
                "(id integer primary key, " +
                "x double not null, " +
                "y double not null, " +
                "z double not null, " +
                "ssid text, " +
                "uid text not null, " +
                "power double, " +
                "frequency double not null, " +
                "site_id integer not null, " +
                "tx_power double);";
    }

    @Override
    protected List<APIObject> LocalDBRead(SQLiteDatabase local_db) {
        String where = "site_id="+Integer.toString(current_site);
        Cursor cursor = local_db.query("ROUTERS", new String[]{"*"}, where, null, null, null, null);
        if(cursor.getCount() < 1) return new ArrayList<APIObject>();
        List<APIObject> list = new ArrayList<APIObject>(cursor.getCount());

        do {
            Router r = new Router();
            r.id = cursor.getInt(0);
            r.x = cursor.getDouble(1);
            r.y = cursor.getDouble(2);
            r.z = cursor.getDouble(3);
            r.ssid = cursor.getString(4);
            r.uid = cursor.getString(5);
            r.power = cursor.getDouble(6);
            r.frequency = cursor.getDouble(7);
            r.site_id = cursor.getInt(8);
            r.tx_power = cursor.getDouble(9);
            list.add(r);
        }while(cursor.moveToNext());
        return list;
    }

    @Override
    protected boolean LocalDBWrite(SQLiteDatabase local_db, List<APIObject> src) {
        Iterator<Router> iterator = ((List<Router>)(List<?>)src).iterator();
        while(iterator.hasNext())
        {
            Router r = iterator.next();
            /*ContentValues cv = new ContentValues();
            cv.put("id", r.id);
            cv.put("x", r.x);
            cv.put("y", r.y);
            cv.put("z", r.z);
            cv.put("ssid", r.ssid);
            cv.put("uid", r.uid);
            cv.put("power", r.power);
            cv.put("frequency", r.frequency);
            cv.put("site_id", r.site_id);
            cv.put("tx_power", r.tx_power);*/
            try {
                local_db.execSQL(
                        "INSERT OR REPLACE INTO ROUTERS (id, x, y, z, ssid, uid, power, frequency,"+
                                "siteid, tx_power) VALUES ("+
                                r.id + ","+
                                r.x + ","+
                                r.y + ","+
                                r.z + ","+
                                r.ssid + ","+
                                r.uid + ","+
                                r.power + ","+
                                r.frequency + ","+
                                r.site_id + ","+
                                r.tx_power+ ","+
                                ");"
                );
                //local_db.insert("ROUTERS", null, cv);
            }catch(SQLiteException e){
                Log.e("Sqlite", e.getMessage());
                return false;
            }
        }
        return true;
    }

    public void SetSite(int site)
    {
        if(current_site != site)
        {
            current_site = site;
            Pull();
        }
    }

	public RouterAPI(Context context)
	{
		super(context);
        //TODO: check if we are up to date for our location
	}

    public RouterAPI(Context context, Integer site_id)
    {
        super(context);
        this.current_site = site_id;
        //TODO: check if we are up to date for our location
    }
/*
	public RouterAPI(Context context, Integer progress, ArrayList<Router> local_list)
	{
        super(context);
		//Eventually pull a cache based on coarse/cached location
        boolean upToDate = true;
        current_site = 0;
		//cache = new ArrayList<Router>();
        if(upToDate) {
            cache = (List<APIObject>)(List<?>)local_list;
        }else{
            Pull();
        }
	}
*/

}
