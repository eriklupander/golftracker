package com.squeed.golftracker.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squeed.golftracker.entity.CourseDTO;
import com.squeed.golftracker.entity.MemberDTO;
import com.squeed.golftracker.helper.DbHelper;

public class RestClient {
	
	public CourseDTO syncCourse(CourseDTO c, DbHelper dbHelper) throws Exception {
		
		
			
			GsonBuilder builder = new GsonBuilder();				
			Gson gson = builder.create();
			
			String json = gson.toJson(c, CourseDTO.class);
			HttpClient hc = new DefaultHttpClient();
			HttpHost target = new HttpHost("192.168.2.5", 8080);				
			HttpPost httpPost = new HttpPost("/GolfTrackerServer/rest/course/course");

			StringEntity se = new StringEntity(json, "utf-8");				
			httpPost.setEntity(se);
			httpPost.setHeader("Content-Type", "application/json;charset=utf-8");

			Log.d("RestClient", "Execute HTTP Post");
			HttpResponse resp = hc.execute(target, httpPost);
			StringBuilder retJson = responseToString(resp);
			return gson.fromJson(retJson.toString(), CourseDTO.class);
			
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
	
	public MemberDTO getMemberDTO(Long id)  {
		try {
			HttpClient hc = new DefaultHttpClient();
			HttpHost target = new HttpHost("192.168.2.5", 8080);				
			HttpGet httpGet = new HttpGet("/GolfTrackerServer/rest/course/member/" + id);
			HttpResponse resp = hc.execute(target, httpGet);
			StringBuilder retJson = responseToString(resp);
			
			GsonBuilder builder = new GsonBuilder();				
			Gson gson = builder.create();
			
			return gson.fromJson(retJson.toString(), MemberDTO.class);
		} catch (Exception e) {
			Log.e("RestClient", "getMemberDTO failed, message: " + e.getMessage());
			return null;
		}
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

	public List<CourseDTO> getAllCourses() throws Exception {
		HttpClient hc = new DefaultHttpClient();
		HttpHost target = new HttpHost("192.168.2.5", 8080);				
		HttpGet httpGet = new HttpGet("/GolfTrackerServer/rest/course");
		HttpResponse resp = hc.execute(target, httpGet);
		StringBuilder retJson = responseToString(resp);
		
		GsonBuilder builder = new GsonBuilder();				
		Gson gson = builder.create();
		List<CourseDTO> courses = gson.fromJson(retJson.toString(), new TypeToken<List<CourseDTO>>(){}.getType());
		Log.d("RestClient", "Got " + courses.size() + " courses back from server");
		return courses;

	}

//	private StringBuilder jsonResponseToString(HttpResponse response)
//			throws IOException {
//		InputStream content = response.getEntity().getContent();
//		int read = content.read();
//		StringBuilder buf = new StringBuilder();
//		while(read != -1) {
//			buf.append((char) read);
//			read = content.read();
//		}
//		return buf;
//	}
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
}
