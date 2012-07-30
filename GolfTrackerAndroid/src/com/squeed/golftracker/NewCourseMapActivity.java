package com.squeed.golftracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.squeed.golftracker.helper.DbHelper;
import com.squeed.golftracker.helper.FixedCoordinates;

public class NewCourseMapActivity extends MapActivity {
	
	private MapView mapView;
	private MapController mapController;
	private Long courseId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_course_map);
		
		courseId = getIntent().getLongExtra("course_id", -1L);
		
		mapView = (MapView) findViewById(R.id.mapView1);
		mapView.getOverlays().clear();
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);
		
		mapController = mapView.getController();
		mapController.setCenter(new GeoPoint((int) (FixedCoordinates.SWEDEN_LAT*1E6), (int) (FixedCoordinates.SWEDEN_LON*1E6))); 
		mapController.setZoom(4); // Zoom 1 is world view
		
		((Button) findViewById(R.id.doneBtn))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				GeoPoint mapCenter = mapView.getMapCenter();
					DbHelper dbHelper = new DbHelper(NewCourseMapActivity.this);
					dbHelper.updateCoursePosition(dbHelper.getWritableDatabase(), courseId, mapCenter.getLatitudeE6() / 1E6, mapCenter.getLongitudeE6() / 1E6);
				
					Intent i = new Intent(NewCourseMapActivity.this, EditHoleActivity.class);
					
					i.putExtra("course_id", courseId);
					startActivity(i);
				}
			
		});
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
