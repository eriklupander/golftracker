package com.squeed.golftracker.helper;

import com.squeed.ui.MyListItem;

/**
 * Add hard-coded stuff here...
 * @author Erik
 *
 */
public class StaticDataStore {

	
	
	public static MyListItem[] getHoleResult(int par) {
		String[] resultStr = null;
		
		switch(par) {
		case 3:
			resultStr = new String[]{"HIO", "Birdie", "Par", "Bogey", "Dbl Bogey", "Trpl Bogey", "Quad Bogey"};
			return generateScoreList(resultStr);
		case 4:
			resultStr = new String[]{"HIO", "Eagle", "Birdie", "Par", "Bogey", "Dbl Bogey", "Trpl Bogey", "Quad Bogey"};
			return generateScoreList(resultStr);
		case 5:
			resultStr = new String[]{"HIO", "Albatross","Birdie", "Par", "Bogey", "Dbl Bogey", "Trpl Bogey", "Quad Bogey"};
			return generateScoreList(resultStr);
		case 6:
			resultStr = new String[]{"HIO", "Dbl Albatross","Albatross", "Birdie", "Par", "Bogey", "Dbl Bogey", "Trpl Bogey", "Quad Bogey"};
			return generateScoreList(resultStr);
		}
		
		return null;
	}

	private static MyListItem[] generateScoreList(String[] resultStr) {
		MyListItem[] res;
		res = new  MyListItem[resultStr.length];
		for(int i=0; i < resultStr.length; i++) {
			res[i] = new MyListItem(new Long(i+1), resultStr[i], "" + (i+1));
		}
		return res;
	}

	public static MyListItem[] getPutts(int par, int currentScore) {
		// E.g if we're playing par 4 and currentscore is 4, we can have a max of 3 putts.
		MyListItem[] res = new  MyListItem[currentScore];
		for(int i=0; i < currentScore; i++) {
			res[i] = new MyListItem(new Long(i), ""+i, null);
		}
		return res;
	}

	/**
	 * Returns a list with values from MIN to MAX.
	 * @param min
	 * @param max
	 * @return
	 */
	public static MyListItem[] getNumbers(int min, int max) {
		MyListItem[] res = new  MyListItem[max - min];
		for(int i=0; i < (max - min); i++) {
			res[i] = new MyListItem(new Long(i), "" + (min+i), null);
		}
		return res;
	}

}
