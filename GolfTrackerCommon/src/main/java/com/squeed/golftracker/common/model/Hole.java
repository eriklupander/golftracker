package com.squeed.golftracker.common.model;

import java.io.Serializable;
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
@Table(name="hole")
public class Hole implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Integer number;
	private Integer hcp;
	private Integer par;
	
	private Set<Tee> tees = new HashSet<Tee>();
	private Set<PointOfInterest> pois = new HashSet<PointOfInterest>();
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public Integer getHcp() {
		return hcp;
	}
	public void setHcp(Integer hcp) {
		this.hcp = hcp;
	}
	public Integer getPar() {
		return par;
	}
	public void setPar(Integer par) {
		this.par = par;
	}
	
	@OneToMany(orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public Set<Tee> getTees() {
		return tees;
	}
	public void setTees(Set<Tee> tees) {
		this.tees = tees;
	}
	
	@OneToMany(orphanRemoval=true, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	public Set<PointOfInterest> getPois() {
		return pois;
	}
	public void setPois(Set<PointOfInterest> pois) {
		this.pois = pois;
	}
	
}
