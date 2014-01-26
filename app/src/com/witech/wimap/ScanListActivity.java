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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ScanListActivity extends Activity {
	private WifiManager wifi_man;
	private WifiReciever wifi_rec;
	private List<ScanResult> wifi_list;
	private ScanListAdapter adapter;
	private BasicResult rt;
	private RouterDatabase db;
	static final int EDITROUTER = 1;
	
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
		
		db = new RouterDatabase(this);
		db.open();
		rt = new BasicResult(0, null, null);
		
		final ListView listview = (ListView) findViewById(R.id.scan_list);
		Intent viewintent = getIntent();
		if(viewintent.hasExtra("powers") && viewintent.hasExtra("ssids") && viewintent.hasExtra("uids"))
		{
			int[] power = viewintent.getIntArrayExtra("powers");
			String[] ssid = viewintent.getStringArrayExtra("ssids");
			String[] uids = viewintent.getStringArrayExtra("uids");
			adapter = new ScanListAdapter(this, ssid, uids, power);
		}else adapter = new ScanListAdapter(this);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

            	Intent edit_router = new Intent(view.getContext(), AddRouter.class);
        		rt = (BasicResult) parent.getItemAtPosition(position);
        		edit_router.putExtra("dBm", rt.GetPower());
        		startActivityForResult(edit_router, EDITROUTER);
            }
		});
		wifi_man = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi_rec = new WifiReciever();
        registerReceiver(wifi_rec, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi_man.startScan();
	}
	protected void onResume()
	{
		wifi_man.startScan();
		db.open();
		super.onResume();
	}
	protected void onPause()
	{
		db.close();
		super.onPause();
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
