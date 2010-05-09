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

import java.util.Vector;

import net.rim.device.api.io.file.FileSystemJournal;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.io.file.FileSystemJournalListener;
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
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.tssoftgroup.tmobile.component.BitmapFieldWithStatus;
import com.tssoftgroup.tmobile.component.ButtonListener;
import com.tssoftgroup.tmobile.component.EditFieldwithFocus;
import com.tssoftgroup.tmobile.component.LabelFieldWithFullBG;
import com.tssoftgroup.tmobile.component.MainListVerticalFieldManager;
import com.tssoftgroup.tmobile.component.MyButtonField;
import com.tssoftgroup.tmobile.component.VideoListField;
import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.component.engine.LoadPicThread;
import com.tssoftgroup.tmobile.model.PicInfo;
import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.CrieUtils;
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
public class VideoConnectScreen extends FixMainScreen implements
		FileSystemJournalListener, FieldChangeListener {
	Img imgstock = Img.getInstance();
	private MainItem _mainMenuItem = new MainItem();
	String topic = "";
	long _lastUSN;
	String _fileFullName = "";
	boolean alreadyPush = false;
	static VideoConnectScreen instance;

	VideoListField _list;
	Vector all = new Vector();
	EditFieldwithFocus searchTextField;
	public VerticalFieldManager commentsManager = new VerticalFieldManager();
	public LoadPicThread loader;
	

	public static VideoConnectScreen getInstance() {
		if (instance == null) {
			instance = new VideoConnectScreen();
		}
		return instance;
	}

	

	private VideoConnectScreen() {
		super(MODE_VIDEOCONNECT);
		nextBT.setChangeListener(this);
		previousBT.setChangeListener(this);

		UiApplication.getUiApplication().addFileSystemJournalListener(this);
		_lastUSN = FileSystemJournal.getNextUSN();
		createVideoMain();
	}

	private VideoConnectScreen(String topic) {
		super(MODE_VIDEOCONNECT);
		UiApplication.getUiApplication().addFileSystemJournalListener(this);
		_lastUSN = FileSystemJournal.getNextUSN();
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
					// CrieUtils.browserURL(picInfo.getVideoUrl());
					// final String choices[] = { "By Media Player",
					// "Inside Application" };
					// final int values[] = { Dialog.OK, Dialog.CANCEL };
					// Dialog dia = new Dialog("How do you want to play?",
					// choices, values, Dialog.OK, Bitmap
					// .getPredefinedBitmap(Bitmap.INFORMATION), 0);
					// int result = dia.doModal();
					// if (result == Dialog.OK) {
					// CrieUtils.browserURL(picInfo.getVideoUrl());
					// } else if (result == Dialog.CANCEL) {
					// VideoPlayerScreen player = VideoPlayerScreen
					// .getInstance();
					// UiApplication.getUiApplication().pushScreen(player);
					// player.playFile(picInfo.getVideoUrl());
					// }
					UiApplication.getUiApplication().pushModalScreen(
							new VideoConnectDetail(picInfo));
				}
			}
		}
	}

	BitmapFieldWithStatus bf;

	public void setStatus(String status) {
		bf.setStatus(status);
	}

	public void createVideoMain() {
		// videoList = HttpConn.getList(topic, Const.type_movie);
		XYEdges edge = new XYEdges(24, 25, 8, 25);

		Bitmap img = imgstock.getHeader();
		bf = new BitmapFieldWithStatus(img, BitmapField.FIELD_HCENTER
				| BitmapField.USE_ALL_WIDTH, "");
		add(bf);
		Engine.getInstance().registerStatus(bf);
		try {
			RadioButtonGroup rgrp = new RadioButtonGroup();

			edge = new XYEdges(5, 25 * Display.getWidth() / 480, 2,
					25 * Display.getWidth() / 480);

			HorizontalFieldManager mainHorizontalManager = new HorizontalFieldManager(
					HorizontalFieldManager.FIELD_HCENTER
							| HorizontalFieldManager.USE_ALL_WIDTH
							| HorizontalFieldManager.FIELD_VCENTER);
			// mainHorizontalManager.setBorder(BorderFactory.createSimpleBorder(
			// edge, Border.STYLE_TRANSPARENT));

			// EditField
			searchTextField = new EditFieldwithFocus(
					"Search: ",
					topic,
					35,
					BasicEditField.FIELD_HCENTER | BasicEditField.FIELD_VCENTER,
					MyColor.FONT_SEARCH, MyColor.SEARCH_COLOR) {
				public void layout(int width, int height) {
					super.layout(getPreferredWidth(), getPreferredHeight());
					setExtent(getPreferredWidth(), getPreferredHeight());
				}

				public int getPreferredHeight() {
					return 25;
				}

				public int getPreferredWidth() {
					return 340 * Display.getWidth() / 480;
				}
			};
			// edit.setMaxSize(35);
			// edit.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
			// add(edit);
			mainHorizontalManager.setMargin(edge);
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
			// button
			HorizontalFieldManager buttonManager = new HorizontalFieldManager(
					HorizontalFieldManager.FIELD_HCENTER
							| HorizontalFieldManager.USE_ALL_WIDTH);
			// buttonManager.setBorder(BorderFactory.createSimpleBorder(edge,
			// Border.STYLE_TRANSPARENT));

			// MyButtonField playButton = new MyButtonField("Play",
			// ButtonField.ELLIPSIS);
			// stopButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
			// playButton
			// .setChangeListener(new ButtonListener(rgrp, videoList, 26));
			// mainHorizontalManager.add(playButton);

			MyButtonField shareButton = new MyButtonField("Upload a Video",
					ButtonField.ELLIPSIS);
			// stopButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
			shareButton.setChangeListener(new ButtonListener(rgrp, 21));
			buttonManager.add(shareButton);

			MyButtonField button = new MyButtonField("Capture Live",
					ButtonField.ELLIPSIS);
			// stopButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));

			button.setChangeListener(new ButtonListener(null, searchTextField,
					28));
			buttonManager.add(button);
			buttonManager.setMargin(edge);
			videoManager.add(buttonManager);

			videoManager.add(mainHorizontalManager);
			// topicBitmap.setMargin(edge);
			// topicBitmap.setBorder(BorderFactory.createSimpleBorder(edge,
			// Border.STYLE_TRANSPARENT));
			// videoManager.add(topicBitmap);
			LabelField topicLabel = new LabelFieldWithFullBG("Video Connect",
					MyColor.FONT_TOPIC, MyColor.FONT_TOPIC_COLOR, MyColor.TOPIC_BG,
					Const.LABEL_WIDTH);
			edge = new XYEdges(2, 25 * Display.getWidth() / 480, 2,
					25 * Display.getWidth() / 480);
			topicLabel.setMargin(edge);
			videoManager.add(topicLabel);
			_list = new VideoListField();
			videoManager.add(_list);
			pagingManager.setMargin(edge);
			videoManager.add(pagingManager);
			edge = new XYEdges(2, 25, 2, 25);
			// RadioButtonField (must be part of group)

			add(videoManager);

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

	// protected boolean keyDown(int arg0, int arg1) {
	// // TODO Auto-generated method stub
	// try {
	// switch (arg0) {
	// case 1179648:
	// close();
	// break;
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// handleSearchType();
	// return super.keyDown(arg0, arg1);
	// }

	public boolean keyChar(char c, int status, int time) {

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

	public void fileJournalChanged() {
		long nextUSN = FileSystemJournal.getNextUSN();
		String msg = null;
		for (long lookUSN = nextUSN - 1; lookUSN >= _lastUSN && msg == null; --lookUSN) {
			FileSystemJournalEntry entry = FileSystemJournal.getEntry(lookUSN);
			if (entry == null) { // we didn't find an entry.
				break;
			}

			// check if this entry was added or deleted
			final String path = entry.getPath();
			System.out.println("Pathhhhhhhhhhhhhhhhhh:" + path);
			if (path != null) {
				switch (entry.getEvent()) {
				case FileSystemJournalEntry.FILE_ADDED:
					System.out.println("\n-----\n FILE_ADDED \n");
					// LogScreen.debug("File added " + path);
					try {
						msg = " File Added: " + entry.getPath();
						_fileFullName = entry.getPath();
						if (_fileFullName.toLowerCase().endsWith("3gp")
								|| _fileFullName.toLowerCase().endsWith("mp5")) {

							if (!alreadyPush) {
								Engine.getInstance().picInfo
										.setLocalFilename("file://" + path);
								// videoScr = VideoSendScreen
								// .getInstance("file://" + path);
								System.out.println("taking screen shot");

								// delayAndTakeScreenShot();
							} else {
								Engine.getInstance().picInfo
										.setLocalFilename("file://" + path);
								// videoScr = VideoSendScreen
								// .getInstance("file://" + path);
							}
						}

						break;
					} catch (Exception e) {
						Dialog.alert("Error" + e.toString());
					}
				case FileSystemJournalEntry.FILE_DELETED:
					System.out.println("\n-----\n FILE_DELETED \n");
					// LogScreen.debug("File delete " + path);
					msg = " File Deleted: " + entry.getPath();
					_fileFullName = entry.getPath();
					if (_fileFullName.toLowerCase().indexOf("3gp") >= 0
							|| _fileFullName.toLowerCase().indexOf("mp5") >= 0) {
					}
					break;
				case FileSystemJournalEntry.FILE_CHANGED:
					System.out.println("\n-----\n FILE_CHANGED \n");
					// LogScreen.debug( " File change " + path);
					_fileFullName = entry.getPath();
					// checkFinishThread.lastFileChange = System
					// .currentTimeMillis();
					if (_fileFullName.toLowerCase().endsWith("3gp")
							|| _fileFullName.toLowerCase().endsWith("mp5")) {

						final Engine engine = Engine.getInstance();
						// if (CrieUtils.getFileSize("file://" + path) > 0) {
						if (!alreadyPush) {
							System.out.println("taking snapshot");
							alreadyPush = true;
						} else {
							System.out.println("already pushhhhhhhh");
						}

						// }

					}
					break;
				}
			}
		}

		_lastUSN = nextUSN;

	}

	protected void onExposed() {
		if (loader != null) {
			loader.myResume();
		}
		System.out.println("on Expose "
				+ Engine.getInstance().picInfo.getLocalFilename());
		System.out.println(" file size  ");
		if (!Engine.getInstance().picInfo.getLocalFilename().equals("")
				&& CrieUtils.getFileSize(Engine.getInstance().picInfo
						.getLocalFilename()) > 0) {
			Engine.getInstance().sendVideo();
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
			Engine.getInstance().viewVideoConnect(currentIndex,search);
		} else if (btnField == previousBT) {
//			Dialog.alert("prev");
			currentIndex = currentIndex - Const.NUM_LIST;
			UiApplication.getUiApplication().pushScreen(WaitScreen.getInstance());
			Engine.getInstance().viewVideoConnect(currentIndex,search);
		}else if (btnField == searchBT) {
			search = searchTextField.getText();
			UiApplication.getUiApplication().pushScreen(WaitScreen.getInstance());
			Engine.getInstance().viewVideoConnect(currentIndex, search);
		}
	}
}
