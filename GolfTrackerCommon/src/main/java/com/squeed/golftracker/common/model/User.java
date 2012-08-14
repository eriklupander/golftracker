package com.squeed.golftracker.common.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="golftracker_user")
public class User {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name="username", unique=true, nullable=false)
	private String username;
	
	private String email;
	private String name;
	
	@OneToMany
	private Set<Game> games;
	
	@ManyToOne
	private ClubSet clubSet;
	
	public User() {}
	
	public User(Long userId, String username, String email, String name) {
		this.id = userId;
		this.username = username;
		this.email = email;
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public Set<Game> getGames() {
		return games;
	}
	public void setGames(Set<Game> games) {
		this.games = games;
	}
	
	
	public ClubSet getClubSet() {
		return clubSet;
	}
	public void setClubSet(ClubSet clubSet) {
		this.clubSet = clubSet;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	
}
