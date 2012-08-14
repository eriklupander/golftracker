package com.squeed.golftracker;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squeed.golftracker.common.model.Course;
import com.squeed.golftracker.helper.DbExporter;
import com.squeed.golftracker.helper.DbHelper;
import com.squeed.golftracker.rest.RestClient;

public class SyncWithDbActivity extends Activity {
	
	ProgressBar progressBar;
	int increment = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.syncwithserverdialog);
		
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		final Button startSyncBtn = (Button) findViewById(R.id.startSyncBtn);
		startSyncBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startSyncBtn.setClickable(false);
				sync();
				startSyncBtn.setClickable(true);
			}
		});
    }
	
	private void sync() {
		
		
        // create a thread for updating the progress bar
        Thread background = new Thread (new Runnable() {
           public void run() {
               try {
                   // enter the code to be run while displaying the progressbar.
                   //
                   // This example is just going to increment the progress bar:
                   // So keep running until the progress value reaches maximum value
                   while (progressBar.getProgress()<= progressBar.getMax()) {
                       // wait 500ms between each update
                       Thread.sleep(500);
 
                       // active the update handler
                       progressHandler.sendMessage(progressHandler.obtainMessage());
                   }
               } catch (java.lang.InterruptedException e) {}
           }
        });
 
        // start the background thread
        background.start();
       
        
        
        // Start updating
        
        // 1. Backup local db to sdcard
        RestClient rc = new RestClient();
		DbHelper dbHelper = new DbHelper(this);
//		List<Course> courses = dbHelper.loadCourses(dbHelper.getReadableDatabase(), null);
//        DbExporter ex = new DbExporter(new DbHelper(getApplicationContext()));
//        String data = ex.dbToCsvString();
//        ex.dataToSdCard(data, "database.csv");
//        increment = 10;
//        try {
//			
//        	// First, push all local courses to DB. (which will add new POI:s, courses etc. to the main DB)
//        	for(Course temp : courses) {
//				Course c = dbHelper.loadCourseFromDb(dbHelper.getReadableDatabase(), temp.getId());			
//				c = rc.syncCourse(c, dbHelper);
//				// Replace Course in DB
//				dbHelper.deleteCourse(dbHelper.getWritableDatabase(), c);
//				dbHelper.storeCourse(dbHelper.getWritableDatabase(), c);
//				increment += 10;
//			}
//        	
//        	// Next, load all courses on the server into the device. NOTE! This might have to change in the future due to memory reasons.
//        	// A better option might be to have a client-side flag which says if it's a shallow or deep copy. I.e. if shallow, we only keep 
//        	// the course entity locally, not loading holes and pois until the user actually wants to play/edit the course.
//        	
//        	List<Course> allCourses = rc.getAllCourses();
//        	for(Course dto : allCourses) {
//        		
//				dbHelper.storeCourse(dbHelper.getWritableDatabase(), dto);
//        	}
//		} catch (Exception e) {
//			Toast.makeText(getBaseContext(),
//	                "Fel: " + e.getMessage(),
//	                Toast.LENGTH_LONG).show();
//			Log.e("SyncWithDbActivity", "Error occured syncing with server: " + e.getMessage());
//		}
        increment = 100;
        Toast.makeText(getBaseContext(),
                "Klart!",
                Toast.LENGTH_LONG).show();
        finish();
	}
 
    // handler for the background updating
    Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
        	progressBar.setProgress(increment);        	
        }
    };
}

