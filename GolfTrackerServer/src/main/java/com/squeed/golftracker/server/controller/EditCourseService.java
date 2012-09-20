package com.squeed.golftracker.server.controller;

import java.util.List;

import javax.ws.rs.Consumes;
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
import com.squeed.golftracker.common.model.TeeType;
import com.squeed.golftracker.common.model.tiny.TinyGolfVenue;

@Path("editcourse")
public interface EditCourseService {
	
	@Path("/venue/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	GolfVenue addVenue(GolfVenue venue);
	
	
	@Path("/venue/{venueId}/course/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Course addCourseToVenue(@PathParam("venueId") Long venueId, Course course) throws Exception;
	
	
	@Path("/course/{courseId}/createtee")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	TeeType createTee(@PathParam("courseId") Long courseId, TeeType teeType);
	
	
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
	
	
	@Path("/poitypes")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	List<PoiType> getPoiTypes();
	
	
	@Path("/venues/tiny")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	List<TinyGolfVenue> getTinyVenues();
}
