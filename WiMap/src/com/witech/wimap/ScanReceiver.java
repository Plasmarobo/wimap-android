package com.witech.wimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public class ScanReceiver extends BroadcastReceiver
{
	protected ScanListConsumer callback;
	
	protected List<HashMap<String, ScanResult>> aggrigator;
	protected WifiManager wifi_man;
	protected Timer timer;
	protected Activity parent;
	protected long rate;
	protected int aggrigate;
	protected int aggrigate_index;
	
	
	public ScanReceiver(Activity parent, WifiManager wifi_man, long rate, ScanListConsumer callback)
	{
		this(parent, wifi_man, rate, callback, 0);
	}

	public ScanReceiver(Activity parent, WifiManager wifi_man, long rate, ScanListConsumer callback, int aggrigate)
	{
		super();
		this.parent = parent;
		this.wifi_man = wifi_man;
		this.callback = callback;
		//this.timer = new Timer();
		this.rate = rate;
		this.aggrigate = aggrigate;
		aggrigator = new ArrayList<HashMap<String,ScanResult>>();
		for(int i = 0; i < aggrigate; ++i)
		{
			aggrigator.add(new HashMap<String, ScanResult>());
		}
		this.aggrigate_index = 0;
	}
	@Override
	public void onReceive(Context c, Intent intent)
	{
		Log.d("ScanReceiver", "Scan Results updated");
		List<ScanResult> wifi_list = wifi_man.getScanResults();
		callback.onScanResult(wifi_list);
		for(int i = 0; i < wifi_list.size(); ++i)
		{
			aggrigator.get(aggrigate_index).put(wifi_list.get(i).BSSID, wifi_list.get(i));
		}
		++aggrigate_index;
		Log.d("ScanReceiver", "Aggrigate index: "+aggrigate_index);
		if(aggrigate_index >= aggrigate)
		{
			callback.onScanAggrigate(aggrigator, aggrigate);
			aggrigate_index = 0;
		}
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				wifi_man.startScan();
			}
		}, rate);
		
	}
	
	public void start()
	{
		timer = new Timer();
		parent.registerReceiver(this, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wifi_man.startScan();
	}
	
	public void stop()
	{
		parent.unregisterReceiver(this);
		timer.cancel();
	}
	public List<BasicResult> AverageAggrigate()
	{
		Map<String, BasicResult> wifi_map = new HashMap<String,BasicResult>();
		int i;
		for(i = 0; i < aggrigate; ++i)
		{
			HashMap<String, ScanResult> scan_map = aggrigator.get(i);
			//Merge appropriate
			Iterator<Entry<String, BasicResult>> it = wifi_map.entrySet().iterator();
		    while (it.hasNext()) {
		        HashMap.Entry pair = (HashMap.Entry)it.next();
		        if(scan_map.containsKey(pair.getKey()))
		        {
		        	((BasicResult)pair.getValue()).Merge(new BasicResult(scan_map.get(pair.getKey())));
		        	it.remove();
		        }else{
		        	((BasicResult)pair.getValue()).CompensateForMiss();
		        }
		        
		    }
		    Iterator<Entry<String, ScanResult>> miss = scan_map.entrySet().iterator();
		    while(miss.hasNext())
		    {
		    	HashMap.Entry pair = (HashMap.Entry)miss.next();
		    	wifi_map.put((String)pair.getKey(), ((new BasicResult((ScanResult)pair.getValue()).CompensateForMisses(i))));
		    }
		}
		List<BasicResult> aggrigates = new ArrayList<BasicResult>(wifi_map.values());
		for(int j = 0; j < aggrigates.size(); ++j)
		{
			aggrigates.get(j).Average(i);
		}
		return aggrigates;
	}
}