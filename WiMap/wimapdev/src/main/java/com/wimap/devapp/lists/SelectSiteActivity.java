/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.devapp.lists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wimap.api.SitesAPI;
import com.wimap.common.Site;
import com.wimap.devapp.R;
import com.wimap.devapp.adapters.SiteListAdapter;

import java.util.List;

public class SelectSiteActivity extends Activity {
    protected ListView listview;
    protected SiteListAdapter adapter;
    protected List<Site> sites;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait);
        Intent viewintent = getIntent();
        SitesAPI site_api = new SitesAPI(this);
        site_api.SyncPull();
        setContentView(R.layout.activity_select_site);
        sites = site_api.Sites();
        listview = (ListView)findViewById(R.id.site_list);
        if(sites != null)
            adapter = new SiteListAdapter(this,sites);
        else
            adapter = new SiteListAdapter(this);
        listview.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> list, View row, int position, long id) {

                TextView id_text = (TextView)row.findViewById(R.id.site_id);
                TextView name = (TextView)row.findViewById(R.id.site_name);

                Intent result = new Intent();
                result.putExtra("id", Integer.getInteger(id_text.getText().toString(),0));
                result.putExtra("name",name.getText());
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.site_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.site_new) {
            setContentView(R.layout.wait);
            SitesAPI sites_api = new SitesAPI(getBaseContext());
            Site s = new Site();
            s.longitude = 7;
            s.latitude = 7;
            s.name = "TEST SITE";
            s.range = 10;
            sites_api.Push(s);
            sites_api.SyncPush();
            adapter.add(s);
            setContentView(R.layout.activity_select_site);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
