package com.tssoft.one.webservice.model;

public class Team {
	private String id;
	private String name;
	private String imageURL;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public Team(String id, String name, String imageURL) {
		this.id = id;
		this.name = name;
		this.imageURL = imageURL;
	}
}
