package com.squeed.golftracker;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.squeed.golftracker.common.model.Course;
import com.squeed.golftracker.common.model.Hole;
import com.squeed.golftracker.common.model.PointOfInterest;

public class EditHoleMapActivity extends MapActivity {
	
	MapView mapView;
	MapController mapController;
	//GeoPoint mgPoint;
	
	List<Overlay> mapOverlays;
	
	
	private TouchOverlay touchOverlay;
	
	boolean showFrontBackGreenMarkers = true;
	boolean showFwBunkerMarkers = true;
	boolean showGreenBunkerMarkers = true;
	
	Hole currentHole;
	Course course;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_hole_map);
		currentHole = (Hole) getIntent().getSerializableExtra("hole");
		course = (Course) getIntent().getSerializableExtra("course");
		init();
	}


	private void init() {
			
		mapView = (MapView) findViewById(R.id.mapView1);
		mapView.getOverlays().clear();
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);
	
		mapController = mapView.getController();
		mapController.setZoom(18); // Zoon 1 is world view

		((Button) findViewById(R.id.prevBtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(currentHole.getNumber()-2 < 0) {
					Toast.makeText(EditHoleMapActivity.this, "Du är på hål 1.", Toast.LENGTH_SHORT).show();
					return;
				}
				currentHole = (Hole) course.getHoles().get(currentHole.getNumber()-2);
				init();					
			}
		});
		
		((Button) findViewById(R.id.nextBtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(currentHole.getNumber() > course.getHoles().size()) {
					Toast.makeText(EditHoleMapActivity.this, "Du är på hål " + course.getHoles().size() + ".", Toast.LENGTH_SHORT).show();
					return;
				}
				currentHole = (Hole) course.getHoles().get(currentHole.getNumber());
				init();			
			}
		});
		
		((Button) findViewById(R.id.addPoiBtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				touchOverlay = new TouchOverlay();		
				touchOverlay.isWaitForTap = true;			
				mapOverlays.add(touchOverlay);
				Toast.makeText(EditHoleMapActivity.this, "Ange intressepunkt genom att peka på kartan", Toast.LENGTH_SHORT).show();
			}
		});
		
		addStaticOverlaysToHole();	
	}

	private void addStaticOverlaysToHole() {
		
		//GeoPoint ytPoint = new GeoPoint((int) (yellowTee.getLat() * 1E6), (int) (yellowTee.getLon() * 1E6));
		//mgPoint = new GeoPoint((int) (midGreen.getLat() * 1E6), (int) (midGreen.getLon() * 1E6));
		PointOfInterest yellowTee = (PointOfInterest) currentHole.getYellowTee();
		PointOfInterest midGreen = (PointOfInterest) currentHole.getMidGreen();
		
		double centerLat, centerLon;
		if(yellowTee != null && midGreen != null) {
			centerLat = yellowTee.getLat() - ( (yellowTee.getLat() - midGreen.getLat()) / 2);
			centerLon = yellowTee.getLon() - ( (yellowTee.getLon() - midGreen.getLon()) / 2);
		} else if(yellowTee != null && midGreen == null) {
			centerLat = yellowTee.getLat();
			centerLon = yellowTee.getLon();
		} else if(midGreen != null && yellowTee == null) {
			centerLat = midGreen.getLat();
			centerLon = midGreen.getLon();
		} else {
			centerLat = course.getLat();
			centerLon = course.getLon();
		}
		
		
		int lat = (int) (centerLat * 1E6);
		int lng = (int) (centerLon * 1E6);
		GeoPoint point = new GeoPoint(lat, lng);
		mapController.animateTo(point);

		
		mapOverlays = mapView.getOverlays();
		Drawable yelTeeDrawable = this.getResources().getDrawable(R.drawable.marker_yellow);
		Drawable redTeeDrawable = this.getResources().getDrawable(R.drawable.marker_red);
		Drawable greenDrawable = this.getResources().getDrawable(R.drawable.marker_green);
		Drawable bunkerDrawable = this.getResources().getDrawable(R.drawable.marker_orange);
		
		HoleItemizedOverlay yelTeeOverlay = new HoleItemizedOverlay(1L, yelTeeDrawable);
		HoleItemizedOverlay redTeeOverlay = new HoleItemizedOverlay(2L, redTeeDrawable);
		HoleItemizedOverlay greenOverlay = new HoleItemizedOverlay(3L, greenDrawable);
		HoleItemizedOverlay bunkerOverlay = new HoleItemizedOverlay(4L, bunkerDrawable);
		
		
		
		
		for(PointOfInterest p1 : currentHole.getPois()) {
			int latx = (int) (p1.getLat() * 1E6);
			int lngx = (int) (p1.getLon() * 1E6);
			
			GeoPoint px = new GeoPoint(latx, lngx);
			OverlayItem overlayitem = new OverlayItem(px, p1.getName(), p1.getName());
			if(p1.getType().equals("yt")) {
				yelTeeOverlay.addOverlay(overlayitem);
			}
			if(p1.getType().equals("rt")) {
				redTeeOverlay.addOverlay(overlayitem);
			}
			
			if(isGreenPoi(p1) ) {
				if(isMidGreenPoi(p1)) {
					greenOverlay.addOverlay(overlayitem);
				} else if(showFrontBackGreenMarkers) {
					greenOverlay.addOverlay(overlayitem);
				}
			}
			if(showFwBunkerMarkers && isFwBunkerPoi(p1)) {
				bunkerOverlay.addOverlay(overlayitem);
			}
			if(showGreenBunkerMarkers && isGreenBunkerPoi(p1)) {
				bunkerOverlay.addOverlay(overlayitem);
			}
		}
		
		if(yelTeeOverlay.size() > 0) {
			mapOverlays.add(yelTeeOverlay);
		}
		
		if(redTeeOverlay.size() > 0) {
			mapOverlays.add(redTeeOverlay);
		}
		
		if(greenOverlay.size() > 0) {
			mapOverlays.add(greenOverlay);
		}
		
		if(bunkerOverlay.size() > 0) {
			mapOverlays.add(bunkerOverlay);
		}
		
		// Finally, always add a "touch" dummy overlay if the
		
	}

	
	
	
	
	private boolean isGreenPoi(PointOfInterest p1) {
		return p1.getType().equals("fg") ||  p1.getType().equals("mg") ||p1.getType().equals("bg");
	}
	
	private boolean isMidGreenPoi(PointOfInterest p1) {
		return p1.getType().equals("mg");
	}

	private boolean isFwBunkerPoi(PointOfInterest p1) {
		return p1.getType().equals("fbr") ||  p1.getType().equals("fbl");
	}

	private boolean isGreenBunkerPoi(PointOfInterest p1) {
		return p1.getType().equals("gbf") ||  p1.getType().equals("gbl") || p1.getType().equals("gbr") ||  p1.getType().equals("gbb");
	}
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 1, 1, R.string.previousHoleLbl);
		menu.add(Menu.NONE, 2, 2, R.string.nextHoleLbl);
		menu.add(Menu.NONE, 3, 3, "Lägg till punkt");
		menu.getItem(0).setIcon(R.drawable.ic_menu_back);
		menu.getItem(1).setIcon(R.drawable.ic_menu_forward);
		menu.getItem(2).setIcon(R.drawable.ic_menu_add);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Course c = (Course) getIntent().getSerializableExtra("course");
		switch (item.getItemId()) {
		case 1:
			if(currentHole.getNumber()-2 < 0) {
				Toast.makeText(EditHoleMapActivity.this, "Du är på hål 1.", Toast.LENGTH_SHORT).show();
				return true;
			}
			currentHole = (Hole) c.getHoles().get(currentHole.getNumber()-2);
			init();
			break;
		case 2:
			if(currentHole.getNumber() > c.getHoles().size()) {
				Toast.makeText(EditHoleMapActivity.this, "Du är på hål " + c.getHoles().size() + ".", Toast.LENGTH_SHORT).show();
				return true;
			}
			currentHole = (Hole) c.getHoles().get(currentHole.getNumber());
			init();
			break;
		case 3:
			// Listen for next "tap" on MapView overlay.
			touchOverlay = new TouchOverlay();		
			touchOverlay.isWaitForTap = true;			
			mapOverlays.add(touchOverlay);
			//init();
			break;
		}
		return true;
	}
	
	
	class TouchOverlay extends Overlay {
		
		public TouchOverlay() {
			
		}

		public boolean isWaitForTap = false;

		@Override
		public boolean onTap(GeoPoint point, MapView mapView) {
			
			Log.i("EditHoleMapActivity", "GeoPoint lat: " + point.getLatitudeE6() + " lon: " + point.getLongitudeE6());
			if(isWaitForTap) {
				Log.i("EditHoleMapActivity", "Do something with this coordinate!");

				Intent i = new Intent(EditHoleMapActivity.this, NewPoiActivity.class);
				i.putExtra("lat", point.getLatitudeE6() / 1E6);
				i.putExtra("lon", point.getLongitudeE6() / 1E6);
				i.putExtra("hole_id", currentHole.getId());
				EditHoleMapActivity.this.startActivityForResult(i, 777);
				isWaitForTap = false;
			}
			return true;
		}
	}
	
	/**
	 * When the "create new poi" activity returns, put the new POI into the object graph.
	 * (It has already been saved to the hole in the DB)
	 */
	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent data) {
		Log.i("EditHoleMapActivity", "ENTER - EditHoleMapActivity: " + reqCode + " / " + resCode);
		if(reqCode == 777 && data != null && data.getSerializableExtra("poi") != null) {
			PointOfInterest poi = (PointOfInterest) data.getSerializableExtra("poi");
			currentHole.getPois().add(poi);
			course.getHoles().remove(currentHole);
			course.getHoles().add(currentHole);
			init();
		}
	}
}
