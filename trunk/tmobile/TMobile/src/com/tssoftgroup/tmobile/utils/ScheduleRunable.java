package com.tssoftgroup.tmobile.utils;

import java.util.Date;
import java.util.Vector;

import net.rim.device.api.i18n.SimpleDateFormat;

import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.model.Video;

public class ScheduleRunable implements Runnable {
	public boolean isRunning = true;
	SimpleDateFormat myDtTm = new SimpleDateFormat("hh:mm");

	public void run() {
		while (isRunning) {
			ProfileEntry profile = ProfileEntry.getInstance();
			Vector videos = Video.convertStringToVector(profile.videos);
			Vector scheduleVideos = Video.getScheduleVideo(videos);
			for (int i = 0; i < scheduleVideos.size(); i++) {
				Video video = (Video) scheduleVideos.elementAt(i);
				try {
					if (new Date().getTime() > Long.parseLong(video
							.getScheduleTime())
							|| isTimeInSetting()) {
						String url = Const.URL_VIDEO_DOWNLOAD + video.getName();
						String localPatht = CrieUtils
								.getVideoFolderConnString()
								+ video.getName();
						DownloadCombiner download = new DownloadCombiner(url,
								localPatht, 40000, true, video.getName(), video
										.getTitle());
						download.start();
						// remove from vector
						video.setStatus("2");
						profile.videos = Video
								.convertVectorToString(scheduleVideos);
						profile.saveProfile();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private boolean isTimeInSetting() {
		try {
			String current = myDtTm.formatLocal(new Date().getTime());
			String inSetting = myDtTm.formatLocal(Long.parseLong(ProfileEntry
					.getInstance().settingTime));
			if (current.equals(inSetting)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
