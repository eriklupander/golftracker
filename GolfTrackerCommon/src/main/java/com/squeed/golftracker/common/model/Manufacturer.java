package com.squeed.golftracker.common.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Manufacturer {
	
	
	private Long id;
	private String name;
	
	public Manufacturer() {}
	
	public Manufacturer(String name) {
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
