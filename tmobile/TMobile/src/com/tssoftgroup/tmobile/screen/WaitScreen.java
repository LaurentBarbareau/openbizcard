package com.tssoftgroup.tmobile.screen;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.tssoftgroup.tmobile.component.AnimatedGIFField;
import com.tssoftgroup.tmobile.component.CrieLabelField;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.MyColor;

public class WaitScreen extends PopupScreen {
	private static WaitScreen instance = null;
	CrieLabelField label = new CrieLabelField("Waiting", MyColor.FONT_WAIT,13 , Field.NON_FOCUSABLE);
	HorizontalFieldManager gifManager;
	HorizontalFieldManager textManager;
	
	private WaitScreen() {
//		Manager _manager = (VerticalFieldManager) getMainManager();
//		Bitmap bgBmp = Img.getInstance().getBackground();
//		Background bg = BackgroundFactory.createBitmapBackground(bgBmp);
//		if (bgBmp.getWidth() < Display.getWidth()
//				|| bgBmp.getHeight() < Display.getHeight()) {
//			EncodedImage encodeBg = Img.getInstance().getBackgroundEncoded();
//			EncodedImage newBg = CrieUtils.scaleImageToWidthHeight(encodeBg,
//					Display.getWidth(), Display.getHeight());
//			bg = BackgroundFactory.createBitmapBackground(newBg.getBitmap());
//		}
//		_manager.setBackground(bg);
		super(new VerticalFieldManager());
//		setTitle(header);
		gifManager = new HorizontalFieldManager(Field.FIELD_HCENTER);
		AnimatedGIFField gifImg = new AnimatedGIFField(Img.getInstance()
				.getLoading());
		gifManager.add(gifImg);
		// Center of Screen
		int leftEmptySpace = (this.getWidth() - gifManager
				.getPreferredWidth()) / 2;
		int topEmptySpace = (this.getHeight() - gifManager
				.getPreferredHeight()) / 2;
		// make sure the picture fits to the screen
		if (topEmptySpace >= 0 && leftEmptySpace >= 0) {
			gifManager.setMargin(0, 0, 0, 0);
		}
		add(gifManager);
		textManager = new HorizontalFieldManager(Field.FIELD_HCENTER);
		textManager.add(label);
		textManager.setMargin(20, 0, 0, 0);
		add(textManager);
	}

	public static WaitScreen getInstance() {
		if (instance == null) {
			instance = new WaitScreen();
		}
		
		// Center of Screen
		int leftEmptySpace = (Display.getWidth() - instance.gifManager
				.getPreferredWidth()) / 2;
		int topEmptySpace = (Display.getHeight() - instance.gifManager
				.getPreferredHeight()) / 2;
		// make sure the picture fits to the screen
		if (topEmptySpace >= 0 && leftEmptySpace >= 0) {
			instance.gifManager.setMargin(0, 0, 0, 0);
		}
		return instance;
	}

	public void setText(String text) {
		label.setText(text);
	}

	public boolean keyChar(char c, int status, int time) {
		switch (c) {
		case Characters.ESCAPE:
			System.exit(0);
			return super.keyChar(c, status, time);
		default:
			return super.keyChar(c, status, time);
		}
	}
	protected boolean onSavePrompt() {
		return true;
	}
}
