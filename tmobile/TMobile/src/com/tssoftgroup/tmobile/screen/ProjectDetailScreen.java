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

import com.tssoftgroup.tmobile.component.ButtonListener;
import com.tssoftgroup.tmobile.component.CrieLabelField;
import com.tssoftgroup.tmobile.component.LabelFieldWithFullBG;
import com.tssoftgroup.tmobile.component.MainListVerticalFieldManager;
import com.tssoftgroup.tmobile.component.MyButtonField;
import com.tssoftgroup.tmobile.model.ProjectInfo;
import com.tssoftgroup.tmobile.model.User;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.MyColor;
import com.tssoftgroup.tmobile.utils.Scale;

public class ProjectDetailScreen extends FixMainScreen implements
		FieldChangeListener {
	private MainItem _mainMenuItem = new MainItem();
	private Vector contactList = null;

	EditField postCommentEditField = null;
	Img imgStock = Img.getInstance();
	String videoPath = "";
	HorizontalFieldManager thumnailPlayDurationManager = new HorizontalFieldManager();
	public VerticalFieldManager contactsManager = new VerticalFieldManager();

	public ProjectDetailScreen(ProjectInfo projectInfo) {
		super(MODE_CONTACT);
		XYEdges edge = new XYEdges(5, 25 * Display.getWidth() / 480, 5,
				25 * Display.getWidth() / 480);
		Bitmap img = imgStock.getHeader();
		BitmapField bf = new BitmapField(img, BitmapField.FIELD_HCENTER
				| BitmapField.USE_ALL_WIDTH);
		add(bf);

		// try {
		MainListVerticalFieldManager mainManager = new MainListVerticalFieldManager();

		// select a file label
		LabelField titleLabel = new LabelFieldWithFullBG(
				projectInfo.getTitle(), MyColor.FONT_TOPIC, 0xffffff,
				MyColor.TOPIC_BG, Display.getWidth() - 50 * Display.getWidth()
						/ 480);
		titleLabel.setMargin(edge);
		mainManager.add(titleLabel);
		// 
		CrieLabelField descriptionLabel = new CrieLabelField(projectInfo
				.getDescription(), 0x00,
				Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT,
				LabelField.NON_FOCUSABLE);
		descriptionLabel.setMargin(edge);
		descriptionLabel.isFix = true;
		mainManager.add(descriptionLabel);

		LabelField contactLB = new LabelFieldWithFullBG("contacts",
				MyColor.FONT_TOPIC, 0xffffff, MyColor.TOPIC_BG, Display
						.getWidth()
						- 50 * Display.getWidth() / 480);
		contactLB.setMargin(edge);
		mainManager.add(contactLB);
		// Deal with Comment
		contactList = new Vector();

		// temp data
		// String[] comment1 = { "AAA", "BBBB", "CCCCC" };
		// String[] comment2 = { "AAA2", "BBBB2", "CCCCC2" };
		for (int i = 0; i < projectInfo.getUsers().size(); i++) {
			User contact = (User) projectInfo.getUsers().elementAt(i);
			String[] comment = { contact.getName(), contact.getEmail(),
					contact.getMobile(), contact.getPosition() ,contact.getPhone() };
			contactList.addElement(comment);
		}
		// commentList.addElement(comment1);
		// commentList.addElement(comment2);
		if (contactList != null && contactList.size() > 0) {
			for (int i = 0; i < contactList.size(); i++) {
				String[] commentArr = (String[]) contactList.elementAt(i);
				String positionStr = commentArr[3].equals("") ? "" : "("
						+ commentArr[3] + ")";
				LabelField nameLabel = new LabelFieldWithFullBG(commentArr[0]
						+ positionStr, MyColor.COMMENT_LABEL_FONT, 0x00,
						MyColor.COMMENT_LABEL_BG, Display.getWidth() - 50
								* Display.getWidth() / 480);
				// nameLabel.setMargin(edge);

				nameLabel.setFont(nameLabel.getFont().derive(Font.BOLD));
				contactsManager.add(nameLabel);
				CrieLabelField emailLabel = new CrieLabelField( commentArr[1], 0x00,
						Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT
								- (Display.getWidth() > 350 ? 4 : 2),
						LabelField.FOCUSABLE);
				contactsManager.add(emailLabel);
				emailLabel.isFix = true;
				CrieLabelField mobileLabel = new CrieLabelField(commentArr[2], 0x00,
						Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT
								- (Display.getWidth() > 350 ? 4 : 2),
						LabelField.FOCUSABLE);
				contactsManager.add(mobileLabel);
				mobileLabel.isFix = true;
				CrieLabelField phoneLabel = new CrieLabelField(commentArr[4], 0x00,
						Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT
								- (Display.getWidth() > 350 ? 4 : 2),
						LabelField.FOCUSABLE);
				contactsManager.add(phoneLabel);
				phoneLabel.isFix = true;
			}
		}
		contactsManager.setMargin(edge);
		mainManager.add(contactsManager);

		HorizontalFieldManager buttonHorizontalManager = new HorizontalFieldManager(
				HorizontalFieldManager.FIELD_HCENTER
						| HorizontalFieldManager.USE_ALL_WIDTH);

		MyButtonField backButton = new MyButtonField("Back",
				ButtonField.ELLIPSIS);
		backButton.setMargin(edge);
		backButton.setChangeListener(new ButtonListener(20));
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

	public void fieldChanged(Field field, int context) {
		ButtonField btnField = (ButtonField) field;
		if (btnField.getLabel().equals("Submit")) {
			if (postCommentEditField.getText() == null
					|| postCommentEditField.getText().equals("")) {
				Dialog.alert("Please enter a comment");
			} else {
				// HttpConn.postComment(arr[0], edit.getText(),
				// Const.type_movie_comment);
				// UiApplication.getUiApplication().popScreen(
				// UiApplication.getUiApplication().getActiveScreen());
				// UiApplication.getUiApplication().pushModalScreen(
				// new VideoConnectDetail());
			}
		}
	}
}
