package com.tssoftgroup.tmobile.screen;

/**
 *
 * HelloWorld.java
 * The sentinal sample!
 *
 * Copyright © 1998-2008 Research In Motion Ltd.
 *
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

import java.util.Date;
import java.util.Vector;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.tssoftgroup.tmobile.component.ButtonListener;
import com.tssoftgroup.tmobile.component.CrieLabelField;
import com.tssoftgroup.tmobile.component.CustomButtonField;
import com.tssoftgroup.tmobile.component.LabelFieldWithFullBG;
import com.tssoftgroup.tmobile.component.MainListVerticalFieldManager;
import com.tssoftgroup.tmobile.component.MyButtonField;
import com.tssoftgroup.tmobile.component.ScreenWithComment;
import com.tssoftgroup.tmobile.component.VideoDownloadDialog;
import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.model.Comment;
import com.tssoftgroup.tmobile.model.MoreInfo;
import com.tssoftgroup.tmobile.model.PicInfo;
import com.tssoftgroup.tmobile.model.Video;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.MyColor;
import com.tssoftgroup.tmobile.utils.Scale;
import com.tssoftgroup.tmobile.utils.Wording;

public class DownloadQueueScreen extends FixMainScreen {
	private MainItem _mainMenuItem = new MainItem();
	Img imgStock = Img.getInstance();
	// MyButtonField playButton = new MyButtonField("Play",
	// ButtonField.ELLIPSIS);
	String videoPath = "";
	public VerticalFieldManager downloadingManager = new VerticalFieldManager();
	public VerticalFieldManager scheduleManager = new VerticalFieldManager();
	HorizontalFieldManager durationPlayManager;

	public DownloadQueueScreen() {
		super(MODE_MCAST);
		XYEdges edge = new XYEdges(24, 25, 8, 25);
		XYEdges detailEdge = new XYEdges(2, 35 * Display.getWidth() / 480, 2,
				35 * Display.getWidth() / 480);
		Bitmap img = imgStock.getHeader();
		BitmapField bf = new BitmapField(img, BitmapField.FIELD_HCENTER
				| BitmapField.USE_ALL_WIDTH);
		add(bf);

		try {
			MainListVerticalFieldManager mainManager = new MainListVerticalFieldManager();

			edge = new XYEdges(2, 35, 17, 35);
			LabelField titleLabel = new LabelFieldWithFullBG("Download Queue",
					MyColor.FONT_TOPIC, MyColor.FONT_TOPIC_COLOR,
					MyColor.TOPIC_BG, Display.getWidth() - 50
							* Display.getWidth() / 480);
			edge = new XYEdges(2, 25 * Display.getWidth() / 480, 2,
					25 * Display.getWidth() / 480);
			titleLabel.setMargin(edge);
			mainManager.add(titleLabel);
			// <<<<<============ Downloading
			LabelField downloadingLB = new LabelFieldWithFullBG("downloading",
					MyColor.COMMENT_LABEL_FONT,
					MyColor.COMMENT_LABEL_FONT_COLOR, MyColor.COMMENT_LABEL_BG,
					Display.getWidth() - 50 * Display.getWidth() / 480);

			edge = new XYEdges(Scale.EDGE, 25 * Display.getWidth() / 480,
					Scale.EDGE, 25 * Display.getWidth() / 480);
			downloadingLB.setMargin(edge);
			downloadingLB.setFont(Scale.FONT_DETAIL_TITLE);
			ProfileEntry profile = ProfileEntry.getInstance();
			Vector videos = Video.convertStringToVector(profile.videos);
			Vector downloadingVideos = Video.getDownloadingVideo(videos);
			Vector scheduleVideos = Video.getScheduleVideo(videos);

			// / Add item
			for (int i = 0; i < downloadingVideos.size(); i++) {
				Video v = (Video) downloadingVideos.elementAt(i);
				CrieLabelField test1 = new CrieLabelField(v.getTitle() + " : "
						+ v.getPercent() + "%", MyColor.FONT_DESCRIPTION,
						Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT,
						LabelField.FOCUSABLE);
				test1.setMargin(detailEdge);
				downloadingManager.add(test1);
			}

			// <<<<<============ Schedule
			LabelField scheduleLB = new LabelFieldWithFullBG("schedule",
					MyColor.COMMENT_LABEL_FONT,
					MyColor.COMMENT_LABEL_FONT_COLOR, MyColor.COMMENT_LABEL_BG,
					Display.getWidth() - 50 * Display.getWidth() / 480);
			edge = new XYEdges(Scale.EDGE, 25 * Display.getWidth() / 480,
					Scale.EDGE, 25 * Display.getWidth() / 480);
			scheduleLB.setMargin(edge);
			scheduleLB.setFont(Scale.FONT_DETAIL_TITLE);
			// Add item
			for (int i = 0; i < scheduleVideos.size(); i++) {
				Video v = (Video) scheduleVideos.elementAt(i);
				String dateString = "";
				try {
					Date videoDate = new Date(Long.parseLong(v
							.getScheduleTime()));
					dateString = videoDate.toString();
				} catch (Exception e) {
					e.printStackTrace();
				}
				CrieLabelField test1 = new CrieLabelField(v.getTitle() + " : "
						+ dateString, MyColor.FONT_DESCRIPTION,
						Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT,
						LabelField.FOCUSABLE);
				test1.setMargin(detailEdge);
				scheduleManager.add(test1);
			}
			// / Add to Main Manager
			mainManager.add(downloadingLB);
			mainManager.add(downloadingManager);
			mainManager.add(scheduleLB);
			mainManager.add(scheduleManager);

			add(mainManager);
		} catch (Exception e) {
			System.out.println("" + e.toString());
		}

		edge = new XYEdges(5, 0, 0, 0);

		addMenuItem(_mainMenuItem);
	}

	private final class MainItem extends MenuItem {
		/**
		 * Constructor.
		 */
		private MainItem() {
			super("Main Menu", 100, 1);
		}

		/**
		 * Attempts to save the screen's data to its associated memo. If
		 * successful, the edit screen is popped from the display stack.
		 */
		public void run() {
			UiApplication.getUiApplication().popScreen(
					UiApplication.getUiApplication().getActiveScreen());
		}
	}

	public boolean keyChar(char c, int status, int time) {
		switch (c) {
		case Characters.ENTER:
			return true;
		case Characters.ESCAPE:
			UiApplication.getUiApplication().popScreen(
					UiApplication.getUiApplication().getActiveScreen());
			return true;
		default:
			return super.keyChar(c, status, time);
		}
	}

}
