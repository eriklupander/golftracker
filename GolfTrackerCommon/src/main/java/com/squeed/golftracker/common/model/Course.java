package com.squeed.golftracker.common.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="course")
public class Course {
	
	
	private Long id;
	private String name;
	private String description;
		
	private Set<Hole> holes;
	private Set<TeeType> tees;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	@OneToMany(cascade=CascadeType.ALL)
	public Set<Hole> getHoles() {
		return holes;
	}
	public void setHoles(Set<Hole> holes) {
		this.holes = holes;
	}
	
	@OneToMany(cascade=CascadeType.ALL)
	public Set<TeeType> getTees() {
		return tees;
	}
	public void setTees(Set<TeeType> tees) {
		this.tees = tees;
	}
	
	
}
