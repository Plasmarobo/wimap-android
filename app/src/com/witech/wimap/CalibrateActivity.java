package com.witech.wimap;

import java.util.ArrayList;
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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


public class CalibrateActivity extends Activity {
	
	static final int EDITROUTER = 1;
	
	private WifiManager wifi_man;
	private WifiReciever wifi_rec;
	private List<ScanResult> wifi_list;
	private ScanListAdapter adapter;
	private Timer timer;
	private BasicResult rt;
	private RouterDatabase db;
	
	
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
	
	class WifiScanner extends TimerTask {
		@Override
		public void run()
		{
			wifi_man.startScan();
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		db = new RouterDatabase(this);
		db.open();
		rt = new BasicResult(0, null, null);
		setContentView(R.layout.calibration_ui);
		final ListView listview = (ListView) findViewById(R.id.scan_list_cal);
		adapter = new ScanListAdapter(this);
		listview.setAdapter(adapter);	
		wifi_man = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi_rec = new WifiReciever();
        registerReceiver(wifi_rec, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        timer = new Timer("ScanInterval", true);
        timer.scheduleAtFixedRate(new WifiScanner(), 0, 2000);
	}
	protected void onResume()
	{
		timer = new Timer("ScanInterval", true);
        timer.scheduleAtFixedRate(new WifiScanner(), 0, 2000);
		db.open();
		super.onResume();
	}
	protected void onPause()
	{
		timer.cancel();
		db.close();
		super.onPause();
	}
	
	public void addRouter(AdapterView<?> arg0, View arg1, int position, long arg3)
	{
		Intent edit_router = new Intent(this, AddRouter.class);
		rt = (BasicResult) arg0.getItemAtPosition(position);
		edit_router.putExtra("dBm", rt.GetPower());
		startActivityForResult(edit_router, EDITROUTER);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == EDITROUTER)
		{
			if(resultCode == RESULT_OK)
			{
				Router r = new Router(data.getIntExtra("X", 0),
						data.getIntExtra("Y", 0),
						data.getIntExtra("Z", 0),
						rt.GetSSID(),
						rt.GetUID(),
						data.getIntExtra("dBm", 0));
				db.WriteRouter(r);
				Toast.makeText(this, "Saved Router", Toast.LENGTH_SHORT).show();
			}
			
		}
	}

}
