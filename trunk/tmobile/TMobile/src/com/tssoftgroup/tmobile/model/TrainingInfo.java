package com.tssoftgroup.tmobile.model;

import java.util.Vector;

import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.StringUtil;

import net.rim.device.api.system.Bitmap;

public class TrainingInfo implements TitleDescriptionObj{
	private String title;
	private String description;
	private String explanation;
	

	private String videoUrl;
	private String thumbnailUrl;
	private String id;
	private Bitmap thumbnail;
	
	public Vector questions = new Vector(); // vector of Question
	
	private String cat;
	
	private String filename= "";
	private String urlDownloadVideo= "";

	
	public String getUrlDownloadVideo() {
		return urlDownloadVideo;
	}

	public String getFilename() {
		return filename;
	}
	public String getCat() {
		return cat;
	}

	public void setCat(String cat) {
		this.cat = cat;
	}

	public Bitmap getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TrainingInfo() {

	}

	public TrainingInfo(String title, String description, String url) {
		this.title = title;
		this.description = description;
		this.videoUrl = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String url) {
		this.videoUrl = url;
		/// find filename and store
		String [] all = StringUtil.split(videoUrl, "/");
		if(all.length > 0){
			String last = all[all.length -1];
			this.filename = last;
			this.urlDownloadVideo = Const.URL_VIDEO_DOWNLOAD  + this.filename;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
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
	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
}
