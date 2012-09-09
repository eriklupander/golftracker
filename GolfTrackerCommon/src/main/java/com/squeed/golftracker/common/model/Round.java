package com.squeed.golftracker.common.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The object that stores info about a round of golf for one or more players.
 * 
 * Keeps track of which course and which players are playing
 * 
 * @author Erik
 *
 */
@Entity
@Table(name="round")
public class Round {
	
	private Long id;
	private Course course;
	private Calendar date;
	private String note;
	
	/** One RoundScore entity per player **/
	private List<RoundScore> roundScore = new ArrayList<RoundScore>(); 
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@OneToMany(orphanRemoval=true)
	public List<RoundScore> getRoundScore() {
		return roundScore;
	}

	public void setRoundScore(List<RoundScore> roundScore) {
		this.roundScore = roundScore;
	}

	@ManyToOne
	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	@Temporal(value=TemporalType.TIMESTAMP)
	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	
}
