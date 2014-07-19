package com.wimap.devapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.wimap.api.RouterAPI;
import com.wimap.common.Router;
import com.wimap.common.math.Intersect;
import com.wimap.location.WiMapLocationService;
import com.wimap.location.models.BasicResult;
import com.wimap.location.templates.WiMapLocationSubscriber;
import com.wimap.location.templates.WiMapScanSubscriber;

import java.util.List;

public class DynamicMapActivity extends WiMapLocationSubscriber  {
    DynamicMap map;
    List<Router> routers;

    protected class ScanReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //WiMapScanSubscriber parent = (WiMapScanSubscriber) context;
            List<BasicResult> raw_scan = WiMapLocationService.getLatestScan();
            onScanResult(raw_scan);
        }
    }
    ScanReceiver scanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sgt = new ScaleGestureDetector(this, )
        setContentView(R.layout.activity_dynamic_map);
        scanner = new ScanReceiver();
        registerReceiver(scanner, new IntentFilter(WiMapLocationService.RAW_READY));
        map = (DynamicMap) findViewById(R.id.map_view);
        map.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                return false;
            }
        });
        RouterAPI router_api = new RouterAPI(this);
        routers = router_api.Routers();
        map.UpdateRouters(routers);
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
                if(res.GetUID().equals(r.uid))
                {
                    r.power = res.GetPower();
                    results.remove(res);
                    break;
                }
            }
        }
        map.UpdateRouters(routers);
    }

    public void onPause()
    {
        super.onPause();
        unregisterReceiver(scanner);

    }
    public void onResume()
    {
        super.onResume();
        registerReceiver(scanner, new IntentFilter(WiMapLocationService.RAW_READY));
    }


}
