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

/**
 * If the supplied str is null or undefined, return the subst value instead
 */
function nullSafeStr(str, subst) {
	if(str == null || str == undefined) {
		return subst;
	}
	return str;
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

/**
 * When clicking a GolfVenue (golf club) marker
 * @param marker
 * @param venueId
 */
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
	
	$('#toolbar').css('display', 'block');
	// Enable some buttons
	$('#edit_course').button('disable');
	$('#delete_course').button('disable');
	
	// Clear holes panel
	$('#datapanel_holes').empty();
	
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
	
	// Enable some buttons
	$('#edit_course').button('enable');
	$('#delete_course').button('enable');
	
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

function openEditPoiDialog(marker, id) {
	var currentPoi;
	for(var a = 0; a < currentHole.pois.length; a++) {
		if(currentHole.pois[a].id == id) {
			currentPoi = currentHole.pois[a];
			break;
		}
	}
	if(currentPoi == null || currentPoi == undefined) {
		alert('ERROR, could not find POI.');
		return;
	}
	//alert('Edit: ' + id + ' Marker: ' + marker);
	$('#dialog').append('<form id="edit_poi_dialog">' +
	'<p><label for="name">Name</label><input type="text" id="poi_title_'+id+'" value="' + currentPoi.title + '"></input></p>'+
	'<p><label for="name">Type</label><select id="poiType_'+id+'"></select></p></form>');
	
	for(var a = 0; a < poiTypes.length; a++) {
		var found = false;
		// A hole can only have one of each XXXXX_GREEN
		// But since this is the edit mode, we must support our own if selected
		if(poiTypes[a].name == 'FRONT_GREEN' || poiTypes[a].name == 'MID_GREEN' || poiTypes[a].name == 'BACK_GREEN') {
			
			// Make sure the hole doesn't have this one already								
			for(var b = 0; b < currentHole.pois.length; b++) {
				if(currentHole.pois[b].type.name == poiTypes[a].name && currentHole.pois[b].id != currentPoi.id) {
					found = true;
					break;
				}
			}
		}
		if(!found) {
			var isSelected = (poiTypes[a].id == currentPoi.type.id);
			$('#poiType_'+id).append('<option ' + (isSelected ? 'selected' : '') + ' value="' + poiTypes[a].id + '">' + poiTypes[a].name + '</option>');
		}							
	}
	
	// Finally, add the Tee Types for this course as well
	for(var a = 0; a < currentCourse.tees.length; a++) {
		var found = false;
		// A hole can only have one of each tee
			
			// Make sure the hole doesn't have this one already								
		for(var b = 0; b < currentHole.tees.length; b++) {
			if(currentHole.tees[b].teeType.id == currentCourse.tees[a].id) {
				found = true;
				break;
			}
		}
		
		if(!found) {
			$('#poiType_'+id).append('<option value="' + currentCourse.tees[a].id + '">' + currentCourse.tees[a].name + '</option>'); 
		}							
	}
	
	doOpenTheDialog(marker, id);

}

function doOpenTheDialog(marker, id) {
	$('#edit_poi_dialog').dialog({'modal':true, 'title': 'Edit point of interest', 'buttons': { 
		"Remove": function() {
			$(this).dialog( "close" );
			marker.setMap(null);
		},
		"Save": function() {
			$(this).dialog( "close" );
			params.$entity = {
			    'id' : id,
				'title':$('#poi_title_' + id).val(),
				'type': {'id':$('#poiType_' + id).val()},
				'longitude':currentPoi.longitude,
				'latitude':currentPoi.latitude
			};
			var dbPoi = EditCourseService.updatePoi(params);
			
			// TODO Update client-side POI
			for(var a = 0; currentHole.pois.length;a++) {
				if(currentHole.pois[a].id == id) {
					currentHole.pois[a] = dbPoi;
				}
			}
		}	
			// Send command to the REST backend.
//			var poiTypeId = $('#poiType'+marker.__gm_id).val();
//			var poiTypeName = $('#poiType'+marker.__gm_id + ' option:selected').text();
//			var isTee = false;
//			// Fugly hack to determine if "poi" is actually a tee....
//			for(var a = 0; a < currentCourse.tees.length; a++) {
//				
//				if(currentCourse.tees[a].name == poiTypeName) {
//					isTee = true;
//				}
//			}
//			
//			var params = {};
//			if(isTee) {
//				params.holeId = currentHole.id;
//				params.$entity = {
//						
//						'teeType': {'id':poiTypeId},
//						'longitude':marker.position.Ya,
//						'latitude':marker.position.Xa
//				 };
//				var dbTee = EditCourseService.createTee(params);
//				currentHole.tees.push(dbTee);
//			} else {
//				params.holeId = currentHole.id;
//				params.$entity = {
//						'title':poiTypeName,
//						'type': {'id':poiTypeId},
//						'longitude':marker.position.Ya,
//						'latitude':marker.position.Xa
//				 };
//				var dbPoi = EditCourseService.createPoi(params);
//				currentHole.pois.push(dbPoi);
//			}
//			
//			
//		}
	}});
	
}

function createPoiFunc(i) {
	return function() { return openEditPoiDialog(this, i) };
}

function createTeeFunc(i) {
	return function() { return openEditPoiDialog(this, i) };
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
				$('#details_' + currentHole.id).text('Par ' + nullSafeStr(currentHole.par, '-') + ' Hcp ' + nullSafeStr(currentHole.hcp, '--'));
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
				$('#details_' + currentHole.id).text('Par ' + nullSafeStr(currentHole.par, '-') + ' Hcp ' + nullSafeStr(currentHole.hcp, '--'));
			});
		
			break;
		}
	}
	
	
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
		
		var iconStr = getIconStr(poi.poiType);
		
		var posStr = poi.latitude + ", " + poi.longitude;
		$('#map_canvas').gmap('addMarker', {
			'id':poi.id,
			'position': posStr, 
			'draggable': true, 
			'bounds': false,
			'title' : poi.title,
			'icon': iconStr //'images/poi.png'
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
	
	// Center map over hole using the panTo function
    if(currentHole.pois.length > 0 && currentHole.tees.length > 0) {
    	var coord = new google.maps.LatLng(intp(currentHole.tees[0].latitude, currentHole.pois[0].latitude), intp(currentHole.tees[0].longitude, currentHole.pois[0].longitude));
    	var map = $('#map_canvas').gmap('get','map');
    	map.panTo(coord);
    }
}

/**
 * Returns a proper URL string for the marker image depending on POI type.
 */
function getIconStr(poiType) {
	switch(poiType) {
		case 'FRONT_GREEN':
		case 'MID_GREEN':
		case 'BACK_GREEN':
			return 'images/poi.png';
		case 'BUNKER':
			return 'images/poi.png';
		case 'WATER':
			return 'images/poi.png';
		default:
			return 'images/poi.png';
	}
}

/**
 * 
 */
function updateAfterDrag(event, marker, type) {

	// Send update directly to server
	var params = {};
	params.$entity = {
			'id':marker.id,			 
			 'longitude':event.latLng.Ya,
			 'latitude':event.latLng.Xa
	 };
	
	if(type == 'poi') {
		EditCourseService.updatePoi(params);		
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

function openEditCourseDialog() {
	if(currentVenue == undefined || currentVenue == null) {
		alert('No golf club selected!');
		return;
	}
	
	$('#dialog').append('<form id="edit_course_dialog" method="get" action="/">' +
			'<p><label for="name">Name</label><input type="text" id="edit_course_name" value="' + currentCourse.name + '"></input></p>'+
			'<p><label for="name">Description</label><textarea id="edit_course_desc">' + currentCourse.description + '</textarea></p>' +
			// Tee form
			'<div id="tees"><label for="name">Tees</label><br/>' +
			// Tees goes here
			'</div>' +
			'</form>');
	
	// Note that we use the ID, not the index here
	if(currentCourse.tees.length > 0) {
		for(var a = 0; a < currentCourse.tees.length; a++) {
			$('#tees').append('<div id="edit_tee_div_' + (a+1) + '">Tee ' + (a+1) + ': <input type="text" id="existing_tee_' + currentCourse.tees[a].id + '" value="' + currentCourse.tees[a].name + '"/></div>')
		}
	} else {
		$('#tees').append('<div id="add_tee_div_1">Tee 1: <input type="text" id="add_tee_1"/></div>');
	}
		
	$('#edit_course_dialog').dialog({'modal':true, 'title': 'Edit course', 'buttons': { 
		"Add tee": function() {
			var existingCount = ($('#tees input').length) + 1;
			if(existingCount < 8) {
				$('#tees').append('<div id="edit_tee_div_' + existingCount + '">Tee ' + existingCount + ': <input type="text" id="add_tee_' + existingCount + '"/></div>');
			} else {
				alert('Cannot have more than ' + 7 + ' tees on a course.');
			}
		},
		"Remove tee": function() {			
			var existingCount = ($('#tees input').length);
			if(existingCount > 1) {
				$('#edit_tee_div_' + existingCount).remove();
			} else {
				alert('A course must have at least one tee');
			}
		},
		"Remove": function() {
			$(this).dialog( "close" );			
		},
		"Save": function() {
			$(this).dialog( "close" );
			var course = {
				'name' : $('#edit_course_name').val(),
				'description' : $('#edit_course_desc').val()
			};
			
			// Get all inputs for tees. If id starts 'existing_tee_' we have updated an existing one. If it starts with add_tee_, it's a new ones. As for deleted ones, we must do some kind of diff server-side
			var teeCount = ($('#tees input').length);
			course.tees = new Array();
			for(var a = 1; a <= teeCount; a++) {
				var inputElement = $('#tees input')[a-1];
				var elemId = $(inputElement).attr('id');
				if(elemId.substring(0,4) == 'add_') {
					// NEW
					var teeType = {};
					teeType.name =  $(inputElement).val();
					if(teeType.name == undefined || teeType.name == '') {
						alert('Please enter a name for each tee.');
						return;
					}
					course.tees.push(teeType);
				} else if(elemId.substring(0,9) == 'existing_') {
					// UPDATED
					// Find the existing one using the ID in the currentCourse.tees... (fugly)
							
					var teeType = {};
					teeType.id = elemId.substring(13);
					teeType.name = $(inputElement).val();
					if(teeType.name == undefined || teeType.name == '') {
						alert('Please enter a name for each tee.');
						return;
					}
					course.tees.push(teeType);
				}
			}
			
			var params = {};
			params.courseId = currentCourse.id;
			params.$entity = course;
			EditCourseService.updateCourse(params);
		}
	}});
}
	
function openAddCourseToVenueDialog() {
	if(currentVenue == undefined || currentVenue == null) {
		alert('No golf club selected!');
		return;
	}
	
	$('#dialog').append('<form id="add_course_dialog">' +
			'<p><label for="name">Name</label><input type="text" id="course_name"></input></p>'+
			'<p><label for="name">Number of holes</label><select id="course_no_holes"></select></p>' +
			'<p><label for="name">Description</label><textarea id="course_desc"></textarea></p><hr/>' +
			// Tee form
			'<div id="tees"><label for="name">Tees</label><br/>' +
			'<div id="add_tee_div_1">Tee 1: <input type="text" id="add_tee_1"/></div>' +
			'<div id="add_tee_div_2">Tee 2: <input type="text" id="add_tee_2"/></div>' +
			'</div>' +
			'</form>');
	
	for(var a = 18; a > 0; a--) {
		$('#course_no_holes').append('<option value="' + a + '">' + a + '</option>');
	}
	
	$('#add_course_dialog').dialog({'modal':true, 'title': 'Add course to this golf club', 'buttons': { 
		"Add tee": function() {
			var existingCount = ($('#tees input').length) + 1;
			if(existingCount < 8) {
				$('#tees').append('<div id="add_tee_div_' + existingCount + '">Tee ' + existingCount + ': <input type="text" id="add_tee_' + existingCount + '"/></div>');
			} else {
				alert('Cannot have more than ' + 7 + ' tees on a course.');
			}
		},
		"Remove tee": function() {			
			var existingCount = ($('#tees input').length);
			if(existingCount > 1) {
				$('#add_tee_div_' + existingCount).remove();
			} else {
				alert('A course must have at least one tee');
			}
		},
		"Remove": function() {
			$(this).dialog( "close" );			
		},
		"Save": function() {
			
			var course = {
				'name' : $('#course_name').val(),
				'description' : $('#course_desc').val(),
				'holeCount' : $('#course_no_holes  option:selected').val()
			};
			
			// Add (and validate tees)
			var teeCount = ($('#tees input').length);
			course.tees = new Array();
			for(var a = 1; a <= teeCount; a++) {
				var teeType = {};
				teeType.name = $('#add_tee_' + a).val();
				
				if(teeType.name == undefined || teeType.name == '') {
					alert('Please enter a name for each tee.');
					return;
				}
				
				course.tees.push(teeType);
			}
			
			$(this).dialog( "close" );
			
			var params = {};
			params.venueId = currentVenue.id;
			params.$entity = course;
			currentVenue.courses.push(EditCourseService.addCourseToVenue(params));
		}
	}});
}