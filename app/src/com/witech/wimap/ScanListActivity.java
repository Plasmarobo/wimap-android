package com.witech.wimap;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ScanListActivity extends Activity {
	private WifiManager wifi_man;
	private WifiReciever wifi_rec;
	private List<ScanResult> wifi_list;
	private ScanListAdapter adapter;
	private AndroidRouter rt;
	private RouterDatabase db;
	private Timer timer;
	static final int EDITROUTER = 1;
	
	class WifiReciever extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context c, Intent intent)
		{
			Log.i("ScanListActivity", "Scan Results updated");
			wifi_list = wifi_man.getScanResults();
			adapter.clear();
			
			for(int i = 0; i < wifi_list.size(); ++i)
			{
				//adapter.add(new BasicResult(wifi_list.get(i)));
				adapter.add(new BasicResult(wifi_list.get(i)));
			}
			adapter.sort();
			adapter.notifyDataSetChanged();
			//wifi_man.startScan();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("ScanListActivity", "Created");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_list);
		
		db = new RouterDatabase(this);
		db.open();
		//rt = new BasicResult(0, null, null);
		timer = new Timer();
		final ListView listview = (ListView) findViewById(R.id.scan_list);
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

		wifi_man = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi_rec = new WifiReciever();
        registerReceiver(wifi_rec, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi_man.startScan();
        timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				wifi_man.startScan();
				
			}}, 0, 5000);
	}
	@Override
	protected void onResume()
	{
		wifi_man.startScan();
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				wifi_man.startScan();
				
			}}, 0, 5000);
		db.open();
		registerReceiver(wifi_rec, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		super.onResume();
	}
	@Override
	protected void onPause()
	{
		timer.cancel();
		db.close();
		unregisterReceiver(wifi_rec);
		super.onPause();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == EDITROUTER)
		{
			if(resultCode == RESULT_OK)
			{
				rt.SetX(data.getDoubleExtra("X", 0));
				rt.SetY(data.getDoubleExtra("Y", 0));
				rt.SetZ(data.getDoubleExtra("Z", 0));
				rt.SetPower(data.getIntExtra("dBm", -90), data.getIntExtra("freq", 2400));
				db.open();
				db.WriteRouter(rt);
				db.close();
				Toast.makeText(this, "Saved Router", Toast.LENGTH_SHORT).show();
			}
			
		}
	}
	public void editRouter(View button)
	{
		Log.i("List", "click");
		View row = (View)button.getParent();
		ListView list = (ListView) row.getParent();
		Intent edit_router = new Intent(list.getContext(), EditRouter.class);
		TextView frequency = (TextView)row.findViewById(R.id.freq);
		TextView power = (TextView)row.findViewById(R.id.power);
		TextView ssid = (TextView)row.findViewById(R.id.ssid);
		TextView uid = (TextView)row.findViewById(R.id.uid);
		int p = Integer.parseInt((String)power.getText());
		int f = Integer.parseInt((String)frequency.getText());
		rt = new AndroidRouter(0, 0, 0, (String)ssid.getText(), (String) uid.getText(), (double)p, (double)f);
		edit_router.putExtra("dBm",p);
		edit_router.putExtra("freq", f);
		startActivityForResult(edit_router, EDITROUTER);
	}
	
	
	
}
