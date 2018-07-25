package com.app.letzchat;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by dilli on 7/10/2015.
 */
public class ServerSettingsActivity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Display the fragment as the main content.
            getFragmentManager().beginTransaction().replace(android.R.id.content,
                    new ServerPreferenceFragment()).commit();
        }
    }

    public static class ServerPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.server_preference);
        }
    }
}