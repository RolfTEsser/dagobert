package de.floresse.dagobert;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class DagosSettingsActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.i(MainActivity.LogTAG, "Dago starting Settings onCreate");

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new DagosSettingsFrag())
                .commit();

		Log.i(MainActivity.LogTAG, "Dago end  SettingsActivity onCreate");
		
    }
    
}
