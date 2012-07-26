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
	
	private Long longitude;
	private Long latitude;
	
	public Tee() {}
	
	public Tee(TeeType teeType) {
		this.teeType = teeType;
	}
	
	public Tee(TeeType teeType, Long longitude, Long latitude) {
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
	public Long getLongitude() {
		return longitude;
	}
	public void setLongitude(Long longitude) {
		this.longitude = longitude;
	}
	public Long getLatitude() {
		return latitude;
	}
	public void setLatitude(Long latitude) {
		this.latitude = latitude;
	}
	
	
}
