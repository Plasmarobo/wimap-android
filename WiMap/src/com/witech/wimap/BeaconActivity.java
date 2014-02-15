package com.witech.wimap;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class BeaconActivity extends WiMapServiceSubscriber {
	private final static int SELECTROUTER = 7;
	private String ssid;
	private String uid;
	private boolean started;
	private double distance;
	BeaconAPI beaconAPI;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("DistanceActivity", "Created");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beacon_view);
		startSelection(getBaseContext());
		started = false;
		distance = 0;
		((SeekBar)findViewById(R.id.distslider)).setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				((EditText)findViewById(R.id.dist)).setText(Integer.toString(arg1));
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		((EditText)findViewById(R.id.dist)).addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				((SeekBar) findViewById(R.id.distslider)).setProgress(Math.abs(Integer.parseInt(s.toString())));
			}
		});
	}
	
	public void onStart(View v)
	{
		started = true;
	}
	
	public void onStop(View v)
	{
		started = false;
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
		
		TextView dBm = (TextView) findViewById(R.id.power_beacon);
		dBm.setText(Integer.toString(r.GetPower()));
		if(started)
		{
			beaconAPI.CommitSample(r.GetPower(), distance);
		}
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
				TextView routername = (TextView) findViewById(R.id.ssid_beacon);
				routername.setText(new String(ssid));
				TextView routermac = (TextView) findViewById(R.id.uid_beacon);
				routermac.setText(new String(uid));
				findViewById(R.id.beacon_root).invalidate();
				beaconAPI = new BeaconAPI(ssid, uid);
				AsyncHTTP http = new AsyncHTTP();
				http.execute(beaconAPI);
				//onScanAggrigate(cache);
			}
			
		}
	}
}
