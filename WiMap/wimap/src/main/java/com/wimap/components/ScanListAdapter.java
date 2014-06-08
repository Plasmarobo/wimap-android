/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.components;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import com.wimap.wimap.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ScanListAdapter extends ArrayAdapter<BasicResult> {
	private final Context context;
	private ArrayList<BasicResult> values;
    private RouterDatabase db;
	
	public ScanListAdapter(Context context, String[] ssids, String[] uids, int[] powers, int[] freqs)
	{
		super(context, R.layout.template_scan_list_item);
        db = new RouterDatabase(context);
		values = new ArrayList<BasicResult>();
		this.context = context;
		int size = ssids.length;
		if(size > uids.length) size = uids.length;
		if(size > powers.length) size = powers.length;
        db.open();
		for(int i = 0; i < size; ++i)
		{
			values.add(new BasicResult(powers[i], ssids[i], uids[i], freqs[i], db.getRouterByUID(uids[i]) != null));
		}
        db.close();
	}
	
	public ScanListAdapter(Context context, List<ScanResult> list)
	{
		super(context, R.layout.template_scan_list_item);
		values = new ArrayList<BasicResult>(list.size());
		this.context = context;
        db = new RouterDatabase(context);
        db.open();
		for(int i = 0; i < list.size(); ++i)
		{
            BasicResult r = new BasicResult(list.get(i));
            r.SetCalibrated(db.getRouterByUID(r.GetUID()) != null);
			values.add(r);
		}
        db.close();
	}
	public ScanListAdapter(Context context)
	{
		super(context, R.layout.template_scan_list_item);
        db = new RouterDatabase(context);
		values = new ArrayList<BasicResult>();
		this.context = context;
		values.clear();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v;
		if(convertView == null)
			v = inflater.inflate(R.layout.template_scan_list_item, parent, false);
		else
			v = convertView;
        CheckBox done = (CheckBox) v.findViewById(R.id.cal_done);

		TextView ssid = (TextView) v.findViewById(R.id.ssid);
		TextView uid = (TextView) v.findViewById(R.id.uid);
		TextView power = (TextView) v.findViewById(R.id.power);
		TextView freq = (TextView) v.findViewById(R.id.freq);
		if(position < values.size())
		{
			BasicResult br = values.get(position);
			ssid.setText(br.GetSSID());
			uid.setText(br.GetUID());
			power.setText(Double.toString(br.GetPower()));
			freq.setText(Double.toString(br.GetFreq()));
            done.setChecked(br.IsCalibrated());
		}else return null;
		return v;
	}
	@Override
	public int getCount()
	{
		return values.size();
	}
	
	@Override
	public void add(BasicResult br)
	{
        db.open();
        br.SetCalibrated(db.getRouterByUID(br.GetUID()) != null);
        db.close();
		values.add(br);
	}
	
	public void clear()
	{
		values = new ArrayList<BasicResult>();
	}
	

	public void sort() {
		Collections.sort(values);
	}

	

}
