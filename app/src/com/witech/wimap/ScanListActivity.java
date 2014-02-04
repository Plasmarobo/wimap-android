package com.witech.wimap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import com.witech.wimap.BasicResult;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ScanListActivity extends Activity implements ScanListConsumer{
	private ScanReceiver scan_manager;
	private ScanListAdapter adapter;
	private AndroidRouter rt;
	private RouterDatabase db;
	static final int EDITROUTER = 1;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("ScanListActivity", "Created");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_list);
		
		db = new RouterDatabase(this);
		
		new Timer();
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
		adapter.setNotifyOnChange(false);
		
		scan_manager = new ScanReceiver(this, (WifiManager) getSystemService(Context.WIFI_SERVICE), 500, this, 10);
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
	@Override
	public void onScanResult(List<ScanResult> l) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onScanAggrigate(List<ScanResult>[] l, int aggrigate) {
		Map<String, BasicResult> wifi_map = new HashMap<String,BasicResult>();
		for(int i = 0; i < aggrigate; ++i)
		{
			List<ScanResult> scan = l[aggrigate];
			for(int j = 0; j < scan.size(); ++i)
			{
				BasicResult br = new BasicResult(scan.get(i));
				if(wifi_map.containsKey(br.GetUID()))
					wifi_map.get(br.GetUID()).Merge(br);
				else
					wifi_map.put(br.GetUID(), br);
			}
		}	
		List<BasicResult> wifi_list = (List<BasicResult>) wifi_map.values();
		adapter.clear();
		for(int i = 0; i < wifi_list.size(); ++i)
		{
			adapter.add(wifi_list.get(i).Average(aggrigate));
		}
		adapter.sort();
		adapter.notifyDataSetChanged();
	}
	
	
	
}
