package com.squeed.golftracker.common.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="hole_score")
public class HoleScore {
	
	
	private Long id;
	private User player;
	private Hole hole;
	private Integer score;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne
	public User getPlayer() {
		return player;
	}
	public void setPlayer(User player) {
		this.player = player;
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
	
	// TODO Add statistics stuff such as tee club, direction of drive, putts, bunker shots, penalties etc.

	
}
