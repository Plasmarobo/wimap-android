package com.witech.wimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;


public class ScanService extends IntentService implements ScanListConsumer {
	ScanReceiver scan_manager;
	ScanListConsumer callback;
	public static final String BROADCAST_ACTION = "com.witech.wimap.AGGRIGATE_READY";
	public static final String EXTENDED_DATA = "AGGRIGATE_MAP";
	boolean pause;
	public ScanService(String name) {
		super(name);
		pause = false;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(scan_manager != null)
			if(scan_manager.isStopped())
				scan_manager.stop();
		long rate = intent.getLongExtra("rate", 500);
		scan_manager = new ScanReceiver(this, (WifiManager) getSystemService(Context.WIFI_SERVICE),rate,this);
	}

	
	public void onDestroy()
	{
		scan_manager.stop();
	}

	@Override
	public void onScanAggrigate(List<HashMap<String, BasicResult>> l,
			int aggrigate) {
		ArrayList<BasicResult> data = new ArrayList<BasicResult>(scan_manager.AverageAggrigate());
		Intent localIntent = new Intent(ScanService.BROADCAST_ACTION).putParcelableArrayListExtra("aggrigate", data);
	    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
	}

}
