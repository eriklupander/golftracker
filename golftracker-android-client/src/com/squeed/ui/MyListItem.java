package com.squeed.ui;

import android.graphics.Bitmap;

/**
 * Dummy implementation of a "list item" for the {@link CustomSpinnerView}
 * @author Erik Lupander, squeed
 *
 */
public class MyListItem {
	Long id;
	String text;
	String text2;
	Bitmap icon;
	
	public MyListItem(Long id, String text, String text2) {
		super();
		this.id = id;
		this.text = text;
		this.text2 = text2;
	}
	
	public MyListItem(Long id, String text, String text2, Bitmap icon) {
		super();
		this.id = id;
		this.text = text;
		this.icon = icon;
		this.text2 = text2;
	}

	public Long getId() {
		return id;
	}
}
