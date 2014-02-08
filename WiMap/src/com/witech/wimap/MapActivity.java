package com.witech.wimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.witech.wimap.Intersect;

public class MapActivity extends Activity implements ScanListConsumer {
	ScanReceiver scan_receiver;
	private List<AndroidRouter> routers;
	private ImageView map;
	private ImageView icon;
	
	
	
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
        scan_receiver = new ScanReceiver(this, (WifiManager) getSystemService(Context.WIFI_SERVICE), 500, this, 10);
        scan_receiver.start();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
        params.leftMargin = map.getWidth()/2; //XCOORD
        params.topMargin = map.getHeight()/2; //YCOORD
        icon.setLayoutParams(params);
    }
	@Override
	protected void onResume()
	{
		scan_receiver.start();
		super.onResume();
	}
	@Override
	protected void onPause()
	{
		scan_receiver.stop();
		super.onPause();
	}
	
	@Override
	public void onScanAggrigate(List<HashMap<String, BasicResult>> l, int aggrigate) {
		List<BasicResult> wifi_list = (List<BasicResult>) scan_receiver.AverageAggrigate();
		for(int i = 0; i < wifi_list.size(); ++i)
		{
			wifi_list.get(i).Average(aggrigate);
		}
		if(wifi_list.size() > 4)
		{
		List<RadialDistance> ld = new ArrayList<RadialDistance>();
		for(int i = 0; i < wifi_list.size(); ++i)
		{
			BasicResult sr = wifi_list.get(i);
			if(sr.GetPower() > -90)
			{
				for(int j = 0; j < routers.size(); ++j)
				{
					AndroidRouter rt = (AndroidRouter) routers.get(j);
					if(rt == null)
						continue;
					if(sr.GetUID().equals(rt.GetUID()))
					{
						ld.add(new RadialDistance(rt.GetX(), rt.GetY(), rt.GetX(), rt.GetAverageDistance(sr)));
						break;
					}
				}
			}
		}
		//Try a weighted average algorithm
		
		if(ld.size() >= 4)
		{
			//Guess at last known point (improve this)
			Intersect point = new Intersect(ld, 0, 0, 128);
			Log.i("USER_X", Double.toString(point.x));
			Log.i("USER_Y", Double.toString(point.y));
			Log.i("USER_Z", Double.toString(point.z));
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
	        params.leftMargin = (int) point.x; //XCOORD
	        params.topMargin = (int) point.y; //YCOORD
	        icon.setLayoutParams(params);
			icon.setBackgroundResource(R.drawable.man32);
		}else{
			icon.setBackgroundResource(R.drawable.lost32);
		}
		
		}
		
	}
}
