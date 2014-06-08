/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api;

import org.apache.http.HttpResponse;

import java.util.List;

abstract class CachedAPI<T> extends BasicAPI {
    //If the remote database has the same sync key, we have the most up to date version of it
    protected List<T> cache;
    protected List<T> queue;
    protected static final int queue_limit=64;

    abstract List<T> ParseResponse(HttpResponse response);

    public void Push(T item)
    {
        cache.add(item);
        queue.add(item);
        if(queue.size() > queue_limit)
            AsyncPush();
    }

    public List<T> Pull()
    {
        //TODO: Check sync key
        return cache = ParseResponse(SyncPull());
    }

    public void Update()
    {
        AsyncPull();
    }

    public boolean OnResult(HttpResponse response)
    {
        cache = ParseResponse(response);
        return true;
    }

    public void Flush()
    {
        SyncPush();
    }


}
