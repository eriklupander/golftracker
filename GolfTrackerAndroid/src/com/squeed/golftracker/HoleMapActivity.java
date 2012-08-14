package com.squeed.golftracker;

import java.text.NumberFormat;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class HoleMapActivity extends MapActivity {
	
	MapView mapView;
	MapController mapController;
	GeoPoint mgPoint;
	
	List<Overlay> mapOverlays;
	private LocationManager locMgr;
	private NumberFormat nf;
	
	boolean showTeeToGreenLine = false;
	boolean showPlayerToGreenLine = false;
	boolean showDistancesToMidGreen = false;
	boolean showDistancesToGreenBunkers = false;
	boolean showDistancesToFwBunkers = false;
	
	boolean showFrontBackGreenMarkers = false;
	boolean showFwBunkerMarkers = false;
	boolean showGreenBunkerMarkers = false;
	
	Hole currentHole;
	Course course;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hole_map);
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(0);
		currentHole = (Hole) getIntent().getSerializableExtra("hole");
		course = (Course) getIntent().getSerializableExtra("course");
		
		((Button) findViewById(R.id.prevBtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(currentHole.getNumber()-2 < 0) {
					Toast.makeText(HoleMapActivity.this, "Du är på hål 1.", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(HoleMapActivity.this, "Du är på hål " + course.getHoles().size() + ".", Toast.LENGTH_SHORT).show();
					return;
				}
				currentHole = (Hole) course.getHoles().get(currentHole.getNumber());
				init();			
			}
		});
		
		init();
	}
	
//	protected void onResume() {
//		super.onResume();
//		init();
//	}

	private void init() {
		SharedPreferences preferences = 
	        	PreferenceManager.getDefaultSharedPreferences(this);
		showTeeToGreenLine = preferences.getBoolean("teeToGreenLinePref", false);
		showPlayerToGreenLine = preferences.getBoolean("playerToGreenLinePref", false);
		showDistancesToMidGreen = preferences.getBoolean("distanceToMidGreenPref", false);
		showDistancesToGreenBunkers = preferences.getBoolean("distanceToGreenBunkersPref", false);
		showDistancesToFwBunkers = preferences.getBoolean("distanceToFwBunkersPref", false);
		
		showFrontBackGreenMarkers = preferences.getBoolean("frontBackGreenMarkersPref", false);
		showFwBunkerMarkers = preferences.getBoolean("fwBunkerMarkersPref", false);
		showGreenBunkerMarkers = preferences.getBoolean("greenBunkerMarkersPref", false);
		
		mapView = (MapView) findViewById(R.id.mapView1);
		mapView.getOverlays().clear();
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);
		
		mapController = mapView.getController();
		mapController.setZoom(18); // Zoon 1 is world view
		
		Intent i = new Intent("");
		i.putExtra("hole_number", currentHole.getNumber());
		this.setResult(777, i);
		
		PointOfInterest yellowTee = (PointOfInterest) currentHole.getYellowTee();
		PointOfInterest midGreen = (PointOfInterest) currentHole.getMidGreen();
		
		addStaticOverlaysToHole(yellowTee, midGreen);
		
		locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1,
				locationListener);
	}

	private void addStaticOverlaysToHole(PointOfInterest yellowTee,
			PointOfInterest midGreen) {
		if(yellowTee == null || midGreen == null) {
			Toast.makeText(HoleMapActivity.this, "Hål " + currentHole.getNumber() + " saknar GPS-koordinater för gul tee och mitten green, kan ej visa.", Toast.LENGTH_LONG).show();
			return;
		}
		GeoPoint ytPoint = new GeoPoint((int) (yellowTee.getLat() * 1E6), (int) (yellowTee.getLon() * 1E6));
		mgPoint = new GeoPoint((int) (midGreen.getLat() * 1E6), (int) (midGreen.getLon() * 1E6));
		
		double centerLat = yellowTee.getLat() - ( (yellowTee.getLat() - midGreen.getLat()) / 2);
		double centerLon = yellowTee.getLon() - ( (yellowTee.getLon() - midGreen.getLon()) / 2);
		
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
		
		
				
		
		List<PointOfInterest> pois = currentHole.getPois();
		
		for(PointOfInterest p1 : pois) {
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
		
		
		mapOverlays.add(yelTeeOverlay);
		
		if(redTeeOverlay.size() > 0) {
			mapOverlays.add(redTeeOverlay);
		}
		
		if(greenOverlay.size() > 0) {
			mapOverlays.add(greenOverlay);
		}
		
		if(bunkerOverlay.size() > 0) {
			mapOverlays.add(bunkerOverlay);
		}
		
		if(showTeeToGreenLine) {
			LineOverlay line = new LineOverlay(5L, ytPoint, mgPoint, Color.RED, Paint.Style.STROKE);
			mapOverlays.add(line);
		}
	}

	
	
	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {			
			Log.i("onLocationChanged", "Location updated...");
			
			//mapOverlays.remove(golferOverlay);
			
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			
			HoleItemizedOverlay golferOverlay = new HoleItemizedOverlay(6L, HoleMapActivity.this.getResources().getDrawable(R.drawable.golfer_icon2));
			OverlayItem overlayitem = new OverlayItem(point, "", "");
			golferOverlay.addOverlay(overlayitem);
			mapOverlays.remove(golferOverlay);
			mapOverlays.add(golferOverlay);
			
			if(showPlayerToGreenLine) {
				LineOverlay line = new LineOverlay(7L, point, mgPoint, Color.BLUE, Paint.Style.STROKE);
				mapOverlays.remove(line);
				mapOverlays.add(line);
			}
			
			
			// Print distances at POI:s?
			
			for(PointOfInterest p1 : currentHole.getPois()) {
				int latx = (int) (p1.getLat() * 1E6);
				int lngx = (int) (p1.getLon() * 1E6);
				if(showDistancesToMidGreen && isGreenPoi(p1)) {
					
										
					MarkerOverlay distance = new MarkerOverlay(1000L+p1.getId(), new GeoPoint(latx, lngx), point);
					mapOverlays.remove(distance);
					mapOverlays.add(distance);
				}
				if(showDistancesToGreenBunkers && isGreenBunkerPoi(p1)) {
															
					MarkerOverlay distance = new MarkerOverlay(1000L+p1.getId(), new GeoPoint(latx, lngx), point);
					mapOverlays.remove(distance);
					mapOverlays.add(distance);
				}
				if(showDistancesToMidGreen && isFwBunkerPoi(p1)) {
															
					MarkerOverlay distance = new MarkerOverlay(1000L+p1.getId(), new GeoPoint(latx, lngx), point);
					mapOverlays.remove(distance);
					mapOverlays.add(distance);
				}
			}
			mapView.invalidate();
		}
		
		

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};
	
	private boolean isGreenPoi(PointOfInterest p1) {
		return p1.getType().equals("fg") ||  p1.getType().equals("mg") ||p1.getType().equals("bg");
	}
	
	private boolean isMidGreenPoi(PointOfInterest p1) {
		return p1.getType().equals("mg");
	}

	private boolean isFwBunkerPoi(PointOfInterest p1) {
		return p1.getType().equals("fwbr") ||  p1.getType().equals("fwbr");
	}

	private boolean isGreenBunkerPoi(PointOfInterest p1) {
		return p1.getType().equals("gbf") ||  p1.getType().equals("gbl") || p1.getType().equals("gbr") ||  p1.getType().equals("gbb");
	}
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	class LineOverlay extends Overlay {
		
		Long id;
		GeoPoint gP1;
		GeoPoint gP2;
		int color;
		Paint.Style style;

	    public LineOverlay(Long id, GeoPoint gP1, GeoPoint gP2, int color, Paint.Style style) {
	    	this.id = id;
	    	this.gP1 = gP1;
	    	this.gP2 = gP2;
	    	this.color = color;
	    	this.style = style;
	    }   

	    public void draw(Canvas canvas, MapView mapv, boolean shadow){
	        super.draw(canvas, mapv, shadow);

	    Paint   mPaint = new Paint();
	        mPaint.setDither(true);
	        mPaint.setColor(color);
	        mPaint.setStyle(style);
	        mPaint.setAntiAlias(true);
	        mPaint.setStrokeJoin(Paint.Join.ROUND);
	        mPaint.setStrokeCap(Paint.Cap.ROUND);
	        mPaint.setStrokeWidth(1);

	        Point p1 = new Point();
	        Point p2 = new Point();

	        Path    path = new Path();

	        Projection  projection = mapView.getProjection();
	        projection.toPixels(gP1, p1);
	        projection.toPixels(gP2, p2);
	        
	        path.moveTo(p2.x, p2.y);
	        path.lineTo(p1.x, p1.y);
	        
	        canvas.drawPath(path, mPaint);
	        
	        Paint p = new Paint();
			p.setAntiAlias(true);

			p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
			p.setTextSize(12);
			p.setColor(Color.WHITE);
			p.setTextAlign(Paint.Align.CENTER);
			//Paint.FontMetrics fm = p.getFontMetrics();

			
			float[] results = new float[3];
			
			Location.distanceBetween(gP1.getLatitudeE6() / 1E6, gP1.getLongitudeE6() / 1E6, gP2.getLatitudeE6() / 1E6, gP2.getLongitudeE6() / 1E6, results);
			
			canvas.drawText(nf.format(results[0]) + " m", p2.x - ((p2.x-p1.x) / 2)+5, p2.y - ((p2.y-p1.y) / 2), p);
	    }
	    
	   

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LineOverlay other = (LineOverlay) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		private HoleMapActivity getOuterType() {
			return HoleMapActivity.this;
		}
	}
	
	
	class MarkerOverlay extends Overlay {
		
		Long id;
		GeoPoint poiLocation;
		GeoPoint myLocation;

	    public MarkerOverlay(Long id, GeoPoint poiLocation, GeoPoint myLocation) {
	    	this.id = id;
	    	this.poiLocation = poiLocation;
	    	this.myLocation = myLocation;
	    }   

	    public void draw(Canvas canvas, MapView mapv, boolean shadow){
	        super.draw(canvas, mapv, shadow);
	        
	        Point p1 = new Point();	      
	        Projection  projection = mapView.getProjection();
	        projection.toPixels(poiLocation, p1);

	        Paint p = new Paint();
			p.setAntiAlias(true);

			p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
			p.setTextSize(12);
			p.setColor(Color.WHITE);
			p.setTextAlign(Paint.Align.CENTER);
			
			float[] results = new float[3];
			
			Location.distanceBetween(poiLocation.getLatitudeE6() / 1E6, poiLocation.getLongitudeE6() / 1E6, myLocation.getLatitudeE6() / 1E6, myLocation.getLongitudeE6() / 1E6, results);
			
			canvas.drawText(nf.format(results[0]) + " m", p1.x+30, p1.y+3, p);
	    }
	    
	   

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MarkerOverlay other = (MarkerOverlay) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		private HoleMapActivity getOuterType() {
			return HoleMapActivity.this;
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 1, 1, R.string.previousHoleLbl);
		menu.add(Menu.NONE, 2, 2, R.string.nextHoleLbl);
		menu.getItem(0).setIcon(R.drawable.ic_menu_back);
		menu.getItem(1).setIcon(R.drawable.ic_menu_forward);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Course c = (Course) getIntent().getSerializableExtra("course");
		switch (item.getItemId()) {
		case 1:
			if(currentHole.getNumber()-2 < 0) {
				Toast.makeText(HoleMapActivity.this, "Du är på hål 1.", Toast.LENGTH_SHORT).show();
				return true;
			}
			currentHole = (Hole) c.getHoles().get(currentHole.getNumber()-2);
			init();
			break;
		case 2:
			if(currentHole.getNumber() > c.getHoles().size()) {
				Toast.makeText(HoleMapActivity.this, "Du är på hål " + c.getHoles().size() + ".", Toast.LENGTH_SHORT).show();
				return true;
			}
			currentHole = (Hole) c.getHoles().get(currentHole.getNumber());
			init();
			break;
		}
		return true;
	}
}
