package com.app.letzchat.tasks;

import android.content.ContentValues;
import android.content.Context;

import com.app.letzchat.SmackInvocationException;
import com.app.letzchat.databases.ChatMessageTableHelper;
import com.app.letzchat.xmpp.SmackHelper;


public class SendPlainTextTask extends SendMessageTask {
	public SendPlainTextTask(Response.Listener<Boolean> listener, Context context, String to, String nickname, String body) {
		super(listener, context, to, nickname, body);
	}
	
	@Override
	protected ContentValues newMessage(long timeMillis) {
		return ChatMessageTableHelper.newPlainTextMessage(to, body, timeMillis, true);
	}

	@Override
	protected void doSend(Context context) throws SmackInvocationException {
		SmackHelper.getInstance(context).sendChatMessage(to, body, null);
	}
}