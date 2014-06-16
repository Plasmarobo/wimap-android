/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.devapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.wimap.api.DistanceSampleAPI;
import com.wimap.api.RouterAPI;
import com.wimap.common.DistanceSample;
import com.wimap.devapp.lists.SelectRouterActivity;
import com.wimap.location.models.AndroidRouter;
import com.wimap.location.models.BasicResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TrainBeaconActivity extends Activity{
	private final static int SELECTROUTER = 7;
	AndroidRouter rt;
	private boolean started;
    private double distance;
	private double [][] data;

    DistanceSampleAPI distancesample_api;
	private int sample_number;
	private Timer timer;
	private static int SAMPLE_COUNT=100;
    private static int DISTANCE_MAX=31;
	private WifiManager wifi_man;
	private class WifiRec extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			List<ScanResult> scan_list = wifi_man.getScanResults();;
			List<BasicResult> wifi_list = new ArrayList<BasicResult>(scan_list.size());
			for(int i = 0; i < scan_list.size(); ++i)
			{
				Log.v("ScanResult:", scan_list.get(i).SSID );
				wifi_list.add(new BasicResult(scan_list.get(i)));
			}
			timer.schedule(new TimerTask(){
				@Override
				public void run() {
					while(!wifi_man.startScan()){Log.e("WIFI_ERROR", "SCAN PREVENTED");}
				}
			}, 1000);
			BasicResult r = null;
			for(int i = 0; i < wifi_list.size(); ++i)
			{
				if(wifi_list.get(i).GetUID().equals(rt.GetUID()))
				{
					r = wifi_list.get(i);
					break;
				}
			}
			if(r == null)
				return;
			TextView dBm = (TextView) findViewById(R.id.power_beacon);
			dBm.setText(Double.toString(r.GetPower()));
			if(started)
			{
				String dist = ((EditText)findViewById(R.id.dist)).getText().toString();
				distance = Double.parseDouble(dist);
                data[(int)distance][sample_number] = r.GetPower();
                DistanceSample d = new DistanceSample();
                d.distance = distance;
                d.power = r.GetPower();
                d.time = new Date();
                d.timestamped = true;
				distancesample_api.Push(d);
				++sample_number;
				if(sample_number == TrainBeaconActivity.SAMPLE_COUNT)
				{
					try {
					    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					    Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), notification);
					    rt.play();
					} catch (Exception e) {
					    e.printStackTrace();
					}
                    distancesample_api.AsyncPush();
					onStop(null);
					Toast.makeText(context,  "Finished!", Toast.LENGTH_LONG).show();
					++distance;
					((EditText)findViewById(R.id.dist)).setText(Integer.toString((int)distance));
                    if(distance > DISTANCE_MAX)
                    {
                        TextView result = (TextView) findViewById(R.id.calc_tx_power);
                        double value = rt.TrainTxPower(data, DISTANCE_MAX, SAMPLE_COUNT);
                        result.setText(Double.toString(value));
                        findViewById(R.id.beacon_root).invalidate();
                    }
				}
				((TextView)findViewById(R.id.sample_no)).setText(Integer.toString(sample_number));
				
			}
			
		}
		
	}
	private WifiRec rec;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("DistanceActivity", "Created");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beacon_view);

		wifi_man = ((WifiManager) getSystemService(Context.WIFI_SERVICE));
		timer = new Timer();
		rt = new AndroidRouter();
		started = false;
        distance = 0;
		data = new double[DISTANCE_MAX][SAMPLE_COUNT];
		startSelection(getBaseContext());
		rec = new WifiRec();
		registerReceiver(rec, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
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
        Button save_tx_power = (Button) findViewById(R.id.save_tx_power);
        save_tx_power.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                            public void onClick(View view) {
                                                 double tx_power = rt.GetTxPower();
                                                 RouterAPI db = new RouterAPI(getBaseContext());

                                                 //Copy out existing calibration
                                                 AndroidRouter tmp = (AndroidRouter) db.FilterByUID(rt.GetUID());
                                                 if (tmp == null) {
                                                     tmp = rt;
                                                 } else {
                                                     tmp.SetTxPower(tx_power);
                                                 }
                                                 db.Push(tmp);
                                             }

                                         }
        );
	}
	
	public void onStart(View v)
	{
		if(rt.GetUID().equals(""))
			startSelection(getBaseContext());
		started = true;
		findViewById(R.id.beacon_start).setVisibility(View.GONE);
		findViewById(R.id.beacon_stop).setVisibility(View.VISIBLE);
		sample_number = 0;
	}
	
	public void onStop(View v)
	{
		started = false;
		findViewById(R.id.beacon_start).setVisibility(View.VISIBLE);
		findViewById(R.id.beacon_stop).setVisibility(View.GONE);
	}
	
	
	public void startSelection(View v)
	{
		findViewById(R.id.beacon_stop).setVisibility(View.GONE);
		startSelection(v.getContext());
	}
	public void onPause()
	{
		super.onPause();
		unregisterReceiver(rec);
		timer.cancel();
	}
	public void onResume()
	{
		super.onResume();
		timer = new Timer();
		registerReceiver(rec, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				while(!wifi_man.startScan()){Log.e("WIFI_ERROR", "SCAN PREVENTED");}
			}
		}, 1000);
	}
	
	public void startSelection(Context c)
	{
		startActivityForResult(new Intent(c, SelectRouterActivity.class), SELECTROUTER);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == SELECTROUTER)
		{
			if(resultCode == RESULT_OK)
			{

				rt = new AndroidRouter(0.0, 0.0, 0.0, data.getStringExtra("ssid"), data.getStringExtra("uid"), Double.parseDouble(data.getStringExtra("dBm")), Double.parseDouble(data.getStringExtra("freq")));

				TextView routername = (TextView) findViewById(R.id.ssid_beacon);
				routername.setText(new String(rt.GetSSID()));
				TextView routermac = (TextView) findViewById(R.id.uid_beacon);
				routermac.setText(new String(rt.GetUID()));
				findViewById(R.id.beacon_root).invalidate();
				//beaconAPI = new BeaconAPI(rt.GetSSID(), rt.GetUID());
                this.data = new double[DISTANCE_MAX][SAMPLE_COUNT];
				//http.execute(beaconAPI);
				//onScanAggrigate(cache);
			}
			
		}
	}

    protected void onDestroy()
    {
        super.onDestroy();
        distancesample_api.SyncPush();
    }
}
