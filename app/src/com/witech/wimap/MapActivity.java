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
import android.widget.ImageView;
import android.widget.Toast;

public class MapActivity extends Activity {
	private WifiManager wifi_man;
	private WifiReciever wifi_rec;
	private List<ScanResult> wifi_list;
	private Timer timer;
	private List<Router> routers;
	private double user_x;
	private double user_y;
	private ImageView map;
	private ImageView icon;
	
	class WifiReciever extends BroadcastReceiver
	{
		public void onReceive(Context c, Intent intent)
		{
			Log.i("ScanListActivity", "Scan Results updated");
			wifi_list = wifi_man.getScanResults();
			if(wifi_list.size() > 2)
			{
			List<RadialDistance> ld = new ArrayList<RadialDistance>();
			for(int i = 0; i < wifi_list.size(); ++i)
			{
				ScanResult sr = wifi_list.get(i);
				if(sr.level > -90)
				{
					for(int j = 0; j < routers.size(); ++j)
					{
						Router rt = routers.get(j);
						if(rt == null)
							continue;
						if(sr.BSSID.equals(rt.GetUID()))
						{
							ld.add(new RadialDistance(rt.GetX(), rt.GetY(), rt.GetX(), (rt.GetComparativeDistance(sr) + rt.GetFDSPLDistance(sr))/2.0));
						}
					}
				}
			}
			//Try a weighted average algorithm
			user_x = 0;
			user_y = 0;
			
			
			
			if(ld.size() > 3)
			{
				
				Log.i("USER_X", Double.toString(user_x));
				Log.i("USER_Y", Double.toString(user_y));
				icon.setX((float)user_x);
				icon.setY((float)user_y);
				icon.bringToFront();
				icon.setBackgroundResource(R.drawable.man32);
			}else{
				icon.setBackgroundResource(R.drawable.lost32);
			}
			
			}
			
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
    protected void onCreate(Bundle savedInstanceState) {
    	Log.v("MapActivity", "Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floorplan);  
        map = (ImageView) findViewById(R.id.map_image);
        icon = (ImageView) findViewById(R.id.avatar);
        RouterDatabase db = new RouterDatabase(this);
        db.open();
        //Fix this for production
        routers = db.getAllRouters();
        db.close();
        wifi_man = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi_rec = new WifiReciever();
        registerReceiver(wifi_rec, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        timer = new Timer("ScanInterval", true);
        timer.scheduleAtFixedRate(new WifiScanner(), 0, 3000);
        icon.setX(map.getWidth()/2);
        icon.setY(map.getHeight()/2);
    }
	protected void onResume()
	{
		timer = new Timer("ScanInterval", true);
        timer.scheduleAtFixedRate(new WifiScanner(), 0, 3000);
		super.onResume();
	}
	protected void onPause()
	{
		timer.cancel();
		super.onPause();
	}
}
