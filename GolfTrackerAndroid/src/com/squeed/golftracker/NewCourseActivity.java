package com.squeed.golftracker;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squeed.golftracker.helper.DbHelper;
import com.squeed.golftracker.helper.UserAgent;

public class NewCourseActivity extends Activity {
	
	private Spinner teesSpinner;
	private ArrayAdapter<CharSequence> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course_new);
		
		List<CharSequence> tees = new ArrayList<CharSequence>();
		adapter = new ArrayAdapter<CharSequence>(this,
		                      R.id.nameLbl, tees);
		teesSpinner = (Spinner) findViewById(R.id.teesSpinner);
		teesSpinner.setAdapter(adapter);
		
		((Button) findViewById(R.id.addTeeBtn)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Show dialog which lets user enter tees for this course.
						

				Context mContext = NewCourseActivity.this;
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.new_tee_dialog,
				                               (ViewGroup) findViewById(R.id.new_tee_dialog));
				
				final EditText editText = (EditText) layout.findViewById(R.id.teeNameFld);	
				
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setView(layout);
				final AlertDialog alertDialog = builder.create();
				
				final Button saveBtn = (Button) layout.findViewById(R.id.saveTeeBtn);
				final Button cancelBtn = (Button) layout.findViewById(R.id.cancelTeeBtn);
				
				saveBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						adapter.add(editText.getText().toString().trim());
						alertDialog.dismiss();
					}
				});
				
				cancelBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						alertDialog.dismiss();
					}
				});
				
				alertDialog.show();
			}			
		});
		
		((Button) findViewById(R.id.continueNewCourseBtn)).setClickable(true);	
		((Button) findViewById(R.id.continueNewCourseBtn)).setOnClickListener(new OnClickListener() {
		
			public void onClick(View v) {
				v.setClickable(false);
				String name = ((TextView) findViewById(R.id.courseNameFld)).getText().toString();
				if(name == null || name.trim().length() == 0) {
					Toast.makeText(NewCourseActivity.this, "Ni måste ange ett namn", Toast.LENGTH_LONG).show();
					return;
				}
				
				Object o = ((Spinner) findViewById(R.id.numOfHolesFld)).getSelectedItem();
				Integer numOfHoles = Integer.parseInt((String) o);
				
				// Save course to db, get created id.
				DbHelper courseDbHelper = new DbHelper(NewCourseActivity.this);
				Long id = courseDbHelper.createNewCourse(courseDbHelper.getWritableDatabase(), name, numOfHoles, UserAgent.OWNER_ID);
				if(id == -1L) {
					Toast.makeText(NewCourseActivity.this, "Kunde ej skapa bana, fick inget giltigt ID tillbaka", Toast.LENGTH_LONG).show();
					return;
				}
				
				// Create tees for the course
				for(int a = 0; a < adapter.getCount(); a++) {
					courseDbHelper.addTeeToCourse(courseDbHelper.getWritableDatabase(), id, adapter.getItem(a).toString());
				}
				
				
				Intent i = new Intent(NewCourseActivity.this, NewCourseMapActivity.class);
				i.putExtra("course_id", id);
				i.putExtra("num_of_holes", numOfHoles);
				
				startActivity(i);
			}
		});
	}

}
