package com.tssoftgroup.tmobile.component;

import java.util.Date;
import java.util.Vector;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.model.Video;
import com.tssoftgroup.tmobile.screen.MCastDetail;
import com.tssoftgroup.tmobile.screen.TrainingVideoScreen;
import com.tssoftgroup.tmobile.screen.VideoConnectDetail;
import com.tssoftgroup.tmobile.utils.CrieUtils;
import com.tssoftgroup.tmobile.utils.DownloadCombiner;

public class VideoDownloadDialog extends Dialog implements FieldChangeListener {
	public static String filename = "";
	public static String fileURL = "";
	public static String videoname = "";

	static String choices[] = { "OK", "Cancel" };
	static int values[] = { Dialog.OK, Dialog.CANCEL };
	// / Radio Button to choose method
	private RadioButtonGroup methodGroup = new RadioButtonGroup();
	private RadioButtonField methodNow = new RadioButtonField("Now",
			methodGroup, true);
	private RadioButtonField methodSchedule = new RadioButtonField("Later",
			methodGroup, false);

	// If choose now -> Then download
	// If choose schedule -> show DateField
	// / Video
	//
	// SimpleDateFormat sdF = new SimpleDateFormat("mm:hh aa");
	// DateField hourMin = new DateField("Time", 0, sdF);
	DateField df = new DateField("Schedule Time : ", new Date().getTime(),
			DateFormat.getInstance(DateFormat.DATETIME_DEFAULT));

	Video video;
	VerticalFieldManager manager = new VerticalFieldManager();
	Screen screen;

	public VideoDownloadDialog(Screen screen) {
		super("When do you want to download this video ?", choices, values, 0,
				Bitmap.getPredefinedBitmap(Bitmap.INFORMATION),
				Dialog.GLOBAL_STATUS);
		this.screen = screen;
		add(methodNow);
		add(methodSchedule);
		// Set Listener
		methodSchedule.setChangeListener(this);
		add(manager);
		// 

	}

	public void myshow() {
		int result = this.doModal();
		// Submit
		if (result == Dialog.OK) {
			if (methodNow.isSelected()) {
				System.out.println("select now");
				// Downlaod the file

				// Check file exist
				String localPatht = CrieUtils.getVideoFolderConnString()
						+ filename;
				try {
					DownloadCombiner download = new DownloadCombiner(fileURL,
							localPatht, 40000, true, filename, videoname);
					download.start();
				} catch (Exception e) {

				}
				// Change the button to downloading
				if (screen instanceof VideoConnectDetail) {
					VideoConnectDetail videoConnect = (VideoConnectDetail) screen;
					videoConnect.setDownloadButton("2");
				}
				if (screen instanceof MCastDetail) {
					MCastDetail mcast = (MCastDetail) screen;
					mcast.setDownloadButton("2");
				}
				if (screen instanceof TrainingVideoScreen) {
					TrainingVideoScreen mcast = (TrainingVideoScreen) screen;
					mcast.setDownloadButton("2");
				}
			} else {
				// check select time > current time
				System.out.println("select schedule");
				Date d = new Date();
				long currentTime = d.getTime();
				System.out.println("Current time:" + d.getTime());
				// method schedule
				System.out.println("Schedule time:" + df.getDate());
				long scheduleTime = df.getDate();
				long min5 = 5000;
				if (scheduleTime - currentTime < min5) {
					Dialog
							.alert("Please select time later than 5 minutes from now.");
					return;
				}
				// / do the job
				ProfileEntry profile = ProfileEntry.getInstance();
				Vector videos = Video.convertStringToVector(profile.videos);
				Video newVideo = new Video();
				newVideo.setName(filename);
				newVideo.setPercent("0");
				newVideo.setScheduleTime(scheduleTime + "");
				newVideo.setStatus("1");
				newVideo.setTitle(videoname);
				videos.addElement(newVideo);
				profile.videos = Video.convertVectorToString(videos);
				profile.saveProfile();
				// Change the button to downloading
				if (screen instanceof VideoConnectDetail) {
					VideoConnectDetail videoConnect = (VideoConnectDetail) screen;
					videoConnect.setDownloadButton("1");
				}
				if (screen instanceof MCastDetail) {
					MCastDetail mcast = (MCastDetail) screen;
					mcast.setDownloadButton("1");
				}
				if (screen instanceof TrainingVideoScreen) {
					TrainingVideoScreen mcast = (TrainingVideoScreen) screen;
					mcast.setDownloadButton("1");
				}
			}
		}
		// instance.displayField.setText(choices[result]);
	}

	public void fieldChanged(Field field, int context) {
		// Dialog.alert("test");
		if (field == methodNow) {

		}
		if (field == methodSchedule) {
			if (methodSchedule.isSelected()) {
				// Select Schedule
				System.out.println("method Schedule");
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						manager.add(df);
					}
				});

			} else {
				// Select Now
				System.out.println("method Now");
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						manager.deleteAll();
					}
				});
			}
		}
		super.fieldChanged(field, context);
	}
}