package com.tssoft.one.webservice.model;

public class Article {
	private String id;
	private String title;
	private String scTitle;
	private String imageUrl;
	private String isHighlight;
	private String body;
	
	private String nextId; // Can be null
	private String prevId; // Can be null
	public Article(String id, String title, String scTitle, String imageUrl, String isHighlight){
			this.id = id;
			this.title = title;
			this.scTitle = scTitle;
			this.imageUrl = imageUrl;
			this.isHighlight = isHighlight;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getScTitle() {
		return scTitle;
	}
	public void setScTitle(String scTitle) {
		this.scTitle = scTitle;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getIsHighlight() {
		return isHighlight;
	}
	public void setIsHighlight(String isHighlight) {
		this.isHighlight = isHighlight;
	}
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getNextId() {
		return nextId;
	}

	public void setNextId(String nextId) {
		this.nextId = nextId;
	}

	public String getPrevId() {
		return prevId;
	}

	public void setPrevId(String prevId) {
		this.prevId = prevId;
	}
	
}
