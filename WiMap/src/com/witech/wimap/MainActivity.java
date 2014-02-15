package com.witech.wimap;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.v("MainActivity", "Created");
        super.onCreate(savedInstanceState);
        WiMapServiceSubscriber.cache = new ArrayList<BasicResult>();
        new Thread(new Runnable() {
            public void run() {
            	startService(new Intent(getBaseContext(),WiMapService.class));
            }
        }).start();
        setContentView(R.layout.activity_main);  
        Button cal = (Button) findViewById(R.id.startcal);
        Button scan = (Button) findViewById(R.id.startscan);
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	startActivity(new Intent(getBaseContext(), CalibrateActivity.class));
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	startActivity(new Intent(v.getContext(), MapActivity.class));
            }
        });
        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				startActivity(new Intent(getBaseContext(), DistanceActivity.class));
				return true;
			}
        	
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public void onScience(View v)
    {
    	startActivity(new Intent(v.getContext(), BeaconActivity.class));
    }
    
    
    public void PullDB(View v)
    {
    	startActivity(new Intent(v.getContext(), FetchRouterActivity.class));
    }
    
    public void UploadDB(View v)
    {
    	startActivity(new Intent(v.getContext(), PushRouterActivity.class));
    }
    
    public void ClearDB(View v)
    {
    	RouterDatabase db = new RouterDatabase(v.getContext());
		db.open();
		db.ForceReset();
		db.close();
    }
    
    
}
