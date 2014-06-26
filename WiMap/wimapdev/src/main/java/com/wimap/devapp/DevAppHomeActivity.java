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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wimap.common.Site;
import com.wimap.devapp.lists.SelectSiteActivity;

public class DevAppHomeActivity extends Activity {
    private static final String test_url = "http://wimap:3000/";
    public static final int SELECT_SITE_CODE = 47;
    public static Site current_site;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devapphome);
        TextView site_text = (TextView)findViewById(R.id.site_text);
        Button site_select = (Button)findViewById(R.id.site_btn);
        Button test_select = (Button)findViewById(R.id.test_btn);
        current_site = new Site();
        current_site.id = 0;
        site_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getBaseContext(), SelectSiteActivity.class), SELECT_SITE_CODE);
            }
        });
        test_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent test = new Intent(getBaseContext(), DevAppActivity.class);
                test.putExtra("site_id", current_site.id);
                startActivity(test);
            }
        });
        site_text.setText("0");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == SELECT_SITE_CODE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                current_site = new Site();
                current_site.id = data.getIntExtra("id", 0);
                current_site.name = data.getStringExtra("name");
                TextView site_text = (TextView)findViewById(R.id.site_text);
                site_text.setText(current_site.name + "(" + Integer.toString(current_site.id) + ")");
                findViewById(R.id.devapphome_root).invalidate();
            }

        }
    }


}
