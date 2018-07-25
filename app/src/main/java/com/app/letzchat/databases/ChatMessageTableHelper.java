package com.app.letzchat.databases;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.letzchat.xmpp.UserLocation;


public class ChatMessageTableHelper {
	
	private static final String SQL_CREATE_ENTRIES =
		    "CREATE TABLE " + ChatContract.ChatMessageTable.TABLE_NAME + " (" +
		    ChatContract.ChatMessageTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		    ChatContract.ChatMessageTable.COLUMN_NAME_JID + ChatDbHelper.TEXT_TYPE + ChatDbHelper.COMMA_SEP +
		    ChatContract.ChatMessageTable.COLUMN_NAME_MESSAGE + ChatDbHelper.TEXT_TYPE + ChatDbHelper.COMMA_SEP +
		    ChatContract.ChatMessageTable.COLUMN_NAME_TYPE + ChatDbHelper.INTEGER_TYPE + ChatDbHelper.COMMA_SEP +
		    ChatContract.ChatMessageTable.COLUMN_NAME_STATUS + ChatDbHelper.INTEGER_TYPE + ChatDbHelper.COMMA_SEP +
		    ChatContract.ChatMessageTable.COLUMN_NAME_TIME + " LONG" + ChatDbHelper.COMMA_SEP +
			ChatContract.ChatMessageTable.COLUMN_NAME_LATITUDE + " DOUBLE" + ChatDbHelper.COMMA_SEP +
			ChatContract.ChatMessageTable.COLUMN_NAME_LONGITUDE + " DOUBLE" + ChatDbHelper.COMMA_SEP +
			ChatContract.ChatMessageTable.COLUMN_NAME_ADDRESS + ChatDbHelper.TEXT_TYPE + ChatDbHelper.COMMA_SEP +
			ChatContract.ChatMessageTable.COLUMN_NAME_MEDIA_URL + ChatDbHelper.TEXT_TYPE +
			" )";
	
	private static final String SQL_DELETE_ENTRIES =
		    "DROP TABLE IF EXISTS " + ChatContract.ChatMessageTable.TABLE_NAME;
	
	public static final int TYPE_INCOMING_PLAIN_TEXT = 1;
	public static final int TYPE_OUTGOING_PLAIN_TEXT = 2;
	public static final int TYPE_INCOMING_LOCATION = 3;
	public static final int TYPE_OUTGOING_LOCATION = 4;
	public static final int TYPE_INCOMING_IMAGE = 5;
	public static final int TYPE_OUTGOING_IMAGE = 6;

	public static final int VIEW_TYPE_COUNT = 6;
	
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_PENDING = 2;
	public static final int STATUS_FAILURE = 3;

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(SQL_CREATE_ENTRIES);
	}
	
	public static void onUpgrade(SQLiteDatabase database) {
		database.execSQL(SQL_DELETE_ENTRIES);
	}
	
	public static ContentValues newPlainTextMessage(String jid, String body, long timeMillis, boolean outgoing) {
		ContentValues values = new ContentValues();
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_JID, jid);
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_MESSAGE, body);
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_TIME, timeMillis);
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_TYPE, outgoing ? TYPE_OUTGOING_PLAIN_TEXT : TYPE_INCOMING_PLAIN_TEXT);
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_STATUS, outgoing ? STATUS_PENDING : STATUS_SUCCESS);
		
		return values;
	}

	public static ContentValues newLocationMessage(String jid, String body, long timeMillis, UserLocation location, boolean outgoing) {
		ContentValues values = new ContentValues();
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_JID, jid);
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_MESSAGE, body);
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_TIME, timeMillis);
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_TYPE, outgoing ? TYPE_OUTGOING_LOCATION : TYPE_INCOMING_LOCATION);
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_STATUS, outgoing ? STATUS_PENDING : STATUS_SUCCESS);
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_LATITUDE, location.getLatitude());
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_LONGITUDE, location.getLongitude());
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_ADDRESS, location.getAddress());

		return values;
	}

	public static ContentValues newImageMessage(String jid, String body, long timeMillis, String path, boolean outgoing) {
		ContentValues values = new ContentValues();
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_JID, jid);
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_MESSAGE, body);
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_TIME, timeMillis);
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_TYPE, outgoing ? TYPE_OUTGOING_IMAGE : TYPE_INCOMING_IMAGE);
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_STATUS, outgoing ? STATUS_PENDING : STATUS_SUCCESS);
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_MEDIA_URL, path);

		return values;
	}
	
	public static ContentValues newSuccessStatusContentValues() {
		ContentValues values = new ContentValues();
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_STATUS, STATUS_SUCCESS);
		
		return values;
	}
	
	public static ContentValues newFailureStatusContentValues() {
		ContentValues values = new ContentValues();
		values.put(ChatContract.ChatMessageTable.COLUMN_NAME_STATUS, STATUS_FAILURE);
		
		return values;
	}

	public static boolean isIncomingMessage(Cursor cursor) {
		int type = cursor.getInt(cursor.getColumnIndex(ChatContract.ChatMessageTable.COLUMN_NAME_TYPE));
		return type == TYPE_INCOMING_PLAIN_TEXT || type == TYPE_INCOMING_LOCATION;
	}

	public static boolean isPlainTextMessage(Cursor cursor) {
		int type = cursor.getInt(cursor.getColumnIndex(ChatContract.ChatMessageTable.COLUMN_NAME_TYPE));
		return isPlainTextMessage(type);
	}

	public static boolean isPlainTextMessage(int type) {
		return type == TYPE_INCOMING_PLAIN_TEXT || type == TYPE_OUTGOING_PLAIN_TEXT;
	}

	public static boolean isLocationMessage(Cursor cursor) {
		int type = cursor.getInt(cursor.getColumnIndex(ChatContract.ChatMessageTable.COLUMN_NAME_TYPE));
		return isLocationMessage(type);
	}

	public static boolean isLocationMessage(int type) {
		return type == TYPE_INCOMING_LOCATION || type == TYPE_OUTGOING_LOCATION;
	}

	public static boolean isImageMessage(int type) {
		return type == TYPE_INCOMING_IMAGE || type == TYPE_OUTGOING_IMAGE;
	}

	public static boolean isImageMessage(Cursor cursor) {
		int type = cursor.getInt(cursor.getColumnIndex(ChatContract.ChatMessageTable.COLUMN_NAME_TYPE));
		return isImageMessage(type);
	}

	public static void updateToVersion2(SQLiteDatabase db) {
		final String sqlAddLatitude = "ALTER TABLE " + ChatContract.ChatMessageTable.TABLE_NAME + " ADD " + ChatContract.ChatMessageTable.COLUMN_NAME_LATITUDE + " DOUBLE";
		final String sqlAddLongitude = "ALTER TABLE " + ChatContract.ChatMessageTable.TABLE_NAME + " ADD " + ChatContract.ChatMessageTable.COLUMN_NAME_LONGITUDE + " DOUBLE";
		final String sqlAddAddress = "ALTER TABLE " + ChatContract.ChatMessageTable.TABLE_NAME + " ADD " + ChatContract.ChatMessageTable.COLUMN_NAME_ADDRESS + ChatDbHelper.TEXT_TYPE;
		final String sqlAddMediaUrl = "ALTER TABLE " + ChatContract.ChatMessageTable.TABLE_NAME + " ADD " + ChatContract.ChatMessageTable.COLUMN_NAME_MEDIA_URL + ChatDbHelper.TEXT_TYPE;

		db.execSQL(sqlAddLatitude);
		db.execSQL(sqlAddLongitude);
		db.execSQL(sqlAddAddress);
		db.execSQL(sqlAddMediaUrl);
	}
}