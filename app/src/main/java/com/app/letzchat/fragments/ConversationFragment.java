package com.app.letzchat.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.app.letzchat.ChatActivity;
import com.app.letzchat.MainActivity;
import com.app.letzchat.R;
import com.app.letzchat.adapters.ConversationCursorAdapter;
import com.app.letzchat.databases.ChatContract;


public class ConversationFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {

    private ConversationCursorAdapter adapter;

    private String query;

    private ListView listView;

    private AdapterView.OnItemClickListener onItemClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                new ConversationQueryHandler(getActivity()).startQuery(0, null, ChatContract.ConversationTable.CONTENT_URI,
                        new String[]{ChatContract.ConversationTable.COLUMN_NAME_NAME, ChatContract.ConversationTable.COLUMN_NAME_NICKNAME},
                        ChatContract.ConversationTable._ID + " = ?", new String[]{String.valueOf(id)}, null);
            }
        };

        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        listView = (ListView) view.findViewById(R.id.list);
        listView.setEmptyView(view.findViewById(R.id.empty));
        listView.setOnItemClickListener(onItemClickListener);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new ConversationCursorAdapter(getActivity(), null, ((MainActivity) getActivity()).getImageFetcher());
        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchItem, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                ChatContract.ConversationTable._ID,
                ChatContract.ConversationTable.COLUMN_NAME_NAME,
                ChatContract.ConversationTable.COLUMN_NAME_NICKNAME,
                ChatContract.ConversationTable.COLUMN_NAME_TIME,
                ChatContract.ConversationTable.COLUMN_NAME_LATEST_MESSAGE,
                ChatContract.ConversationTable.COLUMN_NAME_UNREAD
        };

        String selection = null;
        String[] selectionArgs = null;
        if (hasQueryText()) {
            selection = ChatContract.ConversationTable.COLUMN_NAME_NICKNAME + " like ?";
            selectionArgs = new String[]{query + "%"};
        }
        return new CursorLoader(getActivity(), ChatContract.ConversationTable.CONTENT_URI, projection, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void restartLoader(String query) {
        this.query = query;
        getLoaderManager().restartLoader(0, null, this);
    }

    private boolean hasQueryText() {
        return query != null && !query.equals("");
    }

    private static final class ConversationQueryHandler extends AsyncQueryHandler {
        private Activity activity;

        public ConversationQueryHandler(Activity activity) {
            super(activity.getContentResolver());
            this.activity = activity;
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor.moveToFirst()) {
                String to = cursor.getString(cursor.getColumnIndex(ChatContract.ConversationTable.COLUMN_NAME_NAME));
                String nickname = cursor.getString(cursor.getColumnIndex(ChatContract.ConversationTable.COLUMN_NAME_NICKNAME));
                cursor.close();
                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_DATA_NAME_TO, to);
                intent.putExtra(ChatActivity.EXTRA_DATA_NAME_NICKNAME, nickname);
                activity.startActivity(intent);
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        restartLoader(query);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        restartLoader(newText);

        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        if (hasQueryText()) {
            restartLoader(null);
        }

        return true;
    }
}