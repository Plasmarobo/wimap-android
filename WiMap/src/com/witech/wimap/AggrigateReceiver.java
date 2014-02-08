package com.witech.wimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;


public class AggrigateReceiver extends BroadcastReceiver {

protected AggrigateConsumer callback;
	
	protected Context parent;
	
	
	
	public AggrigateReceiver(Context service, AggrigateConsumer callback)
	{
		this.parent = service;
		this.callback = callback;
	}

	@Override
	public void onReceive(Context c, Intent intent)
	{
		List<BasicResult> aggrigate = intent.getParcelableArrayListExtra(ScanService.EXTENDED_DATA);
		callback.onScanAggrigate(l, aggrigate)
	}
	
	public void start()
	{
		parent.registerReceiver(this, new IntentFilter(ScanService.BROADCAST_ACTION));
	}
	
	public void stop()
	{
		parent.unregisterReceiver(this);
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
