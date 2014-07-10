package com.wimap.location.templates;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.wimap.location.R;
import com.wimap.location.WiMapLocationService;

public abstract class ExitActivity extends Activity {

    protected abstract Intent GetExitIntent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Stop WiMap?")
                .setMessage("This will stop WiMap Services and disable indoor navigation, are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        stopService(new Intent(getBaseContext(), WiMapLocationService.class));
                        Intent intent = GetExitIntent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                        finish();
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        finish();
                    }
                })
                .show();
    }




}
