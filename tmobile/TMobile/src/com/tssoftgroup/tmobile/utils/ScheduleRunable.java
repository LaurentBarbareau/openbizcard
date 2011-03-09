package com.tssoftgroup.tmobile.utils;

import java.util.Date;
import java.util.Vector;

import net.rim.device.api.i18n.SimpleDateFormat;

import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.component.engine.HttpDownloadVideoThread;
import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.model.Video;

public class ScheduleRunable implements Runnable {
	public boolean isRunning = true;
	SimpleDateFormat myDtTm = new SimpleDateFormat("hh:mm");
//	boolean checkPercent = false;

	public void run() {
		while (isRunning) {
			ProfileEntry profile = ProfileEntry.getInstance();
			Vector videos = Video.convertStringToVector(profile.videos);
			// Vector scheduleVideos = Video.getScheduleVideo(videos);
			// //
			if (isTimeInSetting()) {
				if (CrieUtils.isRoaming() ) {
					// is Roaming and set not to download when roaming 
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				} else {

					Engine engine = Engine.getInstance();
					engine.checkMCast();
					engine.checkTraining();
					engine.checkVideoConnect();
				}

			}
			// //
			for (int i = 0; i < videos.size(); i++) {
				Video video = (Video) videos.elementAt(i);
				if (video.getStatus().equals("1")) {
					try {
						if (new Date().getTime() > Long.parseLong(video
								.getScheduleTime())
								|| isTimeInSetting()) {
							String url = Const.URL_VIDEO_DOWNLOAD
									+ video.getName();
							String localPatht = CrieUtils
									.getVideoFolderConnString()
									+ video.getName();
							DownloadCombiner download = new DownloadCombiner(
									url, localPatht, Const.DOWNLOAD_SIZE, true,
									video.getName(), video.getTitle());
							// download.start();
							Engine.getInstance().addDownloadVideo(download);
							// remove from vector
							video.setStatus("2");
							profile.videos = Video
									.convertVectorToString(videos);

						}
					} catch (Exception e) {
						System.out.println("error " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
			profile.saveProfile();
			// Check download progress every 2 minute if the percent is the same
			// restart the download thread
//			if (true) {
//				checkDownloadAndRestart();
//			}
//			checkPercent = !checkPercent;
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	String previousName = "";
	String previousPercent = "";
	private void checkDownloadAndRestart(){

		Engine engine = Engine.getInstance();
		String nextName = engine.downloadVideoThread.currentDownloadName;
		// Do something
		System.out.println("<===== check percent");
		System.out.println("nextName " + nextName);
		System.out.println("previousName " + previousName);
		System.out.println("nextName " + nextName);
		ProfileEntry profile= ProfileEntry.getInstance();
		if (!nextName.equals("") && !previousName.equals("")
				&& nextName.equals(previousName)) {
			Vector videoVector = Video
					.convertStringToVector(profile.videos);
			for (int i = 0; i < videoVector.size(); i++) {
				Video vid = (Video) videoVector.elementAt(i);
				if (vid.getName().equals(nextName) && vid.getStatus().equals("2")) {
					System.out.println("vid percent " + vid.getPercent());
					System.out.println("previousPercent " + previousPercent);
					
					if (vid.getPercent().equals(previousPercent)) {
						// The thread maybe die restart it
						engine.downloadVideoThread.cancel();
						engine.downloadVideoThread = new HttpDownloadVideoThread();
						engine.downloadVideoThread.start();
						// Check downloading video and put in Queue
						for (int j = 0; j < videoVector.size(); j++) {
							Video vid2 = (Video) videoVector.elementAt(j);
							if (vid2.getStatus().equals("2")) {
								String url = Const.URL_VIDEO_DOWNLOAD
										+ vid2.getName();
								String localPatht = CrieUtils
										.getVideoFolderConnString()
										+ vid2.getName();
								DownloadCombiner download = new DownloadCombiner(
										url, localPatht,
										Const.DOWNLOAD_SIZE, true, vid2
												.getName(), vid2
												.getTitle());
								// download.start();
								engine.addDownloadVideo(download);
							}
						}
					}
					previousPercent = vid.getPercent();
				}
			}
		}
		previousName = nextName;
	
	}
	private boolean isTimeInSetting() {
		try {
			String current = myDtTm.formatLocal(new Date().getTime());
			String inSetting = myDtTm.formatLocal(Long.parseLong(ProfileEntry
					.getInstance().settingTime));
			System.out.println("<========= current " + current);
			System.out.println("<========= inSetting " + inSetting);

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
