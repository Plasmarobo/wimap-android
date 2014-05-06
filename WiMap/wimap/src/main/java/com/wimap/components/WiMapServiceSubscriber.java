package com.wimap.components;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.wimap.templates.ScanListConsumer;
import com.wimap.services.WiMapService;

import java.util.List;

public class WiMapServiceSubscriber extends Activity implements ScanListConsumer {
	protected AggrigateReceiver aggrigate_reciever;
	protected static List<BasicResult> cache;
	protected class AggrigateReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(Context context, Intent intent) {
			WiMapServiceSubscriber parent = (WiMapServiceSubscriber) context;
			List<BasicResult> aggrigate = intent.getParcelableArrayListExtra(WiMapService.AGGRIGATE_DATA);
			parent.onScanAggrigate(aggrigate);
		}
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		aggrigate_reciever = new AggrigateReceiver();
		
	}
	public void onPause()
	{
		super.onPause();
		unregisterReceiver(aggrigate_reciever);
	}
	public void onResume()
	{
		super.onResume();
		registerReceiver(aggrigate_reciever, new IntentFilter(WiMapService.AGGRIGATE_READY));
	}
	@Override
	public void onScanAggrigate(List<BasicResult> l)
	{
		cache = l;
	}

    public static void setCache(List<BasicResult> cache)
    {
        WiMapServiceSubscriber.cache = cache;
    }
}
