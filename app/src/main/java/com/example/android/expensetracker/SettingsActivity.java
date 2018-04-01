package com.example.android.expensetracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private static final String settings_Fragment = "Fragment_Setting";

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        android.app.Fragment settingsFragment = new SettingsFragment();
        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.settings_fragment_container, settingsFragment, settings_Fragment).commit();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.settingsTile);

    }

}
