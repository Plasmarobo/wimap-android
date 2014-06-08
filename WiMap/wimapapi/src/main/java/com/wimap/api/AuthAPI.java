/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import java.util.List;

class AuthAPI extends BasicAPI {

    private String auth_token;

    private static final String AUTH_ENDPOINT = "auth";

    public String GetEndpoint()
    {
        return AUTH_ENDPOINT;
    }

    @Override
    protected boolean AddPushArguments(List<NameValuePair> arguments) {
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
