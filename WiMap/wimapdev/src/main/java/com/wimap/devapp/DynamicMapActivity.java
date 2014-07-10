package com.wimap.devapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.wimap.api.RouterAPI;
import com.wimap.common.Router;
import com.wimap.common.math.Intersect;
import com.wimap.devapp.dynamic_map.DynamicMap;
import com.wimap.location.WiMapLocationService;
import com.wimap.location.models.BasicResult;
import com.wimap.location.templates.WiMapLocationSubscriber;
import com.wimap.location.templates.WiMapScanSubscriber;

import java.util.ArrayList;
import java.util.List;

public class DynamicMapActivity extends WiMapLocationSubscriber  {
    DynamicMap map;
    List<Router> routers;
    protected class ScanReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            WiMapScanSubscriber parent = (WiMapScanSubscriber) context;
            List<BasicResult> raw_scan = WiMapLocationService.getLatestScan();
            parent.onScanResult(raw_scan);
        }
    }
    ScanReceiver scanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_map);
        scanner = new ScanReceiver();
        map = (DynamicMap) findViewById(R.id.map_view);
        RouterAPI router_api = new RouterAPI(this);
        routers = router_api.Routers();
    }

    @Override
    public void onLocation(Intersect location)
    {
        map.UpdatePosition(location);
    }

    public void onScanResult(List<BasicResult> results)
    {
        for(Router r : routers)
        {
            for(BasicResult res : results)
            {
                if(res.GetUID() == r.uid)
                {
                    r.power = res.GetPower();
                    results.remove(res);
                    break;
                }
            }
        }
        map.UpdateRouters(routers);
    }



}
