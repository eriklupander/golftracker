package com.squeed.golftracker.common.model.tiny;

/**
 * Used to populate lists etc.
 * @author Erik
 *
 */
public class TinyGolfVenue {
	private Long id;
	private String name;
	
	public TinyGolfVenue() {
		
	}
	
	public TinyGolfVenue(Long id, String name) {
		this.id = id;
		this.name = name;
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
	
	
}
