package com.squeed.golftracker.helper;

import java.text.NumberFormat;

import android.location.Location;

import com.squeed.golftracker.common.model.PointOfInterest;

public class LongLatConverter {

	private static NumberFormat nf = NumberFormat.getInstance();
	
	static {
		nf.setMaximumFractionDigits(1);
	}
	
	public static Double getRawDistance(Double lon, Double lat, Double lon2,
			Double lat2) {
		
		double xDiff = lon2 - lon;
		double yDiff = lat2 - lat;
		
		return Math.sqrt( (xDiff*xDiff) + (yDiff*yDiff) ) * 100000;
	}
	
	public static String getDistance(Double lon, Double lat, Double lon2,
			Double lat2) {
		
		return nf.format(getRawDistance(lon, lat, lon2, lat2));
	}

	public static String getDistance(Location location, PointOfInterest poi) {
		if(poi == null)
			return "Ingen data";
		return nf.format(getRawDistance(location.getLongitude(), location.getLatitude(), poi.getLongitude(), poi.getLatitude()));
	}
	
	public static boolean checkProximity(Location location,
			PointOfInterest poi, int proximityDistance) {
		if(poi == null) {
			return false;
		}
		return getRawDistance(location.getLongitude(), location.getLatitude(), poi.getLongitude(), poi.getLatitude()) <= proximityDistance;
	}
		
}
