package com.witech.wimap;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class PushRouterActivity extends Activity {
	public class PushRouterTask extends AsyncTask<List<Router>, Integer, List<Router>> {
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
		protected List<Router> doInBackground(List<Router>... l) {
			List<Router> li;
			RouterDatabase db = new RouterDatabase(getBaseContext());
			db.open();
			li = db.getAllRouters();
			db.close();
			
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
	    protected void onPostExecute(List<Router> result) {
			saveAndExit(result);
	        Toast.makeText(getBaseContext(), "Region Updated", Toast.LENGTH_SHORT).show();
	    }

	}

		@SuppressWarnings("unchecked")
		protected void onCreate(Bundle savedInstance)
		{
			super.onCreate(savedInstance);
			setContentView(R.layout.activity_fetch); 
			List<Router> l = new ArrayList<Router>();
			ProgressBar p = (ProgressBar) findViewById(R.id.progress);
			p.setProgress(0);
			new PushRouterTask(p).execute(l);
		}
		
		protected void saveAndExit(List<Router> l)
		{
			setResult(RESULT_OK, new Intent());
			finish();
		}

}
