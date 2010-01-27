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
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

import com.tssoftgroup.tmobile.component.ButtonListener;
import com.tssoftgroup.tmobile.component.MyButtonField;
import com.tssoftgroup.tmobile.component.NewVerticalFieldManager;
import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.Img;

/**
 * Create a new screen that extends MainScreen, which provides default standard
 * behavior for BlackBerry applications.
 */
/*
 * BlackBerry applications that provide a user interface must extend
 * UiApplication.
 */
/* package */class BrowseUpload extends MainScreen {
	NewVerticalFieldManager manager = new NewVerticalFieldManager();

	/**
	 * HelloWorldScreen constructor.
	 */
	BrowseUpload() {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);
		super.add(manager);

		// Add a field to the title region of the screen. We use a simple
		// LabelField
		// here. The ELLIPSIS option truncates the label text with "..." if the
		// text
		// is too long for the space available.
		// LabelField title = new LabelField("Hello World Demo" ,
		// LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH);
		// setTitle(title);

		// Add a read only text field (RichTextField) to the screen. The
		// RichTextField
		// is focusable by default. In this case we provide a style to make the
		// field
		// non-focusable.
		// add(new RichTextField("Hello World!" ,Field.NON_FOCUSABLE));
	}

	public void add(Field field) {
		manager.add(field);
	}

	/**
	 * Display a dialog box to the user with "Goodbye!" when the application is
	 * closed.
	 * 
	 * @see net.rim.device.api.ui.Screen#close()
	 */
	public void close() {
		// Display a farewell message before closing application.
		System.exit(0);

		super.close();
	}
}

public class MyMovieShare extends FixMainScreen {
	LabelField label = new LabelField("Select a video to upload: "
			+ Const.videoToUpload);
	Img imgstock = Img.getInstance();
	private MainItem _mainMenuItem = new MainItem();
	private static MyMovieShare instance;

	public static MyMovieShare getInstance() {
		if (instance == null) {
			instance = new MyMovieShare();
		}else{
			instance.label.setText("Select a video to upload: "
					+ Const.videoToUpload);
		}
		return instance;
	}
	
	private MyMovieShare() {
		
		XYEdges edge = new XYEdges(24, 25, 8, 25);

		Bitmap img = imgstock.getHeader();
		BitmapField bf = new BitmapField(img, BitmapField.FIELD_HCENTER
				| BitmapField.USE_ALL_WIDTH);
		add(bf);
		try {
			edge = new XYEdges(5, 25, 5, 25);

			// select a file label
			
//			label.setBorder(BorderFactory.createSimpleBorder(edge,
//					Border.STYLE_TRANSPARENT));
			add(label);

			EditField edit = new EditField("Topic: ", "");
//			edit.setBorder(BorderFactory.createSimpleBorder(edge,
//					Border.STYLE_TRANSPARENT));
			add(edit);

			EditField description = new EditField("Description: ", "");
//			description.setBorder(BorderFactory.createSimpleBorder(edge,
//					Border.STYLE_TRANSPARENT));
			add(description);

			edge = new XYEdges(2, 25, 2, 25);

			HorizontalFieldManager mainHorizontalManager = new HorizontalFieldManager(
					HorizontalFieldManager.FIELD_HCENTER
							| HorizontalFieldManager.USE_ALL_WIDTH);
//			mainHorizontalManager.setBorder(BorderFactory.createSimpleBorder(
//					edge, Border.STYLE_TRANSPARENT));

			MyButtonField button = new MyButtonField("Browse",
					ButtonField.ELLIPSIS);
			// stopButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
			button.setChangeListener(new ButtonListener(22));
			mainHorizontalManager.add(button);

			button = new MyButtonField("Submit", ButtonField.ELLIPSIS);
			// stopButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));

			button.setChangeListener(new ButtonListener(null, edit,
					description, 23));
			mainHorizontalManager.add(button);

			button = new MyButtonField("Back", ButtonField.ELLIPSIS);
			// stopButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
			button.setChangeListener(new ButtonListener(24));
			mainHorizontalManager.add(button);

			add(mainHorizontalManager);
		} catch (Exception e) {
			System.out.println("" + e.toString());
		}

		edge = new XYEdges(5, 0, 0, 0);
		// bf = new BitmapField(img, Field.FIELD_BOTTOM | Field.USE_ALL_HEIGHT);
		// bf.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
		// add(bf);

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
