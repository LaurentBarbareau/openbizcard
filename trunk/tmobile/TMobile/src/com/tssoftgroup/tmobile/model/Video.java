package com.tssoftgroup.tmobile.model;

import java.util.Vector;

import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.utils.StringUtil;

public class Video {
	// Status 0= new| 1=will be downloaded | 2=downloading | 3=downloaded
	// 1754514.mp4,0, 40:11 5/6/2010 | 1754515.mp4,0,12:4 5/6/2010

	public static final String SEPERATOR = ",";
	public static final String VIDEO_SEPERATOR = "|";

	private String name = "0";
	private String status = "0";
	private String scheduleTime = "0";
	private String percent = "0";
	private String title = "0";
	private String currentChunk = "0";

	public String getCurrentChunk() {
		return currentChunk;
	}

	public void setCurrentChunk(String currentChunk) {
		this.currentChunk = currentChunk;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String toString() {
		return "name = " + name + " status = " + status + " schedule time = "
				+ scheduleTime + " percent " + percent + " title " + title
				+ " current chunk " + currentChunk;
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	private String toMyString() {
		String ret = name + SEPERATOR + status + SEPERATOR + scheduleTime
				+ SEPERATOR + percent + SEPERATOR + title + SEPERATOR + currentChunk;
		return ret;
	}

	public static String convertVectorToString(Vector videos) {
		String all = "";
		for (int i = 0; i < videos.size(); i++) {
			Video video = (Video) videos.elementAt(i);
			String videoString = video.toMyString();
			if (i > 0) {
				all = all + VIDEO_SEPERATOR;
			}
			all = all + videoString;
		}
		return all;
	}

	public static Vector convertStringToVector(String videosString) {
		if (videosString.equals("")) {
			return new Vector();
		}
		Vector ret = new Vector();
		String[] videoStringArr = StringUtil.split(videosString,
				VIDEO_SEPERATOR);
		for (int i = 0; i < videoStringArr.length; i++) {
			Video vid = new Video();
			String[] videoString = StringUtil.split(videoStringArr[i],
					SEPERATOR);
			try {
				vid.name = videoString[0];
				vid.status = videoString[1];
				vid.scheduleTime = videoString[2];
				vid.percent = videoString[3];
				vid.title = videoString[4];
				vid.currentChunk = videoString[5];
			} catch (Exception e) {
				e.printStackTrace();
			}
			ret.addElement(vid);
		}
		return ret;
	}

	// public static void main(String[] args) {
	// System.out.println("aaa");
	// String videos =
	// "1754514.mp4,0,40:11 5/6/2010|1754515.mp4,1,12:4 8/6/2010";
	// Vector v = convertStringToVector(videos);
	// for (int i = 0; i < v.size(); i++) {
	// Video video = (Video) v.elementAt(i);
	// System.out.println(video);
	// }
	// String aa = convertVectorToString(v);
	// System.out.println(aa);
	//		
	// }
	public static String getVideoStatus(String videoName) {
		ProfileEntry profile = ProfileEntry.getInstance();
		Vector videos = convertStringToVector(profile.videos);
		for (int i = 0; i < videos.size(); i++) {
			Video video = (Video) videos.elementAt(i);
			if (video.name.equals(videoName)) {
				return video.status;
			}
		}
		return "0";
	}

	public static Vector getDownloadingVideo(Vector videos) {
		Vector ret = new Vector();
		for (int i = 0; i < videos.size(); i++) {
			Video video = (Video) videos.elementAt(i);
			if (video.status.equals("2")) {
				ret.addElement(video);
			}
		}
		return ret;
	}

	public static Vector getScheduleVideo(Vector videos) {
		Vector ret = new Vector();
		for (int i = 0; i < videos.size(); i++) {
			Video video = (Video) videos.elementAt(i);
			if (video.status.equals("1")) {
				ret.addElement(video);
			}
		}
		return ret;
	}

	public static Vector getDownloadedVideo(Vector videos) {
		Vector ret = new Vector();
		for (int i = 0; i < videos.size(); i++) {
			Video video = (Video) videos.elementAt(i);
			if (video.status.equals("3")) {
				ret.addElement(video);
			}
		}
		return ret;
	}
}
