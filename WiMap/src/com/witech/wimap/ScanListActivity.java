package com.witech.wimap;

import java.util.List;
import com.witech.wimap.BasicResult;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;


public abstract class ScanListActivity extends Activity{
	protected ScanListAdapter adapter;
	protected AndroidRouter rt;
	protected ListView listview;
	protected class AggrigateReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			List<BasicResult> wifi_list = intent.getParcelableArrayListExtra(ScanService.EXTENDED_DATA);
			adapter.clear();
			for(int i = 0; i < wifi_list.size(); ++i)
			{
				adapter.add(wifi_list.get(i));
			}
			adapter.sort();
			adapter.notifyDataSetChanged();
		}
		
	}
	protected AggrigateReceiver aggrigate_receiver;
	
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
		registerReceiver(aggrigate_receiver, new IntentFilter(com.witech.wimap.ScanService.BROADCAST_ACTION));
	}
	@Override
	protected void onResume()
	{
		registerReceiver(aggrigate_receiver, new IntentFilter(com.witech.wimap.ScanService.BROADCAST_ACTION));
		super.onResume();
	}
	@Override
	protected void onPause()
	{
		unregisterReceiver(aggrigate_receiver);
		super.onPause();
	}

	
	
	
	
}
