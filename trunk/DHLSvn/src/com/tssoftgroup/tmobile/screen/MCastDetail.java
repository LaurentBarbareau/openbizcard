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
import com.tssoftgroup.tmobile.component.CustomButtonField;
import com.tssoftgroup.tmobile.component.LabelFieldWithFullBG;
import com.tssoftgroup.tmobile.component.MainListVerticalFieldManager;
import com.tssoftgroup.tmobile.component.MyButtonField;
import com.tssoftgroup.tmobile.component.ScreenWithComment;
import com.tssoftgroup.tmobile.model.Comment;
import com.tssoftgroup.tmobile.model.MoreInfo;
import com.tssoftgroup.tmobile.model.PicInfo;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.MyColor;
import com.tssoftgroup.tmobile.utils.Scale;
import com.tssoftgroup.tmobile.utils.Wording;

public class MCastDetail extends FixMainScreen implements FieldChangeListener,
		ScreenWithComment {
	private MainItem _mainMenuItem = new MainItem();
	private Vector commentList = null;
	private Vector moreinfoList = null;
	EditField postCommentEditField = null;
	Img imgStock = Img.getInstance();
	// MyButtonField playButton = new MyButtonField("Play",
	// ButtonField.ELLIPSIS);
	CustomButtonField playButtonImg = new CustomButtonField(null, imgStock
			.getPlay(), imgStock.getPlayOn());
	String videoPath = "";
	HorizontalFieldManager thumnailDescDurationPlayManager = new HorizontalFieldManager();
	VerticalFieldManager descDurationPlayManager = new VerticalFieldManager();
	public VerticalFieldManager commentsManager = new VerticalFieldManager();

	public MCastDetail(PicInfo picinfo) {
		super(MODE_MCAST);
		this.videoPath = picinfo.getVideoUrl();
		XYEdges edge = new XYEdges(24, 25, 8, 25);

		Bitmap img = imgStock.getHeader();
		BitmapField bf = new BitmapField(img, BitmapField.FIELD_HCENTER
				| BitmapField.USE_ALL_WIDTH);
		add(bf);

		try {
			MainListVerticalFieldManager mainManager = new MainListVerticalFieldManager();

			edge = new XYEdges(2, 35, 17, 35);

			commentList = new Vector();
			moreinfoList = new Vector();
			// / add data to moreinfo list
			for (int i = 0; i < picinfo.comments.size(); i++) {
				Comment commment = (Comment) picinfo.comments.elementAt(i);
				String[] comment = { commment.getComment(), commment.getTime(),
						commment.getUser() };
				commentList.addElement(comment);
			}

			// / add data to moreinfo list
			for (int i = 0; i < picinfo.moreInfos.size(); i++) {
				MoreInfo moreinfo = (MoreInfo) picinfo.moreInfos.elementAt(i);
				String[] more = { moreinfo.getTitle(), moreinfo.getID() };
				moreinfoList.addElement(more);
			}
			LabelField titleLabel = new LabelFieldWithFullBG(
					picinfo.getTitle(), MyColor.FONT_TOPIC, 0xffffff,
					MyColor.TOPIC_BG, Display.getWidth() - 50
							* Display.getWidth() / 480);
			edge = new XYEdges(2, 25 * Display.getWidth() / 480, 2,
					25 * Display.getWidth() / 480);
			titleLabel.setMargin(edge);
			mainManager.add(titleLabel);
			// 
			playButtonImg.setChangeListener(new ButtonListener(picinfo, 32));
			int thumbWidth = 0;
			if (picinfo.getThumbnail() != null) {
				BitmapField thumb = new BitmapField(picinfo.getThumbnail());
				thumb.setMargin(5, 0, 0, 0);
				thumnailDescDurationPlayManager.add(thumb);
				thumbWidth = thumb.getWidth() + 20;
			}

			HorizontalFieldManager durationPlayManager = new HorizontalFieldManager();
			// /
			CrieLabelField durLabel = new CrieLabelField("duration "
					+ picinfo.getDuration(), MyColor.LIST_DESC_FONT,
					Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT
							- (Display.getWidth() > 350 ? 5 : 0),
					LabelField.NON_FOCUSABLE);
			durationPlayManager.add(durLabel);
			// playButtonImg.setMargin(0, 0, 0, Display.getWidth() - 50
			// - durLabel.getWidth() - playButtonImg.getWidth());
			playButtonImg.setMargin(0, 0, 0, 20);
			durationPlayManager.add(playButtonImg);
			CrieLabelField descriptionLabel = new CrieLabelField(picinfo
					.getDescription(), 0x00,
					Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT,
					LabelField.NON_FOCUSABLE);

			// descriptionLabel.setMargin(0, 25 * Display.getWidth() / 480, 0,
			// 25
			// * Display.getWidth() / 480 );
			descDurationPlayManager.setMargin(0, 25 * Display.getWidth() / 480,
					0, 10);
			// descriptionLabel.setMargin(0, 25, 0, 0);
			descDurationPlayManager.add(descriptionLabel);
			descriptionLabel.isFix = true;
			descriptionLabel.otherMinusWidth = thumbWidth
					+ descriptionLabel.getManager().getMarginLeft()
					+ descriptionLabel.getManager().getMarginRight();
			// descriptionLabel.otherMinusWidth = 200;
			descDurationPlayManager.add(durationPlayManager);
			thumnailDescDurationPlayManager.add(descDurationPlayManager);
			thumnailDescDurationPlayManager.setMargin(edge);

			mainManager.add(thumnailDescDurationPlayManager);

			// mainManager.add(descriptionLabel);
			// CommentListVerticalFieldManager listVerticalManager = new
			// CommentListVerticalFieldManager();
			LabelField commentLB = new LabelFieldWithFullBG("comments",
					MyColor.COMMENT_LABEL_FONT, 0x00, MyColor.COMMENT_LABEL_BG,
					Display.getWidth() - 50 * Display.getWidth() / 480);

			// LabelField commentLB = new LabelField("Comments:");
			edge = new XYEdges(Scale.EDGE, 25 * Display.getWidth() / 480,
					Scale.EDGE, 25 * Display.getWidth() / 480);
			commentLB.setMargin(edge);
			commentLB.setFont(Scale.FONT_DETAIL_TITLE);
			// commentsManager.add(commentLB);
			if (commentList != null && commentList.size() > 0) {
				for (int i = 0; i < commentList.size(); i++) {
					String[] commentArr = (String[]) commentList.elementAt(i);
					CrieLabelField commentLabel = new CrieLabelField("By "
							+ commentArr[2] + " at " + commentArr[1] + ": ",
							MyColor.LIST_DESC_FONT,
							Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT
									- (Display.getWidth() > 350 ? 8 : 2),
							LabelField.FOCUSABLE);
					commentLabel.isFix = true;
					// commentLabel.setBorder(BorderFactory.createSimpleBorder(
					// edge, Border.STYLE_TRANSPARENT));
					edge = new XYEdges(2, 35 * Display.getWidth() / 480, 2,
							35 * Display.getWidth() / 480);
					commentLabel.setMargin(edge);
					commentsManager.add(commentLabel);
					commentLabel = new CrieLabelField(commentArr[0], 0x00,
							Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT,
							LabelField.FOCUSABLE);
					commentLabel.isFix = true;
					// commentLabel.setBorder(BorderFactory.createSimpleBorder(
					// edge, Border.STYLE_TRANSPARENT));
					commentLabel.setMargin(edge);
					commentsManager.add(commentLabel);
				}
			} else if (commentList.size() == 0) {
				CrieLabelField commentLabel = new CrieLabelField(
						Wording.NO_COMMENT, MyColor.LIST_DESC_FONT,
						Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT
								- (Display.getWidth() > 350 ? 8 : 2),
						LabelField.FOCUSABLE);
				commentLabel.isFix = true;
				edge = new XYEdges(2, 35 * Display.getWidth() / 480, 2,
						35 * Display.getWidth() / 480);
				commentLabel.setMargin(edge);
				commentsManager.add(commentLabel);
			}
			FixMainScreen.processHaveComment(commentsManager, picinfo,
					this);
			// Deal with more info
			VerticalFieldManager listVerticalManager = new VerticalFieldManager();
			// LabelField moreinfoLB = new LabelField("More Info:");
			LabelField moreinfoLB = new LabelFieldWithFullBG("more info",
					MyColor.COMMENT_LABEL_FONT, 0x00, MyColor.COMMENT_LABEL_BG,
					Display.getWidth() - 50 * Display.getWidth() / 480);
			edge = new XYEdges(Scale.EDGE, 25 * Display.getWidth() / 480,
					Scale.EDGE, 25 * Display.getWidth() / 480);
			moreinfoLB.setMargin(edge);
			moreinfoLB.setFont(Scale.FONT_DETAIL_TITLE);
			listVerticalManager.add(moreinfoLB);

			if (moreinfoList != null && moreinfoList.size() > 0) {
				for (int i = 0; i < moreinfoList.size(); i++) {
					HorizontalFieldManager moreinfoManager = new HorizontalFieldManager();
					String[] moreinfoArr = (String[]) moreinfoList.elementAt(i);
					// / Label
					CrieLabelField commentLabel = new CrieLabelField(
							moreinfoArr[0], 0x00,
							Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT,
							LabelField.NON_FOCUSABLE);
					// commentLabel.isFix = true;
					// commentLabel.setBorder(BorderFactory.createSimpleBorder(
					// edge, Border.STYLE_TRANSPARENT));
					// / Button
					MyButtonField emailButton = new MyButtonField("Email Me",
							ButtonField.ELLIPSIS);

					emailButton.setChangeListener(new ButtonListener(
							moreinfoArr[1], 33));

					moreinfoManager.add(commentLabel);
					moreinfoManager.add(emailButton);
					edge = new XYEdges(2, 35 * Display.getWidth() / 480, 2,
							35 * Display.getWidth() / 480);
					moreinfoManager.setMargin(edge);
					listVerticalManager.add(moreinfoManager);
				}
			} else if (moreinfoList.size() == 0) {
				CrieLabelField moreinfoLabel = new CrieLabelField(
						Wording.NO_MORE_INFO, MyColor.LIST_DESC_FONT,
						Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT
								- (Display.getWidth() > 350 ? 8 : 2),
						LabelField.FOCUSABLE);
				moreinfoLabel.isFix = true;
				edge = new XYEdges(2, 35 * Display.getWidth() / 480, 2,
						35 * Display.getWidth() / 480);
				moreinfoLabel.setMargin(edge);
				listVerticalManager.add(moreinfoLabel);
			}
			/*
			 * label = new
			 * LabelField("Posted By Sunny at 2009-02-09: This is great!");
			 * label.setBorder(BorderFactory.createSimpleBorder(edge,Border.
			 * STYLE_TRANSPARENT)); listVerticalManager.add(label); label = new
			 * LabelField("Posted By Annie at 2009-02-09: This is useful!");
			 * label.setBorder(BorderFactory.createSimpleBorder(edge,Border.
			 * STYLE_TRANSPARENT)); listVerticalManager.add(label);
			 */
			mainManager.add(commentLB);
			mainManager.add(commentsManager);
			mainManager.add(listVerticalManager);

			// LabelField postCommentLabel = new LabelField("Post Comment: ");
			LabelField postCommentLabel = new LabelFieldWithFullBG(
					"post comment", MyColor.COMMENT_LABEL_FONT, 0x00,
					MyColor.COMMENT_LABEL_BG, Display.getWidth() - 50
							* Display.getWidth() / 480);
			edge = new XYEdges(Scale.EDGE, 25 * Display.getWidth() / 480,
					Scale.EDGE, 25 * Display.getWidth() / 480);
			postCommentLabel.setMargin(edge);
			// postCommentLabel.setBorder(BorderFactory.createSimpleBorder(edge,
			// Border.STYLE_TRANSPARENT));
			postCommentLabel.setFont(Scale.FONT_DETAIL_TITLE);
			mainManager.add(postCommentLabel);

			postCommentEditField = new EditField("", "");
			postCommentEditField.setFont(MyColor.COMMENT_LABEL_FONT);
			// postCommentEditField.setMaxSize(35);
			postCommentEditField.setMargin(edge);
			// edit.setBorder(BorderFactory.createSimpleBorder(edge,
			// Border.STYLE_TRANSPARENT));

			mainManager.add(postCommentEditField);

			HorizontalFieldManager buttonHorizontalManager = new HorizontalFieldManager(
					HorizontalFieldManager.FIELD_HCENTER
							| HorizontalFieldManager.USE_ALL_WIDTH);
			// buttonHorizontalManager.setBorder(BorderFactory.createSimpleBorder(
			// edge, Border.STYLE_TRANSPARENT));

			MyButtonField submitButton = new MyButtonField("Submit",
					ButtonField.ELLIPSIS);
			// stopButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));

			submitButton.setChangeListener(new ButtonListener(
					postCommentEditField, picinfo, 35));
			buttonHorizontalManager.add(submitButton);

			MyButtonField backButton = new MyButtonField("Back",
					ButtonField.ELLIPSIS);
			// stopButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
			backButton.setChangeListener(new ButtonListener(20));
			buttonHorizontalManager.add(backButton);
			buttonHorizontalManager.setMargin(edge);
			mainManager.add(buttonHorizontalManager);
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

	public int getCurrentCommentInd() {
		return currentComment;
	}
	public void setCurrentCommentInd(int currentComment) {
		this.currentComment = currentComment;
	}
}
