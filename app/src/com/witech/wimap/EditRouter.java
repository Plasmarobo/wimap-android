package com.witech.wimap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class EditRouter extends Activity {
	private static final int RESULT_CANCEL = 0;
	private double f;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_router);
		EditText ed_x = (EditText) findViewById(R.id.edit_X);
		EditText ed_y = (EditText) findViewById(R.id.edit_Y);
		EditText ed_z = (EditText) findViewById(R.id.edit_Z);
		EditText ed_dbm = (EditText) findViewById(R.id.edit_dBm);
		SeekBar sb_x = (SeekBar) findViewById(R.id.xbar);
		SeekBar sb_y = (SeekBar) findViewById(R.id.ybar);
		SeekBar sb_z = (SeekBar) findViewById(R.id.zbar);
		SeekBar sb_dbm = (SeekBar) findViewById(R.id.dBmbar);
		Intent data = getIntent();
		ed_x.setText("512");
		ed_y.setText("286");
		ed_z.setText("128");
		this.f = data.getIntExtra("freq", 2400);
		ed_dbm.setText(Integer.toString(Math.abs((data.getIntExtra("dBm", -75)))));
		sb_x.setMax(1024);
		sb_x.setProgress(512);
		sb_y.setMax(572);
		sb_y.setProgress(286);
		sb_z.setMax(256);
		sb_z.setProgress(128);
		sb_dbm.setMax(120);
		sb_dbm.setProgress(Math.abs((data.getIntExtra("dBm", 75))));
		
		sb_x.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				((EditText)findViewById(R.id.edit_X)).setText(Integer.toString(arg1));
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		sb_y.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				((EditText)findViewById(R.id.edit_Y)).setText(Integer.toString(arg1));
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		sb_z.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				((EditText)findViewById(R.id.edit_Z)).setText(Integer.toString(arg1));
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		sb_dbm.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				((EditText)findViewById(R.id.edit_dBm)).setText(Integer.toString(arg1));
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		ed_z.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				((SeekBar) findViewById(R.id.zbar)).setProgress(Math.abs(Integer.parseInt(s.toString())));
			}
		});
		
		ed_y.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				((SeekBar) findViewById(R.id.ybar)).setProgress(Math.abs(Integer.parseInt(arg0.toString())));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}
		});
		
		ed_x.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				((SeekBar) findViewById(R.id.xbar)).setProgress(Math.abs(Integer.parseInt(arg0.toString())));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}
		});
		ed_dbm.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				((SeekBar) findViewById(R.id.dBmbar)).setProgress(Math.abs(Integer.parseInt(arg0.toString())));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	public void Cancel(View v)
	{
		setResult(RESULT_CANCEL, null);
		finish();
	}
	
	public void Commit(View v)
	{
		View form = (View) v.getParent().getParent();
		Intent result = new Intent();
		EditText ed_x = (EditText) form.findViewById(R.id.edit_X);
		EditText ed_y = (EditText) form.findViewById(R.id.edit_Y);
		EditText ed_z = (EditText) form.findViewById(R.id.edit_Z);
		EditText ed_dbm = (EditText) form.findViewById(R.id.edit_dBm);
		result.putExtra("dBm", Integer.parseInt(ed_dbm.getText().toString()));
		result.putExtra("freq", this.f);
		result.putExtra("X", Double.parseDouble(ed_x.getText().toString()));
		result.putExtra("Y", Double.parseDouble(ed_y.getText().toString()));
		result.putExtra("Z", Double.parseDouble(ed_z.getText().toString()));
		setResult(RESULT_OK, result);
		finish();
	}

}
