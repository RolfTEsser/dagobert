package de.floresse.dagobert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class DagosSettingsFrag extends PreferenceFragment
                               implements OnSharedPreferenceChangeListener {

	private Boolean changedFilename = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(MainActivity.LogTAG, "Dago starting SettingsFragment onCreate");

	    // Load the preferences from an XML resource
	    addPreferencesFromResource(R.xml.preferences);
	    PreferenceScreen pfs = getPreferenceScreen();

	    Preference pf;
	    pf = getPreferenceManager().findPreference("pref_isSound_Aua");
	    pf.setOnPreferenceChangeListener(new myChangeListener());
	    pf = getPreferenceManager().findPreference("pref_isSound_haha");
	    pf.setOnPreferenceChangeListener(new myChangeListener());
	    pf = getPreferenceManager().findPreference("pref_isSound_moepse");
	    pf.setOnPreferenceChangeListener(new myChangeListener());
	    pf = getPreferenceManager().findPreference("pref_isSound_werner");
	    pf.setOnPreferenceChangeListener(new myChangeListener());
        pf = findPreference("pref_key_FixSatz");
        pf.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference p) {
            	//((EditTextPreference)p).getEditText().setText("888");
            	Log.i("dagobert", "PreferenceClick on : " + p.getKey());
                return true;
            }
        });
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        // Set summary to be the user-description for the selected value
        pf.setSummary(sharedPreferences.getString("pref_key_FixSatz", "") + " €");
        pf = findPreference("pref_isFixSatz");
        // Set summary to be the user-description for the selected value
        if (sharedPreferences.getBoolean("pref_isFixSatz", false)) {
        	pf.setSummary("Restbedarf wird mit festem Satz berechnet");
        } else {
        	pf.setSummary("Restbedarf wird mit variablem Satz berechnet");
        }
        pf = findPreference("pref_filename");
		pf.setSummary(sharedPreferences.getString("pref_filename", MainActivity.filename));
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_filename")) {
			Log.i(MainActivity.LogTAG, "filename changed");
			changedFilename=true;
			Preference fn = findPreference(key);
			fn.setSummary(sharedPreferences.getString(key, MainActivity.filename));
		}
        if (key.equals("pref_key_FixSatz")) {
            Preference satz = findPreference(key);
            // Set summary to be the user-description for the selected value
            satz.setSummary(sharedPreferences.getString(key, "") + " €");
        }
        if (key.equals("pref_isFixSatz")) {
            Preference pf = findPreference(key);
            if (sharedPreferences.getBoolean(key, false)) {
            	// Set summary to be the user-description for the selected value
            	pf.setSummary("Restbedarf wird mit festem Satz berechnet");
            } else {
            	pf.setSummary("Restbedarf wird mit variablem Satz berechnet");
            }
        }

	}
	
	
	@Override
	public void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onStop() {
		super.onStop();
/*
		if (changedFilename) {
			Intent intent = new Intent(getActivity(), MainActivity.class);
			startActivity(intent);
			getActivity().finish();
		}
*/
	}

	public class myChangeListener implements OnPreferenceChangeListener {
    	public boolean onPreferenceChange(Preference pref, Object newVal) {
    		// 4 CheckButtons wie RadioButtons: nur einer true 
    		String key = pref.getKey();
    		CheckBoxPreference p;
    		Log.i(MainActivity.LogTAG, "Dago SettingsFragment onPreferenceChange");
    		if (key.equals("pref_isSound_Aua") && ((Boolean)newVal)) {
            	p = (CheckBoxPreference)findPreference("pref_isSound_haha");p.setChecked(false);	
            	p = (CheckBoxPreference)findPreference("pref_isSound_moepse");p.setChecked(false);	
            	p = (CheckBoxPreference)findPreference("pref_isSound_werner");p.setChecked(false);	
        		MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.au3);  
                mp.start();
            }
    		if (key.equals("pref_isSound_haha") && ((Boolean)newVal)) {
            	p = (CheckBoxPreference)findPreference("pref_isSound_Aua");p.setChecked(false);	
            	p = (CheckBoxPreference)findPreference("pref_isSound_moepse");p.setChecked(false);	
            	p = (CheckBoxPreference)findPreference("pref_isSound_werner");p.setChecked(false);	
        		MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.haha);  
                mp.start();
            }
    		if (key.equals("pref_isSound_moepse") && ((Boolean)newVal)) {
            	p = (CheckBoxPreference)findPreference("pref_isSound_Aua");p.setChecked(false);	
            	p = (CheckBoxPreference)findPreference("pref_isSound_haha");p.setChecked(false);	
            	p = (CheckBoxPreference)findPreference("pref_isSound_werner");p.setChecked(false);	
        		MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.moepse);  
                mp.start();
            }
    		if (key.equals("pref_isSound_werner") && ((Boolean)newVal)) {
            	p = (CheckBoxPreference)findPreference("pref_isSound_Aua");p.setChecked(false);	
            	p = (CheckBoxPreference)findPreference("pref_isSound_haha");p.setChecked(false);	
            	p = (CheckBoxPreference)findPreference("pref_isSound_moepse");p.setChecked(false);	
        		MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.werner);  
                mp.start();
            }
            return (Boolean)newVal;
        }
	}  // end internal class PrefChangeListener
	

}
