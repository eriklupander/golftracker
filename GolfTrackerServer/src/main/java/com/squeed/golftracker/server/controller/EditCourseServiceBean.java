package com.squeed.golftracker.server.controller;

import java.util.ArrayList;
import java.util.Iterator;
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
import com.squeed.golftracker.common.model.Tee;
import com.squeed.golftracker.common.model.TeeType;
import com.squeed.golftracker.common.model.tiny.TinyGolfVenue;

@Local(EditCourseService.class)
@Stateless
public class EditCourseServiceBean implements EditCourseService {
	
	//Logger log = Logger.getLogger(EditCourseServiceBean.class);
	
	@Inject
	EntityManager em;

	@Override
	public GolfVenue addVenue(GolfVenue venue) {
		//log.info("Creating new GolfVenue: " + venue.getName());
		return em.merge(venue);
	}
	
	@Override
	public GolfVenue updateVenue(Long venueId, GolfVenue venue) {
		GolfVenue dbVenue = em.find(GolfVenue.class, venueId);
		dbVenue.setName(venue.getName());
		dbVenue.setDescription(venue.getDescription());
		return em.merge(dbVenue);
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
	public Course updateCourse(Long courseId, Course course) throws Exception {
		Course dbCourse = em.find(Course.class, courseId);
		dbCourse.setName(course.getName());
		dbCourse.setDescription(course.getDescription());
		
		// Tricky part, add/update/remove TeeTypes
		for(TeeType tt : course.getTees()) {
			if(tt.getId() == null) {
				// NEW, add and persist
				dbCourse.getTees().add(tt);
			} else {
				// UPDATE
				TeeType dbTeeType = em.find(TeeType.class, tt.getId());
				dbTeeType.setName(tt.getName());
				em.merge(dbTeeType);
			}
		}
		
		// Loop over dbCourse tees. If not present in course tees, remove
		Iterator<TeeType> i = dbCourse.getTees().iterator();
		while(i.hasNext()) {
			TeeType dbTt = i.next();
			if(!course.getTees().contains(dbTt)) {
				em.remove(dbTt);
				i.remove();
			}
		}
		
		return em.merge(dbCourse);
	}
	
	@Override
	public void deleteCourse(Long venueId, Long courseId) throws Exception {
		GolfVenue gv = em.find(GolfVenue.class, venueId);
		Course dbCourse = em.find(Course.class, courseId);
		
		gv.getCourses().remove(dbCourse);
		gv = em.merge(gv);

		// Delete tees, pois etc.
//		for(Hole h : dbCourse.getHoles()) {
//			em.remove(h);
//		}
//		
//		// Delete tee types for the course last (hole -> tee -> teeType)
//		for(TeeType tt : dbCourse.getTees()) {
//			em.remove(tt);
//		}
//		em.remove(dbCourse);
	}


	@Override
	public TeeType createTeeType(Long courseId, TeeType teeType) {
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
	
	@Override
	public PointOfInterest updatePoi(PointOfInterest poi) {
		PointOfInterest dbPoi = em.find(PointOfInterest.class, poi.getId());
		dbPoi.setLatitude(poi.getLatitude());
		dbPoi.setLongitude(poi.getLongitude());
		if(poi.getTitle() != null && poi.getTitle().trim().length() > 0) {
			dbPoi.setTitle(poi.getTitle());
		}
		if(poi.getType() != null) {
			dbPoi.setType(poi.getType());
		}
		
		return em.merge(dbPoi);
	}
	
	@Override
	public Tee saveTee(Tee tee) {
		Tee dbTee = em.find(Tee.class, tee.getId());
		dbTee.setLatitude(tee.getLatitude());
		dbTee.setLongitude(tee.getLongitude());
		return em.merge(dbTee);
	}
	
	@Override
	public Tee createTee(Long holeId, Tee tee) {
		Hole dbHole = em.find(Hole.class, holeId);
		dbHole.getTees().add(tee);
		tee = em.merge(tee);
		dbHole = em.merge(dbHole);
		return tee;
	}


	
	@SuppressWarnings("unchecked")
	@Override
	public List<PoiType> getPoiTypes() {
		return em.createQuery("SELECT pt FROM PoiType pt").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TinyGolfVenue> getTinyVenues() {
		List<GolfVenue> resultList = em.createQuery("SELECT v FROM GolfVenue v").getResultList();
		
		List<TinyGolfVenue> l = new ArrayList<TinyGolfVenue>();
		for(GolfVenue gv : resultList) {
			l.add(new TinyGolfVenue(gv.getId(), gv.getName(), gv.getLatitude(), gv.getLongitude()));
		}
		
		return l;
	}
}
