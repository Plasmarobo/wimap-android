package com.witech.wimap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CalibrateActivity extends ScanListActivity {
	private RouterDatabase db;
	static final int EDITROUTER = 1;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		db = new RouterDatabase(this);
		listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> list, View row, int position, long id) {
				Log.i("List", "click");
			
				Intent edit_router = new Intent(list.getContext(), EditRouterActivity.class);
				TextView frequency = (TextView)row.findViewById(R.id.freq);
				TextView power = (TextView)row.findViewById(R.id.power);
				TextView ssid = (TextView)row.findViewById(R.id.ssid);
				TextView uid = (TextView)row.findViewById(R.id.uid);
				int p = Integer.parseInt((String)power.getText());
				int f = Integer.parseInt((String)frequency.getText());
				rt = new AndroidRouter(0, 0, 0, (String)ssid.getText(), (String) uid.getText(), (double)p, (double)f);
				edit_router.putExtra("dBm",p);
				edit_router.putExtra("freq", f);
				startActivityForResult(edit_router, EDITROUTER);
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == EDITROUTER)
		{
			if(resultCode == RESULT_OK)
			{
				rt.SetX(data.getDoubleExtra("X", 0));
				rt.SetY(data.getDoubleExtra("Y", 0));
				rt.SetZ(data.getDoubleExtra("Z", 0));
				rt.SetPower(data.getIntExtra("dBm", -90), data.getIntExtra("freq", 2400));
				db.open();
				db.WriteRouter(rt);
				db.close();
				Toast.makeText(this, "Saved Router", Toast.LENGTH_SHORT).show();
			}
			
		}
	}

}