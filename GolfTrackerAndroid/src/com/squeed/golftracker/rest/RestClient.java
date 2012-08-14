package com.squeed.golftracker.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squeed.golftracker.common.model.Course;
import com.squeed.golftracker.common.model.GolfVenue;
import com.squeed.golftracker.common.model.User;
import com.squeed.golftracker.helper.DbHelper;

public class RestClient {
	
	private static final String _127_0_0_1 = "127.0.0.1";
	private static ObjectMapper mapper = new ObjectMapper();
	
	public Course syncCourse(Course c, DbHelper dbHelper) throws Exception {
		
		
			
			GsonBuilder builder = new GsonBuilder();				
			Gson gson = builder.create();
			
			String json = gson.toJson(c, Course.class);
			HttpClient hc = new DefaultHttpClient();
			HttpHost target = new HttpHost("192.168.2.5", 8080);				
			HttpPost httpPost = new HttpPost("/GolfTrackerServer/rest/course/course");

			StringEntity se = new StringEntity(json, "utf-8");				
			httpPost.setEntity(se);
			httpPost.setHeader("Content-Type", "application/json;charset=utf-8");

			Log.d("RestClient", "Execute HTTP Post");
			HttpResponse resp = hc.execute(target, httpPost);
			StringBuilder retJson = responseToString(resp);
			return gson.fromJson(retJson.toString(), Course.class);
			
	}
	
	private StringBuilder responseToString(HttpResponse response)
			throws IOException {
		InputStream content = response.getEntity().getContent();
		int read = content.read();
		StringBuilder buf = new StringBuilder();
		while(read != -1) {
			buf.append((char) read);
			read = content.read();
		}
		Log.d("RestClient", "Response: " + buf.toString());
		return buf;
	}
	


	public Long registerUser(String username, String password) throws Exception  {
		HttpClient hc = new DefaultHttpClient();
		HttpHost target = new HttpHost("192.168.2.5", 8080);				
		HttpPost httpPost = new HttpPost("/GolfTrackerServer/rest/course/member");

//		StringEntity se = new StringEntity(json, "utf-8");				
//		httpPost.setEntity(se);
		httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
		httpPost.setHeader("username", username);
		httpPost.setHeader("password", password);
		
		Log.d("RestClient", "Execute HTTP Post");
		HttpResponse resp = hc.execute(target, httpPost);
		StringBuilder retJson = responseToString(resp);
		
		return Long.parseLong(retJson.toString());
	}

	public List<Course> getAllCourses() throws Exception {
		HttpClient hc = new DefaultHttpClient();
		HttpHost target = new HttpHost("192.168.2.5", 8080);				
		HttpGet httpGet = new HttpGet("/GolfTrackerServer/rest/course");
		HttpResponse resp = hc.execute(target, httpGet);
		StringBuilder retJson = responseToString(resp);
		
		GsonBuilder builder = new GsonBuilder();				
		Gson gson = builder.create();
		List<Course> courses = gson.fromJson(retJson.toString(), new TypeToken<List<Course>>(){}.getType());
		Log.d("RestClient", "Got " + courses.size() + " courses back from server");
		return courses;

	}

	public User getUser(Long userId) {
		try {
			InputStream is = get(_127_0_0_1, 8080, "/golftracker-server/rest/user/" + userId);
			return mapper.readValue(is, User.class);
		} catch (Exception e) {			
			e.printStackTrace();
			return null;
		}
	}

	private StringBuilder jsonResponseToString(HttpResponse response)
			throws IOException {
		InputStream content = response.getEntity().getContent();
		int read = content.read();
		StringBuilder buf = new StringBuilder();
		while(read != -1) {
			buf.append((char) read);
			read = content.read();
		}
		return buf;
	}
//
//	private Course jsonObjToCourse(JSONObject object)
//			throws JSONException {
//		
//		Long id = object.getLong("id");
//		Course c = new Course();
//		c.setId(id);
//		c.setName(object.getString("name"));
//		
//		
//		addHoles(object, c);
//		
//		
//		return c;
//	}
//
//	private void addHoles(JSONObject object, Course c) throws JSONException {
//		JSONArray array = object.getJSONArray("holes");
//		for(int a = 0; a < array.length(); a++) {
//			
//			JSONObject amnt = array.getJSONObject(a);
//			Hole aObj = new Hole(amnt.getLong("id"), amnt.getInt("number"), amnt.getInt("index"), amnt.getInt("par"));
//
//			addPois(amnt, aObj);
//			
//			c.getHoles().add(aObj);
//		}
//	}
//
//	private void addPois(JSONObject object, Hole aObj) throws JSONException {
//		JSONArray array = object.getJSONArray("pois");
//		for(int a = 0; a < array.length(); a++) {
//			
//			JSONObject amnt = array.getJSONObject(a);
//			
//			PointOfInterest poi = new PointOfInterest(amnt.getLong("id"),  amnt.getString("type"), amnt.getString("name"), amnt.getDouble("lat"), amnt.getDouble("lon") );
//			aObj.getPois().add(poi);
//		}
//	}
	
	private InputStream get(String serverUrl, Integer port, String path) throws ClientProtocolException, IOException {
		HttpClient hc = new DefaultHttpClient();
		HttpHost target = new HttpHost(serverUrl, port);				
		HttpGet httpGet = new HttpGet(path);
		return hc.execute(target, httpGet).getEntity().getContent();
	}

	public List<GolfVenue> getGolfVenues(Double lon, Double lat) {
		try {
			InputStream is = get(_127_0_0_1, 8080, "/golftracker-server/rest/course/nearby?lon=123&lat=345");
			return mapper.readValue(is, new TypeReference<List<GolfVenue>>(){});
		} catch (Exception e) {			
			e.printStackTrace();
			return null;
		}
	}

	public Course getCourse(long courseId) {
		try {
			InputStream is = get(_127_0_0_1, 8080, "/golftracker-server/rest/course/" + courseId);
			return mapper.readValue(is, Course.class);
		} catch (Exception e) {			
			e.printStackTrace();
			return null;
		}
	}

	public List<Course> getCoursesOfVenue(Long venueId) {
		try {
			InputStream is = get(_127_0_0_1, 8080, "/golftracker-server/rest/course/venue/" + venueId + "/courses");
			return mapper.readValue(is, new TypeReference<List<Course>>(){});
		} catch (Exception e) {			
			e.printStackTrace();
			return null;
		}
	}
}
