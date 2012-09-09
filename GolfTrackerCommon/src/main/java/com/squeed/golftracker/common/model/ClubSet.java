package com.squeed.golftracker.common.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="club_set")
public class ClubSet {
	
	
	private Long id;
	
	private String name;
	private Set<Club> clubs = new HashSet<Club>();
	
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
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public Set<Club> getClubs() {
		return clubs;
	}
	public void setClubs(Set<Club> clubs) {
		this.clubs = clubs;
	}
	
}
