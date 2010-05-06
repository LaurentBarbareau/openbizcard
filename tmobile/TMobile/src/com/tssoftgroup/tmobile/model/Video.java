package com.tssoftgroup.tmobile.model;

import java.util.Vector;

import com.tssoftgroup.tmobile.utils.StringUtil;

public class Video {
	public static final String SEPERATOR = ",";
	public static final String VIDEO_SEPERATOR = "|";

	private String name = "0";
	private String status = "0";
	private String scheduleTime = "0";

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
		String ret = name + SEPERATOR + status + SEPERATOR + scheduleTime;
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
		Vector ret = new Vector();
		String[] videoStringArr = StringUtil.split(videosString,
				VIDEO_SEPERATOR);
		for (int i = 0; i < videoStringArr.length; i++) {
			Video vid = new Video();
			String[] videoString = StringUtil.split(videoStringArr[i],
					SEPERATOR);
			vid.name = videoString[0];
			vid.status = videoString[0];
			vid.scheduleTime = videoString[0];
			ret.addElement(vid);
		}
		return ret;
	}
	public static void main(String [] args){
		System.out.println("aaa");
	}
}
