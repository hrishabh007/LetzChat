package com.app.letzchat.fragments;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.app.letzchat.R;
import com.app.letzchat.model.LoginUserProfile;
import com.app.letzchat.tasks.LoadProfileTask;
import com.app.letzchat.tasks.Response;
import com.app.letzchat.utils.PreferenceUtils;


public class ProfileFragment extends PreferenceFragment implements Response.Listener<LoginUserProfile> {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.profile_preferences);

		new LoadProfileTask(this, getActivity()).execute();
	}

	@Override
	public void onResponse(LoginUserProfile profile) {
		if (profile != null) {
			findPreference(PreferenceUtils.AVATAR).setIcon(new BitmapDrawable(getResources(), profile.getAvatar()));
			findPreference(PreferenceUtils.NICKNAME).setSummary(profile.getNickname());
		}
	}
	
	@Override
	public void onErrorResponse(Exception exception) {}
}