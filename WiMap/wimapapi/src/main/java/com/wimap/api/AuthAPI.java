/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api;

import com.wimap.api.templates.BasicAPI;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;

public class AuthAPI extends BasicAPI {

    private String auth_token;

    private static final String AUTH_ENDPOINT = "auth";
    private static final String AUTH_FIELD = "session";
    private String session_token;
    private String username;
    private String password;

    public String GetEndpoint()
    {
        return AUTH_ENDPOINT;
    }

    @Override
    protected String GetAPIFieldName() {
        return AUTH_FIELD;
    }

    @Override
    protected boolean AddPushArguments(List<NameValuePair> arguments) {
        arguments.add(new BasicNameValuePair(AUTH_FIELD, session_token));

        return false;
    }

    @Override
    protected boolean AddPullArguments(List<NameValuePair> arguments) {
        return false;
    }

    @Override
    protected boolean OnResult(HttpResponse response) {
        return false;
    }

    public AuthAPI()
    {

    }
}
