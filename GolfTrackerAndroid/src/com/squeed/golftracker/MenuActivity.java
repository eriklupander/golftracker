package com.squeed.golftracker;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.squeed.golftracker.common.model.User;
import com.squeed.golftracker.helper.UserAgent;
import com.squeed.golftracker.rest.RestClient;

public class MenuActivity extends Activity {
	
	private LocationManager locMgr;
	
	@Override
	protected void onResume() {
		super.onResume();
		setOwnerFromPreference();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		setOwnerFromPreference();

		locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (!locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();
			return;
		}

		
		Button b = (Button) findViewById(R.id.StartButton1);
		
		b.setOnClickListener(
				new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this, VenueListActivity.class);
				startActivity(intent);
			}
			
		});
		
		Button editBtn = (Button) findViewById(R.id.editCourseBtn);
		
		editBtn.setOnClickListener(
				new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this, EditCourseListActivity.class);
				startActivity(intent);
			}
			
		});
		
		Button regBtn = (Button) findViewById(R.id.registerUserButton1);
		
		regBtn.setOnClickListener(
				new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
			
		});
		
		Button settingsBtn = (Button) findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(new OnClickListener() {

	        	public void onClick(View v) {
	                Intent settingsActivity = new Intent(getBaseContext(),
	                                Preferences.class);
	                startActivity(settingsActivity);
	        }
        	
        });
		
		Button exitBtn = (Button) findViewById(R.id.ExitBtn1);
		
		exitBtn.setOnClickListener(
				new OnClickListener() {

			public void onClick(View v) {
				MenuActivity.this.finish();
			}
			
		});
	}

	private void setOwnerFromPreference() {
		SharedPreferences userIdPreference =
			    getSharedPreferences(UserAgent.USER_ID_KEY, Context.MODE_PRIVATE);
		Long ownerId = userIdPreference.getLong(UserAgent.USER_ID_KEY, -1L);
		UserAgent.OWNER_ID = ownerId;
		if(UserAgent.OWNER_ID != -1L) {
			RestClient rc = new RestClient();
			User memberDto = rc.getUser(UserAgent.OWNER_ID);
			if(memberDto != null)
				((TextView) findViewById(R.id.loggedInAsLbl)).setText(memberDto.getUsername());
		}
	}
	
	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Yout GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(
									@SuppressWarnings("unused") final DialogInterface dialog,
									@SuppressWarnings("unused") final int id) {
								launchGPSOptions();
								
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							@SuppressWarnings("unused") final int id) {
						dialog.cancel();
						MenuActivity.this.finish();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void launchGPSOptions() {
        final ComponentName toLaunch = new ComponentName("com.android.settings","com.android.settings.SecuritySettings");
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(toLaunch);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, 0);
    } 

}
