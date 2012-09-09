package com.squeed.golftracker;

import android.app.Application;

import com.squeed.golftracker.common.model.Round;
import com.squeed.golftracker.common.model.User;

/**
 * Consider this something like a Android "session" object.
 * @author Erik
 *
 */
public class GolfTrackerAppContext extends Application {

	private User loggedInUser;
	private Round currentRound;
	
	public User getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public Round getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(Round currentRound) {
		this.currentRound = currentRound;
	}
}
