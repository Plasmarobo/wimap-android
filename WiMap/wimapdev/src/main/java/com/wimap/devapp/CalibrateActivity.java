/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.devapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.wimap.components.AndroidRouter;

import com.wimap.components.RouterDatabase;
import com.wimap.templates.ScanListActivity;
import com.wimap.wimap.R;


public class CalibrateActivity extends ScanListActivity {
	private RouterDatabase db;
	static final int EDITROUTER = 1;
    static final int RESULT_OK = 1;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		db = new RouterDatabase(this);
		listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> list, View row, int position, long id) {
				Log.i("List", "click");
			
				Intent edit_router = new Intent(list.getContext(), EditRouterActivity.class);
				TextView frequency = (TextView)row.findViewById(R.id.freq);
				TextView power = (TextView)row.findViewById(R.id.power);
				TextView ssid = (TextView)row.findViewById(R.id.ssid);
				TextView uid = (TextView)row.findViewById(R.id.uid);
				double p = Double.parseDouble((String)power.getText());
				double f = Double.parseDouble((String)frequency.getText());
				rt = new AndroidRouter(0, 0, 0, (String)ssid.getText(), (String) uid.getText(), p, f);
				edit_router.putExtra("dBm",p);
				edit_router.putExtra("frequency", (int) f);
				startActivityForResult(edit_router, EDITROUTER);
			}
		});
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
				rt.SetPower(data.getIntExtra("dBm", -90), data.getIntExtra("frequency", 2400));

				db.open();
				db.WriteRouter(rt);
				db.close();
				Toast.makeText(this, "Saved Router", Toast.LENGTH_SHORT).show();
                this.listview.invalidate();
			}else{
                Toast.makeText(this, "Could not save calibration", Toast.LENGTH_LONG).show();
            }
			
		}
	}

}
