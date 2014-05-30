package com.wimap.api;

import android.os.AsyncTask;

import com.wimap.api.HTTPInterface;

public class AsyncHTTP extends AsyncTask<HTTPInterface, Integer, Integer> {

	@Override
	protected Integer doInBackground(HTTPInterface... params) {
		Integer progress = 0;
		for(int i = 0; i < params.length; ++i)
		{
			if(params[i].PerformRequest(progress))
				progress++;
			publishProgress(progress/params.length);
		}
		return progress;
	}

}
