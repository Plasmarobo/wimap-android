package com.witech.wimap;

import java.util.ArrayList;
import java.util.List;

import com.witech.wimap.ScanListActivity.WifiReciever;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class MapActivity extends Activity {
	private WifiManager wifi_man;
	private WifiReciever wifi_rec;
	private List<ScanResult> wifi_list;
	private ScanListAdapter adapter;
	private int user_x;
	private int user_y;
	private ImageView map;
	
	class WifiReciever extends BroadcastReceiver
	{
		public void onReceive(Context c, Intent intent)
		{
			Log.i("ScanListActivity", "Scan Results updated");
			wifi_list = wifi_man.getScanResults();
			
			
			//wifi_man.startScan();
		}
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.v("MapActivity", "Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floorplan);  
        map = (ImageView) findViewById(R.id.map_image);
        
    }
}
