package com.witech.wimap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.witech.wimap.R;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.witech.wimap.BasicResult;


public class ScanListAdapter extends ArrayAdapter<BasicResult> {
	private final Context context;
	private ArrayList<BasicResult> values;
	
	public ScanListAdapter(Context context, String[] ssids, String[] uids, int[] powers)
	{
		super(context, R.layout.scan_list_item);
		values = new ArrayList<BasicResult>();
		this.context = context;
		int size = ssids.length;
		if(size > uids.length) size = uids.length;
		if(size > powers.length) size = powers.length; 
		for(int i = 0; i < size; ++i)
		{
			values.add(new BasicResult(powers[i], ssids[i], uids[i]));
		}
	}
	
	public ScanListAdapter(Context context, List<ScanResult> list)
	{
		super(context, R.layout.scan_list_item);
		values = new ArrayList<BasicResult>();
		this.context = context;
		for(int i = 0; i < list.size(); ++i)
		{
			values.add(new BasicResult(list.get(i)));
		}
	}
	public ScanListAdapter(Context context)
	{
		super(context, R.layout.scan_list_item);
		values = new ArrayList<BasicResult>();
		this.context = context;
		values.clear();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.scan_list_item, parent, false);
		TextView ssid = (TextView) v.findViewById(R.id.ssid);
		TextView uid = (TextView) v.findViewById(R.id.uid);
		TextView power = (TextView) v.findViewById(R.id.power);
		if(position < values.size())
		{
			BasicResult br = values.get(position);
			ssid.setText(br.GetSSID());
			uid.setText(br.GetUID());
			power.setText(Integer.toString(br.GetPower()));
		}
		return v;
	}
	@Override
	public void notifyDataSetChanged()
	{
		Collections.sort(values, new Comparator<BasicResult>(){
		@Override
		public int compare(BasicResult a, BasicResult b)
		{
			if(Math.abs(a.GetPower()) > Math.abs(b.GetPower()))
				return 1;
			else
				return 0;
		}
		});
		super.notifyDataSetChanged();
	}
	

}
