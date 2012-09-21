package com.squeed.golftracker.common.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="poi_type")
public class PoiType {
	
	public static final String FRONT_GREEN = "FRONT_GREEN";
	public static final String MID_GREEN = "MID_GREEN";
	public static final String BACK_GREEN = "BACK_GREEN";
	public static final String BUNKER = "BUNKER";
	public static final String WATER = "WATER";
	public static final String OUT_OF_BOUNDS = "OUT_OF_BOUNDS";

	private Long id;	
	private String name;
	
	public PoiType() {}

	public PoiType(String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue
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
}
