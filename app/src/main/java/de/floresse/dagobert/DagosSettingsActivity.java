package de.floresse.dagobert;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class DagosSettingsActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String themeName = pref.getString("pref_theme", "AppTheme");
        if (themeName.equals("Holo")) {
            setTheme(R.style.AppHolo);
        }
        if (themeName.equals("Dark")) {
            setTheme(R.style.AppDark);
        }
        if (themeName.equals("Light")) {
            setTheme(R.style.AppLight);
        }
        super.onCreate(savedInstanceState);
		Log.i(MainActivity.TAG, "Dago starting Settings onCreate");

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new DagosSettingsFrag())
                .commit();

		Log.i(MainActivity.TAG, "Dago end  SettingsActivity onCreate");
		
    }
    
}
