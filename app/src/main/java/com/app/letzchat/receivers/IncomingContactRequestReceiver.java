package com.app.letzchat.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;

import com.app.letzchat.ContactRequestListActivity;
import com.app.letzchat.R;
import com.app.letzchat.service.MessageService;
import com.app.letzchat.utils.NotificationUtils;


public class IncomingContactRequestReceiver extends BroadcastReceiver {
	public static int INCOMING_CONTACT_REQUEST_NOTIFICATION_ID = 1;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action  = intent.getAction();
		if (action != null && action.equals(MessageService.ACTION_CONTACT_REQUEST_RECEIVED)) {
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			stackBuilder.addParentStack(ContactRequestListActivity.class);
			stackBuilder.addNextIntent(new Intent(context, ContactRequestListActivity.class));
			
			PendingIntent pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
			
			NotificationUtils.notify(context, context.getString(R.string.app_name),
					intent.getStringExtra(MessageService.EXTRA_DATA_NAME_NOTIFICATION_TEXT),
					INCOMING_CONTACT_REQUEST_NOTIFICATION_ID, pendingIntent);
		}
	}
}