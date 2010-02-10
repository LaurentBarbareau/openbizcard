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

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

import com.tssoftgroup.tmobile.component.ButtonListener;
import com.tssoftgroup.tmobile.component.CrieLabelField;
import com.tssoftgroup.tmobile.component.LabelFieldWithFullBG;
import com.tssoftgroup.tmobile.component.MainListVerticalFieldManager;
import com.tssoftgroup.tmobile.component.MyButtonField;
import com.tssoftgroup.tmobile.model.TrainingInfo;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.MyColor;
import com.tssoftgroup.tmobile.utils.Scale;

/**
 * Create a new screen that extends MainScreen, which provides default standard
 * behavior for BlackBerry applications.
 */
/*
 * BlackBerry applications that provide a user interface must extend
 * UiApplication.
 */
public class TrainingVideoScreen extends FixMainScreen implements
		FieldChangeListener {
	Img imgstock = Img.getInstance();
	private MainItem _mainMenuItem = new MainItem();
	MainListVerticalFieldManager mainManager = new MainListVerticalFieldManager();

	BitmapField trainingBitmap;
	TrainingInfo info = new TrainingInfo();
	MyButtonField startButton = new MyButtonField("Start", ButtonField.ELLIPSIS);

	MyButtonField nextButton = new MyButtonField("Next", ButtonField.ELLIPSIS);

	public TrainingVideoScreen(TrainingInfo info) {
		super(MODE_TRAIN);
		this.info = info;
		createVideoMain();
	}

	public void createVideoMain() {
		// videoList = HttpConn.getList(topic, Const.type_movie);
		XYEdges edge = new XYEdges(24, 25, 8, 25);

		Bitmap img = imgstock.getHeader();
		BitmapField bf = new BitmapField(img, BitmapField.FIELD_HCENTER
				| BitmapField.USE_ALL_WIDTH);
		add(bf);

		try {
			// submitButton.setChangeListener(new ButtonListener());
			// Topic Photo and List field
			nextButton.setChangeListener(new ButtonListener(info, 34));
			//
			LabelField titleLabel = new LabelFieldWithFullBG(info.getTitle(),
					MyColor.FONT_TOPIC, MyColor.FONT_TOPIC_COLOR, MyColor.TOPIC_BG, Display
							.getWidth()
							- 50 * Display.getWidth() / 480);
			edge = new XYEdges(2, 25 * Display.getWidth() / 480, 2,
					25 * Display.getWidth() / 480);
			titleLabel.setMargin(edge);
			CrieLabelField descriptionLabel = new CrieLabelField(info
					.getDescription(), MyColor.FONT_DESCRIPTION,
					Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT,
					LabelField.NON_FOCUSABLE);
			descriptionLabel.isFix = true;
			edge = new XYEdges(10, 25, 2, 25);
			descriptionLabel.setMargin(edge);
			mainManager.add(titleLabel);
			mainManager.add(descriptionLabel);

			if (!info.getThumbnailUrl().equals("")) {
				if (info.getThumbnail() != null) {
					trainingBitmap = new BitmapField(info.getThumbnail());

				} else {
					trainingBitmap = new BitmapField(imgstock.getLoadList());
				}
				trainingBitmap
						.setMargin(
								10,
								0,
								10,
								(Display.getWidth() - trainingBitmap.getWidth()) / 2 - 25);
				mainManager.add(trainingBitmap);
			}
			HorizontalFieldManager buttonManager = new HorizontalFieldManager();

			startButton.setChangeListener(this);

			edge = new XYEdges(2, 25 * Display.getWidth() / 480, 2,
					25 * Display.getWidth() / 480);
			buttonManager.add(startButton);
			buttonManager.add(nextButton);

			buttonManager.setMargin(edge);
			nextButton.setMargin(0, 0, 0, 10);
			mainManager.add(buttonManager);
			// button
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

	protected boolean keyDown(int arg0, int arg1) {
		// TODO Auto-generated method stub
		try {
			switch (arg0) {
			case 1179648:
				close();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.keyDown(arg0, arg1);
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

	public void fieldChanged(Field field, int context) {
		TrainingPlayerScreen scr = new TrainingPlayerScreen(info);
		UiApplication.getUiApplication().pushScreen(scr);
	}

}
