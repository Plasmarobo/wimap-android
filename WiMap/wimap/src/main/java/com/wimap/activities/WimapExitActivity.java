package com.wimap.activities;

import android.content.Intent;
import android.os.Bundle;

import com.wimap.location.templates.ExitActivity;
import com.wimap.wimap.MainActivity;


public class WimapExitActivity extends ExitActivity {

    @Override
    protected Intent GetExitIntent()
    {
        return new Intent(getApplicationContext(), MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

}
