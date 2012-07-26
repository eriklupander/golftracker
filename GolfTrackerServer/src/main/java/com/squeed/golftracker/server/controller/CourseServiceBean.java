package com.squeed.golftracker.server.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.squeed.golftracker.common.model.Course;
import com.squeed.golftracker.common.model.GolfVenue;
import com.squeed.golftracker.common.model.tiny.TinyGolfVenue;

@Local(CourseService.class)
@Stateless
public class CourseServiceBean implements CourseService {
	
	@Inject
	EntityManager em;	

	@Override
	public List<TinyGolfVenue> getNearbyCourses(Long lon, Long lat) {
		List<GolfVenue> venues = em.createQuery("select gv FROM GolfVenue gv").getResultList();
		List<TinyGolfVenue> tc = new ArrayList<TinyGolfVenue>();
		
		if(venues == null) {
			return tc;
		}
		for(GolfVenue gv : venues) {
			tc.add(new TinyGolfVenue(gv.getId(), gv.getName()));
		}
		return tc;
	}

	@Override
	public Course getCourse(Long id) {
		Course c = em.find(Course.class, id);
		c.getTees().size();
		c.getHoles().size();
		
		return c;
	}
	
	@Override
	public GolfVenue getGolfVenue(Long id) {
		GolfVenue gv = em.find(GolfVenue.class, id);
		for(Course c : gv.getCourses()) {
			c.getTees().size();
			c.getHoles().size();
		}
		return gv;
	}

	@Override
	public List<TinyGolfVenue> findGolfVenueByName(String name) {
		List<GolfVenue> venues = em.createQuery("select gv from GolfVenue gv WHERE lower(gv.name) like :name ORDER BY gv.name")
			.setParameter("name", name + "%")
			.getResultList();
		List<TinyGolfVenue> tc = new ArrayList<TinyGolfVenue>();
		
		if(venues == null) {
			return tc;
		}
		for(GolfVenue gv : venues) {			
			tc.add(new TinyGolfVenue(gv.getId(), gv.getName()));
		}	
		return tc;
	}

}
