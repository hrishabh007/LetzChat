package com.app.letzchat.tasks;

import java.lang.ref.WeakReference;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.app.letzchat.SmackInvocationException;
import com.app.letzchat.databases.ChatContract;
import com.app.letzchat.model.UserProfile;
import com.app.letzchat.utils.AppLog;
import com.app.letzchat.utils.ProviderUtils;
import com.app.letzchat.xmpp.SmackHelper;
import com.app.letzchat.xmpp.SmackVCardHelper;


public class AcceptContactRequestTask extends BaseAsyncTask<Void, Void, UserProfile> {
	private WeakReference<Uri> uriWrapper;
	
	public AcceptContactRequestTask(Response.Listener<UserProfile> listener, Context context, Uri uri) {
		super(listener, context);
		uriWrapper = new WeakReference<Uri>(uri);
	}
	
	@Override
	protected Response<UserProfile> doInBackground(Void... params) {
		Uri requestUri = uriWrapper.get();
		Context context = getContext();
		if (requestUri != null && context != null) {
			Cursor cursor = context.getContentResolver().query(requestUri, 
					new String[]{ChatContract.ContactRequestTable.COLUMN_NAME_NICKNAME, ChatContract.ContactRequestTable.COLUMN_NAME_JID},
					null, null, null);
			try {
				if (cursor.moveToFirst()) {
					String jid = cursor.getString(cursor.getColumnIndex(ChatContract.ContactRequestTable.COLUMN_NAME_JID));
					String nickname = cursor.getString(cursor.getColumnIndex(ChatContract.ContactRequestTable.COLUMN_NAME_NICKNAME));
					
					SmackHelper smackHelper = SmackHelper.getInstance(context);
					// 1. grant subscription to initiator, and request subscription afterwards
					smackHelper.approveSubscription(jid, nickname, true);
					
					// 2. load VCard
					VCard vCard = smackHelper.loadVCard(jid);
					
					// 3. save new contact into db
					ProviderUtils.addNewContact(context, jid, nickname, vCard.getField(SmackVCardHelper.FIELD_STATUS));
					
					return Response.success(new UserProfile(jid, vCard, UserProfile.TYPE_CONTACT));
				}
			} catch(SmackInvocationException e) {
				AppLog.e(String.format("accept contact request error %s", e.toString()), e);
				
				return Response.error(e);
			} finally {
				cursor.close();
			} 
		}
		
		return null;
	}
}