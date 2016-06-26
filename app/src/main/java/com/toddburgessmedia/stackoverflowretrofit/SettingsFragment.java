package com.toddburgessmedia.stackoverflowretrofit;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 26/06/16.
 */
public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);
    }
}
