var mode = 'NO_VENUE_SELECTED';

var currentVenue;
var currentCourse;
var currentHole;

var poiTypes = new Array();

poiTypes = EditCourseService.getPoiTypes();

//calls the service and handles HTTP error codes
//will return the value of the request if there is a return value
function callService(method, parameters) {
	if (parameters == null || typeof parameters == 'undefined') {
		parameters = {};
	}
	var returnValue;
	parameters["$callback"] = function(httpCode, xmlHttpRequest, value) {
		// if HTTP code isnt within 200-399 range, and the workstation is online we need to print out an error
		if ((httpCode < 200 || httpCode > 399) && httpCode != 1223) {
			// strip out html text
			var text = stripHtml(xmlHttpRequest.responseText);
			// limit the no of characters to 200
			if (text.length > 200) {
				text = text.substring(0, 200);
			}
			showError(text);
		}
		returnValue = value;
	};
	EditCourseService[method](parameters);
	return returnValue;
}

//Strips any html elements within a string
//inspired from http://stackoverflow.com/questions/822452/strip-html-from-text-javascript:
function stripHtml(htmlString) {
	// put the string in a div and get the raw text
	var tmp = document.createElement("DIV");
	tmp.innerHTML = htmlString;
	var text = tmp.textContent || tmp.innerText;
	// remove any nested html comments
	return text.replace(/<!--*[^<>]*-->/ig, "");
}

function showError(text) {
	alert(text);
}

function createFunc(i) {
    return function() { return openEditVenueDialog(this, i); };
}


function loadVenues() {
	var venueList = EditCourseService.getTinyVenues(); //callService("getTinyVenues");

	var funcs = new Array();
	// Add map markers
	for(var a = 0; a < venueList.length; a++) {
		var venue = venueList[a];
		
		funcs.push((function(id) {   
	        return function() {          
	           return id;
	        } 
	    })(venue.id));
		
		var id = funcs[a]();
		
		var posStr = venue.latitude + ", " + venue.longitude;
		$('#map_canvas').gmap('addMarker', {
			'position': posStr, 
			'draggable': false, 
			'bounds': false,
			'title' : venue.name,
			'icon': 'images/venue.png'
		})		
		.click(createFunc(id));	
		
	}
}

function openEditVenueDialog(marker, venueId) {
	var params = {};
	params.id = venueId;
	var venue = CourseService.getGolfVenue(params);
	currentVenue = venue;
	$('#datapanel_heading').text(venue.name);
	$('#datapanel_description').text(venue.description);
	
	$('#datapanel_courses').empty();
	for(var a = 0; a < venue.courses.length; a++) {
		var course = venue.courses[a];
		$('#datapanel_courses').append('<li id="' + course.id + '" class="ui-widget-content">' + course.name + ' (' + course.holeCount + ' holes)</li>');
		
	}
	
	$( "#datapanel_courses" ).selectable(
			{
				selected: function(event, ui) { 
					// Load holes and stuff
					loadCourse(ui.selected.id);
				}
			}
	);
	
	$('#course_info').css('display', 'block');
	
	// USE THE CODE BELOW TO POPULATE SOME FORM
//	$('#dialog').append('<form id="dialog'+marker.__gm_id+'" method="get" action="/" style="display:none;">' +
//			'<p><label for="name">Name</label><input id="name'+marker.__gm_id+'" class="txt" name="name" value=""/></p>'+
//			'<p><label for="description">Description</label><textarea id="description'+marker.__gm_id+'" class="txt" name="description" cols="40" rows="5"></textarea></p></form>');
//	
//	$('#name'+marker.__gm_id).val(venue.name);
//	$('#description'+marker.__gm_id).val(venue.description);
	
	$('#dialog'+marker.__gm_id).dialog({'modal':true, 'title': 'Edit golf club', 'buttons': { 
		"Remove": function() {
			$(this).dialog( "close" );
			marker.setMap(null);
		},
		"Save": function() {
			$(this).dialog( "close" );
			// Send command to the REST backend.
			//createVenue($('#name'+marker.__gm_id).val(), $('#description'+marker.__gm_id).val(), marker.position.Xa, marker.position.Ya);
		}
	}});
}


function loadCourse(courseId) {
	for(var a = 0; a < currentVenue.courses.length; a++) {
		var course = currentVenue.courses[a];
		if(course.id == courseId) {
			currentCourse = course;
			break;
		}
	}
	
	// Add form data and holes etc.
	$('#datapanel_holes').empty();
	for(var a = 0; a < currentCourse.holes.length; a++) {
		var hole = currentCourse.holes[a];
		$('#datapanel_holes').append('<li id="' + hole.id + '" class="ui-state-default">' + hole.number + '<div id="details_' + hole.id + '" style="font-size:10px;">Par ' + hole.par + ' Hcp ' + hole.hcp + '</div></li>');
		$('#edit_hole_' + hole.id).click(function() {
			alert("Clicked edit");
		});
	}
	$( "#datapanel_holes" ).selectable(
			{
				selected: function(event, ui) { 
					// Load holes and stuff
					console.log(ui.selected.id);
					loadHole(ui.selected.id);
				}
			}
	);
}

function test(marker, id) {
	alert(id);
}

function createPoiFunc(i) {
    //return function() { return openEditVenueDialog(this, i); };
	return function() { return test(this, i) };
}

function createTeeFunc(i) {
    //return function() { return openEditVenueDialog(this, i); };
	return function() { return test(this, i) };
}

function loadHole(holeId) {
	mode = 'HOLE_SELECTED';
	for(var a = 0; a < currentCourse.holes.length; a++) {
		var hole = currentCourse.holes[a];
		if(hole.id == holeId) {
			currentHole = hole;
			
			$('#hole_data').css('display','block');
			$('#current_hole').text('Hole ' + currentHole.number);
			
			$('#current_hole_par').val(currentHole.par);
			$('#current_hole_par').keyup(function() {
				// Save immediately to backend on key up
				currentHole.par = $('#current_hole_par').val();
				var params = {};
				params.holeId = currentHole.id;
				params.$entity = currentHole;
				currentHole = EditCourseService.updateHole(params);
				$('#details_' + currentHole.id).empty();
				$('#details_' + currentHole.id).text('Par ' + currentHole.par + ' Hcp ' + currentHole.hcp);
			});
			
			$('#current_hole_index').val(currentHole.hcp);
			$('#current_hole_index').keyup(function() {
				// Save immediately to backend on key up
				currentHole.hcp = $('#current_hole_index').val();
				var params = {};
				params.holeId = currentHole.id;
				params.$entity = currentHole;
				currentHole = EditCourseService.updateHole(params);
				$('#details_' + currentHole.id).empty();
				$('#details_' + currentHole.id).text('Par ' + currentHole.par + ' Hcp ' + currentHole.hcp);
			});
		
			break;
		}
	}
	
	//$('#map_canvas').clear(name:'markers');
	
	
	var funcs = new Array();
		
	// Clear markers
	$('#map_canvas').gmap('clear', 'markers');


	// Add map markers for pois
	for(var a = 0; a < currentHole.pois.length; a++) {
		var poi = currentHole.pois[a];
		
		funcs.push((function(id) {   
	        return function() {          
	           return id;
	        } 
	    })(poi.id));
		
		var id = funcs[a]();
		
		var posStr = poi.latitude + ", " + poi.longitude;
		$('#map_canvas').gmap('addMarker', {
			'id':poi.id,
			'position': posStr, 
			'draggable': true, 
			'bounds': false,
			'title' : poi.title,
			'icon': 'images/poi.png'
		})		
		.click(createPoiFunc(id))
		.dragend( function(event) {
			updateAfterDrag(event, this, 'poi');
		});
		
	}
	
	var funcs2 = new Array();
	
	// Add map markers for tees
	for(var a = 0; a < currentHole.tees.length; a++) {
		var tee = currentHole.tees[a];
		
		funcs2.push((function(id) {   
	        return function() {          
	           return id;
	        } 
	    })(tee.id));
		
		var id = funcs2[a]();
		
		var posStr = tee.latitude + ", " + tee.longitude;
		$('#map_canvas').gmap('addMarker', {
			'id':tee.id,
			'position': posStr, 
			'draggable': true, 
			'bounds': false,
			'title' : tee.teeType.name,
			'icon': 'images/tee.png'
		})		
		.click(createTeeFunc(id))
		.dragend( function(event) {
			updateAfterDrag(event, this, 'tee');
		});	
		
	}
	
	// Center map over hole
    if(currentHole.pois.length > 0 && currentHole.tees.length > 0) {
    	var coord = new google.maps.LatLng(intp(currentHole.tees[0].latitude, currentHole.pois[0].latitude), intp(currentHole.tees[0].longitude, currentHole.pois[0].longitude))
    	//$('#map_canvas').gmap('get','map').setOptions({'center':coord});
    	var map = $('#map_canvas').gmap('get','map');
    	map.panTo(coord);

    }
}

function updateAfterDrag(event, marker, type) {

	// Send update directly to server
	var params = {};
	params.$entity = {
			'id':marker.id,			 
			 'longitude':event.latLng.Ya,
			 'latitude':event.latLng.Xa
	 };
	
	if(type == 'poi') {
		EditCourseService.savePoi(params);		
	} else if(type == 'tee') {
		EditCourseService.saveTee(params);
	}
	updateObject(marker.id, type, event.latLng);
}

/**
 * Fugly client-side update code.
 * 
 * @param id
 * @param type
 * @param latLng
 */
function updateObject(id, type, latLng) {
	if(type == 'poi') {
		// brute force for now
		for(var a = 0; currentHole.pois.length;a++) {
			if(currentHole.pois[a].id == id) {
				currentHole.pois[a].longitude = latLng.Ya;
				currentHole.pois[a].latitude = latLng.Xa;
			}
		}
		return;
	}
	if(type == 'tee') {
		// brute force for now
		for(var a = 0; currentHole.tees.length;a++) {
			if(currentHole.tees[a].id == id) {
				currentHole.tees[a].longitude = latLng.Ya;
				currentHole.tees[a].latitude = latLng.Xa;
			}
		}
		return;
	}
}

function intp(a, b) {
	return (a+b) / 2;
}

function createVenue(name, description, latitude, longitude) {
	 venue = {};
	 venue.$entity = {
			 'name':name,
			 'description':description,
			 'longitude':longitude,
			 'latitude':latitude
	 };
	 callService("addVenue", venue);
}


function openCreatePoiDialog(marker) {
	$('#dialog'+marker.__gm_id).dialog({'modal':true, 'title': 'Add point of interest', 'buttons': { 
		"Remove": function() {
			$(this).dialog( "close" );
			marker.setMap(null);
		},
		"Save": function() {
			$(this).dialog( "close" );
			
			// Send command to the REST backend.
			var poiTypeId = $('#poiType'+marker.__gm_id).val();
			var poiTypeName = $('#poiType'+marker.__gm_id + ' option:selected').text();
			var isTee = false;
			// Fugly hack to determine if "poi" is actually a tee....
			for(var a = 0; a < currentCourse.tees.length; a++) {
				
				if(currentCourse.tees[a].name == poiTypeName) {
					isTee = true;
				}
			}
			
			var params = {};
			if(isTee) {
				params.holeId = currentHole.id;
				params.$entity = {
						
						'teeType': {'id':poiTypeId},
						'longitude':marker.position.Ya,
						'latitude':marker.position.Xa
				 };
				var dbTee = EditCourseService.createTee(params);
				currentHole.tees.push(dbTee);
			} else {
				params.holeId = currentHole.id;
				params.$entity = {
						'title':poiTypeName,
						'type': {'id':poiTypeId},
						'longitude':marker.position.Ya,
						'latitude':marker.position.Xa
				 };
				var dbPoi = EditCourseService.createPoi(params);
				currentHole.pois.push(dbPoi);
			}
			
			
		}
	}});
}
