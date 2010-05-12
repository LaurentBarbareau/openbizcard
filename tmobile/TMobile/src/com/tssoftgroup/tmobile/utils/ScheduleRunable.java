package com.tssoftgroup.tmobile.utils;

import java.util.Vector;

import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.model.Video;

public class ScheduleRunable implements Runnable{

	public void run() {
		ProfileEntry profile = ProfileEntry.getInstance();
		Vector videos = Video.convertStringToVector(profile.videos);
		Vector scheduleVideos =  Video.getScheduleVideo(videos);
	}
	
}
