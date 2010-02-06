package com.tssoftgroup.tmobile.model;

import java.util.Vector;

import net.rim.device.api.system.Bitmap;

public class PollInfo implements  TitleDescriptionObj{
	private String title;
	private String description;
	private String id;
	
	public Vector questions = new Vector(); // vector of PollQuestion
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PollInfo() {

	}

	public PollInfo(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean containKey(String key) {
		key = key.toLowerCase();
		if (title.toLowerCase().indexOf(key) >= 0
				|| description.toLowerCase().indexOf(key) >= 0) {
			return true;
		} else {
			return false;
		}
	}
}
