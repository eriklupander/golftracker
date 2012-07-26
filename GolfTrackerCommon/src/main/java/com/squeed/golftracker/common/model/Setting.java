package com.squeed.golftracker.common.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Setting {
	
	
	private Long id;
	private String key;
	private String value;
	
	public Setting() {

	}

	public Setting(String key, String value) {
		this.key = key;
		this.value = value;		
	}
	
	
	@Id
	@GeneratedValue
	@Column(name="identifier")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="setting_key")
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	@Column(name="setting_value")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
