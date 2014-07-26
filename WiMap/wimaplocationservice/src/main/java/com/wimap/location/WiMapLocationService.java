/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.location;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.wimap.api.MessageAPI;
import com.wimap.api.RouterAPI;
import com.wimap.api.TracksAPI;
import com.wimap.common.Track;
import com.wimap.common.math.Intersect;
import com.wimap.common.math.RadialDistance;
import com.wimap.location.helpers.IntersectBundle;
import com.wimap.location.models.AndroidRouter;
import com.wimap.location.models.BasicResult;
import com.wimap.location.wifi.WiMapWifiFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;



public class WiMapLocationService extends Service {
    protected WifiManager wifi_man;
    protected WifiLock lock;
    protected Timer timer;
    protected ScanReceiver scanner;
    protected static List<BasicResult> last_scan;
    protected static List<BasicResult> last_aggrigate;
    //protected static KalmanFilter kalman_filter;
    protected HashMap<String, WiMapWifiFilter> filters;
    private int service_id;
    protected String package_name;
    protected String landing_activity_name;
    protected String exit_activity_name;


    public static final String AGGRIGATE_READY = "com.wimap.AGGRIGATE_READY";
    public static final String AGGRIGATE_DATA= "com.wimap.AGGRIGATE_DATA";
    public static final String RAW_READY = "com.wimap.RAW_READY";
    public static final String LOCATION_READY = "com.wimap.LOCATION_READY";
    public static final String LOCATION_DATA = "com.wimap.LOCATION_DATA";
    public static final long SCAN_RESET_TIMEOUT = 10000;
    public static final long WIFI_DOWNCYCLE_TIME = 1000;
    public static final long WIFI_UPCYCLE_TIME = 12000;
    public static final double DEFAULT_SCAN_LEVEL = -90.0;

    protected static final String PACKAGE_NAME_TAG = "package_name";
    protected static final String LANDING_ACTIVITY_TAG = "landing_activity_name";
    protected static final String EXIT_ACTIVITY_TAG = "exit_activity_name";

    protected int scan_delay;
    //protected List<HashMap<String, BasicResult>> aggrigator;
    protected static final int SAMPLE_COUNT = 4;
    protected int aggrigate_index;

    RouterAPI router_api;
    TracksAPI tracks_api;
    MessageAPI message_api;

    protected static long last_scan_timestamp;
    public static final int EXIT_CODE = -1;



    public static List<BasicResult> getLatestScan()
    {
        return last_scan;
    }

    public class ScanReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context c, Intent intent)
        {
            Log.d("WiMapService", "Scan Completed");
            WiMapLocationService parent = (WiMapLocationService) c;
            List<ScanResult> wifi_list = wifi_man.getScanResults();
            HashMap<String, BasicResult> scan_list = new HashMap<String, BasicResult>();
            for(int i = 0; i < wifi_list.size(); ++i)
            {
                BasicResult br = new BasicResult(wifi_list.get(i));
                Log.v("ScanResult:", br.GetSSID() );
                scan_list.put(br.GetUID(), br);
            }
            parent.onScanResult(scan_list);
            parent.timer.schedule(new TimerTask(){
                @Override
                public void run() {
                    while(!wifi_man.startScan()){Log.e("WIFI_ERROR", "SCAN PREVENTED");}
                }
            }, parent.scan_delay);
            WiMapLocationService.last_scan_timestamp = System.currentTimeMillis();

        }
    }

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private static final int SERVICEUID = 74657922;
    private static final String SERVICENAME = "WiMap Location Service";


    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        WiMapLocationService getService() {
            return WiMapLocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        last_scan_timestamp = System.currentTimeMillis();
        service_id = 0x70;

        this.wifi_man = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //aggrigator = new ArrayList<HashMap<String,BasicResult>>();
        //for(int i = 0; i < WiMapService.SAMPLE_COUNT; ++i)
        //{
        //    aggrigator.add(new HashMap<String, BasicResult>());
        //}
        this.aggrigate_index = 0;
        router_api = new RouterAPI(this.getBaseContext());
        tracks_api = new TracksAPI(this.getBaseContext());

        this.filters = WiMapWifiFilter.GenerateFilters(router_api, SAMPLE_COUNT);
        scanner = new ScanReceiver();
        registerReceiver(scanner, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        Log.v("Wifi Scanner", "Starting Scanner");
        while(!this.wifi_man.isWifiEnabled())
        {
            this.wifi_man.setWifiEnabled(true);
        }
        timer = new Timer();
        lock = wifi_man.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "WiMap Scanner");
        lock.acquire();
        while(!wifi_man.startScan()){Log.e("WIFI_ERROR", "SCAN PREVENTED");}
        timer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                if((System.currentTimeMillis() - last_scan_timestamp) > WiMapLocationService.SCAN_RESET_TIMEOUT)
                {
                    //Somethings wrong! POWERCYCLE WIFI
                    while(wifi_man.isWifiEnabled())
                    {
                        Log.e("WiMap Service", "Turning off Wifi");
                        wifi_man.setWifiEnabled(false);
                        try {
                            Thread.sleep(WiMapLocationService.WIFI_DOWNCYCLE_TIME);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    while(!wifi_man.isWifiEnabled())
                    {
                        Log.e("WiMap Service", "Turning on Wifi");
                        wifi_man.setWifiEnabled(true);
                        try {
                            Thread.sleep(WiMapLocationService.WIFI_UPCYCLE_TIME);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    WiMapLocationService.last_scan_timestamp = System.currentTimeMillis();
                    wifi_man.startScan();
                }

            }

        }, 5000, WiMapLocationService.SCAN_RESET_TIMEOUT);
        last_scan = new ArrayList<BasicResult>();
        last_aggrigate = new ArrayList<BasicResult>();


    }

    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {

        Log.i(WiMapLocationService.SERVICENAME, "Received start id " + startId + ": " + startIntent);

        package_name = startIntent.getStringExtra(PACKAGE_NAME_TAG);
        landing_activity_name = startIntent.getStringExtra(LANDING_ACTIVITY_TAG);
        exit_activity_name = startIntent.getStringExtra(EXIT_ACTIVITY_TAG);

        Intent exit_intent = new Intent().setClassName(package_name, exit_activity_name);
        Intent landing_intent = new Intent().setClassName(package_name, landing_activity_name);

        landing_intent.setAction(Intent.ACTION_MAIN);
        landing_intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent deleteIntent = PendingIntent.getActivity(this, EXIT_CODE, exit_intent, 0);
        PendingIntent focusIntent = PendingIntent.getActivity(this, 0, landing_intent, 0);

        Notification n = new NotificationCompat.Builder(this)
                .setContentTitle("WiMap Service")
                .setContentText("WiMap services running")
                .setOngoing(true)
                .setDeleteIntent(deleteIntent)
                .setContentIntent(focusIntent)
                .addAction(R.drawable.ic_wimap_clear_normal, "Stop", deleteIntent)
                .setSmallIcon(R.drawable.ic_wimap_target)
                .build();

        startForeground(service_id, n);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        tracks_api.Flush(this);
        Log.d("WiMapService", "Destroying");
        lock.release();
        unregisterReceiver(scanner);
        timer.cancel();
    }

    public void onScanAggrigate() {
        Map<String, BasicResult> wifi_map = new HashMap<String,BasicResult>();
        int i;

        ArrayList<BasicResult> levels = new ArrayList<BasicResult>();
        for(Entry<String, WiMapWifiFilter> item : this.filters.entrySet())
        {
            levels.add(item.getValue().ToBasicResult());
        }
        last_aggrigate = levels;
        Intent intent = new Intent();
        intent.setAction(WiMapLocationService.AGGRIGATE_READY);
        intent.putParcelableArrayListExtra(WiMapLocationService.AGGRIGATE_DATA, levels);
        sendBroadcast(intent);
        List<AndroidRouter> routers = (List<AndroidRouter>)(List<?>)router_api.Routers();
        if(routers == null)
            return;
        if(last_aggrigate.size() >= 3)
        {
            List<RadialDistance> ld = new ArrayList<RadialDistance>();
            for(BasicResult sr : last_aggrigate)
            {
                if(sr.GetPower() > -90)
                {
                    for(AndroidRouter rt : routers)
                    {
                        if(rt == null)
                            continue;
                        if(sr.GetUID().equals(rt.uid))
                        {
                            ld.add(new RadialDistance(rt.x, rt.y, rt.z, rt.GetDistance(sr)));
                            break;
                        }
                    }
                }
            }

            if(ld.size() >= 3)
            {
                Intersect point = new Intersect(ld, 0, 0, 128);
                intent = new Intent();
                intent.setAction(WiMapLocationService.LOCATION_READY);
                intent.putExtra(WiMapLocationService.LOCATION_DATA, new IntersectBundle(point));
                sendBroadcast(intent);
                //TODO: Associate with user and site
                tracks_api.Push(new Track(point, 0, 0));
            }
        }
    }

    public void onScanResult(HashMap<String, BasicResult> r) {
        WiMapLocationService.last_scan.clear();
        WiMapLocationService.last_scan.addAll(r.values());
        Intent intent = new Intent();
        intent.setAction(WiMapLocationService.RAW_READY);
        sendBroadcast(intent);
        for(Entry<String, WiMapWifiFilter> item : filters.entrySet())
        {
            if(r.containsKey(item.getKey()))
            {
                item.getValue().filteredMerge(r.get(item.getKey())); //We've got a valid result
            }else{
                item.getValue().insertValue(DEFAULT_SCAN_LEVEL); //MISSED ONE!
            }
            item.getValue().Filter(); //Destructive
        }
        ++aggrigate_index;
        Log.d("ScanReceiver", "Aggrigate index: "+aggrigate_index);
        if(aggrigate_index >= WiMapLocationService.SAMPLE_COUNT)
        {
            Log.d("ScanReceiver", "Scan Initialized");
            onScanAggrigate(); //This used to be something else. Now it's just 'make sure we have enough valid data'
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static Runnable StartServiceThread(final Context c, final String package_name, final String landing_activity_name, final String exit_activity_name)
    {
        final Runnable service = new Runnable() {
            public void run() {
                Intent service_intent = new Intent(c, WiMapLocationService.class);
                service_intent.putExtra(PACKAGE_NAME_TAG, package_name);
                service_intent.putExtra(LANDING_ACTIVITY_TAG, landing_activity_name);
                service_intent.putExtra(EXIT_ACTIVITY_TAG, exit_activity_name);
                c.startService(service_intent);
            }
        };
        new Thread(service).start();
        return service;
    }
}
