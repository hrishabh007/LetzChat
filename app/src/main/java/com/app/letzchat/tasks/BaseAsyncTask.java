package com.app.letzchat.tasks;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.AsyncTask;



public abstract class BaseAsyncTask<Params, Progress, T> extends AsyncTask<Params, Progress, Response<T>> {
	private WeakReference<Response.Listener<T>> listenerWrapper;
	private WeakReference<Context> contextWrapper;
	
	public BaseAsyncTask(Response.Listener<T> listener, Context context) {
		listenerWrapper = new WeakReference<Response.Listener<T>>(listener);
		contextWrapper = new WeakReference<Context>(context);
	}
	
	protected Response.Listener<T> getListener() {
		return listenerWrapper.get();
	}
	
	protected Context getContext() {
		return contextWrapper.get();
	}
	
	@Override
	protected void onPostExecute(Response<T> response) {
		Response.Listener<T> listener = getListener();
		
		if (listener != null && response != null) {
			if (response.isSuccess()) {
				listener.onResponse(response.getResult());
			} else {
				listener.onErrorResponse(response.getException());
			}
		}
	}
}