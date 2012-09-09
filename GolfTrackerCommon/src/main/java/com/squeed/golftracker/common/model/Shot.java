package com.squeed.golftracker.common.model;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Keeps track of a single golf shot. E.g. from coords, to coords. Club. When.
 * 
 * @author Erik
 *
 */
@Entity
@Table(name="golf_shot")
public class Shot {

	private Long id;
	
	private Double fromLongitude;
	private Double fromLatitude;
	
	private Double toLongitude;
	private Double toLatitude;
	
	private Calendar created;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getFromLongitude() {
		return fromLongitude;
	}

	public void setFromLongitude(Double fromLongitude) {
		this.fromLongitude = fromLongitude;
	}

	public Double getFromLatitude() {
		return fromLatitude;
	}

	public void setFromLatitude(Double fromLatitude) {
		this.fromLatitude = fromLatitude;
	}

	public Double getToLongitude() {
		return toLongitude;
	}

	public void setToLongitude(Double toLongitude) {
		this.toLongitude = toLongitude;
	}

	public Double getToLatitude() {
		return toLatitude;
	}

	public void setToLatitude(Double toLatitude) {
		this.toLatitude = toLatitude;
	}

	@Temporal(value=TemporalType.TIMESTAMP)
	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}
	
	
}
