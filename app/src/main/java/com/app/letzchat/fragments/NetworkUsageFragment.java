package com.app.letzchat.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.app.letzchat.R;
import com.app.letzchat.utils.NetworkUtils;
import com.app.letzchat.utils.PreferenceUtils;


/**
 * Created by dilli on 7/8/2015.
 */
public class NetworkUsageFragment extends PreferenceFragment {
    public static final long KB_IN_BYTES = 1024;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.network_traffic_preferences);

        populateStatsFields();
    }

    private void populateStatsFields() {
        NetworkUtils.RxTxBytes rxTxBytes = NetworkUtils.getTotalRxTxBytes(getActivity());
        if (rxTxBytes != null) {
            String unit = " " + getString(R.string.traffic_unit);
            findPreference(PreferenceUtils.TRAFFIC_TRANSMITTED).setSummary(Long.toString(rxTxBytes.txBytes / KB_IN_BYTES) + unit);
            findPreference(PreferenceUtils.TRAFFIC_RECEIVED).setSummary(Long.toString(rxTxBytes.rxBytes / KB_IN_BYTES) + unit);
        }
    }
}