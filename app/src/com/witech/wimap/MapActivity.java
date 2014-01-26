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
			List<LinearDistance> ld = new ArrayList<LinearDistance>();
			for(int i = 0; i < wifi_list.size(); ++i)
			{
				ScanResult sr = wifi_list.get(i);
				for(int j = 0; j < routers.size(); ++j)
				{
					Router rt = routers.get(i);
					if(sr.BSSID == rt.GetUID())
					{
						ld.add(new LinearDistance(rt.GetPower(), sr.level, 0.45f, (float)rt.GetX(), (float)rt.GetY(), (float)rt.GetZ()));
					}
				}
			}
			//Try a weighted average algorithm
			user_x = 0;
			user_y = 0;
			double distance_sum = 0;;
			for(int i = 0; i < ld.size(); ++i)
			{
				user_x += ld.get(i).GetX()*ld.get(i).GetDistance();
				user_y += ld.get(i).GetY()*ld.get(i).GetDistance();
				distance_sum += ld.get(i).GetDistance();
			}
			user_x = user_x/distance_sum;
			user_y = user_y/distance_sum;
			icon.setX((float)user_x);
			icon.setY((float)user_y);
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
        timer.scheduleAtFixedRate(new WifiScanner(), 0, 1000);
        icon.setX(map.getWidth()/2);
        icon.setY(map.getHeight()/2);
    }
	protected void onResume()
	{
		timer = new Timer("ScanInterval", true);
        timer.scheduleAtFixedRate(new WifiScanner(), 0, 1000);
		super.onResume();
	}
	protected void onPause()
	{
		timer.cancel();
		super.onPause();
	}
}
