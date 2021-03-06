package com.tssoftgroup.tmobile.screen;

/**
 *
 * HelloWorld.java
 * The sentinal sample!
 *
 * Copyright � 1998-2008 Research In Motion Ltd.
 *
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeypadListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.HorizontalFieldManager;

import com.tssoftgroup.tmobile.component.EditFieldwithFocus;
import com.tssoftgroup.tmobile.component.LabelFieldWithFullBG;
import com.tssoftgroup.tmobile.component.MainListVerticalFieldManager;
import com.tssoftgroup.tmobile.component.MyButtonField;
import com.tssoftgroup.tmobile.component.VideoListField;
import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.component.engine.LoadPicThread;
import com.tssoftgroup.tmobile.model.PicInfo;
import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.MyColor;

/**
 * Create a new screen that extends MainScreen, which provides default standard
 * behavior for BlackBerry applications.
 */
/*
 * BlackBerry applications that provide a user interface must extend
 * UiApplication.
 */
public class MCastScreen extends FixMainScreen implements FieldChangeListener{
	Img imgstock = Img.getInstance();
	private MainItem _mainMenuItem = new MainItem();
	String topic = "";
	boolean alreadyPush = false;
	static MCastScreen instance;

	VideoListField _list;

	EditFieldwithFocus searchTextField;
	Vector all = new Vector();
	public LoadPicThread loader;

	public static MCastScreen getInstance() {
		if (instance == null) {
			instance = new MCastScreen();
		}
		return instance;
	}

	public static MCastScreen getInstance(String topic) {
		if (instance == null) {
			instance = new MCastScreen(topic);
		} else {
			instance.topic = topic;
		}
		return instance;
	}

	private MCastScreen() {
		super(MODE_MCAST);
		nextBT.setChangeListener(this);
		previousBT.setChangeListener(this);
		createVideoMain();
	}

	public MCastScreen(String topic) {
		super(MODE_MCAST);
		nextBT.setChangeListener(this);
		previousBT.setChangeListener(this);
		this.topic = topic;
		createVideoMain();

	}

	public void setList(Vector picInfos) {
		_list.removeAll();
		all.removeAllElements();
		for (int i = 0; i < picInfos.size(); i++) {
			_list.add((PicInfo) picInfos.elementAt(i));
			all.addElement((PicInfo) picInfos.elementAt(i));
		}
	}

	public boolean navigationClick(int status, int time) {
		System.out.println("navigationClick");
		if (_list.isMyFocus) {
			if ((status & KeypadListener.STATUS_TRACKWHEEL) != KeypadListener.STATUS_TRACKWHEEL) {
				selectAction();
				return true;
			}
		}
		return super.navigationClick(status, time);
	}

	private void handleSearchType() {
		if (searchTextField.isFocus) {
			String text = searchTextField.getText();
			_list.removeAll();
			_list.invalidate();
			for (int i = 0; i < all.size(); i++) {
				PicInfo picInfo = (PicInfo) all.elementAt(i);
				if (picInfo.containKey(text)) {
					_list.add(picInfo);
				}
			}
			_list.invalidate();
		}
	}

	private void selectAction() {
		if (_list != null && _list.getSize() > 0) {

			if (_list.get(_list, _list.getSelectedIndex()) != null) {
				if (loader != null) {
					// loader.stop();
					loader.myWait();
				}
				PicInfo picInfo = (PicInfo) _list.get(_list, _list
						.getSelectedIndex());
				if (picInfo != null) {
					UiApplication.getUiApplication().pushModalScreen(
							new MCastDetail(picInfo));
				}
			}
		}
	}

	public void createVideoMain() {
		// videoList = HttpConn.getList(topic, Const.type_movie);
		XYEdges edge = new XYEdges(24, 25, 8, 25);

		Bitmap img = imgstock.getHeader();
		BitmapField bf = new BitmapField(img, BitmapField.FIELD_HCENTER
				| BitmapField.USE_ALL_WIDTH);
		add(bf);

		try {
			RadioButtonGroup rgrp = new RadioButtonGroup();

			edge = new XYEdges(5, 25* Display.getWidth() / 480, 5, 25 * Display.getWidth() / 480);

			HorizontalFieldManager mainHorizontalManager = new HorizontalFieldManager(
					HorizontalFieldManager.FIELD_HCENTER
							| HorizontalFieldManager.USE_ALL_WIDTH
							| HorizontalFieldManager.FIELD_VCENTER);
			// mainHorizontalManager.setBorder(BorderFactory.createSimpleBorder(
			// edge, Border.STYLE_TRANSPARENT));
			mainHorizontalManager.setMargin(edge);
			// EditField
			searchTextField = new EditFieldwithFocus(
					"Search: ",
					topic,
					35,
					BasicEditField.FIELD_HCENTER | BasicEditField.FIELD_VCENTER,
					MyColor.FONT_SEARCH, MyColor.TOPIC_BG) {
				public void layout(int width, int height) {
					super.layout(getPreferredWidth(), getPreferredHeight());
					setExtent(getPreferredWidth(), getPreferredHeight());
				}

				public int getPreferredHeight() {
					return 25;
				}

				public int getPreferredWidth() {
					return 340 *  Display.getWidth()/480;
				}
			};
			// edit.setMaxSize(35);
			// edit.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
			// add(edit);
			mainHorizontalManager.add(searchTextField);
			mainHorizontalManager.add(searchBT);
			// MyButtonField searchButton = new MyButtonField("Search",
			// ButtonField.ELLIPSIS);
			// stopButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
			// searchButton.setChangeListener(new ButtonListener(rgrp,
			// searchTextField, 27));
			// mainHorizontalManager.add(searchButton);

			// Topic Photo and List field
			MainListVerticalFieldManager videoManager = new MainListVerticalFieldManager();
			videoManager.add(mainHorizontalManager);
			// Topic 
			
//			bf = new BitmapField(img, BitmapField.NON_FOCUSABLE);
			
			LabelField topicLabel = new LabelFieldWithFullBG("MCast",MyColor.FONT_TOPIC,0xffffff,MyColor.TOPIC_BG,Const.LABEL_WIDTH);
			edge = new XYEdges(2, 25* Display.getWidth() / 480, 2, 25* Display.getWidth() / 480);
			topicLabel.setMargin(edge);
			videoManager.add(topicLabel);
//			bf.setMargin(edge);
			// bf.setBorder(BorderFactory.createSimpleBorder(edge,
			// Border.STYLE_TRANSPARENT));
//			videoManager.add(bf);
			_list = new VideoListField();
			videoManager.add(_list);
			pagingManager.setMargin(edge);
			videoManager.add(pagingManager);
			add(videoManager);

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
		handleSearchType();
		switch (c) {
		case Characters.ENTER:
			return true;
		case Characters.ESCAPE:
			if (loader != null) {
				// loader.stop();
				loader.myWait();
			}
			UiApplication.getUiApplication().popScreen(
					UiApplication.getUiApplication().getActiveScreen());
			return true;
		default:
			boolean retValue = super.keyChar(c, status, time);
			handleSearchType();
			return retValue;
		}
	}

	protected void onExposed() {
		if (loader != null) {
			loader.myResume();
		}
	}

	protected void onUiEngineAttached(boolean attached) {
		System.out.println("onUiEngineAttached " + attached);
		if (attached) {
			if (loader != null) {
				loader.myResume();
			}
		}
		if (_list != null) {
			this.invalidate();
			// fieldManager.invalidate();
			_list.invalidate();
		}

		super.onUiEngineAttached(attached);
	}
	public void fieldChanged(Field field, int context) {
//		Dialog.alert("test");
		if(!(field instanceof MyButtonField)){
			super.fieldChanged(field, context);
			return;
		}
		MyButtonField btnField = (MyButtonField) field;
		if (btnField == nextBT) {
//			Dialog.alert("next");
			currentIndex = currentIndex + Const.NUM_LIST;
			UiApplication.getUiApplication().pushScreen(WaitScreen.getInstance());
			Engine.getInstance().viewVideoMCast(currentIndex,search);
		} else if (btnField == previousBT) {
//			Dialog.alert("prev");
			currentIndex = currentIndex - Const.NUM_LIST;
			UiApplication.getUiApplication().pushScreen(WaitScreen.getInstance());
			Engine.getInstance().viewVideoMCast(currentIndex,search);
		} else if (btnField == searchBT) {
			search = searchTextField.getText();
			UiApplication.getUiApplication().pushScreen(WaitScreen.getInstance());
			Engine.getInstance().viewVideoMCast(currentIndex, search);
		}
	}
}


