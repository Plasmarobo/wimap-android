/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.wimap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.wimap.activities.HomeActivity;
import com.wimap.location.core.WiMapLocationService;
import com.wimap.location.models.BasicResult;
import com.wimap.location.templates.WiMapLocationSubscriber;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private Runnable service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        setContentView(R.layout.activity_main);

        Button login_submit = (Button) findViewById(R.id.login_submit);
        login_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                WiMapLocationSubscriber.setCache(new ArrayList<BasicResult>());
                StartService();
                startActivity(new Intent(getBaseContext(), HomeActivity.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    public void onDestroy(){
        super.onDestroy();
        finish();
    }

    private void StartService()
    {
        service = new Runnable() {
            public void run() {
                startService(new Intent(getBaseContext(),WiMapLocationService.class));
            }
        };
        new Thread(service).start();
    }

}
