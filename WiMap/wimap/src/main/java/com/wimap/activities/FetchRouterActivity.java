package com.wimap.activities;

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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FetchRouterActivity extends Activity {

public class FetchRouterTask extends AsyncTask<List<AndroidRouter>, Integer, List<AndroidRouter>> {
	protected Integer prog; 
	protected View progressIndicator;
	
	public FetchRouterTask()
	{
		progressIndicator = null;
	}
	
	public FetchRouterTask(View v)
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
		HttpResponse resp = RouterAPI.PerformGet();
		InputStream inputStream = null;
		String json_str = "";
		Integer total = 0;
		try{
			HttpEntity entity = resp.getEntity();
			Header h = resp.getFirstHeader("Content-length");
			total = Integer.parseInt(h.getValue());
			int bytes_read = 0;
			inputStream = entity.getContent();
			// json is UTF-8 by default
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
				bytes_read = line.length();
				prog = (int) (100*((float)bytes_read/(float)total));
				publishProgress(prog);
			}
			json_str = sb.toString();
			li = RouterAPI.JsonToCache(json_str);
		} catch (Exception e) { 
			// Oops
		}
		finally {
			try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
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
		List<AndroidRouter> l = new ArrayList<AndroidRouter>();
		new FetchRouterTask(findViewById(R.id.progress)).execute(l);
	}
	
	protected void saveAndExit(List<AndroidRouter> l)
	{
		RouterDatabase db = new RouterDatabase(this);
		db.open();
		db.cacheList(l);
		db.close();
		setResult(RESULT_OK, new Intent());
		finish();
	}
}
