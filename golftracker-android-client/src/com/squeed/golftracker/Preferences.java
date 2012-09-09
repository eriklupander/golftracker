package com.squeed.golftracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class Preferences extends PreferenceActivity {
	
	boolean testModePreference;
	String serverUrlPreference;
	String userNamePreference;
	String passwordPreference;
	 
	private void getPrefs() {
	                // Get the xml/preferences.xml preferences
	                SharedPreferences prefs = PreferenceManager
	                                .getDefaultSharedPreferences(getBaseContext());
	                testModePreference = prefs.getBoolean("testModePref", true);
	                
	                serverUrlPreference = prefs.getString("serverUrlPref", "http://");
	                userNamePreference = prefs.getString("userNamePref", "");
	                passwordPreference = prefs.getString("passwordPref", "");
	                
//	                editTextPreference = prefs.getString("editTextPref",
//	                                "Nothing has been entered");
//	                ringtonePreference = prefs.getString("ringtonePref",
//	                                "DEFAULT_RINGTONE_URI");
//	                secondEditTextPreference = prefs.getString("SecondEditTextPref",
//	                                "Nothing has been entered");
//	                // Get the custom preference
//	                SharedPreferences mySharedPreferences = getSharedPreferences(
//	                                "myCustomSharedPrefs", Activity.MODE_PRIVATE);
//	                customPref = mySharedPreferences.getString("myCusomPref", "");
//	                
	                System.out.println("Loaded preferences!");
	        }
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            getListView().setBackgroundResource(R.drawable.background_01p);
            // Get the custom preference
            Preference customPref = (Preference) findPreference("dumpDbCsvPref");
            customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                    public boolean onPreferenceClick(Preference preference) {
                            Toast.makeText(getBaseContext(),
                                            "Skriver databas till SD-kort...",
                                            Toast.LENGTH_LONG).show();
//                            DbExporter ex = new DbExporter(new DbHelper(getApplicationContext()));
//                            String data = ex.dbToCsvString();
//                            ex.dataToSdCard(data, "database.csv");
                            Toast.makeText(getBaseContext(),
                                    "Not implemented",
                                    Toast.LENGTH_LONG).show();
                            return true;
                    }

            });
            
            Preference customPref2 = (Preference) findPreference("readDbCsvPref");
            customPref2.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                public boolean onPreferenceClick(Preference preference) {
                        Toast.makeText(getBaseContext(),
                                        "L�ser databas fr�n SD-kort...",
                                        Toast.LENGTH_LONG).show();
                        // TODO fix CSV import.
                        
                        return true;
                }
            });
            
            Preference customPref3 = (Preference) findPreference("syncDbPref");
            customPref3.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                public boolean onPreferenceClick(Preference preference) {
                	
//                	Intent i = new Intent(Preferences.this, SyncWithDbActivity.class);
//                	startActivity(i);                	
                    return true;
                }
            });
            getPrefs();
    }
}
