package com.squeed.golftracker;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squeed.golftracker.entity.CourseDTO;
import com.squeed.golftracker.entity.HoleDTO;
import com.squeed.golftracker.entity.PointOfInterestDTO;
import com.squeed.golftracker.entity.RoundDTO;
import com.squeed.golftracker.entity.TeeDTO;
import com.squeed.golftracker.helper.DbHelper;
import com.squeed.golftracker.helper.LongLatConverter;

public class GolfTrackerActivity extends Activity {

	private LocationManager locMgr;
	private SQLiteDatabase readableDatabase;
	
	private CourseDTO course;
	private HoleDTO currentHole;
	private TeeDTO tee;
	
	private Animation inAnimation;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hole);

		
		
		// Setup db if necessary
		DbHelper db = new DbHelper(this);
		readableDatabase = db.getReadableDatabase();

		// When starting the tracking activity, load the selected course (and
		// tee) into an object graph.

		course = db.loadCourseFromDb(readableDatabase, this.getIntent()
				.getLongExtra("courseId", 1L));
		
		// Set selected tee.
		Long teeId = getIntent().getLongExtra("teeId", -1L);
		for(TeeDTO t : course.getTees()) {
			if(t.getId().longValue() == teeId.longValue()) {
				tee = t;
				break;
			}
		}
		
		((TextView) findViewById(R.id.hole_header_text)).setText(course.getName());

		locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1,
				locationListener);

		inAnimation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
		inAnimation.setDuration(200L);
		
		setupButtons();
		changeHole();
	}
	
	protected void onResume() {
		super.onResume();
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1,
				locationListener);
	}

	private void setupButtons() {
		((Button) findViewById(R.id.enterScoreBtn))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(GolfTrackerActivity.this, HoleScoreActivity.class);
				i.putExtra("currentHole", currentHole);
				i.putExtra("currentRound", new RoundDTO(1L, -1L, new Date(), 1L, 1L));
				startActivity(i);
			}
		});
		
		((Button) findViewById(R.id.forwardButton1))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						currentHoleNo++;
						if(currentHoleNo+1 > course.getHoles().size()) {
							currentHoleNo = course.getHoles().size() -1;
							Toast.makeText(GolfTrackerActivity.this, "Du är redan på hål " + (course.getHoles().size()), Toast.LENGTH_SHORT).show();
						}
						
						
						((ViewGroup)findViewById(android.R.id.content)).getChildAt(0).startAnimation(inAnimation);
						
						changeHole();
						
					}
				});

		((Button) findViewById(R.id.backButton1))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				currentHoleNo--;
				if(currentHoleNo < 0) {
					currentHoleNo = 0;
					Toast.makeText(GolfTrackerActivity.this, "Du är redan på hål 1", Toast.LENGTH_SHORT).show();
				}

				((ViewGroup)findViewById(android.R.id.content)).getChildAt(0).startAnimation(inAnimation);
				
				changeHole();
				
			}
		});
		
		((Button) findViewById(R.id.openListBtn1))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				Intent i = new Intent(GolfTrackerActivity.this, PoiListActivity.class);
				i.putExtra("hole", currentHole);
				startActivity(i);
			}
		});
		
		((Button) findViewById(R.id.showMapBtn))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				if(currentHole.getYellowTee() != null && currentHole.getMidGreen() != null) {
				
					Intent i = new Intent(GolfTrackerActivity.this, HoleMapActivity.class);
					i.putExtra("hole", currentHole);
					i.putExtra("course", course);
					startActivityForResult(i, 777);
				} else {
					Toast.makeText(GolfTrackerActivity.this, "Hålet saknar GPS-koordinater för gul tee och mitten green, kan ej visa.", Toast.LENGTH_SHORT).show();
				}
			}
		});		
		
		((Button) findViewById(R.id.measureShotBtnId))
		.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {				
				
				Toast.makeText(GolfTrackerActivity.this, "Hämtar GPS-position...", Toast.LENGTH_SHORT).show();
				
				final Intent i = new Intent(GolfTrackerActivity.this, RecordShotActivity.class);
				i.putExtra("currentHole", currentHole);
				locMgr.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
					
					@Override
					public void onStatusChanged(String provider, int status, Bundle extras) {}
					
					@Override
					public void onProviderEnabled(String provider) {}
					
					@Override
					public void onProviderDisabled(String provider) {}
					
					@Override
					public void onLocationChanged(Location location) {
						i.putExtra("startLat", location.getLatitude());
						i.putExtra("startLon", location.getLongitude());
						startActivity(i);
					}
				}, null);
				
			}
		});
	}

	/**
	 * When the "create new poi" activity returns, put the new POI into the object graph.
	 * (It has already been saved to the hole in the DB)
	 */
	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent data) {
		
		if(reqCode == 777 && data != null && data.getSerializableExtra("hole_number") != null) {
			Integer holeNumber = data.getIntExtra("hole_number", 1);
			currentHoleNo = holeNumber - 1;
			changeHole();
		}
	}

	private static int currentHoleNo = 0; // first hole, array index 0.

	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {			
			Log.i("onLocationChanged", "Location updated...");

			// If we're not entered the first tee yet, show distance to first
			// tee after checking whether
			// we are within the defined proximity of the first tee.
//			if (!onCourse) {
//				// Are we on hole 1?
//				PointOfInterest yellowTee = course.getHoles().get(0)
//						.getYellowTee();
//
//				if (LongLatConverter.checkProximity(location, yellowTee, 20)) {
//					onCourse = true;
//
//					((TextView) findViewById(R.id.hole_header_text))
//							.setText(buildTitleText(course, course.getHoles()
//									.get(0)));
//				} else {
//					((TextView) findViewById(R.id.midGreenVal)).setText(LongLatConverter.getDistance(
//							location.getLongitude(), location.getLatitude(),
//							yellowTee.getLon(), yellowTee.getLat())
//							+ " meter till första tee.");
//					return;
//				}
//			}

			// If we have entered a hole, make sure we're not moved on to the
			// next one.
			// TODO introduce a timer, for example 5 minutes after a switch,
			// until we start to check for new hole
			if(currentHoleNo+1 < course.getHoles().size()) {
				PointOfInterestDTO nextTee = (PointOfInterestDTO) ((HoleDTO) course.getHoles().get(currentHoleNo+1))
						.getYellowTee();
				if (LongLatConverter.checkProximity(location, nextTee, 10)) {
					currentHoleNo++;
					changeHole();
				}
			}
				
			if (currentHole == null) {
				currentHole = (HoleDTO) course.getHoles().get(currentHoleNo);
			}

			// Calculate and display the three default distances.
			String frontGreen = LongLatConverter.getDistance(location,
					currentHole.getFrontGreen());
			String midGreen = LongLatConverter.getDistance(location,
					currentHole.getMidGreen());
			String backGreen = LongLatConverter.getDistance(location,
					currentHole.getBackGreen());

			((TextView) findViewById(R.id.frontGreenVal)).setText(frontGreen
					+ " m");
			((TextView) findViewById(R.id.midGreenVal))
					.setText(midGreen + " m");
			((TextView) findViewById(R.id.backGreenVal)).setText(backGreen
					+ " m");
		}

		

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 1, 1, R.string.exitLbl);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case 1:
			GolfTrackerActivity.this.finish();
			break;
		}
		return true;
	}

	private void changeHole() {
		currentHole = (HoleDTO) course.getHoles()
				.get(currentHoleNo);
		//locMgr.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
		//onCourse = true;
		
		((TextView) findViewById(R.id.hole_header_text))
		.setText(buildTitleText(course, currentHole));
		
		((TextView) findViewById(R.id.holeValue)).setText(" " + currentHole.getNumber());
		((TextView) findViewById(R.id.parVal)).setText(" " + currentHole.getPar());
		((TextView) findViewById(R.id.hcpVal)).setText(" " + currentHole.getIndex());

		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//		if (v != null)
//			v.vibrate(300L);

		Log.i("GolfTrackerActivity", "Switched hole, new hole " + currentHoleNo);
		
		
		
		if (currentHoleNo > course.getHoles().size()) {
			GolfTrackerActivity.this.finish();
		}
	}
	
	private CharSequence buildTitleText(CourseDTO course, HoleDTO hole) {
		StringBuilder buf = new StringBuilder();
		buf.append(course.getName());
//		.append(" | Hål ")
//		.append(hole.getNumber()).append(" | par ")
//		.append(hole.getPar()).append(" | index ")
//		.append(hole.getId());
		return buf.toString();
	}

}