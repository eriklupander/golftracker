package com.squeed.golftracker.server.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.squeed.golftracker.common.model.Course;
import com.squeed.golftracker.common.model.GolfVenue;
import com.squeed.golftracker.common.model.Hole;
import com.squeed.golftracker.common.model.PoiType;
import com.squeed.golftracker.common.model.PointOfInterest;
import com.squeed.golftracker.common.model.Tee;
import com.squeed.golftracker.common.model.TeeType;
import com.squeed.golftracker.common.model.tiny.TinyGolfVenue;

@Path("editcourse")
public interface EditCourseService {
	
	@Path("/venue/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	GolfVenue addVenue(GolfVenue venue);
	
	/**
	 * Updates name and/or description only!
	 * 
	 * @param venueId
	 * @param venue
	 * @return
	 */
	@Path("/venue/{venueId}/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	GolfVenue updateVenue(@PathParam("venueId") Long venueId, GolfVenue venue);
	
	@Path("/venue/{venueId}/course/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Course addCourseToVenue(@PathParam("venueId") Long venueId, Course course) throws Exception;
	
	@Path("/course/{courseId}/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Course updateCourse(@PathParam("courseId") Long courseId, Course course) throws Exception;
	
	@Path("/venue/{venueId}/course/{courseId}/delete")
    @DELETE   
	void deleteCourse(@PathParam("venueId") Long venueId, @PathParam("courseId") Long courseId) throws Exception;
	
	
	@Path("/course/{courseId}/createtee")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	TeeType createTeeType(@PathParam("courseId") Long courseId, TeeType teeType);
	
	
	@Path("/hole/{holeId}/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Hole updateHole(@PathParam("holeId") Long holeId, Hole hole);
	
	
	@Path("/hole/{holeId}/createpoi")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	PointOfInterest createPoi(@PathParam("holeId") Long holeId, PointOfInterest poi);
	
	@Path("/poi/save")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	PointOfInterest savePoi(PointOfInterest poi);
	
	@Path("/tee/save")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Tee saveTee(Tee tee);
	
	@Path("/hole/{holeId}/createtee")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Tee createTee(@PathParam("holeId") Long holeId, Tee tee);
	
	
	@Path("/poitypes")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	List<PoiType> getPoiTypes();
	
	
	@Path("/venues/tiny")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	List<TinyGolfVenue> getTinyVenues();
}
