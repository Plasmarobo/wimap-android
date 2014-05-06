package com.wimap.wimap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.wimap.activities.CalibrationActivity;
import com.wimap.activities.HomeActivity;
import com.wimap.components.BasicResult;
import com.wimap.components.WiMapServiceSubscriber;
import com.wimap.services.WiMapService;

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
        ImageView logo = (ImageView) findViewById(R.id.splash);
        logo.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                WiMapServiceSubscriber.setCache(new ArrayList<BasicResult>());
                StartService();
                startActivity(new Intent(getBaseContext(), CalibrationActivity.class));
                return true;
            }

        });
        Button login_submit = (Button) findViewById(R.id.login_submit);
        login_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                WiMapServiceSubscriber.setCache(new ArrayList<BasicResult>());
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
                startService(new Intent(getBaseContext(),WiMapService.class));
            }
        };
        new Thread(service).start();
    }

}
