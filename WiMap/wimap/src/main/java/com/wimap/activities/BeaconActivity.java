package com.wimap;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.content.BroadcastReceiver;

public class BeaconActivity extends Activity{
	private final static int SELECTROUTER = 7;
	private String ssid;
	private String uid;
	private boolean started;
	private double distance;
	BeaconAPI beaconAPI;
	private int sample_number;
	private Timer timer;
	private static int SAMPLE_COUNT=100;
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
				if(wifi_list.get(i).GetUID().equals(uid))
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
				String dist = ((EditText)findViewById(R.id.dist)).getText().toString();
				distance = Double.parseDouble(dist);
				beaconAPI.CommitSample(r.GetPower(), distance);
				++sample_number;
				if(sample_number == BeaconActivity.SAMPLE_COUNT)
				{
					try {
					    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					    Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), notification);
					    rt.play();
					} catch (Exception e) {
					    e.printStackTrace();
					}
					onStop(null);
					Toast.makeText(context,  "Finished!", Toast.LENGTH_LONG).show();
					++distance;
					((EditText)findViewById(R.id.dist)).setText(Integer.toString((int)distance));
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
		setContentView(R.layout.beacon_view);
		wifi_man = ((WifiManager) getSystemService(Context.WIFI_SERVICE));
		timer = new Timer();
		ssid = "";
		uid = "";
		started = false;
		distance = 0;
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
	}
	
	public void onStart(View v)
	{
		if(uid.equals(""))
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
