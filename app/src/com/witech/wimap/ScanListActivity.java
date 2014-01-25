package com.witech.wimap;

import java.util.ArrayList;
import java.util.List;

import com.witech.wimap.BasicResult;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class ScanListActivity extends Activity {
	private WifiManager wifi_man;
	private WifiReciever wifi_rec;
	private List<ScanResult> wifi_list;
	private ScanListAdapter adapter;
	class WifiReciever extends BroadcastReceiver
	{
		public void onReceive(Context c, Intent intent)
		{
			Log.i("ScanListActivity", "Scan Results updated");
			wifi_list = wifi_man.getScanResults();
			List<BasicResult> list = new ArrayList<BasicResult>();
			for(int i = 0; i < wifi_list.size(); ++i)
			{
				list.add(new BasicResult(wifi_list.get(i)));
			}
			adapter.clear();
			adapter.addAll(list);
			//wifi_man.startScan();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("ScanListActivity", "Created");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_list);
		final ListView listview = (ListView) findViewById(R.id.scan_list);
		Intent viewintent = getIntent();
		int[] power = viewintent.getIntArrayExtra("powers");
		String[] ssid = viewintent.getStringArrayExtra("ssids");
		String[] uids = viewintent.getStringArrayExtra("uids");
		adapter = new ScanListAdapter(this, ssid, uids, power);
		listview.setAdapter(adapter);	
		wifi_man = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi_rec = new WifiReciever();
        registerReceiver(wifi_rec, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi_man.startScan();
	}
	
	
}
