package com.tssoftgroup.tmobile.screen;

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
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.HorizontalFieldManager;

import com.tssoftgroup.tmobile.component.BitmapFieldWithStatus;
import com.tssoftgroup.tmobile.component.ButtonListener;
import com.tssoftgroup.tmobile.component.EditFieldwithFocus;
import com.tssoftgroup.tmobile.component.LabelFieldWithFullBG;
import com.tssoftgroup.tmobile.component.MainListVerticalFieldManager;
import com.tssoftgroup.tmobile.component.MyButtonField;
import com.tssoftgroup.tmobile.component.TrainingListField;
import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.model.DocumentInfo;
import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.CrieUtils;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.MyColor;

public class DocumentListScreen extends FixMainScreen implements FieldChangeListener {
	private MainItem _mainMenuItem = new MainItem();
	private String topic = "";
	Img imgstock = Img.getInstance();
	TrainingListField _list;
	private static DocumentListScreen instance;

	EditFieldwithFocus searchTextField;
	Vector all = new Vector();

	public static DocumentListScreen getInstance() {
		if (instance == null) {
			instance = new DocumentListScreen();
		}
		return instance;
	}

	private DocumentListScreen() {
		super (MODE_DOC);
		nextBT.setChangeListener(this);
		previousBT.setChangeListener(this);
		runMain();
	}

	public void setList(Vector trainInfos) {
		_list.removeAll();
		all.removeAllElements();
		for (int i = 0; i < trainInfos.size(); i++) {
			_list.add((DocumentInfo) trainInfos.elementAt(i));
			all.addElement((DocumentInfo) trainInfos.elementAt(i));
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

	private void selectAction() {
		if (_list != null && _list.getSize() > 0) {

			if (_list.get(_list, _list.getSelectedIndex()) != null) {
				DocumentInfo trainInfo = (DocumentInfo) _list.get(_list, _list
						.getSelectedIndex());
				if (trainInfo != null) {
					DocumentDetailScreen scr = new DocumentDetailScreen(trainInfo);
					UiApplication.getUiApplication().pushScreen(scr);
				}
			}
		}
	}

	private void handleSearchType() {
		if (searchTextField.isFocus) {
			String text = searchTextField.getText();
			_list.removeAll();
			_list.invalidate();
			for (int i = 0; i < all.size(); i++) {
				DocumentInfo picInfo = (DocumentInfo) all.elementAt(i);
				if (picInfo.containKey(text)) {
					_list.add(picInfo);
				}
			}
			_list.invalidate();
		}
	}

	public void runMain() {

		Bitmap img = imgstock.getHeader();
		BitmapFieldWithStatus bf = new BitmapFieldWithStatus(img,
				BitmapField.FIELD_HCENTER | BitmapField.USE_ALL_WIDTH, "");
		add(bf);
		Engine.getInstance().registerStatus(bf);
		try {
			MainListVerticalFieldManager mainManager = new MainListVerticalFieldManager();
			XYEdges edge = new XYEdges(5, 25* Display.getWidth() / 480, 2, 25* Display.getWidth() / 480);
			HorizontalFieldManager mainHorizontalManager = new HorizontalFieldManager(
					HorizontalFieldManager.FIELD_HCENTER
							| HorizontalFieldManager.USE_ALL_WIDTH
							| HorizontalFieldManager.FIELD_VCENTER);
			RadioButtonGroup rgrp = new RadioButtonGroup();

			// EditField

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
			// edit.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
			// add(edit);
			MyButtonField button = new MyButtonField("Share a file",
					ButtonField.ELLIPSIS);
			// stopButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
			button.setChangeListener(new ButtonListener(rgrp, 12));
			button.setMargin(edge);
			mainManager.add(button);
//			searchTextField.setMargin(edge);
			mainHorizontalManager.add(searchTextField);
			mainHorizontalManager.add(searchBT);
			mainHorizontalManager.setMargin(edge);
			mainManager.add(mainHorizontalManager);
			// BitmapField bff = new BitmapField(img,
			// BitmapField.NON_FOCUSABLE);
			// mainManager.add(bff);
			LabelField topicLabel = new LabelFieldWithFullBG("Document Sharing",
					MyColor.FONT_TOPIC, MyColor.FONT_TOPIC_COLOR, MyColor.TOPIC_BG, Const.LABEL_WIDTH);
			edge = new XYEdges(2, 25* Display.getWidth() / 480, 2, 25* Display.getWidth() / 480);
			topicLabel.setMargin(edge);

			mainManager.add(topicLabel);
			_list = new TrainingListField();
			mainManager.add(_list);
			pagingManager.setMargin(edge);
			mainManager.add(pagingManager);
			add(mainManager);
		} catch (Exception e) {
			System.out.println("" + e.toString());
		}
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
		handleSearchType();
		return super.keyDown(arg0, arg1);
	}

	public boolean keyChar(char c, int status, int time) {
		handleSearchType();
		switch (c) {
		case Characters.ENTER:
			return true;
		case Characters.ESCAPE:
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
		System.out.println("on Expose "
				+ Engine.getInstance().docInfo.getLocalFilename());
		System.out.println(" file size  ");
		if (!Engine.getInstance().docInfo.getLocalFilename().equals("")
				&& CrieUtils.getFileSize(Engine.getInstance().docInfo
						.getLocalFilename()) > 0) {
			Engine.getInstance().sendDocument();
		}

	}

	protected void onUiEngineAttached(boolean attached) {
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
			Engine.getInstance().getDocument(currentIndex,search);
		} else if (btnField == previousBT) {
//			Dialog.alert("prev");
			currentIndex = currentIndex - Const.NUM_LIST;
			UiApplication.getUiApplication().pushScreen(WaitScreen.getInstance());
			Engine.getInstance().getDocument(currentIndex,search);
		}else if (btnField == searchBT) {
			search = searchTextField.getText();
			UiApplication.getUiApplication().pushScreen(WaitScreen.getInstance());
			Engine.getInstance().getDocument(currentIndex, search);
		}
	}
}