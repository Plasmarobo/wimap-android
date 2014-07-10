/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.devapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.wimap.api.RouterAPI;

public class DevAppActivity extends Activity {
    private RouterAPI routers;
    public static final int DISTANCEACTIVITY = 6;
    public static final int EDITROUTER = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data = getIntent();
        routers = new RouterAPI(this, data.getIntExtra("site_id", 0));
        setContentView(R.layout.wait);
        routers.SyncPull();

        setContentView(R.layout.activity_devapp);
        //Button ClearButton = (Button) findViewById(R.id.clear_cal);
        Button DistButton = (Button) findViewById(R.id.dist_cal);
        Button EditButton = (Button) findViewById(R.id.edit_cal);
        Button TrainButton = (Button) findViewById(R.id.train_cal);
        Button Upload = (Button) findViewById(R.id.up_cal);
        Button Download = (Button) findViewById(R.id.down_cal);
        DistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("DistButton", "click");
                Intent distance = new Intent(view.getContext(), DistanceActivity.class);
                startActivityForResult(distance, DISTANCEACTIVITY);
            }

        });
        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Edit", "click");

                Intent edit_router = new Intent(view.getContext(), CalibrateActivity.class);
                startActivityForResult(edit_router, EDITROUTER);
            }
        });
        TrainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Log.i("Train", "click");

                Intent train_router = new Intent(view.getContext(), TrainBeaconActivity.class);
                startActivity(train_router);
            }
        });
        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.wait);
                routers.SyncPush();
                setContentView(R.layout.activity_devapp);
            }
        });
        Download.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                setContentView(R.layout.wait);
                routers.SyncPull();
                setContentView(R.layout.activity_devapp);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calibration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
