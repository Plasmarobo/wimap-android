package com.witech.wimap;

import java.util.List;
import com.witech.wimap.BasicResult;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;


public abstract class ScanListActivity extends Activity implements ScanListConsumer{
	protected ScanReceiver scan_manager;
	protected ScanListAdapter adapter;
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
		
		scan_manager = new ScanReceiver(this, (WifiManager) getSystemService(Context.WIFI_SERVICE), 100, this, 4);
        scan_manager.start();
	}
	@Override
	protected void onResume()
	{
		scan_manager.start();
		super.onResume();
	}
	@Override
	protected void onPause()
	{
		scan_manager.stop();
		super.onPause();
	}

	@Override
	public void onScanResult(List<ScanResult> l) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onScanAggrigate(List<List<ScanResult>> l, int aggrigate) {
		
		List<BasicResult> wifi_list = scan_manager.AverageAggrigate();
		adapter.clear();
		for(int i = 0; i < wifi_list.size(); ++i)
		{
			adapter.add(wifi_list.get(i).Average(aggrigate));
		}
		adapter.sort();
		adapter.notifyDataSetChanged();
	}
	
	
	
}
