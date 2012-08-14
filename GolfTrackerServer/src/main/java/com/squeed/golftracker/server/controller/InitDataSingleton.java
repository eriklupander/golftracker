package com.squeed.golftracker.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.squeed.golftracker.common.model.Club;
import com.squeed.golftracker.common.model.ClubSet;
import com.squeed.golftracker.common.model.ClubType;
import com.squeed.golftracker.common.model.Country;
import com.squeed.golftracker.common.model.Course;
import com.squeed.golftracker.common.model.GolfVenue;
import com.squeed.golftracker.common.model.Hole;
import com.squeed.golftracker.common.model.Manufacturer;
import com.squeed.golftracker.common.model.Setting;
import com.squeed.golftracker.common.model.Tee;
import com.squeed.golftracker.common.model.TeeType;
import com.squeed.golftracker.common.model.User;


@Startup
@Singleton
public class InitDataSingleton {

	private static final String INIT_DATA_RUN = "init.data.run";
	@Inject
	EntityManager em;	
	
	@PostConstruct
	public void init() {
		
		System.out.println("ENTER - init() of Stat");
		
		boolean runInitData = true;
		
		try {
			Setting s = (Setting) em.createQuery("select s from Setting s WHERE s.key=:key").setParameter("key", INIT_DATA_RUN).getSingleResult();
			if("1".equals(s.getValue())) {
				runInitData = false;
			}
		} catch (NoResultException e) {
			
		}
		
		if(!runInitData)
			return;
	
		
		 Locale[] locales = Locale.getAvailableLocales();
	    for (Locale locale : locales) {
	      String iso = locale.getISO3Country();
	      String code = locale.getCountry();
	      String name = locale.getDisplayCountry();
	      if(name.trim().length() > 0) {
	    	  em.merge(new Country(code, iso, name));
	      }	
	      
	    }
	    
	    
	    em.flush();

		Country sweden = (Country) em.createQuery("select c from Country c WHERE c.code='SE'").getSingleResult();
		
		setupReferenceCourse(sweden);
		setupUsers();
		
		// Write setting flag
		Setting settingsInitialized = new Setting(INIT_DATA_RUN, "1");
		em.persist(settingsInitialized);
		
		System.out.println("FINISHED setting up core data.");
	}

	private void setupUsers() {
		User u = new User();
		u.setEmail("erik.lupander@squeed.com");
		u.setName("Erik Lupander");
		u.setUsername("erilup");
		setupClubSet(u);
		
		u = em.merge(u);
	}

	private void setupClubSet(User u) {
		Manufacturer cobra = em.merge(new Manufacturer("Cobra"));
		Manufacturer taylormade = em.merge(new Manufacturer("Taylormade"));
		Manufacturer cleveland = em.merge(new Manufacturer("Cleveland"));
		Manufacturer callaway = em.merge(new Manufacturer("Callaway"));
		Manufacturer titleist = em.merge(new Manufacturer("Titleist"));
		Manufacturer ping = em.merge(new Manufacturer("Ping"));
		Manufacturer adams = em.merge(new Manufacturer("Adams"));
		Manufacturer wilson = em.merge(new Manufacturer("Wilson"));
		Manufacturer nike = em.merge(new Manufacturer("Nike"));
		Manufacturer topFlite = em.merge(new Manufacturer("Top Flite"));
		
		ClubSet cs = new ClubSet();
		cs.setName("Standard");
		cs.getClubs().add(new Club(ClubType.DRIVER,-1,"Driver", taylormade));
		cs.getClubs().add(new Club(ClubType.FAIRWAY_WOOD, 5, "Trä-5", taylormade));
		cs.getClubs().add(new Club(ClubType.HYBRID,5,"Hybrid-5", taylormade));
		
		cs.getClubs().add(new Club(ClubType.IRON,4,"Järn-4", taylormade));
		cs.getClubs().add(new Club(ClubType.IRON,5,"Järn-5", taylormade));
		cs.getClubs().add(new Club(ClubType.IRON,6,"Järn-6", taylormade));
		cs.getClubs().add(new Club(ClubType.IRON,7,"Järn-7", taylormade));
		cs.getClubs().add(new Club(ClubType.IRON,8,"Järn-8", taylormade));
		cs.getClubs().add(new Club(ClubType.IRON,9,"Järn-9", taylormade));
		cs.getClubs().add(new Club(ClubType.WEDGE,-1,"PW", taylormade));
		
		cs.getClubs().add(new Club(ClubType.WEDGE,-1,"SW", topFlite));
		cs.getClubs().add(new Club(ClubType.WEDGE,-1,"LW 58", cleveland));
		
		cs.getClubs().add(new Club(ClubType.PUTTER,-1,"Putter", ping));
		
		cs = em.merge(cs);
		u.setClubSet(cs);
	}

	private void setupReferenceCourse(Country country) {
		GolfVenue gv = new GolfVenue();
		gv.setCountry(country);
		gv.setName("Sotenäs Golfklubb");
		
		Course c = new Course();		
		c.setName("Gul/Röd");
		c.setDescription("5480 meters, mixed park/forest");
		
		addTeeTypes(c);
		addHoles(c);
		gv.getCourses().add(c);
		em.merge(gv);
	}

	private void addHoles(Course c) {
		List<Hole> holes = new ArrayList<Hole>();
		for(int a = 1 ; a < 19; a++) {
			Hole h = new Hole();
			h.setHcp(a);
			h.setNumber(a);
			h.setPar(4);
			for(TeeType tt : c.getTees()) {
				h.getTees().add(new Tee(tt));
			}
			holes.add(h);
		}
		c.setHoles(holes);
	}

	private void addTeeTypes(Course c) {
		List<TeeType> tees = new ArrayList<TeeType>();
		TeeType yellow = new TeeType();
		yellow.setName("Gul");
		tees.add(yellow);
		TeeType red = new TeeType();
		red.setName("Röd");
		tees.add(red);
		
		c.setTees(tees);
	}
	
}
