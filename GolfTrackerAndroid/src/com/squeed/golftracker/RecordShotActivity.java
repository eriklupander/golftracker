package com.squeed.golftracker;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.squeed.golftracker.common.model.Club;
import com.squeed.golftracker.common.model.Hole;
import com.squeed.golftracker.helper.DbHelper;
import com.squeed.golftracker.helper.LongLatConverter;
import com.squeed.ui.CustomSpinnerView;
import com.squeed.ui.MyListItem;

public class RecordShotActivity extends Activity {
	private LocationManager locMgr;
	
	private double startLon;
	private double startLat;
	
	private List<Club> clubs;
	private List<ShotProfile> shotProfiles;

	private Hole currentHole;
	
	private Bitmap[] icons;
	
	private Location currentLocation;
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordshot);
		
		startLat = getIntent().getDoubleExtra("startLat", -1.0);
		startLon = getIntent().getDoubleExtra("startLon", -1.0);
		currentHole = (Hole) getIntent().getSerializableExtra("currentHole"); 
		
		TextView tv = (TextView) findViewById(R.id.holeVal);
		tv.setText("" + currentHole.getNumber());
		
		final DbHelper db = new DbHelper(this);
		
		clubs = db.getClubs(db.getReadableDatabase());
		shotProfiles = db.getShotProfiles(db.getReadableDatabase());
		
		icons = new Bitmap[8];
		icons[4] = BitmapFactory.decodeResource(getResources(), R.drawable.wedge);
		icons[3] = BitmapFactory.decodeResource(getResources(), R.drawable.iron);
		icons[2] = BitmapFactory.decodeResource(getResources(), R.drawable.hybrid);
		icons[1] = BitmapFactory.decodeResource(getResources(), R.drawable.fwood);
		icons[0] = BitmapFactory.decodeResource(getResources(), R.drawable.driver);
		
		icons[5] = BitmapFactory.decodeResource(getResources(), R.drawable.draw);
		icons[6] = BitmapFactory.decodeResource(getResources(), R.drawable.straight);
		icons[7] = BitmapFactory.decodeResource(getResources(), R.drawable.fade);
		
		
		final CustomSpinnerView clubCsv = (CustomSpinnerView) findViewById(R.id.clubSpinner);
		List<MyListItem> l = new ArrayList<MyListItem>();
		for(Club c : clubs) {
			
			l.add(new MyListItem(c.getId(), c.getLongName(), null, icons[c.getIconId()]));
		}
		clubCsv.setItems(l.toArray(new MyListItem[]{}));
		clubCsv.setSelectedIndex(0);
		
		
		final CustomSpinnerView shotShapeCsv = (CustomSpinnerView) findViewById(R.id.shotShapeSpinner);
		List<MyListItem> l2 = new ArrayList<MyListItem>();
		for(ShotProfile c : shotProfiles) {
			l2.add(new MyListItem(c.getId(), c.getName(), null,  icons[c.getIconId()]));
		}
		shotShapeCsv.setItems(l2.toArray(new MyListItem[]{}));
		shotShapeCsv.setSelectedIndex(2);
		
		((Button) findViewById(R.id.saveShotBtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clubCsv.getSelectedItemId();
				db.createShot(
						db.getWritableDatabase(), currentHole.getId(), 
						clubCsv.getSelectedItemId(), shotShapeCsv.getSelectedItemId(),
						startLon, startLat, 
						currentLocation.getLongitude(), currentLocation.getLatitude(), -1L);
				finish();
			}
		});
		
		locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1,
				locationListener);
	}
	
	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {			
			Log.i("onLocationChanged", "Location updated...");

			String distance = LongLatConverter.getDistance(startLon, startLat, location.getLongitude(), location.getLatitude());
			TextView tv = (TextView) findViewById(R.id.lengthVal);
			tv.setText(distance + " m");	
			
			currentLocation = location;
		}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};
}
