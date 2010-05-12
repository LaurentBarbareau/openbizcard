package com.tssoftgroup.tmobile.utils;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.GIFEncodedImage;

public class Img {
	private static Img instance;

	private Bitmap footer;
	private Bitmap header;
	private Bitmap videoOnDemand;
	private Bitmap videoOnDemandOn;

	private Bitmap videoConnect;
	private Bitmap videoConnectOn;

	private Bitmap documentSharing;
	private Bitmap documentSharingOn;

	private Bitmap training;
	private Bitmap trainingOn;

	private Bitmap contactManagement;
	private Bitmap contactManagementOn;

	private Bitmap poll;
	private Bitmap pollOn;


	private Bitmap commentField;

	private Bitmap videoBackground;

	private GIFEncodedImage loading;

	private Bitmap loadList;

	private Bitmap play;
	private Bitmap playOn;
	private Bitmap stop;
	private Bitmap stopOn;
	private Bitmap fullScreen;
	private Bitmap fullScreenOn;
	private Bitmap comment;
	private Bitmap commentOn;

	private Bitmap login;
	private Bitmap passcode;
	double scale = Display.getWidth() / 480;

	private Bitmap zip;
	private Bitmap doc;
	private Bitmap docDefault;
	private Bitmap pdf;
	private Bitmap ppt;
	private Bitmap xls;
	/// Downloading and Download button
	private Bitmap download;
	private Bitmap downloadFocus;
	private Bitmap downloading;
	private Bitmap downloadingFocus;
	private Bitmap schedule;
	private Bitmap scheduleFocus;
	// Dont have moreinfo Button now
	public Bitmap getZip() {
		if (zip == null) {
			zip = CrieUtils.scaleImageToWidthHeight(
					EncodedImage.getEncodedImageResource("zip.png"),
					(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD -Scale.INDENT_LEFT_RIGHT_TOPIC)
							,
					(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD -Scale.INDENT_LEFT_RIGHT_TOPIC)
							).getBitmap();
		}
		return zip;
	}
	public Bitmap getDoc() {
		if (doc == null) {
			doc = CrieUtils.scaleImageToWidthHeight(
					EncodedImage.getEncodedImageResource("doc.png"),
					(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD -Scale.INDENT_LEFT_RIGHT_TOPIC)
							,
					(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD -Scale.INDENT_LEFT_RIGHT_TOPIC)
							).getBitmap();
		}
		return doc;
	}
	public Bitmap getDocDefault() {
		if (docDefault == null) {
			docDefault = CrieUtils.scaleImageToWidthHeight(
					EncodedImage.getEncodedImageResource("docde.png"),
					(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD -Scale.INDENT_LEFT_RIGHT_TOPIC)
							,
					(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD -Scale.INDENT_LEFT_RIGHT_TOPIC)
							).getBitmap();
		}
		return docDefault;
	}
	public Bitmap getPdf() {
		if (pdf == null) {
			pdf = CrieUtils.scaleImageToWidthHeight(
					EncodedImage.getEncodedImageResource("pdf.png"),
					(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD -Scale.INDENT_LEFT_RIGHT_TOPIC)
							,
					(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD -Scale.INDENT_LEFT_RIGHT_TOPIC)
							).getBitmap();
		}
		return pdf;
	}
	public Bitmap getPpt() {
		if (ppt == null) {
			ppt = CrieUtils.scaleImageToWidthHeight(
					EncodedImage.getEncodedImageResource("ppt.png"),
					(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD -Scale.INDENT_LEFT_RIGHT_TOPIC)
							,
					(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD -Scale.INDENT_LEFT_RIGHT_TOPIC)
							).getBitmap();
		}
		return ppt;
	}
	public Bitmap getXls() {
		if (xls == null) {
			xls = CrieUtils.scaleImageToWidthHeight(
					EncodedImage.getEncodedImageResource("xls.png"),
					(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD -Scale.INDENT_LEFT_RIGHT_TOPIC)
							,
					(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD -Scale.INDENT_LEFT_RIGHT_TOPIC)
							).getBitmap();
		}
		return xls;
	}
	public Bitmap getLoadList() {
		if (loadList == null) {
			loadList = CrieUtils.scaleImageToWidthHeight(
					EncodedImage.getEncodedImageResource("loadlist.png"),
					(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD -Scale.INDENT_LEFT_RIGHT_TOPIC)
							,
					(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD -Scale.INDENT_LEFT_RIGHT_TOPIC)
							).getBitmap();
		}
		return loadList;
	}

	public static Img getInstance() {
		if (instance == null) {
			instance = new Img();
		}
		return instance;
	}

	public Bitmap getFooter() {
		if (footer == null) {
			footer = CrieUtils.createScaleBitmap("Footer_480.png", Display
					.getWidth());
		}
		return footer;
	}
	public Bitmap getPlay() {
		if (play == null) {
			play = CrieUtils.createScaleBitmap(
					"detailPlay.png",  58 * Display.getWidth()/480);
		}
		return play;
	}

	public Bitmap getPlayOn() {
		if (playOn == null) {
			playOn = CrieUtils.createScaleBitmap(
					"detailPlaySelected.png",  58 * Display.getWidth()/480);
		}
		return playOn;
	}
	//
	public Bitmap getDownloading() {
		if (downloading == null) {
			downloading = CrieUtils.createScaleBitmap(
					"downloading.png",  105 * Display.getWidth()/480);
		}
		return downloading;
	}
	public Bitmap getDownloadingOn() {
		if (downloadingFocus == null) {
			downloadingFocus = CrieUtils.createScaleBitmap(
					"downloadingSelected.png",  105 * Display.getWidth()/480);
		}
		return downloadingFocus;
	}
	public Bitmap getDownload() {
		if (download == null) {
			download = CrieUtils.createScaleBitmap(
					"download.png",  105 * Display.getWidth()/480);
		}
		return download;
	}
	public Bitmap getDownloadOn() {
		if (downloadFocus == null) {
			downloadFocus = CrieUtils.createScaleBitmap(
					"downloadSelected.png",  105 * Display.getWidth()/480);
		}
		return downloadFocus;
	}
	public Bitmap getSchedule() {
		if (schedule == null) {
			schedule = CrieUtils.createScaleBitmap(
					"scheduled.png",  105 * Display.getWidth()/480);
		}
		return schedule;
	}
	public Bitmap getScheduleOn() {
		if (scheduleFocus == null) {
			scheduleFocus = CrieUtils.createScaleBitmap(
					"scheduledSelected.png",  105 * Display.getWidth()/480);
		}
		return scheduleFocus;
	}
	
	//
	public Bitmap getHeader() {
		if (header == null) {
			header = CrieUtils.createScaleBitmap("Header_480.png", Display
					.getWidth());
		}
		return header;
	}

	public Bitmap getVideoOnDemand() {
		if (videoOnDemand == null) {
			videoOnDemand = CrieUtils.createScaleBitmap(
					"Video_On_Demand_480.png", Display.getWidth());
		}
		return videoOnDemand;
	}

	public Bitmap getVideoOnDemandOn() {
		if (videoOnDemandOn == null) {
			videoOnDemandOn = CrieUtils.createScaleBitmap(
					"Video_On_Demand_On_480.png", Display.getWidth());
		}
		return videoOnDemandOn;
	}

	public Bitmap getVideoConnect() {
		if (videoConnect == null) {
			videoConnect = CrieUtils.createScaleBitmap("Video_Connect_480.png",
					Display.getWidth());
		}
		return videoConnect;
	}

	public Bitmap getVideoConnectOn() {
		if (videoConnectOn == null) {
			videoConnectOn = CrieUtils.createScaleBitmap(
					"Video_Connect_On_480.png", Display.getWidth());
		}
		return videoConnectOn;
	}

	public Bitmap getDocumentSharing() {
		if (documentSharing == null) {
			documentSharing = CrieUtils.createScaleBitmap(
					"Document_Sharing_480.png", Display.getWidth());
		}
		return documentSharing;
	}

	public Bitmap getDocumentSharingOn() {
		if (documentSharingOn == null) {
			documentSharingOn = CrieUtils.createScaleBitmap(
					"Document_Sharing_On_480.png", Display.getWidth());
		}
		return documentSharingOn;
	}

	public Bitmap getTraining() {
		if (training == null) {
			training = CrieUtils.createScaleBitmap("Training_480.png", Display
					.getWidth());
		}
		return training;
	}

	public Bitmap getTrainingOn() {
		if (trainingOn == null) {
			trainingOn = CrieUtils.createScaleBitmap("Training_On_480.png",
					Display.getWidth());
		}
		return trainingOn;
	}

	public Bitmap getContactManagement() {
		if (contactManagement == null) {
			contactManagement = CrieUtils.createScaleBitmap(
					"Contact_Management_480.png", Display.getWidth());
		}
		return contactManagement;
	}

	public Bitmap getContactManagementOn() {
		if (contactManagementOn == null) {
			contactManagementOn = CrieUtils.createScaleBitmap(
					"Contact_Management_On_480.png", Display.getWidth());
		}
		return contactManagementOn;
	}

	public Bitmap getPoll() {
		if (poll == null) {
			poll = CrieUtils.createScaleBitmap("Company_Poll_480.png", Display
					.getWidth());
		}
		return poll;
	}

	public Bitmap getPollOn() {
		if (pollOn == null) {
			pollOn = CrieUtils.createScaleBitmap("Company_Poll_On_480.png",
					Display.getWidth());
		}
		return pollOn;
	}



	public Bitmap getCommentField() {
		if (commentField == null) {
			int width = (Display.getWidth() * 422) / 480;
			commentField = CrieUtils.createScaleBitmap("CommentField_480.png",
					width);
		}
		return commentField;
	}

	public Bitmap getVideoBackground() {
		if (videoBackground == null) {
			int width = (Display.getWidth() * 432) / 480;
			videoBackground = CrieUtils.createScaleBitmap(
					"Video_Background_480.png", width);
		}
		return videoBackground;
	}

	public GIFEncodedImage getLoading() {
		if (loading == null) {
			loading = (GIFEncodedImage) GIFEncodedImage
					.getEncodedImageResource("bar-circle.gif");
		}
		return loading;
	}

	// left 13 top 5
	public Bitmap getLogin() {
		if (login == null) {
			int width = (480 * 103) / 480;
			login = CrieUtils.createScaleBitmap("login.png", width);
		}
		return login;
	}

	// left 34 top 22
	public Bitmap getPasscode() {
		if (passcode == null) {
			int width = (480 * 109) / 480;
			passcode = CrieUtils.createScaleBitmap("passcode.png", width);
		}
		return passcode;
	}
	
}
