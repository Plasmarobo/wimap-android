package com.witech.wimap;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;


public abstract class ScanListActivity extends Activity implements ScanListConsumer{
	protected ScanListAdapter adapter;
	protected AndroidRouter rt;
	protected ListView listview;

	protected ScanReceiver aggrigate_receiver;
	
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
		this.aggrigate_receiver = new ScanReceiver(this, 500, this);
		aggrigate_receiver.start();
		onScanAggrigate(aggrigate_receiver.getCachedResults());
	}
	@Override
	protected void onResume()
	{
		aggrigate_receiver.start();
		super.onResume();
	}
	@Override
	protected void onPause()
	{
		aggrigate_receiver.stop();
		super.onPause();
	}
	
	@Override
	public void onScanAggrigate(List<BasicResult> wifi_list)
	{
		adapter.clear();
		for(int i = 0; i < wifi_list.size(); ++i)
		{
			adapter.add(wifi_list.get(i));
		}
		adapter.sort();
		adapter.notifyDataSetChanged();
	}

	
	
	
	
}
