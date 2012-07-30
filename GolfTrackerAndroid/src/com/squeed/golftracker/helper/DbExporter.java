package com.squeed.golftracker.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import android.os.Environment;

import com.squeed.golftracker.entity.CourseDTO;
import com.squeed.golftracker.entity.HoleDTO;
import com.squeed.golftracker.entity.PointOfInterestDTO;

public class DbExporter {
	
	private DbHelper dbHelper;
	
	public DbExporter(DbHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	
	
	public String dbToCsvString() {
		List<CourseDTO> loadCourses = dbHelper.loadCourses(dbHelper.getReadableDatabase(), null);
		
		StringBuilder cbuf = new StringBuilder();
		cbuf.append("id;name;state;lat;lon\n");
		
		StringBuilder hbuf = new StringBuilder();
		hbuf.append("id;course_id;number;par;index\n");
		
		StringBuilder pbuf = new StringBuilder();
		pbuf.append("id;hole_id;type;name;lat;lon\n");
		
		for(CourseDTO temp : loadCourses) {
			CourseDTO c = dbHelper.loadCourseFromDb(dbHelper.getReadableDatabase(), temp.getId());
			cbuf.append(c.toCsvString() + "\n");
			for(HoleDTO h : c.getHoles()) {
				hbuf.append(((HoleDTO)h).toCsvString(c.getId()) + "\n");
				for(PointOfInterestDTO poi : h.getPois()) {
					pbuf.append(((PointOfInterestDTO) poi).toCsvString(h.getId()) + "\n");
				}
			}
		}
		
		StringBuilder dump = new StringBuilder();
		dump.append(cbuf.toString()).append("\n\n");
		dump.append(hbuf.toString()).append("\n\n");
		dump.append(pbuf.toString()).append("\n\n");
		
		
		return dump.toString();
	}
	
	public void dataToSdCard(String data, String fileName) {
		//FileContainer profileCv = compDbFacade.getProfileCv(profileId);
		
		// Write the CV to the SD card first...
		//String base64data = profileCv.getBase64file();
		try {
	
			
			File directory = new File(Environment.getExternalStorageDirectory() + "/GolfTracker/");
			if(!directory.exists()) {
				directory.mkdir();
			}
			BufferedWriter fos = 
					new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(directory.getAbsolutePath() + "/" + fileName)),"UTF8"));
			//FileOutputStream fos = new FileOutputStream(new File(directory.getAbsolutePath() + "/" + fileName));
			fos.write(data);
			fos.flush();
			fos.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void dumpToSqlInserts() {
		
	}
}
