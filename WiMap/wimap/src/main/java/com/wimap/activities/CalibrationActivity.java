package com.wimap.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.wimap.components.RouterDatabase;
import com.wimap.wimap.R;

public class CalibrationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        Button ClearButton = (Button) findViewById(R.id.clear_cal);
        Button DistButton = (Button) findViewById(R.id.dist_cal);
        Button EditButton = (Button) findViewById(R.id.edit_cal);
        ClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouterDatabase db = new RouterDatabase(view.getContext());
                db.open();
                db.ForceReset();
                db.close();
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
