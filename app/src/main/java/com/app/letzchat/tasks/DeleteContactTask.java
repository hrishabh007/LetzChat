package com.app.letzchat.tasks;

import android.content.Context;

import com.app.letzchat.SmackInvocationException;
import com.app.letzchat.xmpp.SmackHelper;


public class DeleteContactTask extends BaseAsyncTask<Void, Void, Boolean> {
	private String jid;
	
	public DeleteContactTask(Response.Listener<Boolean> listener, Context context, String jid) {
		super(listener, context);
		
		this.jid = jid;
	}
	
	@Override
	protected Response<Boolean> doInBackground(Void... params) {
		Context context = getContext();
		
		if (context != null) {
			try {
				SmackHelper.getInstance(context).delete(jid);
				
				//ContactTableHelper.getInstance(context).delete(jid);
				
				return Response.success(true);
			} catch(SmackInvocationException e) {
				return Response.error(e);
			}
		} else {
			return null;
		}
	}
}