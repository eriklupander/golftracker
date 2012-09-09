package com.squeed.golftracker.common.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="poi")
public class PointOfInterest {
	
	
	private Long id;
	private PoiType type;
	private String title;
	private Double longitude;
	private Double latitude;
	private User createdBy;
	
	public PointOfInterest() {}
	
	public PointOfInterest(PoiType type, String title, Double longitude,
			Double latitude, User createdBy) {
		super();
		this.type = type;
		this.title = title;
		this.longitude = longitude;
		this.latitude = latitude;
		this.createdBy = createdBy;
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
	public PoiType getType() {
		return type;
	}
	public void setType(PoiType type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	
	@ManyToOne(optional=true)
	public User getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	
}
