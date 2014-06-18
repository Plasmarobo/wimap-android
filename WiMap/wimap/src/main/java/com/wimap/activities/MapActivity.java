/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.wimap.api.RouterAPI;
import com.wimap.api.SitesAPI;
import com.wimap.common.Router;
import com.wimap.common.math.Intersect;
import com.wimap.common.math.RadialDistance;
import com.wimap.location.models.AndroidRouter;
import com.wimap.location.models.BasicResult;
import com.wimap.location.templates.WiMapLocationSubscriber;
import com.wimap.wimap.R;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends WiMapLocationSubscriber {
	private SitesAPI sites;
    private RouterAPI routers;
	private ImageView map;
	private ImageView icon;
	
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.v("MapActivity", "Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floorplan);
        
        map = (ImageView) findViewById(R.id.map_image);
        icon = (ImageView) findViewById(R.id.avatar);
        sites = new SitesAPI(this);
        routers = new RouterAPI(this);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
        params.leftMargin = map.getWidth()/2; //XCOORD
        params.topMargin = map.getHeight()/2; //YCOORD
        icon.setLayoutParams(params);
        onScanAggrigate(cache);
    }
	
	
	@Override
	public void onScanAggrigate(List<BasicResult> wifi_list) {
		
		if(wifi_list.size() > 4)
		{
		List<RadialDistance> ld = new ArrayList<RadialDistance>();
        List<Router> router_list = routers.Routers();
		for(int i = 0; i < wifi_list.size(); ++i)
		{
			BasicResult sr = wifi_list.get(i);
			if(sr.GetPower() > -90)
			{
				for(int j = 0; j < router_list.size(); ++j)
				{
					AndroidRouter rt = (AndroidRouter) router_list.get(j);
					if(rt == null)
						continue;
					if(sr.GetUID().equals(rt.uid))
					{
						ld.add(new RadialDistance(rt.x, rt.y, rt.z, rt.GetAverageDistance(sr)));
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
			icon.setBackgroundResource(R.drawable.man16);
		}else{
			icon.setBackgroundResource(R.drawable.lost32);
		}
		
		}
		
	}
}
