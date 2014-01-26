package com.witech.wimap;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import com.witech.wimap.ScanListActivity;

public class MainActivity extends Activity {
	private WifiManager wifi_man;
	private WifiReciever wifi_rec;
	private List<ScanResult> wifi_list;
	
	class WifiReciever extends BroadcastReceiver
	{
		public void onReceive(Context c, Intent intent)
		{
			Log.i("MainActivity", "Got Scan results");
			wifi_list = wifi_man.getScanResults();
			int [] powers = new int[wifi_list.size()];
			String[] ssids = new String[wifi_list.size()];
			String[] uids = new String[wifi_list.size()];
			for(int i = 0; i < wifi_list.size(); ++i)
			{
				ScanResult sr = wifi_list.get(i);
				powers[i] = sr.level;
				ssids[i] = sr.SSID;
				uids[i] = sr.BSSID;
			}
			Log.i("MainActivity", "Preparing Intent");
			Intent listIntent = new Intent(c, ScanListActivity.class);
			listIntent.putExtra("powers", powers);
			listIntent.putExtra("ssids", ssids);
			listIntent.putExtra("uids", uids);
			//ListView scan_list = (ListView) findViewById(R.id.scan_list);
			Log.i("MainActivity", "Starting Activity");
			startActivity(listIntent);
			unregisterReceiver(wifi_rec);
		}
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.v("MainActivity", "Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    protected void initScan()
    {
    	 wifi_man = (WifiManager) getSystemService(Context.WIFI_SERVICE);
         if(!wifi_man.isWifiEnabled())
         {
         	Log.i("MainActivity", "Enabling Wireless");
         	Toast.makeText(this, "Enabling wifi", Toast.LENGTH_SHORT).show();
         	wifi_man.setWifiEnabled(true);
         	wifi_man.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "Router Triangulation");
         }
         wifi_rec = new WifiReciever();
         registerReceiver(wifi_rec, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
         wifi_man.startScan();
         Toast.makeText(this,"Scanning Area", Toast.LENGTH_SHORT).show();
    }
    
    protected void startCalibration()
    {
    	Intent i = new Intent(this, CalibrateActivity.class);
    	startActivity(i);
    }
    
    
}
