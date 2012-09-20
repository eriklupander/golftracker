var currentVenue;
var currentCourse;
var currentHole;

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
		$('#datapanel_holes').append('<li id="' + hole.id + '" class="ui-state-default">' + hole.number + '<div style="font-size:10px;">Par ' + hole.par + ' Hcp ' + hole.hcp + '</div></li>');
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
	for(var a = 0; a < currentCourse.holes.length; a++) {
		var hole = currentCourse.holes[a];
		if(hole.id == holeId) {
			currentHole = hole;
			break;
		}
	}
	
	//$('#map_canvas').clear(name:'markers');
	
	
	var funcs = new Array();
		
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
			'position': posStr, 
			'draggable': true, 
			'bounds': false,
			'title' : poi.title,
			'icon': 'images/poi.png'
		})		
		.click(createPoiFunc(id))
		.dragend( function(event) {
			alert('Poi dragend');
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
			'position': posStr, 
			'draggable': true, 
			'bounds': false,
			'title' : tee.teeType.name,
			'icon': 'images/tee.png'
		})		
		.click(createTeeFunc(id))
		.dragend( function(event) {
			alert('Tee dragend');
		});	
		
	}
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
