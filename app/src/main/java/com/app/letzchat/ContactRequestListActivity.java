package com.app.letzchat;

import android.app.LoaderManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.app.letzchat.adapters.ContactRequestCursorAdapter;
import com.app.letzchat.databases.ChatContract;
import com.app.letzchat.model.UserProfile;
import com.app.letzchat.receivers.IncomingContactRequestReceiver;
import com.app.letzchat.service.MessageService;
import com.app.letzchat.tasks.AcceptContactRequestTask;
import com.app.letzchat.tasks.Response;


public class ContactRequestListActivity extends AppCompatActivity
	implements LoaderManager.LoaderCallbacks<Cursor>, ContactRequestCursorAdapter.OnAcceptButtonClickListener {

	private ListView listView;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action != null && action.equals(MessageService.ACTION_CONTACT_REQUEST_RECEIVED)) {
				abortBroadcast();
			}
		}
	};
	
	private ContactRequestCursorAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contact_requests);
		listView = (ListView)findViewById(R.id.list);

		// Create an empty adapter we will use to display the loaded data.
		adapter = new ContactRequestCursorAdapter(this, null, 0);
		adapter.setOnAcceptButtonClicklistener(this);
		listView.setAdapter(adapter);
		
		listView.setItemsCanFocus(false);
		
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		IntentFilter filter = new IntentFilter(MessageService.ACTION_CONTACT_REQUEST_RECEIVED);
		filter.setPriority(10);
		registerReceiver(receiver, filter);
	
		// cancel notification if existing
		cancelNotificationIfExisting();
	}
	
	protected void onPause() {
		super.onPause();
		
		unregisterReceiver(receiver);
	}
	
	@Override
	public void onAcceptButtonClick(Uri uri) {
		new AcceptContactRequestTask(new Response.Listener<UserProfile>() {
			@Override
			public void onResponse(UserProfile result){
				// start contact profile activity
				Intent intent = new Intent(ContactRequestListActivity.this, UserProfileActivity.class);
				intent.putExtra(UserProfileActivity.EXTRA_DATA_NAME_USER_PROFILE, result);
				startActivity(intent);
			}
			
			@Override
			public void onErrorResponse(Exception exception) {}
			
		}, this, uri).execute();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] PROJECTION = new String[] {
			ChatContract.ContactRequestTable._ID,
			ChatContract.ContactRequestTable.COLUMN_NAME_JID,
			ChatContract.ContactRequestTable.COLUMN_NAME_NICKNAME,
			ChatContract.ContactRequestTable.COLUMN_NAME_STATUS};
		return new CursorLoader(this, ChatContract.ContactRequestTable.CONTENT_URI, PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
	
	private void cancelNotificationIfExisting() {
		((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).cancel(
				IncomingContactRequestReceiver.INCOMING_CONTACT_REQUEST_NOTIFICATION_ID);
	}
}