package com.wimap.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wimap.components.AndroidRouter;
import com.wimap.components.RouterDatabase;
import com.wimap.wimap.R;

public class CalibrationActivity extends Activity {
    public static final int DISTANCEACTIVITY = 6;
    public static final int EDITROUTER = 5;
    public static final int UPLOAD = 8;
    public static final int DOWNLOAD = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        Button ClearButton = (Button) findViewById(R.id.clear_cal);
        Button DistButton = (Button) findViewById(R.id.dist_cal);
        Button EditButton = (Button) findViewById(R.id.edit_cal);
        Button Upload = (Button) findViewById(R.id.up_cal);
        Button Download = (Button) findViewById(R.id.down_cal);
        ClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouterDatabase db = new RouterDatabase(view.getContext());
                db.open();
                db.ForceReset();
                db.close();
            }
        });
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
                Log.i("List", "click");

                Intent edit_router = new Intent(view.getContext(), CalibrateActivity.class);
                startActivityForResult(edit_router, EDITROUTER);
            }
        });
        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent upload =  new Intent(view.getContext(), PushRouterActivity.class);
                startActivityForResult(upload, UPLOAD);
            }
        });
        Download.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent download = new Intent(view.getContext(), FetchRouterActivity.class);
                startActivityForResult(download, DOWNLOAD);

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
