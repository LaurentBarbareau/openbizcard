package com.tssoftgroup.tmobile.model;

import java.util.Vector;

import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.StringUtil;

import net.rim.device.api.system.Bitmap;

public class PicInfo {
	// private String username = "";
	//
	private String id = "";
	

	private String localFilename = "";
	private String title = "";
	private String description = "";
	private String videoUrl = "";
	private String duration= "";
	private String cat= "";
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

	public Vector comments = new Vector();
	public Vector moreInfos = new Vector();
	private String thumbnailURL;
	
	private Bitmap thumbnail = null;

	private boolean isMCast = true;

	public PicInfo() {

	}

	public PicInfo(boolean isMCast) {
		this.isMCast = isMCast;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
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

	public byte[] byteArrayThumbnail;

	private int fileSize = 0;

	public boolean isMCast() {
		return isMCast;
	}

	public void setMCast(boolean isMCast) {
		this.isMCast = isMCast;
	}

	public String getLocalFilename() {
		return localFilename;
	}

	//
	public String getTitle() {
		return title;
	}

	public void setLocalFilename(String localFilename) {
		this.localFilename = localFilename;
	}

	//
	public void setTitle(String title) {
		this.title = title;
	}

	public int getFileSoze() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		// this.haveFileSize = true;
		this.fileSize = fileSize;
	}

	public Bitmap getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}
	public boolean containKey(String key){
		key = key.toLowerCase();
		if(title.toLowerCase().indexOf(key) >= 0 || description.toLowerCase().indexOf(key) >= 0){
			return true;
		}else{
			return false;
		}
	}
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getThumbnailURL() {
		return thumbnailURL;
	}

	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}

}
