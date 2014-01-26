package com.witech.wimap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class AddRouter extends Activity {
	private static final int RESULT_CANCEL = 0;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_router);
		TextView ed_x = (TextView) findViewById(R.id.edit_X);
		TextView ed_y = (TextView) findViewById(R.id.edit_Y);
		TextView ed_z = (TextView) findViewById(R.id.edit_Z);
		TextView ed_dbm = (TextView) findViewById(R.id.edit_dBm);
		SeekBar sb_x = (SeekBar) findViewById(R.id.xbar);
		SeekBar sb_y = (SeekBar) findViewById(R.id.ybar);
		SeekBar sb_z = (SeekBar) findViewById(R.id.zbar);
		SeekBar sb_dbm = (SeekBar) findViewById(R.id.dBmbar);
		Intent data = getIntent();
		ed_x.setText("512");
		ed_y.setText("286");
		ed_z.setText("128");
		ed_dbm.setText(Integer.toString(Math.abs((data.getIntExtra("dBm", 75)))));
		sb_x.setMax(1024);
		sb_x.setProgress(512);
		sb_y.setMax(572);
		sb_y.setProgress(286);
		sb_z.setMax(256);
		sb_z.setProgress(128);
		sb_dbm.setMax(120);
		sb_dbm.setProgress(Math.abs((data.getIntExtra("dBm", 75))));
		ed_x.addTextChangedListener(TextWatcher)
	}
	
	public void Cancel()
	{
		setResult(RESULT_CANCEL, null);
		finish();
	}
	
	public void Commit()
	{
		Intent result = new Intent();
		TextView ed_x = (TextView) findViewById(R.id.edit_X);
		TextView ed_y = (TextView) findViewById(R.id.edit_Y);
		TextView ed_z = (TextView) findViewById(R.id.edit_Z);
		TextView ed_dbm = (TextView) findViewById(R.id.edit_dBm);
		result.putExtra("dBm", Integer.parseInt((String) ed_dbm.getText()));
		result.putExtra("X", Integer.parseInt((String) ed_x.getText()));
		result.putExtra("Y", Integer.parseInt((String) ed_y.getText()));
		result.putExtra("Z", Integer.parseInt((String) ed_z.getText()));
		setResult(RESULT_OK, result);
		finish();
	}

}
