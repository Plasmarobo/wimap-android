/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.location.templates;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.wimap.common.math.Intersect;
import com.wimap.location.WiMapLocationService;
import com.wimap.location.helpers.IntersectBundle;
import com.wimap.location.models.BasicResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public abstract class WiMapLocationSubscriber extends Activity implements LocationConsumer {
    protected LocationReceiver location_receiver;

    protected static List<Intersect> cache;
    protected class LocationReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            WiMapLocationSubscriber parent = (WiMapLocationSubscriber) context;
            IntersectBundle bundle = intent.getParcelableExtra(WiMapLocationService.LOCATION_DATA);
            parent.onLocation(bundle);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        location_receiver = new LocationReceiver();
        if(cache == null)
            cache = new ArrayList<Intersect>();

    }
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(location_receiver);

    }
    public void onResume()
    {
        super.onResume();
        registerReceiver(location_receiver, new IntentFilter(WiMapLocationService.LOCATION_READY));
    }
    @Override
    public void onLocation(Intersect location)
    {
        cache.add(location);
    }
}
