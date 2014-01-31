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
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.witech.wimap.Intersect;

public class MapActivity extends Activity {
	private float meters_to_pixels_x;
	private float meters_to_pixels_y;
	private WifiManager wifi_man;
	private WifiReciever wifi_rec;
	private List<ScanResult> wifi_list;
	private Timer timer;
	private List<Router> routers;
	private ImageView map;
	private ImageView icon;
	
	class WifiReciever extends BroadcastReceiver
	{
		@Override
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
							break;
						}
					}
				}
			}
			//Try a weighted average algorithm
			
			if(ld.size() >= 3)
			{
				Intersect point = new Intersect(ld, icon.getX(), icon.getY(), 128);
				Log.i("USER_X", Double.toString(point.GetX()));
				Log.i("USER_Y", Double.toString(point.GetY()));
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
		        params.leftMargin = (int) point.GetX(); //XCOORD
		        params.topMargin = (int) point.GetY(); //YCOORD
		        icon.setLayoutParams(params);
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
        meters_to_pixels_x = 30/1024;
        meters_to_pixels_y = 10/560;
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
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
        params.leftMargin = map.getWidth()/2; //XCOORD
        params.topMargin = map.getHeight()/2; //YCOORD
        icon.setLayoutParams(params);
        //icon.setX(map.getWidth()/2);
        //icon.setY(map.getHeight()/2);
    }
	@Override
	protected void onResume()
	{
		timer = new Timer("ScanInterval", true);
        timer.scheduleAtFixedRate(new WifiScanner(), 0, 3000);
		super.onResume();
	}
	@Override
	protected void onPause()
	{
		timer.cancel();
		super.onPause();
	}
}
