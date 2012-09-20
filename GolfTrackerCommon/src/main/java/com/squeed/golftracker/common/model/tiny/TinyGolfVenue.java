package com.squeed.golftracker.common.model.tiny;

/**
 * Used to populate lists etc.
 * @author Erik
 *
 */
public class TinyGolfVenue {
	private Long id;
	private String name;
	private Double longitude;
	private Double latitude;
	
	public TinyGolfVenue() {
		
	}
		
	public TinyGolfVenue(Long id, String name, Double latitude,
			Double longitude) {
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	
}
