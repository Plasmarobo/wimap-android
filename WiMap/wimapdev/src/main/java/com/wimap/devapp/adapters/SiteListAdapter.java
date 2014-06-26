/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.devapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wimap.api.SitesAPI;
import com.wimap.common.Site;
import com.wimap.devapp.R;

import java.util.ArrayList;
import java.util.List;

public class SiteListAdapter extends ArrayAdapter<Site> {
    private final Context context;
    private SitesAPI site_api;
    private ArrayList<Site> values;

    public SiteListAdapter(Context context, List<Site> list)
    {
        super(context, R.layout.template_scan_list_item);
        this.context = context;
        values = new ArrayList<Site>(list.size());
        for(int i = 0; i < list.size(); ++i)
        {
            values.add(list.get(i));
        }
    }

    public SiteListAdapter(Context context)
    {
        super(context, R.layout.template_scan_list_item);
        this.context = context;
        this.site_api = new SitesAPI(context);
        site_api.SyncPull();
        values = (ArrayList<Site>)site_api.Sites();
        values.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        if(convertView == null)
            v = inflater.inflate(R.layout.template_site_list_item, parent, false);
        else
            v = convertView;

        TextView id = (TextView) v.findViewById(R.id.site_id);
        TextView name = (TextView) v.findViewById(R.id.site_name);

        if(position < values.size())
        {
            Site st = values.get(position);
            id.setText(Integer.toString(st.id));
            name.setText(st.name);
        }else return null;
        return v;
    }
    @Override
    public int getCount()
    {
        return values.size();
    }

    @Override
    public void add(Site st)
    {
        values.add(st);
    }

    public void clear()
    {
        values = new ArrayList<Site>();
    }
}
