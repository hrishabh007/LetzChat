package com.app.letzchat.utils;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.database.SQLException;


import com.app.letzchat.databases.ChatContract;
import com.app.letzchat.databases.ContactRequestTableHelper;
import com.app.letzchat.databases.ContactTableHelper;
import com.app.letzchat.providers.DatabaseContentProvider;

import java.util.ArrayList;

public class ProviderUtils {
	/**
	 * Save new contact to db, and update request status as accepted
	 * 
	 * @param context
	 * @param jid
	 * @param nickname
	 * @return
	 */
	public static ContentProviderResult[] addNewContact(Context context, String jid, String nickname, String status) {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		operations.add(ContentProviderOperation.newInsert(ChatContract.ContactTable.CONTENT_URI).
				withValues(ContactTableHelper.newContentValues(jid, nickname, status)).build());
		operations.add(ContentProviderOperation.newUpdate(ChatContract.ContactRequestTable.CONTENT_URI).
				withValues(ContactRequestTableHelper.newContentValuesWithAcceptedStatus()).
				withSelection(ChatContract.ContactRequestTable.COLUMN_NAME_JID + " = ?", new String[]{jid}).build());
		try {
			return context.getContentResolver().applyBatch(DatabaseContentProvider.AUTHORITY, operations);
		} catch (Exception e) {
			throw new SQLException("Failed to add contact");
		}
	}
}