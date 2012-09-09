package com.squeed.golftracker.common.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * This entity keeps track, for a single round of golf:
 * 
 * For one player
 * The score for each hole (HoleScore)
 * Which Tee for the round.
 * 
 * @author Erik
 *
 */
@Entity
@Table(name="user_round_score")
public class RoundScore {

	private Long id;
	private User user;
	private TeeType teeType;
	private List<HoleScore> holeScores;

	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne
	public TeeType getTeeType() {
		return teeType;
	}

	public void setTeeType(TeeType teeType) {
		this.teeType = teeType;
	}

	@OneToMany(orphanRemoval=true)
	public List<HoleScore> getHoleScores() {
		return holeScores;
	}

	public void setHoleScores(List<HoleScore> holeScores) {
		this.holeScores = holeScores;
	}

	
	
}
