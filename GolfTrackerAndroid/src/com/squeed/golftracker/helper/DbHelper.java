package com.squeed.golftracker.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.squeed.golftracker.entity.ClubDTO;
import com.squeed.golftracker.entity.CourseDTO;
import com.squeed.golftracker.entity.HoleDTO;
import com.squeed.golftracker.entity.PointOfInterestDTO;
import com.squeed.golftracker.entity.ShotProfileDTO;
import com.squeed.golftracker.entity.TeeDTO;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    
    private static final String COURSE_TABLE_NAME = "course";
	private static final String KEY_ID = "id";
	private static final String KEY_EXTERNAL_ID = "external_id";
	private static final String KEY_NAME = "name";
	private static final String KEY_LON = "lon";
	private static final String KEY_LAT = "lat";
	private static final String KEY_OWNER_ID = "owner_id";
	private static final String KEY_SHALLOW_COPY = "shallow";
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 0 == OK, uploaded, no user changes.
	 * 1 == OK, uploaded, but has local changes.
	 * 2 == IN_PROGRESS. Not uploaded. Needs completion (correct HCP assignment, at least yel tee and mid green for each hole).
	 */
	private static final String KEY_STATE_CODE = "state";
	
    private static final String COURSE_TABLE_CREATE =
                "CREATE TABLE " + COURSE_TABLE_NAME + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_EXTERNAL_ID + " INTEGER, " + 
                KEY_NAME + " TEXT, " +
                KEY_STATE_CODE + " INTEGER," +
                KEY_LON + " REAL, " +
                KEY_LAT + " REAL," +
                KEY_OWNER_ID + " INTEGER, " +
                KEY_SHALLOW_COPY + " INTEGER);";
    
    
    private static final String TEE_TABLE_NAME = "tee";
    
    private static final String KEY_COURSE_ID = "course_id";
    
    private static final String TEE_TABLE_CREATE =
            "CREATE TABLE " + TEE_TABLE_NAME + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_EXTERNAL_ID + " INTEGER, " +
            KEY_COURSE_ID + " INTEGER," +
            KEY_NAME + " TEXT, " +
            KEY_OWNER_ID + " INTEGER);";
    
    private static final String HOLE_TABLE_NAME = "hole";
	
	private static final String KEY_NUMBER = "number";
	private static final String KEY_INDEX = "idx";
	private static final String KEY_PAR = "par";
	
	
    private static final String HOLE_TABLE_CREATE =
                "CREATE TABLE " + HOLE_TABLE_NAME + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_EXTERNAL_ID + " INTEGER, " + 
                KEY_NAME + " TEXT, " +
                KEY_NUMBER + " INTEGER, " +
                KEY_INDEX + " INTEGER, " +
                KEY_PAR + " INTEGER, " +
                KEY_COURSE_ID + " INTEGER," +
                KEY_OWNER_ID + " INTEGER);";
    
    
    
    
    private static final String POI_TABLE_NAME = "poi";
	
    private static final String KEY_HOLE_ID = "hole_id";
	private static final String KEY_TYPE = "type";
	
	
    private static final String POI_TABLE_CREATE =
            "CREATE TABLE " + POI_TABLE_NAME + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_EXTERNAL_ID + " INTEGER, " + 
            KEY_HOLE_ID + " INTEGER, " +
            KEY_TYPE + " TEXT, " +
            KEY_NAME + " TEXT, " +
            KEY_LON + " REAL, " +
            KEY_LAT + " REAL," +
            KEY_OWNER_ID + " INTEGER);";
    
    private static final String SHOT_TABLE_NAME = "shot";

	private static final String KEY_CLUB_ID = "club_id";
	private static final String KEY_LENGTH = "shot_len";
	private static final String KEY_START_LON = "start_lon";
	private static final String KEY_START_LAT = "start_lat";
	private static final String KEY_END_LON = "end_lon";
	private static final String KEY_END_LAT = "end_lat";
	private static final String KEY_SHOT_PROFILE_ID = "shot_profile_id";
    
    private static final String SHOT_TABLE_CREATE =
            "CREATE TABLE " + SHOT_TABLE_NAME + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_EXTERNAL_ID + " INTEGER, " + 
            KEY_HOLE_ID + " INTEGER, " +
            KEY_CLUB_ID + " INTEGER, " +
            KEY_SHOT_PROFILE_ID + " INTEGER, " + 
            KEY_LENGTH + " TEXT, " +
            KEY_START_LON + " REAL, " +
            KEY_START_LAT + " REAL," +
            KEY_END_LON + " REAL, " +
            KEY_END_LAT + " REAL," +
            KEY_OWNER_ID + " INTEGER);";
    
    
    private static final String CLUB_TABLE_NAME = "club";
    
    private static final String KEY_EST_LEN = "est_len";

	private static final String KEY_LONG_NAME = "long_name";

	private static final String KEY_ICON_ID = "icon_id"; // These are hard-coded...
   
    private static final String CLUB_TABLE_CREATE =
            "CREATE TABLE " + CLUB_TABLE_NAME + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_EXTERNAL_ID + " INTEGER, " + 
            KEY_NAME + " TEXT, " + 
            KEY_LONG_NAME + " TEXT, " + 
            KEY_EST_LEN + " INTEGER, "+
            KEY_ICON_ID + " INTEGER, "+
            KEY_OWNER_ID + " INTEGER);";
    
    
    private static final String SHOT_PROFILE_TABLE_NAME = "shot_profile";
       
    private static final String SHOT_PROFILE_TABLE_CREATE =
            "CREATE TABLE " + SHOT_PROFILE_TABLE_NAME + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_EXTERNAL_ID + " INTEGER, " + 
            KEY_NAME + " TEXT, " +
            KEY_ICON_ID + " INTEGER);";
    
    
    private static final String DRIVE_RESULT_TABLE_NAME = "drive_result";
    
    private static final String DRIVE_RESULT_TABLE_CREATE =
            "CREATE TABLE " + DRIVE_RESULT_TABLE_NAME + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_EXTERNAL_ID + " INTEGER, " + 
            KEY_NAME + " TEXT, " +
            KEY_ICON_ID + " INTEGER);";
    
    private static final String ROUND_TABLE_NAME = "round";

	private static final String KEY_DATE = "created";
	private static final String KEY_TEE_ID = "tee_id";
    
    private static final String ROUND_TABLE_CREATE =
            "CREATE TABLE " + ROUND_TABLE_NAME + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_EXTERNAL_ID + " INTEGER, " + 
            KEY_DATE + " TEXT, " +
            KEY_COURSE_ID + " INTEGER, " +
            KEY_TEE_ID + " INTEGER);";    
    
    
    private static final String HOLE_SCORE_TABLE_NAME = "hole_score";

	private static final String KEY_ROUND_ID = "round_id";

	private static final String KEY_SCORE = "score";
	private static final String KEY_PUTTS = "putts";
	private static final String KEY_BUNKER = "bunker_shots";
	private static final String KEY_PENALTY = "penalties";
	private static final String KEY_DRIVE_CLUB_ID = "drive_club_id";
	private static final String KEY_DRIVE_RESULT_ID = "drive_result_id";
    
    private static final String HOLE_SCORE_TABLE_CREATE =
            "CREATE TABLE " + HOLE_SCORE_TABLE_NAME + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_EXTERNAL_ID + " INTEGER, " + 
            KEY_DATE + " TEXT, " +
            KEY_HOLE_ID + " INTEGER, " +
            KEY_ROUND_ID + " INTEGER, " +
            KEY_SCORE + " INTEGER, " +
            KEY_PUTTS + " INTEGER, " +
            KEY_BUNKER + " INTEGER, " +
            KEY_PENALTY + " INTEGER, " +
            KEY_DRIVE_CLUB_ID + " INTEGER, " +
            KEY_DRIVE_RESULT_ID + " INTEGER, " +
            KEY_OWNER_ID + " INTEGER);";    
    
	private static final String DATABASE_NAME = "GolfTracker";

	private static final String YELLOW_TEE_KEY = "yt";
	private static final String RED_TEE_KEY = "rt";
	private static final String FRONT_GREEN_KEY = "fg";
	private static final String MID_GREEN_KEY = "mg";
	private static final String BACK_GREEN_KEY = "bg";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	
    	db.execSQL(TEE_TABLE_CREATE);
        db.execSQL(COURSE_TABLE_CREATE);
        db.execSQL(HOLE_TABLE_CREATE);
        db.execSQL(POI_TABLE_CREATE);
        
        db.execSQL(CLUB_TABLE_CREATE);
        db.execSQL(DRIVE_RESULT_TABLE_CREATE);
        db.execSQL(SHOT_TABLE_CREATE);
        db.execSQL(SHOT_PROFILE_TABLE_CREATE);
        
        db.execSQL(ROUND_TABLE_CREATE);
        db.execSQL(HOLE_SCORE_TABLE_CREATE);
        
        db.execSQL("INSERT INTO " + COURSE_TABLE_NAME + " VALUES(1, -1, 'Sotenäs GK Gul/Röd', 2, 11.380046, 58.435646, -1, 0);");
        
        db.execSQL("INSERT INTO " + TEE_TABLE_NAME + " VALUES(1, -1, 1, 'Gul', -1);");
        db.execSQL("INSERT INTO " + TEE_TABLE_NAME + " VALUES(2, -1, 1, 'Röd', -1);");
        
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(1,-1,'Gul 1', 1, 7, 4, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(2,-1,'Gul 2', 2, 13, 3, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(3,-1,'Gul 3', 3, 3, 5, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(4,-1,'Gul 4', 4, 9, 5, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(5,-1,'Gul 5', 5, 15, 3, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(6,-1,'Gul 6', 6, 5, 5, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(7,-1,'Gul 7', 7, 11, 4, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(8,-1,'Gul 8', 8, 17, 3, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(9,-1,'Gul 9', 9, 1, 4, 1, -1);");
        
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(10,-1,'Röd 1', 10, 17, 3, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(11,-1,'Röd 2', 11, 4, 5, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(12,-1,'Röd 3', 12, 2, 3, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(13,-1,'Röd 4', 13, 16, 4, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(14,-1,'Röd 5', 14, 14, 3, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(15,-1,'Röd 6', 15, 6, 4, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(16,-1,'Röd 7', 16, 12, 4, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(17,-1,'Röd 8', 17, 10, 5, 1, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(18,-1,'Röd 9', 18, 8, 4, 1, -1);");
        
        db.execSQL("INSERT INTO " + POI_TABLE_NAME + " VALUES(1,-1, 1, '" + YELLOW_TEE_KEY + "', 'Gul tee', 11.380046, 58.435646, -1);");
        db.execSQL("INSERT INTO " + POI_TABLE_NAME + " VALUES(2,-1, 1, '" + RED_TEE_KEY + "', 'Röd tee', 11.379507, 58.435499, -1);");
        db.execSQL("INSERT INTO " + POI_TABLE_NAME + " VALUES(3,-1, 1, '" + FRONT_GREEN_KEY + "', 'Framkant green', 11.376318, 58.433425, -1);");
        db.execSQL("INSERT INTO " + POI_TABLE_NAME + " VALUES(4,-1, 1, '" + MID_GREEN_KEY + "', 'Mitten green', 11.376281, 58.433306, -1);");
        db.execSQL("INSERT INTO " + POI_TABLE_NAME + " VALUES(5,-1, 1, '" + BACK_GREEN_KEY + "', 'Bakkant green', 11.376262, 58.433244, -1);");
        
        db.execSQL("INSERT INTO " + POI_TABLE_NAME + " VALUES(6,-1, 2 , '" + YELLOW_TEE_KEY + "', 'Gul tee', 11.377222, 58.432922, -1);");
        db.execSQL("INSERT INTO " + POI_TABLE_NAME + " VALUES(7,-1, 2, '" + RED_TEE_KEY + "', 'Röd tee', 11.376911, 58.43295, -1);");
        db.execSQL("INSERT INTO " + POI_TABLE_NAME + " VALUES(8,-1, 2, '" + FRONT_GREEN_KEY + "', 'Framkant green', 11.375076, 58.432729, -1);");
        db.execSQL("INSERT INTO " + POI_TABLE_NAME + " VALUES(9,-1, 2, '" + MID_GREEN_KEY + "', 'Mitten green', 11.374775, 58.432703, -1);");
        db.execSQL("INSERT INTO " + POI_TABLE_NAME + " VALUES(10,-1, 2, '" + BACK_GREEN_KEY + "', 'Bakkant green', 11.374497, 58.432647, -1);");
    
        db.execSQL("INSERT INTO " + COURSE_TABLE_NAME + " VALUES(2, -1, 'Testbanan', 2, 11.268512, 58.528761, -1, 0);");
        
        db.execSQL("INSERT INTO " + TEE_TABLE_NAME + " VALUES(3, -1, 2, 'Tee 57', -1);");
        db.execSQL("INSERT INTO " + TEE_TABLE_NAME + " VALUES(4, -1, 2, 'Tee 49', -1);");
        db.execSQL("INSERT INTO " + TEE_TABLE_NAME + " VALUES(5, -1, 2, 'Tee 44', -1);");
        
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(19,-1,'Huset', 1, 3, 4, 2, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(20,-1,'Parkeringen', 2, 17, 3, 2, -1);");
        db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " VALUES(21,-1,'Bryggan', 3, 3, 5, 2, -1);");
        
        // Create a default club set.
        db.execSQL("INSERT INTO " + CLUB_TABLE_NAME + " VALUES(1,-1, 'fw-1','Driver',230, 0, -1);");
        db.execSQL("INSERT INTO " + CLUB_TABLE_NAME + " VALUES(2,-1, 'fw-5','Trä-5',210,1, -1);");
        db.execSQL("INSERT INTO " + CLUB_TABLE_NAME + " VALUES(3,-1, 'fw-7','Trä-7',185,1, -1);");
        db.execSQL("INSERT INTO " + CLUB_TABLE_NAME + " VALUES(4,-1, 'hy-5','Hybrid-5',165,2, -1);");
        db.execSQL("INSERT INTO " + CLUB_TABLE_NAME + " VALUES(5,-1, 'i-5','Järn-5',160,3,-1);");
        db.execSQL("INSERT INTO " + CLUB_TABLE_NAME + " VALUES(6,-1, 'i-6','Järn-6',155,3,-1);");
        db.execSQL("INSERT INTO " + CLUB_TABLE_NAME + " VALUES(7,-1, 'i-7','Järn-7',145,3,-1);");
        db.execSQL("INSERT INTO " + CLUB_TABLE_NAME + " VALUES(8,-1, 'i-8','Järn-8',135,3,-1);");
        db.execSQL("INSERT INTO " + CLUB_TABLE_NAME + " VALUES(9,-1, 'i-9','Järn-9',120,3,-1);");
        db.execSQL("INSERT INTO " + CLUB_TABLE_NAME + " VALUES(10,-1, 'pw','Pitching Wedge',100,3,-1);");
        db.execSQL("INSERT INTO " + CLUB_TABLE_NAME + " VALUES(11,-1, 'sw','Sand Wedge',60,4,-1);");
        db.execSQL("INSERT INTO " + CLUB_TABLE_NAME + " VALUES(12,-1, '58w','58gr wedge',45,4,-1);");
        
        
        db.execSQL("INSERT INTO " + SHOT_PROFILE_TABLE_NAME + " VALUES(1,-1, 'Hook', 5);");
        db.execSQL("INSERT INTO " + SHOT_PROFILE_TABLE_NAME + " VALUES(2,-1, 'Draw', 5);");
        db.execSQL("INSERT INTO " + SHOT_PROFILE_TABLE_NAME + " VALUES(3,-1, 'Rak', 6);");
        db.execSQL("INSERT INTO " + SHOT_PROFILE_TABLE_NAME + " VALUES(4,-1, 'Fade', 7);");
        db.execSQL("INSERT INTO " + SHOT_PROFILE_TABLE_NAME + " VALUES(5,-1, 'Slice', 7);");
        
        db.execSQL("INSERT INTO " + DRIVE_RESULT_TABLE_NAME + " VALUES(1,-1, 'Vänster', 5);");
        db.execSQL("INSERT INTO " + DRIVE_RESULT_TABLE_NAME + " VALUES(2,-1, 'Rak', 5);");
        db.execSQL("INSERT INTO " + DRIVE_RESULT_TABLE_NAME + " VALUES(3,-1, 'Höger', 6);");
        db.execSQL("INSERT INTO " + DRIVE_RESULT_TABLE_NAME + " VALUES(4,-1, 'Kort', 7);");
        db.execSQL("INSERT INTO " + DRIVE_RESULT_TABLE_NAME + " VALUES(5,-1, 'Lång', 7);");
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public List<ClubDTO> getClubs(SQLiteDatabase db) {
		Cursor q = db.rawQuery("SELECT id, external_id, name, long_name, " + KEY_EST_LEN + ", icon_id, owner_id from " + CLUB_TABLE_NAME + ";", null);
		q.moveToFirst();
		List<ClubDTO> clubs = new ArrayList<ClubDTO>();
		while(!q.isAfterLast()) {
			clubs.add(new ClubDTO(q.getLong(0), q.getLong(1), q.getString(2), q.getString(3), q.getInt(4), q.getInt(5)));
			q.moveToNext();
		}
		return clubs;
	}
	
	
	public List<ShotProfileDTO> getShotProfiles(SQLiteDatabase db) {
		Cursor q = db.rawQuery("SELECT id, external_id, name, icon_id from " + SHOT_PROFILE_TABLE_NAME + ";", null);
		q.moveToFirst();
		List<ShotProfileDTO> shotProfiles = new ArrayList<ShotProfileDTO>();
		while(!q.isAfterLast()) {
			shotProfiles.add(new ShotProfileDTO(q.getLong(0), q.getLong(1), q.getString(2), q.getInt(3)));
			q.moveToNext();
		}
		return shotProfiles;
	}
	
	
	public Long startRound(SQLiteDatabase db, Long courseId, Long teeId, Long ownerId) {
		ContentValues m = new ContentValues();
		m.put(KEY_COURSE_ID, courseId);
		m.put(KEY_TEE_ID, teeId);
		m.put(KEY_DATE, sdf.format(new Date()));		
		m.put(KEY_OWNER_ID, ownerId);
		m.put(KEY_EXTERNAL_ID, -1L);
		
		long id = db.insert(ROUND_TABLE_NAME, null, m);
		
		CourseDTO course = loadCourseFromDb(db, courseId);
		
		for(int i = 0; i < course.getHoles().size(); i++) {
			// Add a score-holder for each hole right away.
			HoleDTO hole = course.getHoles().get(i);
			
			m = new ContentValues();
			m.put(KEY_HOLE_ID, hole.getId());
			m.put(KEY_EXTERNAL_ID, -1L);
			m.put(KEY_ROUND_ID, id);
			m.put(KEY_OWNER_ID, ownerId);
			db.insert(HOLE_SCORE_TABLE_NAME, null, m);
		}
		db.close();
		return -1L;
	}
	
	
	public void saveHoleScore(SQLiteDatabase db, Long holeId, Long roundId, Integer score, Integer putts, Integer bunkerShots, Integer penalties, Long clubId, Long driveResultId) {
		
		ContentValues m = new ContentValues();
		m.put(KEY_PUTTS, putts);
		m.put(KEY_SCORE, score);
		m.put(KEY_DATE, sdf.format(new Date()));		
		m.put(KEY_BUNKER, bunkerShots);
		m.put(KEY_PENALTY, penalties);
		m.put(KEY_DRIVE_CLUB_ID, clubId);
		m.put(KEY_DRIVE_RESULT_ID, driveResultId);
		
		db.update(HOLE_SCORE_TABLE_NAME, m, "WHERE " + KEY_HOLE_ID + "=? AND " + KEY_ROUND_ID + "=?", new String[]{holeId.toString(), roundId.toString()});
		
	}
	
	
	public CourseDTO loadCourseFromDb(SQLiteDatabase db, Long courseId) {
		CourseDTO c = new CourseDTO();
		
		// Get course name
		Cursor courseQuery = db.rawQuery("SELECT id, external_id, name, lat, lon, owner_id, shallow FROM " + COURSE_TABLE_NAME + " WHERE id=?", new String[]{courseId.toString()});
		courseQuery.moveToFirst();
		c.setId(courseQuery.getLong(0));
		c.setExternalId(courseQuery.getLong(1));
		c.setName(courseQuery.getString(2));
		c.setLat(courseQuery.getDouble(3));
		c.setLon(courseQuery.getDouble(4));
		c.setOwnerId(courseQuery.getLong(5));
		c.setShallow(courseQuery.getInt(6));
		courseQuery.close();
		
		// Add holes to course.
		addHoles(db, courseId, c);
		
		// For every loaded  hole, load POI
		addPois(db, c);
		
		// Add tees to course.
		addTees(db, courseId, c);
		
		db.close();
		return c;
	}

	private void addHoles(SQLiteDatabase db, Long courseId, CourseDTO c) {
		Cursor holesQuery = db.rawQuery("SELECT id, number, name, idx, par, owner_id FROM " + HOLE_TABLE_NAME + " WHERE course_id=? ORDER BY number ASC", new String[]{courseId.toString()});
		holesQuery.moveToFirst();
		while(!holesQuery.isAfterLast()) {
			HoleDTO h = new HoleDTO(
					holesQuery.getLong(0), 
					holesQuery.getInt(1), 
					holesQuery.getString(2),
					holesQuery.getInt(3), 
					holesQuery.getInt(4));
			h.setOwnerId(holesQuery.getLong(5));
			c.getHoles().add(h);
			holesQuery.moveToNext();
		}
		holesQuery.close();
	}

	private void addPois(SQLiteDatabase db, CourseDTO c) {
		for(HoleDTO h : c.getHoles()) {
			Cursor poiQuery = db.rawQuery("SELECT id, type, name, lat, lon, owner_id, external_id FROM " + POI_TABLE_NAME + " WHERE hole_id=?", new String[]{h.getId().toString()});
			poiQuery.moveToFirst();
			while(!poiQuery.isAfterLast()) {
				PointOfInterestDTO poi = new PointOfInterestDTO(poiQuery.getLong(0), poiQuery.getString(1), poiQuery.getString(2), poiQuery.getDouble(3), poiQuery.getDouble(4));
				poi.setOwnerId(poiQuery.getLong(5));
				poi.setExternalId(poiQuery.getLong(6));
				h.getPois().add(poi);
				poiQuery.moveToNext();
			}
			poiQuery.close();
		}
	}

	private void addTees(SQLiteDatabase db, Long courseId, CourseDTO c) {
		Cursor teesQuery = db.rawQuery("SELECT id, external_id, name, owner_id FROM " + TEE_TABLE_NAME + " WHERE course_id=? ORDER BY name ASC", new String[]{courseId.toString()});
		teesQuery.moveToFirst();
		while(!teesQuery.isAfterLast()) {
			TeeDTO t = new TeeDTO(
					teesQuery.getLong(0), 
					teesQuery.getLong(1), 
					teesQuery.getString(2),
					teesQuery.getLong(3)
					);
			
			c.getTees().add(t);
			teesQuery.moveToNext();
		}
		teesQuery.close();
	}
	
	public HoleDTO loadHole(SQLiteDatabase db, Long id) {
		Cursor holeQuery = db.rawQuery("SELECT id, number, name, idx, par, owner_id FROM " + HOLE_TABLE_NAME + " WHERE id=? ORDER BY number ASC", new String[]{id.toString()});
		holeQuery.moveToFirst();
		
		HoleDTO h = new HoleDTO(
		holeQuery.getLong(0), 
		holeQuery.getInt(1), 
		holeQuery.getString(2),
		holeQuery.getInt(3), 
		holeQuery.getInt(4));
		h.setOwnerId(holeQuery.getLong(5));
		holeQuery.close();
		db.close();
		return h;
	}

	public List<CourseDTO> loadCourses(SQLiteDatabase db, String term) {
		List<CourseDTO> courses = new ArrayList<CourseDTO>();
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT id, external_id, name, lat, lon, owner_id, shallow FROM " + COURSE_TABLE_NAME);
		if(term != null && term.trim().length() > 0) {
			sql.append(" WHERE name like '" + term + "%'");
		}
		sql.append(" ORDER BY name ASC");
		
		Cursor courseQuery = db.rawQuery(sql.toString(), null);
		courseQuery.moveToFirst();
		while(!courseQuery.isAfterLast()) {
			CourseDTO c = new CourseDTO();
			c.setId(courseQuery.getLong(0));
			c.setExternalId(courseQuery.getLong(1));
			c.setName(courseQuery.getString(2));
			c.setLat(courseQuery.getDouble(3));
			c.setLon(courseQuery.getDouble(4));
			c.setOwnerId(courseQuery.getLong(5));
			c.setShallow(courseQuery.getInt(6));
			courses.add(c);
			courseQuery.moveToNext();
		}
		
		courseQuery.close();
		db.close();
		return courses;
		
		
	}

	public Long createNewCourse(SQLiteDatabase db, String name, Integer numOfHoles, Long ownerId) {
		// Get id
		
		Cursor rawQuery = db.rawQuery("SELECT MAX(id) FROM " + COURSE_TABLE_NAME, null);
		
		Long newId=null;
		if(rawQuery.moveToFirst()) {
			long long1 = rawQuery.getLong(0);
			newId = long1+1;
		}
		rawQuery.close();
		if(newId == null) {
			return -1L;
		}
		
		db.execSQL("INSERT INTO " + COURSE_TABLE_NAME + " (" + KEY_ID + "," + KEY_NAME + "," + KEY_STATE_CODE + "," + KEY_OWNER_ID + "," + KEY_SHALLOW_COPY + ") VALUES(" + newId + ",'" + name + "', 2, " + ownerId + ",0);");
		
		for(int a = 0; a < numOfHoles; a++) {
			db.execSQL("INSERT INTO " + HOLE_TABLE_NAME + " (" + KEY_COURSE_ID + "," + KEY_NUMBER + "," + KEY_OWNER_ID + ") VALUES(" + newId + "," + (a+1) + "," + ownerId + ");");
		}
		db.close();
		return newId;
	}
	
	
	
	public void updateCoursePosition(SQLiteDatabase db, Long courseId, Double lat, Double lon) {
		db.execSQL("UPDATE " + COURSE_TABLE_NAME + " SET " + KEY_LAT + "=" + lat + ", " + KEY_LON + "=" + lon + " WHERE " + KEY_ID + "=" + courseId + ";");
		db.close();
	}

	public void updateHole(SQLiteDatabase db, HoleDTO currentHole) {
		db.execSQL("UPDATE " + HOLE_TABLE_NAME + " SET " + KEY_INDEX + "=" + currentHole.getIndex() + ", " + KEY_PAR + "=" + currentHole.getPar() + " " +
				"WHERE id=" + currentHole.getId() + ";");
		db.close();
	}

	
	public long createNewPoi(SQLiteDatabase db, Long holeId,
			Location gpsLocation, String type, String name, Long ownerId) {
		ContentValues m = new ContentValues();
		m.put(KEY_HOLE_ID, holeId);
		m.put(KEY_TYPE, type);
		m.put(KEY_NAME, name);
		m.put(KEY_LON, gpsLocation.getLongitude());
		m.put(KEY_LAT, gpsLocation.getLatitude());
		m.put(KEY_OWNER_ID, ownerId);
		m.put(KEY_EXTERNAL_ID, -1L);
		
		long id = db.insert(POI_TABLE_NAME, null, m);
		
		db.close();
		return id;
	}
	
	public long createShot(SQLiteDatabase db, Long holeId, Long clubId, Long shotProfileId, Double startLon, Double startLat, Double endLon, Double endLat, Long ownerId) {
		ContentValues m = new ContentValues();
		m.put(KEY_HOLE_ID, holeId);
		m.put(KEY_CLUB_ID, clubId);
		m.put(KEY_SHOT_PROFILE_ID, shotProfileId);
		m.put(KEY_START_LON, startLon);
		m.put(KEY_START_LAT, startLat);
		m.put(KEY_END_LON, endLon);
		m.put(KEY_END_LAT, endLat);
		m.put(KEY_LENGTH, LongLatConverter.getDistance(startLon, startLat, endLon, endLat));
		m.put(KEY_OWNER_ID, ownerId);
		m.put(KEY_EXTERNAL_ID, -1L);
		
		long id = db.insert(SHOT_TABLE_NAME, null, m);
		
		db.close();
		return id;
	}

	public void deletePoi(SQLiteDatabase db, PointOfInterestDTO poi, Long ownerId) {
		if(!poi.getOwnerId().equals(ownerId)) {
			// Cannot delete stuff not created by yourself, even locally.
			return;
		}
		db.execSQL("DELETE FROM " + POI_TABLE_NAME + " WHERE " + KEY_ID + "=" + poi.getId() + ";");
		db.close();
	}

	public void updatePoiName(SQLiteDatabase db, Long id,
			String name, Long poiOwnerId, Long ownerId) {
		if(!poiOwnerId.equals(ownerId)) {
			// Cannot delete stuff not created by yourself, even locally.
			return;
		}
		db.execSQL("UPDATE " + POI_TABLE_NAME + " SET " + KEY_NAME + "='" + name + "' WHERE id=" + id + ";");
		db.close();
	}

	/**
	 * NOTE! This is a full cascading delete. All holes and poi:s are deleted as well!
	 * @param db
	 * @param dto
	 */
	public void deleteCourse(SQLiteDatabase db, CourseDTO dto) {
		
		for(HoleDTO h : dto.getHoles()) {
			for(PointOfInterestDTO poi : h.getPois()) {
				db.execSQL("DELETE FROM " + POI_TABLE_NAME + " WHERE " + KEY_ID + "=" + poi.getId() + ";");
			}
			db.execSQL("DELETE FROM " + HOLE_TABLE_NAME + " WHERE " + KEY_ID + "=" + h.getId() + ";");
		}
		db.execSQL("DELETE FROM " + COURSE_TABLE_NAME + " WHERE " + KEY_ID + "=" + dto.getId() + ";");
		db.close();
	}

	public void storeCourse(SQLiteDatabase db,
			CourseDTO dto) {
		boolean isShallow = dto.getHoles() == null || dto.getHoles().size() == 0;
		boolean courseExists = false;
		// Check if course exists and whether the local one is shallow or not
		courseExists = objectExists(COURSE_TABLE_NAME, dto.getExternalId(), db);
		ContentValues cm = new ContentValues();
		
		cm.put(KEY_NAME, dto.getName());
		cm.put(KEY_LON, dto.getLon());
		cm.put(KEY_LAT, dto.getLat());
		cm.put(KEY_EXTERNAL_ID, dto.getExternalId());
		cm.put(KEY_OWNER_ID, dto.getOwnerId());
		cm.put(KEY_SHALLOW_COPY, (isShallow ? 1 : 0));
		
		if(courseExists) {
			db.update(COURSE_TABLE_NAME, cm, "externalId=?", new String[]{dto.getExternalId().toString()});
		} else {
			db.insert(COURSE_TABLE_NAME, null, cm);
		}
		
		
		for(HoleDTO h : dto.getHoles()) {
			boolean holeExists = objectExists(HOLE_TABLE_NAME, dto.getExternalId(), db);
			ContentValues hm = new ContentValues();
			hm.put(KEY_COURSE_ID, dto.getId());
			hm.put(KEY_NUMBER, h.getNumber());
			hm.put(KEY_NAME, h.getName());
			hm.put(KEY_PAR, h.getPar());
			hm.put(KEY_INDEX, h.getIndex());
			hm.put(KEY_EXTERNAL_ID, h.getExternalId());
			hm.put(KEY_OWNER_ID, h.getOwnerId());
			if(holeExists) {
				db.update(HOLE_TABLE_NAME, hm, "externalId=?", new String[]{h.getExternalId().toString()});
			} else {
				db.insert(HOLE_TABLE_NAME, null, hm);
			}
			
			for(PointOfInterestDTO poi : h.getPois()) {
				boolean poiExists = objectExists(POI_TABLE_NAME, poi.getExternalId(), db);
				ContentValues pm = new ContentValues();
				pm.put(KEY_HOLE_ID, h.getId());
				pm.put(KEY_TYPE, poi.getType());
				pm.put(KEY_NAME, poi.getName());
				pm.put(KEY_LON, poi.getLon());
				pm.put(KEY_LAT, poi.getLat());
				pm.put(KEY_EXTERNAL_ID, poi.getExternalId());
				pm.put(KEY_OWNER_ID, poi.getOwnerId());
				
				if(poiExists) {
					db.update(POI_TABLE_NAME, pm, "externalId=?", new String[]{poi.getExternalId().toString()});
				} else {
					db.insert(POI_TABLE_NAME, null, pm);
				}
			}			
		}
		db.close();
	}

	private boolean objectExists(String tableName, Long externalId,
			SQLiteDatabase db) {
		Cursor query = db.query(tableName, new String[]{"id"}, KEY_EXTERNAL_ID + "=?", new String[]{externalId.toString()}, null, null, null);
		return query.moveToFirst();
	}

	public long addTeeToCourse(SQLiteDatabase db, Long courseId,
			String teeName) {

		ContentValues m = new ContentValues();
		m.put(KEY_COURSE_ID, courseId);
		m.put(KEY_NAME, teeName);
		m.put(KEY_OWNER_ID, -1L);
		m.put(KEY_EXTERNAL_ID, -1L);
		
		long id = db.insert(TEE_TABLE_NAME, null, m);
		
		db.close();
		return id;
	}
}