package com.witech.wimap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SelectRouterActivity extends ScanListActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> list, View row, int position, long id) {
				Log.i("List", "click");	
				TextView frequency = (TextView)row.findViewById(R.id.freq);
				TextView power = (TextView)row.findViewById(R.id.power);
				TextView ssid = (TextView)row.findViewById(R.id.ssid);
				TextView uid = (TextView)row.findViewById(R.id.uid);
				
				Intent result = new Intent();
				result.putExtra("dBm", power.getText());
				result.putExtra("freq", frequency.getText());
				result.putExtra("ssid", ssid.getText());
				result.putExtra("uid", uid.getText());
				setResult(RESULT_OK, result);
				finish();
			}
		});
		
	}
	
}
