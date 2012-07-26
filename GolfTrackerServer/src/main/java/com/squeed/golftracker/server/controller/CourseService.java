package com.squeed.golftracker.server.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.squeed.golftracker.common.model.Course;
import com.squeed.golftracker.common.model.GolfVenue;
import com.squeed.golftracker.common.model.tiny.TinyGolfVenue;


/**
 * This interface specifies REST endpoint methods that serve
 * "static" course information, e.g. a course with holes and pois.
 * @author Erik
 *
 */

@Path("course")
public interface CourseService {
	
	@Path("/nearby")
    @GET
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	List<TinyGolfVenue> getNearbyCourses(@QueryParam("lon") Long lon, @QueryParam("lat") Long lat);
	
	@Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	Course getCourse(@PathParam("id") Long id);
	
	@Path("/venue/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	GolfVenue getGolfVenue(@PathParam("id") Long id);

	@Path("/name/{name}")
    @GET
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	List<TinyGolfVenue> findGolfVenueByName(@PathParam("name") String name);
	
}
