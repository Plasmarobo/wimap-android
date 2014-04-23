package com.wimap;

import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DistanceActivity extends WiMapServiceSubscriber {
	private final static int SELECTROUTER = 7;
	private String ssid;
	private String uid;
	private AndroidRouter current;
	RouterDatabase db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("DistanceActivity", "Created");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.distance_view); 
		db = new RouterDatabase(this);
		startSelection(getBaseContext());

	}
	
	
	public void startSelection(View v)
	{
		startSelection(v.getContext());
	}
	
	public void startSelection(Context c)
	{
		startActivityForResult(new Intent(c, SelectRouterActivity.class), SELECTROUTER);
	}
	
	
	@Override
	public void onScanAggrigate(List<BasicResult> wifi_list) {
		if(current == null)
			return;
		BasicResult r = null;
		for(int i = 0; i < wifi_list.size(); ++i)
		{
			if(wifi_list.get(i).GetUID().equals(this.uid))
			{
				r = wifi_list.get(i);
				break;
			}
		}
		if(r == null)
			return;
		TextView distance = (TextView) findViewById(R.id.distance);
		distance.setText(Double.toString(current.GetAverageDistance(r)));
		TextView dBm = (TextView) findViewById(R.id.dBm);
		dBm.setText(Integer.toString(r.GetPower()));
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == SELECTROUTER)
		{
			if(resultCode == RESULT_OK)
			{
				ssid = data.getStringExtra("ssid");
				uid = data.getStringExtra("uid");
				db.open();
				current = db.getRouterByUID(uid);
				db.close();
				if(current == null)
				{
					Toast.makeText(this, "Could not find router in DB", Toast.LENGTH_LONG).show();
					return;
				}
				
				TextView routername = (TextView) findViewById(R.id.routername);
				routername.setText(new String(ssid));
				TextView routermac = (TextView) findViewById(R.id.routermac);
				routermac.setText(new String(uid));
				findViewById(R.id.distance_root).invalidate();
				onScanAggrigate(cache);
			}
			
		}
	}

}
