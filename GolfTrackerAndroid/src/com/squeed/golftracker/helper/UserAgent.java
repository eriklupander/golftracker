package com.squeed.golftracker.helper;

public class UserAgent {
	
	public static final String USER_ID_KEY = "userId";
	/**
	 * The default value. Unless the GolfTracker user registers a proper account on server, this "local" user will be used. No data created by -1
	 * can be pushed to the server.
	 */
	public static Long OWNER_ID = -1L;
}
