package com.app.letzchat.databases;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;



public class ConversationTableHelper {
	private static final String SQL_CREATE_ENTRIES =
		    "CREATE TABLE " + ChatContract.ConversationTable.TABLE_NAME + " (" +
		    ChatContract.ConversationTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		    ChatContract.ConversationTable.COLUMN_NAME_NAME + ChatDbHelper.TEXT_TYPE + ChatDbHelper.COMMA_SEP +
		    ChatContract.ConversationTable.COLUMN_NAME_NICKNAME + ChatDbHelper.TEXT_TYPE + ChatDbHelper.COMMA_SEP +
		    ChatContract.ConversationTable.COLUMN_NAME_LATEST_MESSAGE + ChatDbHelper.TEXT_TYPE + ChatDbHelper.COMMA_SEP +
		    ChatContract.ConversationTable.COLUMN_NAME_UNREAD + ChatDbHelper.INTEGER_TYPE + ChatDbHelper.COMMA_SEP +
		    ChatContract.ConversationTable.COLUMN_NAME_TIME + " LONG" +
		    " )";
	
	private static final String SQL_DELETE_ENTRIES =
		    "DROP TABLE IF EXISTS " + ChatContract.ConversationTable.TABLE_NAME;
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(SQL_CREATE_ENTRIES);
	}
	
	public static void onUpgrade(SQLiteDatabase database) {
		database.execSQL(SQL_DELETE_ENTRIES);
	}
	
	public static ContentValues newUpdateContentValues(String message, long timeMillis) {
		ContentValues values = new ContentValues();
		values.put(ChatContract.ConversationTable.COLUMN_NAME_LATEST_MESSAGE, message);
		values.put(ChatContract.ConversationTable.COLUMN_NAME_TIME, timeMillis);
		
		return values;
	}
	
	public static ContentValues newInsertContentValues(String name, String nickname, String message, long timeMillis, int unread) {
		ContentValues values = new ContentValues();
		values.put(ChatContract.ConversationTable.COLUMN_NAME_NAME, name);
		values.put(ChatContract.ConversationTable.COLUMN_NAME_NICKNAME, nickname);
		values.put(ChatContract.ConversationTable.COLUMN_NAME_LATEST_MESSAGE, message);
		values.put(ChatContract.ConversationTable.COLUMN_NAME_TIME, timeMillis);
		values.put(ChatContract.ConversationTable.COLUMN_NAME_UNREAD, unread);
		
		return values;
	}
}