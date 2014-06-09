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

import java.util.List;

public class MessageAPI extends BasicAPI {

    public static final String MESSAGES_ENDPOINT = "messages";
    public static final String MESSAGES_FIELD = "message";

    @Override
    protected String GetEndpoint() {
        return MESSAGES_ENDPOINT;
    }

    @Override
    protected String GetAPIFieldName()
    {
        return MESSAGES_FIELD;
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
}
