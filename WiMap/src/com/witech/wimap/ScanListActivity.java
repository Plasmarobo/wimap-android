package com.witech.wimap;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;


public abstract class ScanListActivity extends WiMapServiceSubscriber{
	protected static ScanListAdapter adapter;
	protected AndroidRouter rt;
	protected ListView listview;

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("ScanListActivity", "Created");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_list);

		listview = (ListView) findViewById(R.id.scan_list);
		Intent viewintent = getIntent();
		if(viewintent.hasExtra("powers") && viewintent.hasExtra("ssids") && viewintent.hasExtra("uids"))
		{
			int[] power = viewintent.getIntArrayExtra("powers");
			String[] ssid = viewintent.getStringArrayExtra("ssids");
			String[] uids = viewintent.getStringArrayExtra("uids");
			int[] freq = viewintent.getIntArrayExtra("freqs");
			adapter = new ScanListAdapter(this, ssid, uids, power, freq);
		}else adapter = new ScanListAdapter(this);
		listview.setAdapter(adapter);
		adapter.setNotifyOnChange(false);
		List<BasicResult> wifi_list = WiMapService.getLatestScan();
		for(int i = 0; i < wifi_list.size(); ++i)
		{
			adapter.add(wifi_list.get(i));
		}
		adapter.notifyDataSetChanged();
		findViewById(R.id.scan_list).invalidate();
	}
	
	
	@Override
	public void onScanAggrigate(List<BasicResult> wifi_list)
	{
		super.onScanAggrigate(wifi_list);
		adapter.clear();
		for(int i = 0; i < wifi_list.size(); ++i)
		{
			adapter.add(wifi_list.get(i));
		}
		adapter.sort();
		adapter.notifyDataSetChanged();
	}

	
	
	
	
}
