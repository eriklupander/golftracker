package com.squeed.golftracker;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.sax.StartElementListener;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class HoleItemizedOverlay extends ItemizedOverlay {
	
	private Long id;
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	public HoleItemizedOverlay(Long id, Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		this.id = id;
	}
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	public void clear() {
		mOverlays.clear();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		HoleItemizedOverlay other = (HoleItemizedOverlay) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public boolean isWaitForTap = false;

	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
		
		//GeoPoint point = mapView.getProjection().fromPixels( (int) event.getX(), (int) event.getY());
		Log.i("EditHoleMapActivity", "TAP. ID: " + id + " at GeoPoint lat: " + point.getLatitudeE6() + " lon: " + point.getLongitudeE6());
		if(isWaitForTap) {
			Log.i("EditHoleMapActivity", "Do something with this coordinate!");
			// TODO Bring up Dialog, enter type, location etc. and save to database.
			Intent i = new Intent(mapView.getContext(), NewPoiActivity.class);
			i.putExtra("lat", point.getLatitudeE6() / 1E6);
			i.putExtra("lon", point.getLongitudeE6() / 1E6);
			mapView.getContext().startActivity(i);
			isWaitForTap = false;
		}
		return true;
	}
	
	
}
