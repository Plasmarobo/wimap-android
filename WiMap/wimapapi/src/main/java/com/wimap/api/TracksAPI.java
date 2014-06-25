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
import com.wimap.common.Track;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TracksAPI extends CachedAPI {

	public static final String TRACKS_ENDPOINT = "tracks";
    public static final String TRACKS_FIELD = "tracks";

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
        return null;
    }

    @Override
    protected List<APIObject> LocalDBRead(SQLiteDatabase local_db) {
        return null;
    }


    @Override
    protected boolean LocalDBWrite(SQLiteDatabase local_db, List<APIObject> src) {
        return false;
    }
}
