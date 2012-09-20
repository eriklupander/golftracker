package com.squeed.golftracker.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="course")
public class Course implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String description;
	private Integer holeCount = 18;
		
	private List<Hole> holes = new ArrayList<Hole>();
	private List<TeeType> tees = new ArrayList<TeeType>();
	
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
	
	
	
	public Integer getHoleCount() {
		return holeCount;
	}
	public void setHoleCount(Integer holeCount) {
		this.holeCount = holeCount;
	}
	@OneToMany(cascade=CascadeType.ALL)
	public List<Hole> getHoles() {
		return holes;
	}
	public void setHoles(List<Hole> holes) {
		this.holes = holes;
	}
	
	@OneToMany(cascade=CascadeType.ALL)
	public List<TeeType> getTees() {
		return tees;
	}
	public void setTees(List<TeeType> tees) {
		this.tees = tees;
	}
	
}
