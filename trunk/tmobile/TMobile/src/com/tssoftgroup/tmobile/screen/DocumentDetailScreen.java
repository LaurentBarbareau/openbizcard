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

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.tssoftgroup.tmobile.component.BitmapFieldWithStatus;
import com.tssoftgroup.tmobile.component.ButtonListener;
import com.tssoftgroup.tmobile.component.CrieLabelField;
import com.tssoftgroup.tmobile.component.DocFilenameDialog;
import com.tssoftgroup.tmobile.component.LabelFieldWithFullBG;
import com.tssoftgroup.tmobile.component.MainListVerticalFieldManager;
import com.tssoftgroup.tmobile.component.MyButtonField;
import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.model.DocumentInfo;
import com.tssoftgroup.tmobile.model.User;
import com.tssoftgroup.tmobile.utils.CrieUtils;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.MyColor;
import com.tssoftgroup.tmobile.utils.Scale;

public class DocumentDetailScreen extends FixMainScreen implements
		FieldChangeListener {
	private MainItem _mainMenuItem = new MainItem();
	Img imgStock = Img.getInstance();
	String videoPath = "";
	HorizontalFieldManager thumnailPlayDurationManager = new HorizontalFieldManager();
	DocumentInfo docInfo;
	BitmapFieldWithStatus bf = new BitmapFieldWithStatus(imgStock.getHeader(),
			BitmapField.FIELD_HCENTER | BitmapField.USE_ALL_WIDTH, "");

	public DocumentDetailScreen(DocumentInfo docInfo) {
		super (MODE_DOC);
		this.docInfo = docInfo;
		XYEdges edge = new XYEdges(10, 25, 10, 25);
		add(bf);

		// try {
		MainListVerticalFieldManager mainManager = new MainListVerticalFieldManager();

		// select a file label
		LabelField titleLabel = new LabelFieldWithFullBG(docInfo.getTitle(),
				MyColor.FONT_TOPIC, MyColor.FONT_TOPIC_COLOR, MyColor.TOPIC_BG, Display
						.getWidth()
						- 50 * Display.getWidth() / 480);
		edge = new XYEdges(10, 25 * Display.getWidth() / 480, 10, 25 * Display
				.getWidth() / 480);
		titleLabel.setMargin(edge);
		mainManager.add(titleLabel);
		// 
		CrieLabelField descriptionLabel = new CrieLabelField(docInfo
				.getDescription(), MyColor.FONT_DESCRIPTION,
				Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT,
				LabelField.NON_FOCUSABLE);
		descriptionLabel.setMargin(edge);
		mainManager.add(descriptionLabel);

		LabelField fileInfoLB = new LabelFieldWithFullBG("file",
				MyColor.COMMENT_LABEL_FONT, MyColor.COMMENT_LABEL_FONT_COLOR, MyColor.COMMENT_LABEL_BG,
				Display.getWidth() - 50 * Display.getWidth() / 480);
		fileInfoLB.setMargin(edge);
		mainManager.add(fileInfoLB);
		// Deal with Comment

		HorizontalFieldManager buttonHorizontalManager = new HorizontalFieldManager(
				HorizontalFieldManager.FIELD_HCENTER
						| HorizontalFieldManager.USE_ALL_WIDTH);

		CrieLabelField filenameLabel = new CrieLabelField(""
				+ docInfo.getFileName(), MyColor.FONT_DESCRIPTION,
				Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT,
				LabelField.NON_FOCUSABLE);

		MyButtonField downloadButton = new MyButtonField("Download",
				ButtonField.ELLIPSIS);
		MyButtonField emailButton = new MyButtonField("Email Me",
				ButtonField.ELLIPSIS);
		downloadButton.setChangeListener(this);
		emailButton.setChangeListener(new EmailListener());
		MyButtonField backButton = new MyButtonField("Back",
				ButtonField.ELLIPSIS);
		backButton.setMargin(edge);
		backButton.setChangeListener(new ButtonListener(20));

		buttonHorizontalManager.setMargin(edge);
		filenameLabel.setMargin(edge);
		mainManager.add(filenameLabel);
		edge = new XYEdges(0, 0, 0, 10);
		downloadButton.setMargin(edge);
		backButton.setMargin(edge);
		buttonHorizontalManager.add(downloadButton);
		buttonHorizontalManager.add(emailButton);
		buttonHorizontalManager.add(backButton);
		mainManager.add(buttonHorizontalManager);
		add(mainManager);
		// } catch (Exception e) {
		// System.out.println("" + e.toString());
		// }

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

	public static final int CHUNKSIZE = 4000;

	public void fieldChanged(Field field, int context) {
		DocFilenameDialog.getInstance(docInfo.getHTTPfilePath(),
				docInfo.getFileName()).myshow();
		// CrieUtils.browserURL(docInfo.getHTTPfilePath());
	}

	class EmailListener implements FieldChangeListener {
		public void fieldChanged(Field field, int context) {
			Engine.getInstance().sendEmailDoc(docInfo.getId());
		}
	}
}
