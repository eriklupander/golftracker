package com.squeed.golftracker.helper;

import com.squeed.golftracker.entity.PointOfInterestDTO;

public class MockLocation {

	public static PointOfInterestDTO  bryggan = new PointOfInterestDTO(1L,"oth", "Bryggan", 58.528761, 11.268512);
	public PointOfInterestDTO clarysHus = new PointOfInterestDTO(2L, "oth", "Clarys hus", 58.527613,11.271392);
	public static PointOfInterestDTO stromsHus = new PointOfInterestDTO(3L, "oth", "Ströms hus", 58.526232,11.270153);
	
	public static PointOfInterestDTO tee1Yel = new PointOfInterestDTO(4L, "yt", "Hål 1 gul tee", 58.435646,11.380046);
	public PointOfInterestDTO tee1Red = new PointOfInterestDTO(5L, "rt", "Hål 1 röd tee", 58.435499,11.379507);
	public static PointOfInterestDTO green1Front = new PointOfInterestDTO(6L, "fg", "Green 1 framkant", 58.433425,11.376318);
	public PointOfInterestDTO green1Mid = new PointOfInterestDTO(7L, "mg", "Green 1 mitt", 58.433306,11.376281);
	public PointOfInterestDTO green1Back = new PointOfInterestDTO(8L, "bg", "Green 1 bakkant", 58.433244,11.376262);
	
	public static void main(String[] args) {
		System.out.println("Gul till framkant: " + 
				LongLatConverter.getDistance(
						tee1Yel.getLon(), tee1Yel.getLat(), 
						green1Front.getLon(), green1Front.getLat()));
	}
	
}
