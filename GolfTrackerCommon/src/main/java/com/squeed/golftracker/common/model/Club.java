package com.squeed.golftracker.common.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="club")
public class Club {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Enumerated(value=EnumType.STRING)
	private ClubType clubType;
	
	/**
	 * E.g. 5 if "IRON" 5.
	 */
	private Integer number;
	
	/**
	 * May be used for a more human-readable name
	 */
	private String name;
	
	/**
	 * The two below are just there in case they might be needed. The manufacturers 
	 * should be put in a proper entity.
	 */
	private Float loft;
	
	@ManyToOne
	private Manufacturer manufacturer;
	
	public Club() {}
	
	public Club(ClubType clubType, Integer number, String name,
			Manufacturer manufacturer) {
		super();
		this.clubType = clubType;
		this.number = number;
		this.name = name;
		this.manufacturer = manufacturer;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public ClubType getClubType() {
		return clubType;
	}
	public void setClubType(ClubType clubType) {
		this.clubType = clubType;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Float getLoft() {
		return loft;
	}
	public void setLoft(Float loft) {
		this.loft = loft;
	}
	
	
	public Manufacturer getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	
}
