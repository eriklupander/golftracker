package com.squeed.golftracker.common.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="tee")
public class Tee {
	
	
	private Long id;
	
	/**
	 * Should be uniform across a course, e.g. "Yellow", "Red", "57", "Blue", "Professional" etc.
	 */
	private TeeType teeType;
	
	private Double longitude;
	private Double latitude;
	
	public Tee() {}
	
	public Tee(TeeType teeType) {
		this.teeType = teeType;
	}
	
	public Tee(TeeType teeType, Double longitude, Double latitude) {
		super();
		this.teeType = teeType;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne
	public TeeType getTeeType() {
		return teeType;
	}
	public void setTeeType(TeeType teeType) {
		this.teeType = teeType;
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
