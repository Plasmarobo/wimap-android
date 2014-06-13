/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.location.templates;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.wimap.location.core.WiMapLocationService;
import com.wimap.location.models.BasicResult;

import java.util.List;

public abstract class WiMapLocationSubscriber extends Activity implements ScanListConsumer {
	protected AggrigateReceiver aggrigate_receiver;
    protected RawReceiver scan_receiver;
	protected static List<BasicResult> cache;
	protected class AggrigateReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(Context context, Intent intent) {
			WiMapLocationSubscriber parent = (WiMapLocationSubscriber) context;
			List<BasicResult> aggrigate = intent.getParcelableArrayListExtra(WiMapLocationService.AGGRIGATE_DATA);
			parent.onScanAggrigate(aggrigate);
		}
		
	}
    protected class RawReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            WiMapLocationSubscriber parent = (WiMapLocationSubscriber) context;
            List<BasicResult> raw_scan = WiMapLocationService.getLatestScan();
            parent.onScanResult(raw_scan);
        }
    }
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		aggrigate_receiver = new AggrigateReceiver();
        scan_receiver = new RawReceiver();
		
	}
	public void onPause()
	{
		super.onPause();
		unregisterReceiver(aggrigate_receiver);
        unregisterReceiver(scan_receiver);
	}
	public void onResume()
	{
		super.onResume();
		registerReceiver(aggrigate_receiver, new IntentFilter(WiMapLocationService.AGGRIGATE_READY));
        registerReceiver(scan_receiver, new IntentFilter(WiMapLocationService.RAW_READY));
	}
	@Override
	public void onScanAggrigate(List<BasicResult> l) { cache = l; }

    public void onScanResult(List<BasicResult> l) {}; //Do nothing

    public static void setCache(List<BasicResult> cache)
    {
        WiMapLocationSubscriber.cache = cache;
    }
}
