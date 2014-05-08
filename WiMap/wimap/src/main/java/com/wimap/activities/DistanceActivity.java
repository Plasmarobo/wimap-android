package com.wimap.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wimap.components.AndroidRouter;
import com.wimap.components.BasicResult;
import com.wimap.components.RouterDatabase;
import com.wimap.components.WiMapServiceSubscriber;
import com.wimap.wimap.R;

import java.util.List;

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
		setContentView(R.layout.activity_distance_view);
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
		for(BasicResult item : wifi_list)
		{
			if(item.GetUID().equals(this.uid))
			{
				r = item;
				break;
			}
		}
		if(r == null)
			return;
		TextView distance = (TextView) findViewById(R.id.distance);
        TextView comp = (TextView) findViewById(R.id.comp_dist);
        TextView fspl = (TextView) findViewById(R.id.fspl_dist);
        TextView rel = (TextView) findViewById(R.id.relative_dist);
		distance.setText(Double.toString(current.GetAverageDistance(r)));
        fspl.setText(Double.toString(current.GetDistance(r)));
        comp.setText(Double.toString(current.GetComparativeDistance(r)));
        rel.setText(Double.toString(current.GetFSPLRelativeDistance(r)));
		TextView dBm = (TextView) findViewById(R.id.dBm);
		dBm.setText(Double.toString(r.GetPower()));
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
