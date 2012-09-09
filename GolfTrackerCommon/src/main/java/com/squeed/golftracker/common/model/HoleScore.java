package com.squeed.golftracker.common.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="hole_score")
public class HoleScore {
	
	
	private Long id;
	private Hole hole;
	private Integer score;
	private List<Shot> shots;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne
	public Hole getHole() {
		return hole;
	}
	public void setHole(Hole hole) {
		this.hole = hole;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	
	@OneToMany(orphanRemoval=true)
	public List<Shot> getShots() {
		return shots;
	}
	public void setShots(List<Shot> shots) {
		this.shots = shots;
	}
	
	// TODO Add statistics stuff such as tee club, direction of drive, putts, bunker shots, penalties etc.

	
}
