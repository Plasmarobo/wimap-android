package com.witech.wimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	protected List<List<ScanResult>> aggrigator;
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
		aggrigator = new ArrayList<List<ScanResult>>();
		for(int i = 0; i < aggrigate; ++i)
		{
			aggrigator.add(new ArrayList<ScanResult>());
		}
		this.aggrigate_index = 0;
	}
	@Override
	public void onReceive(Context c, Intent intent)
	{
		Log.d("ScanReceiver", "Scan Results updated");
		List<ScanResult> wifi_list = wifi_man.getScanResults();
		callback.onScanResult(wifi_list);
		
		aggrigator.get(aggrigate_index).addAll(wifi_list);
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
		for(int i = 0; i < aggrigate; ++i)
		{
			List<ScanResult> scan = aggrigator.get(i);
			for(int j = 0; j < scan.size(); ++j)
			{
				BasicResult br = new BasicResult(scan.get(j));
				if(wifi_map.containsKey(br.GetUID()))
					wifi_map.get(br.GetUID()).Merge(br);
				else
					wifi_map.put(br.GetUID(), br.Average(i));
			}
		}	
		return new ArrayList<BasicResult>(wifi_map.values());
	}
}