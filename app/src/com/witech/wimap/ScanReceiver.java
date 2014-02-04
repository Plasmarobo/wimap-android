package com.witech.wimap;

import java.util.List;
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
	
	protected List<ScanResult> aggrigator[];
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
		this.wifi_man = wifi_man;
		this.callback = callback;
		this.timer = new Timer();
		this.rate = rate;
		this.aggrigate = aggrigate;
		this.aggrigate_index = 0;
	}
	@Override
	public void onReceive(Context c, Intent intent)
	{
		Log.i("ScanReceiver", "Scan Results updated");
		List<ScanResult> wifi_list = wifi_man.getScanResults();
		callback.onScanResult(wifi_list);
		
		aggrigator[aggrigate_index] = wifi_list;
		++aggrigate_index;
		if(aggrigate_index >= aggrigate)
		{
			callback.onScanAggrigate(aggrigator, aggrigate);
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
		parent.registerReceiver(this, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wifi_man.startScan();
	}
	
	public void stop()
	{
		parent.unregisterReceiver(this);
		timer.cancel();
	}
}