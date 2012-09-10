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

@Path("editcourse")
public interface EditCourseService {
	
	@Path("/venue/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	GolfVenue addVenue(GolfVenue venue);
	
	
	@Path("/venue/{venueId}/course/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	Course addCourseToVenue(@PathParam("venueId") Long venueId, Course course) throws Exception;
	
	
	@Path("/course/{courseId}/createtee")
    @POST
    @Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	TeeType createTee(@PathParam("courseId") Long courseId, TeeType teeType);
	
	
	@Path("/hole/{holeId}/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	Hole updateHole(@PathParam("holeId") Long holeId, Hole hole);
	
	
	@Path("/hole/{holeId}/createpoi")
    @POST
    @Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	PointOfInterest createPoi(@PathParam("holeId") Long holeId, PointOfInterest poi);
	
	
	@Path("/poitypes")
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	List<PoiType> getPoiTypes();
	
}
