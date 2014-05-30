package com.wimap.services;

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

import com.wimap.activities.ExitActivity;
import com.wimap.apis.AsyncHTTP;
import com.wimap.apis.MessageAPI;
import com.wimap.apis.RouterAPI;
import com.wimap.apis.TracksAPI;
import com.wimap.components.AndroidRouter;
import com.wimap.components.BasicResult;
import com.wimap.components.IntersectBundle;
import com.wimap.components.WiMapServiceScanFilter;
import com.wimap.common.math.Intersect;
import com.wimap.common.math.RadialDistance;
import com.wimap.wimap.MainActivity;
import com.wimap.wimap.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class WiMapService extends Service {
    protected WifiManager wifi_man;
    protected WifiLock lock;
    protected Timer timer;
    protected ScanReceiver scanner;
    protected static List<BasicResult> last_scan;
    protected static List<BasicResult> last_aggrigate;
    //protected static KalmanFilter kalman_filter;
    protected HashMap<String, WiMapServiceScanFilter> filters;
    private int service_id;
    public static final String AGGRIGATE_READY = "com.witech.wimap.AGGRIGATE_READY";
    public static final String AGGRIGATE_DATA= "com.witech.wimap.AGGRIGATE_DATA";
    public static final String RAW_READY = "com.witech.wimap.RAW_READY";
    public static final String LOCATION_READY = "com.witech.wimap.LOCATION_READY";
    public static final String LOCATION_DATA = "com.witech.wimap.LOCATION_DATA";
    public static final long SCAN_RESET_TIMEOUT = 10000;
    public static final long WIFI_DOWNCYCLE_TIME = 1000;
    public static final long WIFI_UPCYCLE_TIME = 12000;
    public static final double DEFAULT_SCAN_LEVEL = -90.0;
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
            WiMapService parent = (WiMapService) c;
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
            WiMapService.last_scan_timestamp = System.currentTimeMillis();

        }
    }

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private static int SERVICENAME = R.string.ScanService;


    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        WiMapService getService() {
            return WiMapService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        last_scan_timestamp = System.currentTimeMillis();
        service_id = 0x70;
        Intent intent = new Intent(this, ExitActivity.class);
        PendingIntent deleteIntent = PendingIntent.getActivity(this, EXIT_CODE, intent, 0);
        intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent focusIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification n = new NotificationCompat.Builder(this)
                .setContentTitle("WiMap Service")
                .setContentText("WiMap services running")
                .setOngoing(true)
                .setDeleteIntent(deleteIntent)
                .setContentIntent(focusIntent)
                .addAction(R.drawable.ic_clear_normal, "Stop", deleteIntent)
                .setSmallIcon(R.drawable.ic_target)
                .build();
        startForeground(service_id, n);
        this.wifi_man = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //aggrigator = new ArrayList<HashMap<String,BasicResult>>();
        //for(int i = 0; i < WiMapService.SAMPLE_COUNT; ++i)
        //{
        //    aggrigator.add(new HashMap<String, BasicResult>());
        //}
        this.aggrigate_index = 0;
        router_api = new RouterAPI(this.getBaseContext());
        AsyncHTTP http = new AsyncHTTP();
        http.execute(router_api);
        this.filters = router_api.GetFilters(SAMPLE_COUNT);
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
                if((System.currentTimeMillis() - last_scan_timestamp) > WiMapService.SCAN_RESET_TIMEOUT)
                {
                    //Somethings wrong! POWERCYCLE WIFI
                    while(wifi_man.isWifiEnabled())
                    {
                        Log.e("WiMap Service", "Turning off Wifi");
                        wifi_man.setWifiEnabled(false);
                        try {
                            Thread.sleep(WiMapService.WIFI_DOWNCYCLE_TIME);
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
                            Thread.sleep(WiMapService.WIFI_UPCYCLE_TIME);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    WiMapService.last_scan_timestamp = System.currentTimeMillis();
                    wifi_man.startScan();
                }

            }

        }, 5000, WiMapService.SCAN_RESET_TIMEOUT);
        last_scan = new ArrayList<BasicResult>();
        last_aggrigate = new ArrayList<BasicResult>();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(getText(WiMapService.SERVICENAME).toString(), "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        AsyncHTTP http = new AsyncHTTP();
        http.execute(tracks_api);
        Log.d("WiMapService", "Destroying");
        lock.release();
        unregisterReceiver(scanner);
        timer.cancel();
        try {
            http.get(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {

        }

    }



    public void onScanAggrigate() {
        Map<String, BasicResult> wifi_map = new HashMap<String,BasicResult>();
        int i;

        ArrayList<BasicResult> levels = new ArrayList<BasicResult>();
        for(Entry<String, WiMapServiceScanFilter> item : this.filters.entrySet())
        {
            levels.add(item.getValue().ToBasicResult());
        }
        last_aggrigate = levels;
        Intent intent = new Intent();
        intent.setAction(WiMapService.AGGRIGATE_READY);
        intent.putParcelableArrayListExtra(WiMapService.AGGRIGATE_DATA, levels);
        sendBroadcast(intent);
        List<AndroidRouter> routers = RouterAPI.Routers();
        if(routers == null)
            return;
        if(last_aggrigate.size() > 4)
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
                        if(sr.GetUID().equals(rt.GetUID()))
                        {
                            ld.add(new RadialDistance(rt.GetX(), rt.GetY(), rt.GetX(), rt.GetDistance(sr)));
                            break;
                        }
                    }
                }
            }

            if(ld.size() >= 4)
            {
                Intersect point = new Intersect(ld, 0, 0, 128);
                intent = new Intent();
                intent.setAction(WiMapService.LOCATION_READY);
                intent.putExtra(WiMapService.LOCATION_DATA, new IntersectBundle(point));
                sendBroadcast(intent);
                TracksAPI.CommitPoint(point);
            }
        }
    }

    public void onScanResult(HashMap<String, BasicResult> r) {
        WiMapService.last_scan.clear();
        WiMapService.last_scan.addAll(r.values());
        Intent intent = new Intent();
        intent.setAction(WiMapService.RAW_READY);
        sendBroadcast(intent);
        for(Entry<String, WiMapServiceScanFilter> item : filters.entrySet())
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
        if(aggrigate_index >= WiMapService.SAMPLE_COUNT)
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
}
