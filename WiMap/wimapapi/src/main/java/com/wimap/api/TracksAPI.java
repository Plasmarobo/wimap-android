/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api;

import com.wimap.api.templates.CachedAPI;
import com.wimap.common.APIObject;
import com.wimap.common.Track;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class TracksAPI extends CachedAPI {

	public static final String TRACKS_ENDPOINT = "tracks";
    public static final String TRACKS_FIELD = "tracks";

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
    protected List<APIObject> JSONToCache(String json_str) throws JSONException {
        try {
            JSONArray tracks_json = new JSONArray(json_str);
            cache = new ArrayList<APIObject>();
            for(int i = 0; i < tracks_json.length(); ++i)
            {
                JSONObject json_object = tracks_json.getJSONObject(i);
                Track model = new Track();
                model.FromJSON(json_object);
                cache.add(model);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return cache;
    }
}
