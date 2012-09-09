package com.squeed.golftracker;

import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squeed.golftracker.helper.UserAgent;
import com.squeed.golftracker.rest.RestClient;

public class RegisterActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_page);
		
		
		((Button) findViewById(R.id.registerBtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String username = ((EditText) findViewById(R.id.usernameFld)).getText().toString();
				String password = ((EditText) findViewById(R.id.passwordFld)).getText().toString();
				
				if(validateUsername(username)) {
					if(validateUsername(password)) {
						register(username, password);
					} else {
						Toast.makeText(RegisterActivity.this, "Lösenordet måste vara minst 4 tecken långt och sakna mellanslag", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(RegisterActivity.this, "Användarnamnet måste vara minst 4 tecken långt och sakna mellanslag", Toast.LENGTH_LONG).show();
				}
			}

			

			
		});
	}
	
	Pattern p = Pattern.compile("[a-zA-Z0-9]{4,}");
	
	private boolean validateUsername(String username) {
		return p.matcher(username).matches();
	}
	
	private void register(String username, String password) {
		RestClient rc = new RestClient();
		try {
			Long userId = rc.registerUser(username, password);
			UserAgent.OWNER_ID = userId;
			SharedPreferences userIdPreference =
				    getSharedPreferences(UserAgent.USER_ID_KEY, Context.MODE_PRIVATE);
			Editor edit = userIdPreference.edit();
			edit.putLong(UserAgent.USER_ID_KEY, userId);
			edit.commit();
			Toast.makeText(RegisterActivity.this, "Användarkonto skapat!", Toast.LENGTH_LONG).show();
			finish();
			
		} catch (Exception e) {
			Toast.makeText(RegisterActivity.this, "Ett fel uppstod: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}
