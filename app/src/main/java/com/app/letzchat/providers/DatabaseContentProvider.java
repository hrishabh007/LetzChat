package com.app.letzchat.providers;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


import com.app.letzchat.databases.ChatContract;
import com.app.letzchat.databases.ChatDbHelper;

import java.util.ArrayList;

public class DatabaseContentProvider extends ContentProvider {
	public static final String AUTHORITY = "com.app.letzchat.provider";
	
	private static final int CONTACT = 1;
	private static final int CONTACT_ID = 2;
	
	private static final int CONTACT_REQUEST = 3;
	private static final int CONTACT_REQUEST_ID = 4;
	
	private static final int CHAT_MESSAGE = 5;
	private static final int CHAT_MESSAGE_ID = 6;
	
	private static final int CONVERSATION = 7;
	private static final int CONVERSATION_ID = 8;
	
	private final UriMatcher uriMatcher;
	
	private ChatDbHelper dbHelper;
	
	public DatabaseContentProvider() {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		uriMatcher.addURI(AUTHORITY, ChatContract.ContactTable.TABLE_NAME, CONTACT);
		uriMatcher.addURI(AUTHORITY, ChatContract.ContactTable.TABLE_NAME + "/#", CONTACT_ID);
		
		uriMatcher.addURI(AUTHORITY, ChatContract.ContactRequestTable.TABLE_NAME, CONTACT_REQUEST);
		uriMatcher.addURI(AUTHORITY, ChatContract.ContactRequestTable.TABLE_NAME + "/#", CONTACT_REQUEST_ID);
		
		uriMatcher.addURI(AUTHORITY, ChatContract.ChatMessageTable.TABLE_NAME, CHAT_MESSAGE);
		uriMatcher.addURI(AUTHORITY, ChatContract.ChatMessageTable.TABLE_NAME + "/#", CHAT_MESSAGE_ID);
		
		uriMatcher.addURI(AUTHORITY, ChatContract.ConversationTable.TABLE_NAME, CONVERSATION);
		uriMatcher.addURI(AUTHORITY, ChatContract.ConversationTable.TABLE_NAME + "/#", CONVERSATION_ID);
	}
	
	@Override
	public boolean onCreate() {
		dbHelper = ChatDbHelper.getInstance(getContext());
		
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String table = null;
		final int code = uriMatcher.match(uri);
		
		if (code == CONTACT || code == CONTACT_ID) {
			table = ChatContract.ContactTable.TABLE_NAME;
			if (sortOrder == null) {
				sortOrder = ChatContract.ContactTable.DEFAULT_SORT_ORDER;
			}
			
			if (code == CONTACT_ID) {
				selection = ChatContract.ContactTable._ID + "=?";
				selectionArgs = new String[] {uri.getLastPathSegment()};
			}
		} else if (code == CONTACT_REQUEST || code == CONTACT_REQUEST_ID) {
			table = ChatContract.ContactRequestTable.TABLE_NAME;
			if (sortOrder == null) {
				sortOrder = ChatContract.ContactRequestTable.DEFAULT_SORT_ORDER;
			}
			
			if (code == CONTACT_REQUEST_ID) {
				selection = ChatContract.ContactRequestTable._ID + "=?";
				selectionArgs = new String[] {uri.getLastPathSegment()};
			}
		} else if (code == CHAT_MESSAGE || code == CHAT_MESSAGE_ID){
			table = ChatContract.ChatMessageTable.TABLE_NAME;
			if (sortOrder == null) {
				sortOrder = ChatContract.ChatMessageTable.DEFAULT_SORT_ORDER;
			}
			
			if (code == CHAT_MESSAGE_ID) {
				selection = ChatContract.ChatMessageTable._ID + "=?";
				selectionArgs = new String[] {uri.getLastPathSegment()};
			}
		} else if (code == CONVERSATION || code == CONVERSATION_ID) {
			table = ChatContract.ConversationTable.TABLE_NAME;
			if (sortOrder == null) {
				sortOrder = ChatContract.ConversationTable.DEFAULT_SORT_ORDER;
			}
			
			if (code == CONVERSATION_ID) {
				selection = ChatContract.ConversationTable._ID + "=?";
				selectionArgs = new String[] {uri.getLastPathSegment()};
			}
		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case CONTACT:
			return ChatContract.ContactTable.CONTENT_TYPE;
		
		case CONTACT_ID:
			return ChatContract.ContactTable.CONTENT_ITEM_TYPE;
			
		case CONTACT_REQUEST:
			return ChatContract.ContactRequestTable.CONTENT_TYPE;
			
		case CONTACT_REQUEST_ID:
			return ChatContract.ContactRequestTable.CONTENT_ITEM_TYPE;
			
		case CHAT_MESSAGE:
			return ChatContract.ChatMessageTable.CONTENT_TYPE;
			
		case CHAT_MESSAGE_ID:
			return ChatContract.ChatMessageTable.CONTENT_ITEM_TYPE;
			
		case CONVERSATION:
			return ChatContract.ConversationTable.CONTENT_TYPE;
			
		case CONVERSATION_ID:
			return ChatContract.ConversationTable.CONTENT_ITEM_TYPE;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri contentUri = null;
		String table = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		switch (uriMatcher.match(uri)) {
		case CONTACT:
			table = ChatContract.ContactTable.TABLE_NAME;
			contentUri = ChatContract.ContactTable.CONTENT_URI;
			break;
			
		case CONTACT_REQUEST:
			table = ChatContract.ContactRequestTable.TABLE_NAME;
			contentUri = ChatContract.ContactRequestTable.CONTENT_URI;
			break;
			
		case CHAT_MESSAGE:
			table = ChatContract.ChatMessageTable.TABLE_NAME;
			contentUri = ChatContract.ChatMessageTable.CONTENT_URI;
			break;
			
		case CONVERSATION:
			table = ChatContract.ConversationTable.TABLE_NAME;
			contentUri = ChatContract.ConversationTable.CONTENT_URI;
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		long rowId = db.insert(table, null, values);
		if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(contentUri, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
		
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		String table = null;
		switch (uriMatcher.match(uri)) {
			case CHAT_MESSAGE:
				table = ChatContract.ChatMessageTable.TABLE_NAME;
				break;

			case CONVERSATION:
				table = ChatContract.ConversationTable.TABLE_NAME;
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = db.delete(table, where, whereArgs);
		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		String table = null;
		
		switch (uriMatcher.match(uri)) {
		case CONTACT:
			table = ChatContract.ContactTable.TABLE_NAME;
			break;
			
		case CONTACT_REQUEST:
			table = ChatContract.ContactRequestTable.TABLE_NAME;
			break;
		
		case CONTACT_REQUEST_ID:
			table = ChatContract.ContactRequestTable.TABLE_NAME;
			where = DatabaseUtils.concatenateWhere(ChatContract.ContactRequestTable._ID + " = " + ContentUris.parseId(uri), where);
			break;
			
		case CHAT_MESSAGE_ID:
			table = ChatContract.ChatMessageTable.TABLE_NAME;
			where = DatabaseUtils.concatenateWhere(ChatContract.ChatMessageTable._ID + " = " + ContentUris.parseId(uri), where);
			break;
			
		case CONVERSATION:
			table = ChatContract.ConversationTable.TABLE_NAME;
			break;
			
		case CONVERSATION_ID:
			table = ChatContract.ConversationTable.TABLE_NAME;
			where = DatabaseUtils.concatenateWhere(ChatContract.ConversationTable._ID + " = " + ContentUris.parseId(uri), where);
			break;
		
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = db.update(table, values, where, whereArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}
	
	@Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			ContentProviderResult[] results = super.applyBatch(operations);
			db.setTransactionSuccessful();
			
			return results;
		} catch (OperationApplicationException e) {
			e.printStackTrace();
			return null;
		} finally {
			db.endTransaction();
		}
	}
}