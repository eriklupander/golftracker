package com.squeed.golftracker.server.controller;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.squeed.golftracker.common.model.Course;
import com.squeed.golftracker.common.model.GolfVenue;
import com.squeed.golftracker.common.model.Hole;
import com.squeed.golftracker.common.model.PoiType;
import com.squeed.golftracker.common.model.PointOfInterest;
import com.squeed.golftracker.common.model.TeeType;

@Local(EditCourseService.class)
@Stateless
public class EditCourseServiceBean implements EditCourseService {
	
	@Inject
	EntityManager em;

	@Override
	public GolfVenue addVenue(GolfVenue venue) {
		return em.merge(venue);
	}

	@Override
	public Course addCourseToVenue(Long venueId, Course course) throws Exception {
		GolfVenue venue = em.find(GolfVenue.class, venueId);
		if(venue == null) {
			throw new Exception("Venue identified by " + venueId + " not found");
		}
		
		for(int a = 0; a < course.getHoleCount(); a++) {
			course.getHoles().add(new Hole(a+1));
		}
		
		
		venue.getCourses().add(course);
		course = em.merge(course);
		venue = em.merge(venue);
		return course;
	}

	@Override
	public TeeType createTee(Long courseId, TeeType teeType) {
		Course course = em.find(Course.class, courseId);
		course.getTees().add(teeType);
		course = em.merge(course);
		teeType = em.merge(teeType);
		return teeType;
	}

	@Override
	public Hole updateHole(Long holeId, Hole hole) {
		Hole dbHole = em.find(Hole.class, holeId);
		hole.setId(dbHole.getId());
		return em.merge(hole);
	}

	@Override
	public PointOfInterest createPoi(Long holeId, PointOfInterest poi) {
		Hole dbHole = em.find(Hole.class, holeId);
		dbHole.getPois().add(poi);
		poi = em.merge(poi);
		dbHole = em.merge(dbHole);
		return poi;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<PoiType> getPoiTypes() {
		return em.createQuery("SELECT pt FROM PoiType pt").getResultList();
	}

}
