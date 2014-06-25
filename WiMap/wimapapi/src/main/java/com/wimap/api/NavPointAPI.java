/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wimap.api.templates.CachedAPI;
import com.wimap.common.APIObject;
import com.wimap.common.NavPoint;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NavPointAPI extends CachedAPI {

    public static final String NAVPOINTS_ENDPOINT = "navpoints";
    public static final String NAVPOINTS_FIELD = "navpoint";

    public static final String NAVPOINTS_SITE_ID = "site_id";

    protected int current_site;
    protected int cached_site;

    public NavPointAPI(Context c)
    {
        super(c);
        current_site = 0;
    }

    public void SetSite(int site)
    {
        current_site = site;
        if(current_site != cached_site)
        {
            current_site = site;
            UpdateNow();
        }
    }

    @Override
    protected APIObject ParseJSON(JSONObject obj) throws JSONException
    {
        NavPoint n = new NavPoint();
        n.FromJSON(obj);
        return n;
    }

    @Override
    protected String GetLocalDBName() {
        return "navpoints.db";
    }

    @Override
    protected int GetLocalDBVersion() {
        return 1;
    }

    @Override
    protected String GetCreateSQL() {
        return  "create table NAVPOINTS " +
                "(id integer not null primary key autoincrement, " +
                "x double not null, " +
                "y double not null, " +
                "z double not null, " +
                "range double not null, " +
                "title text, " +
                "body text);";
    }

    @Override
    protected List<APIObject> LocalDBRead(SQLiteDatabase local_db) {
        return null;
    }

    @Override
    protected boolean LocalDBWrite(SQLiteDatabase local_db, List<APIObject> src) {

        return false;
    }

    @Override
    protected String GetEndpoint() {
        return NAVPOINTS_ENDPOINT;
    }

    @Override
    protected String GetAPIFieldName() {
        return NAVPOINTS_FIELD;
    }

    @Override
    protected boolean AddPullArguments(List<NameValuePair> arguments) {
        arguments.add(new BasicNameValuePair(NAVPOINTS_SITE_ID, Integer.toString(current_site)));
        return true;
    }
}
