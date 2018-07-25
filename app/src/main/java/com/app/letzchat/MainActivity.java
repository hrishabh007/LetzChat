package com.app.letzchat;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;


import com.app.letzchat.bitmapcache.AvatarImageFetcher;
import com.app.letzchat.fragments.ContactListFragment;
import com.app.letzchat.fragments.ConversationFragment;
import com.app.letzchat.service.MessageService;
import com.app.letzchat.utils.AESEncryption;
import com.app.letzchat.utils.PreferenceUtils;
import com.app.letzchat.xmpp.SmackHelper;

import org.jivesoftware.smack.XMPPConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {
    private AvatarImageFetcher imageFetcher;
    XMPPConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        String serverhost = PreferenceUtils.getServerHost(this);
        String ss = AESEncryption.decrypt(this, serverhost);

        Log.e("hostdes", ">>>>>" + ss);
        Log.e("host", ">>>>>>>>>>>" + serverhost);
        SmackHelper smackHelper = SmackHelper.getInstance(this);
      //  smackHelper.lastseen("1234567894@localhost");

        // start service to login
        startService(new Intent(MessageService.ACTION_CONNECT, null, this, MessageService.class));

        imageFetcher = AvatarImageFetcher.getAvatarImageFetcher(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        MainFragmentPagerAdapter adapter = new MainFragmentPagerAdapter(this);
        pager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

    }

    public AvatarImageFetcher getImageFetcher() {
        return imageFetcher;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivity(new Intent(this, SearchUserActivity.class));
                return true;

            case R.id.action_set_status:
                startActivity(new Intent(this, SetStatusActivity.class));
                return true;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.action_logout:
                //    startActivity(new Intent(this, LoginActivity.class));
                startActivity(new Intent(this, StartupActivity.class));

                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        imageFetcher.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        imageFetcher.closeCache();
    }

    static class MainFragmentPagerAdapter extends FragmentPagerAdapter {
        private static final int COUNT = 2;
        private Activity activity;

        public MainFragmentPagerAdapter(Activity activity) {
            super(activity.getFragmentManager());
            this.activity = activity;
        }

        public Fragment getItem(int position) {
            if (position == 0) {
                return new ConversationFragment();
            }

            if (position == 1) {
                return new ContactListFragment();
            }

            throw new IllegalArgumentException("invalid position");
        }

        public int getCount() {
            return COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return activity.getString(R.string.chats);
            }

            if (position == 1) {
                return activity.getString(R.string.contacts);
            }

            throw new IllegalArgumentException("invalid position");
        }
    }

}