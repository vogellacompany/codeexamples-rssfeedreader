package com.example.android.rssreader;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by vogella on 22.02.17.
 */

public class MyPreferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.mypreferenes);
    }
}
