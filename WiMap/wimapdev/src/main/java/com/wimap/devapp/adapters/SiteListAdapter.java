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

import com.wimap.common.Site;
import com.wimap.devapp.R;

import java.util.ArrayList;
import java.util.List;

public class SiteListAdapter extends ArrayAdapter<Site> {
    private final Context context;
    private ArrayList<Site> values;

    public SiteListAdapter(Context context, String [] names, int [] ids)
    {
        super(context, R.layout.template_scan_list_item);
        this.context = context;
        for(int i = 0; i < names.length; ++i)
        {
            values.add(new Site(ids[i], names[i]));
        }
    }

    public SiteListAdapter(Context context, List<Site> list)
    {
        super(context, R.layout.template_scan_list_item);
        values = new ArrayList<Site>(list.size());
        this.context = context;

        for(int i = 0; i < list.size(); ++i)
        {
            values.add(list.get(i));
        }
    }

    public SiteListAdapter(Context context)
    {
        super(context, R.layout.template_scan_list_item);

        values = new ArrayList<Site>();
        this.context = context;
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
            id.setText(st.id);
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
