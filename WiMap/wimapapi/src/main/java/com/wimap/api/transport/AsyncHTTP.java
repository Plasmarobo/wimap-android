/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api.transport;

import android.os.AsyncTask;

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
