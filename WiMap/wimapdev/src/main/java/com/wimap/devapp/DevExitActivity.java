package com.wimap.devapp;

import android.content.Intent;
import android.os.Bundle;

import com.wimap.location.templates.ExitActivity;

public class DevExitActivity extends ExitActivity {

    @Override
    protected Intent GetExitIntent()
    {
        return new Intent(getApplicationContext(), DevAppHomeActivity.class);
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

}
