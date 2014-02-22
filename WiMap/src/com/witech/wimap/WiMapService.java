package com.witech.wimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import android.app.Notification;
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

public class WiMapService extends Service{
	protected WifiManager wifi_man;
	protected WifiLock lock;
	protected Timer timer;
	protected ScanReceiver scanner;
	protected static List<BasicResult> last_scan;
	protected static List<BasicResult> last_aggrigate;
	private int service_id;
	public static final String AGGRIGATE_READY = "com.witech.wimap.AGGRIGATE_READY";
	public static final String AGGRIGATE_DATA= "com.witech.wimap.AGGRIGATE_DATA";
	public static final String LOCATION_READY = "com.witech.wimap.LOCATION_READY";
	public static final String LOCATION_DATA = "com.witech.wimap.LOCATION_DATA";
	public static final long SCAN_RESET_TIMEOUT = 10000;
	public static final long WIFI_DOWNCYCLE_TIME = 1000;
	public static final long WIFI_UPCYCLE_TIME = 12000;
	protected int scan_delay;
	protected List<HashMap<String, BasicResult>> aggrigator;
	protected static final int SAMPLE_COUNT = 4;
	protected int aggrigate_index;
	RouterAPI router_api;
	TracksAPI tracks_api;
	MessageAPI message_api;
	protected static long last_scan_timestamp;
	
	public static List<BasicResult> getLatestScan()
	{
		return last_aggrigate;
	}
	
	public class ScanReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context c, Intent intent)
		{
			Log.i("WiMapService", "Scan Completed");
			WiMapService parent = (WiMapService) c;
			List<ScanResult> wifi_list = wifi_man.getScanResults();
			List<BasicResult> br = new ArrayList<BasicResult>(wifi_list.size());
			for(int i = 0; i < wifi_list.size(); ++i)
			{
				Log.v("ScanResult:", wifi_list.get(i).SSID );
				br.add(new BasicResult(wifi_list.get(i)));
			}
			parent.onScanResult(br);
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
    	 Notification n = new NotificationCompat.Builder(this)
         .setContentTitle("WiMap Service")
         .setContentText("WiMap services running")
         .setSmallIcon(R.drawable.ic_target)
         .build();
    	startForeground(service_id, n);
    	this.wifi_man = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		aggrigator = new ArrayList<HashMap<String,BasicResult>>();
		for(int i = 0; i < WiMapService.SAMPLE_COUNT; ++i)
		{
			aggrigator.add(new HashMap<String, BasicResult>());
		}
		this.aggrigate_index = 0;
		router_api = new RouterAPI();
		AsyncHTTP http = new AsyncHTTP();
		http.execute(router_api);
		
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
					//Fuuuuuuck, somethings fucked up! POWERCYCLE WIFI
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    

	public void onScanAggrigate() {
		Map<String, BasicResult> wifi_map = new HashMap<String,BasicResult>();
		int i;
		for(i = 0; i < WiMapService.SAMPLE_COUNT; ++i)
		{
			HashMap<String, BasicResult> scan_map = aggrigator.get(i);
			//Merge appropriate
			Iterator<Entry<String, BasicResult>> it = wifi_map.entrySet().iterator();
		    while (it.hasNext()) {
		        Entry<String, BasicResult> pair = (Entry<String, BasicResult>)it.next();
		        if(scan_map.containsKey(pair.getKey()))
		        {
		        	((BasicResult)pair.getValue()).Merge((BasicResult)scan_map.get(pair.getKey()), (double)i);
		        	scan_map.remove(pair.getKey());
		        }else{
		        	((BasicResult)pair.getValue()).CompensateForMiss();
		        	scan_map.remove(pair.getKey());
		        }
		        
		    }
		    Iterator<Entry<String, BasicResult>> miss = scan_map.entrySet().iterator();
		    while(miss.hasNext())
		    {
		    	Entry<String, BasicResult> pair = (Entry<String, BasicResult>)miss.next();
		    	wifi_map.put((String)pair.getKey(), (((BasicResult)pair.getValue()).CompensateForMisses(i)));
		    }
		}
		ArrayList<BasicResult> aggrigates = new ArrayList<BasicResult>(wifi_map.values());
		for(int j = 0; j < aggrigates.size(); ++j)
		{
			aggrigates.get(j).Average(this.AverageFactor());
		}
		last_aggrigate = aggrigates;
		Intent intent = new Intent();
        intent.setAction(WiMapService.AGGRIGATE_READY);
        intent.putParcelableArrayListExtra(WiMapService.AGGRIGATE_DATA, aggrigates);
        sendBroadcast(intent);
        List<AndroidRouter> routers = RouterAPI.Routers();
        if(routers == null)
        	return;
        if(last_aggrigate.size() > 4)
		{
        	List<RadialDistance> ld = new ArrayList<RadialDistance>();
        	for(i = 0; i < last_aggrigate.size(); ++i)
        	{
        		BasicResult sr = last_aggrigate.get(i);
        		if(sr.GetPower() > -90)
        		{
        			for(int j = 0; j < routers.size(); ++j)
        			{
        				AndroidRouter rt = (AndroidRouter) routers.get(j);
        				if(rt == null)
        					continue;
        				if(sr.GetUID().equals(rt.GetUID()))
        				{
        					ld.add(new RadialDistance(rt.GetX(), rt.GetY(), rt.GetX(), rt.GetAverageDistance(sr)));
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


	private double AverageFactor() {
		double factor = 0;
		for(int i = 0; i < WiMapService.SAMPLE_COUNT; ++i)
		{
			factor += WiMapService.SAMPLE_COUNT-i;
		}
		return factor;
	}

	public void onScanResult(List<BasicResult> r) {
		WiMapService.last_scan = r;
		for(int i = 0; i < r.size(); ++i)
		{
			Log.v("ScanResult:", r.get(i).GetSSID() );
			aggrigator.get(aggrigate_index).put(new String(r.get(i).GetUID()), r.get(i));
		}
		++aggrigate_index;
		Log.d("ScanReceiver", "Aggrigate index: "+aggrigate_index);
		if(aggrigate_index >= WiMapService.SAMPLE_COUNT)
		{
			Log.d("ScanReceiver", "Scan Aggrigate updated");
			onScanAggrigate();
			aggrigate_index = 0;
		}
	}

}
