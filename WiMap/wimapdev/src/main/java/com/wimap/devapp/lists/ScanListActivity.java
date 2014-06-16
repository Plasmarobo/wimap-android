/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.devapp.lists;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;


import com.wimap.devapp.adapters.ScanListAdapter;
import com.wimap.location.core.WiMapLocationService;
import com.wimap.location.models.AndroidRouter;
import com.wimap.location.models.BasicResult;
import com.wimap.location.templates.WiMapLocationSubscriber;

import java.util.List;


public abstract class ScanListActivity extends WiMapLocationSubscriber {
	protected static ScanListAdapter adapter;
	protected AndroidRouter rt;
	protected ListView listview;

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("ScanListActivity", "Created");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_list);

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
		List<BasicResult> wifi_list = WiMapLocationService.getLatestScan();
        if(! wifi_list.isEmpty()) {
            for (BasicResult item : wifi_list) {
                adapter.add(item);
            }
            adapter.notifyDataSetChanged();
            findViewById(R.id.scan_list).invalidate();
        }
	}
	
	
	@Override
	public void onScanResult(List<BasicResult> wifi_list)
	{
		//super.onScanResult(wifi_list);
		adapter.clear();
		for(int i = 0; i < wifi_list.size(); ++i)
		{
			adapter.add(wifi_list.get(i));
		}
		adapter.sort();
		adapter.notifyDataSetChanged();
	}



	
	
	
	
}
