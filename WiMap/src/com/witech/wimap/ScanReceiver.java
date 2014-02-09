package com.witech.wimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

public class ScanReceiver extends BroadcastReceiver
{
	protected ScanListConsumer callback;
	
	protected List<HashMap<String, BasicResult>> aggrigator;
	protected WifiManager wifi_man;
	protected Timer timer;
	protected Context parent;
	protected int rate;
	protected int aggrigate;
	protected int aggrigate_index;
	protected boolean stopped;
	
	
	public ScanReceiver(Context service, int rate, ScanListConsumer callback)
	{
		this(service, rate, callback, 5);
	}

	public ScanReceiver(Context parent,int rate, ScanListConsumer callback, int aggrigate)
	{
		super();
		//this.parent = parent;
		this.parent = parent;
		this.wifi_man = (WifiManager) parent.getSystemService(Context.WIFI_SERVICE);
		this.callback = callback;
		//this.timer = new Timer();
		this.rate = rate;
		this.aggrigate = aggrigate;
		aggrigator = new ArrayList<HashMap<String,BasicResult>>();
		for(int i = 0; i < aggrigate; ++i)
		{
			aggrigator.add(new HashMap<String, BasicResult>());
		}
		this.aggrigate_index = 0;
	}

	@Override
	public void onReceive(Context c, Intent intent)
	{
		PendingResult instance = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
			instance = this.goAsync();
		Log.d("ScanReceiver", "Scan Results updated");
		List<ScanResult> wifi_list = wifi_man.getScanResults();
		for(int i = 0; i < wifi_list.size(); ++i)
		{
			Log.v("ScanResult:", wifi_list.get(i).SSID );
			aggrigator.get(aggrigate_index).put(new String(wifi_list.get(i).BSSID), new BasicResult(wifi_list.get(i)));
		}
		++aggrigate_index;
		Log.d("ScanReceiver", "Aggrigate index: "+aggrigate_index);
		if(aggrigate_index >= aggrigate)
		{
			Log.d("ScanReceiver", "Scan Aggrigate updated");
			callback.onScanAggrigate(this.AverageAggrigate());
			aggrigate_index = 0;
		}
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				wifi_man.startScan();
			}
		}, rate);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
			instance.finish();
		
	}
	
	public void start()
	{
		stopped = false;
		timer = new Timer();
		parent.registerReceiver(this, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wifi_man.startScan();
	}
	
	public void stop()
	{
		stopped = true;
		parent.unregisterReceiver(this);
		timer.cancel();
	}
	public boolean isStopped()
	{
		return stopped;
	}
	
	public List<BasicResult> AverageAggrigate()
	{
		Map<String, BasicResult> wifi_map = new HashMap<String,BasicResult>();
		int i;
		for(i = 0; i < aggrigate; ++i)
		{
			HashMap<String, BasicResult> scan_map = aggrigator.get(i);
			//Merge appropriate
			Iterator<Entry<String, BasicResult>> it = wifi_map.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry pair = (HashMap.Entry)it.next();
		        if(scan_map.containsKey(pair.getKey()))
		        {
		        	((BasicResult)pair.getValue()).Merge((BasicResult)scan_map.get(pair.getKey()));
		        	scan_map.remove(pair.getKey());
		        }else{
		        	((BasicResult)pair.getValue()).CompensateForMiss();
		        	scan_map.remove(pair.getKey());
		        }
		        
		    }
		    Iterator<Entry<String, BasicResult>> miss = scan_map.entrySet().iterator();
		    while(miss.hasNext())
		    {
		    	HashMap.Entry pair = (HashMap.Entry)miss.next();
		    	wifi_map.put((String)pair.getKey(), (((BasicResult)pair.getValue()).CompensateForMisses(i)));
		    }
		}
		List<BasicResult> aggrigates = new ArrayList<BasicResult>(wifi_map.values());
		for(int j = 0; j < aggrigates.size(); ++j)
		{
			aggrigates.get(j).Average(aggrigate);
		}
		return aggrigates;
	}
}