/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.devapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wimap.apis.RouterAPI;
import com.wimap.components.AndroidRouter;
import com.wimap.components.RouterDatabase;
import com.wimap.wimap.R;

import java.util.List;

public class PushRouterActivity extends Activity {
	public class PushRouterTask extends AsyncTask<List<AndroidRouter>, Integer, List<AndroidRouter>> {
		protected Integer prog; 
		protected View progressIndicator;
		
		public PushRouterTask()
		{
			progressIndicator = null;
		}
		
		public PushRouterTask(View v)
		{
			progressIndicator = v;
		}
		@Override
		protected void onPreExecute()
		{
			prog = Integer.valueOf(0);
		}
		@Override
		protected List<AndroidRouter> doInBackground(List<AndroidRouter>... l) {
			List<AndroidRouter> li = l[0];
			
			try{
				for(int i = 0; i < li.size(); ++i)
				{
					RouterAPI.Store(li.get(i));
					prog = (int) (100*((float)(i+1)/(float)li.size()));
					publishProgress(prog);
				}
			} catch (Exception e) { 
				// Oops
			}
			
	        return li;
	    }
		@Override
	    protected void onProgressUpdate(Integer... progress) {
			((ProgressBar) progressIndicator).setProgress(progress[0]);
	        //setProgressPercent(progress[0]);
	    }
		@Override
	    protected void onPostExecute(List<AndroidRouter> result) {
			saveAndExit(result);
	        Toast.makeText(getBaseContext(), "Region Updated", Toast.LENGTH_SHORT).show();
	    }

	}

		@SuppressWarnings("unchecked")
		protected void onCreate(Bundle savedInstance)
		{
			super.onCreate(savedInstance);
			setContentView(R.layout.activity_fetch);
			List<AndroidRouter> l;
			RouterDatabase db = new RouterDatabase(getBaseContext());
			db.open();
			l = db.getAllRouters();
			db.close();
			ProgressBar p = (ProgressBar) findViewById(R.id.progress);
			p.setProgress(0);
			new PushRouterTask(p).execute(l);
		}
		
		protected void saveAndExit(List<AndroidRouter> result)
		{
			setResult(RESULT_OK, new Intent());
			finish();
		}

}
