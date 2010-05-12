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
import com.tssoftgroup.tmobile.component.VideoDownloadDialog;
import com.tssoftgroup.tmobile.model.Comment;
import com.tssoftgroup.tmobile.model.PicInfo;
import com.tssoftgroup.tmobile.model.Video;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.MyColor;
import com.tssoftgroup.tmobile.utils.Scale;
import com.tssoftgroup.tmobile.utils.Wording;

public class VideoConnectDetail extends FixMainScreen implements
		FieldChangeListener, ScreenWithComment {
	private MainItem _mainMenuItem = new MainItem();
	private Vector commentList = null;

	EditField postCommentEditField = null;
	Img imgStock = Img.getInstance();
	CustomButtonField playButtonImg = new CustomButtonField(null, imgStock
			.getPlay(), imgStock.getPlayOn());
	String videoPath = "";
	HorizontalFieldManager thumnailDescDurationPlayManager = new HorizontalFieldManager();
	VerticalFieldManager descDurationPlayManager = new VerticalFieldManager();
	public VerticalFieldManager commentsManager = new VerticalFieldManager();
	HorizontalFieldManager durationPlayManager;
	PicInfo picInfo = null;

	public VideoConnectDetail(PicInfo picinfo) {
		super(MODE_VIDEOCONNECT);
		this.picInfo = picinfo;
		VideoDownloadDialog.filename = picinfo.getFilename();
		VideoDownloadDialog.fileURL = picinfo.getUrlDownloadVideo();
		VideoDownloadDialog.videoname = picinfo.getTitle();

		this.videoPath = picinfo.getVideoUrl();
		XYEdges edge = new XYEdges(24, 25, 8, 25);

		Bitmap img = imgStock.getHeader();
		BitmapField bf = new BitmapField(img, BitmapField.FIELD_HCENTER
				| BitmapField.USE_ALL_WIDTH);
		add(bf);

		try {
			MainListVerticalFieldManager mainManager = new MainListVerticalFieldManager();

			// select a file label
			LabelField titleLabel = new LabelFieldWithFullBG(
					picinfo.getTitle(), MyColor.FONT_TOPIC,
					MyColor.FONT_TOPIC_COLOR, MyColor.TOPIC_BG, Display
							.getWidth()
							- 50 * Display.getWidth() / 480);
			edge = new XYEdges(2, 25 * Display.getWidth() / 480, 2,
					25 * Display.getWidth() / 480);
			titleLabel.setMargin(edge);
			mainManager.add(titleLabel);
			// 
			// Check video status
			String videoStatus = Video.getVideoStatus(picinfo.getFilename());
			if (videoStatus.equals("0")) {
				// new video
				// / Listener for show download dialog
				playButtonImg = new CustomButtonField(null, imgStock
						.getDownload(), imgStock.getDownloadOn());
				playButtonImg.setChangeListener(new ButtonListener(picinfo, 31,
						this));
			} else if (videoStatus.equals("3")) {
				// video is downloaded
				playButtonImg.setChangeListener(new ButtonListener(picinfo,
						311, this));
			} else if (videoStatus.equals("2")) {
				// video is downloading
				playButtonImg = new CustomButtonField(null, imgStock
						.getDownloading(), imgStock.getDownloadingOn());
				playButtonImg.setChangeListener(new ButtonListener(picinfo,
						312, this));
			} else if (videoStatus.equals("1")) {
				// video is downloading
				playButtonImg = new CustomButtonField(null, imgStock
						.getSchedule(), imgStock.getScheduleOn());
				playButtonImg.setChangeListener(new ButtonListener(picinfo,
						313, this));
			}
			int thumbWidth = 0;
			if (picinfo.getThumbnail() != null) {
				BitmapField thumb = new BitmapField(picinfo.getThumbnail());
				thumb.setMargin(5, 0, 0, 0);
				thumnailDescDurationPlayManager.add(thumb);
				thumbWidth = thumb.getWidth() + 10;
			}
			durationPlayManager = new HorizontalFieldManager();
			// /
			CrieLabelField durLabel = new CrieLabelField("duration "
					+ picinfo.getDuration(), MyColor.FONT_DESCRIPTION_TITLE,
					Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT
							- (Display.getWidth() > 350 ? 5 : 0),
					LabelField.NON_FOCUSABLE);
			durationPlayManager.add(durLabel);
			// playButtonImg.setMargin(0, 0, 0, Display.getWidth() - 50
			// - durLabel.getWidth() - playButtonImg.getWidth());
			playButtonImg.setMargin(0, 0, 0, 20);
			durationPlayManager.add(playButtonImg);
			CrieLabelField descriptionLabel = new CrieLabelField(picinfo
					.getDescription(), MyColor.FONT_DESCRIPTION,
					Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT,
					LabelField.NON_FOCUSABLE);
			descDurationPlayManager.setMargin(0, 25, 0, 10);
			// descriptionLabel.setMargin(0, 25, 0, 0);

			descDurationPlayManager.add(descriptionLabel);
			descriptionLabel.isFix = true;
			descriptionLabel.otherMinusWidth = thumbWidth
					+ descriptionLabel.getManager().getMarginLeft()
					+ descriptionLabel.getManager().getMarginRight();

			descDurationPlayManager.add(durationPlayManager);
			thumnailDescDurationPlayManager.add(descDurationPlayManager);
			thumnailDescDurationPlayManager.setMargin(edge);

			mainManager.add(thumnailDescDurationPlayManager);
			// CommentListVerticalFieldManager listVerticalManager = new
			// CommentListVerticalFieldManager();
			LabelField commentLB = new LabelFieldWithFullBG("comments",
					MyColor.COMMENT_LABEL_FONT,
					MyColor.COMMENT_LABEL_FONT_COLOR, MyColor.COMMENT_LABEL_BG,
					Display.getWidth() - 50 * Display.getWidth() / 480);
			edge = new XYEdges(Scale.EDGE, 25 * Display.getWidth() / 480,
					Scale.EDGE, 25 * Display.getWidth() / 480);
			commentLB.setMargin(edge);
			commentLB.setFont(Scale.FONT_DETAIL_TITLE);
			// commentsManager.add(commentLB);
			edge = new XYEdges(2, 35 * Display.getWidth() / 480, 2,
					35 * Display.getWidth() / 480);
			// Deal with Comment
			commentList = new Vector();

			// temp data
			// String[] comment1 = { "AAA", "BBBB", "CCCCC" };
			// String[] comment2 = { "AAA2", "BBBB2", "CCCCC2" };
			for (int i = 0; i < picinfo.comments.size(); i++) {
				Comment commment = (Comment) picinfo.comments.elementAt(i);
				String[] comment = { commment.getComment(), commment.getTime(),
						commment.getUser() };
				commentList.addElement(comment);
			}
			// commentList.addElement(comment1);
			// commentList.addElement(comment2);
			if (commentList != null && commentList.size() > 0) {
				for (int i = 0; i < commentList.size(); i++) {
					String[] commentArr = (String[]) commentList.elementAt(i);
					CrieLabelField commentLabel = new CrieLabelField("By "
							+ commentArr[2] + " at " + commentArr[1] + ": ",
							MyColor.FONT_DESCRIPTION_TITLE,
							Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT
									- (Display.getWidth() > 350 ? 8 : 2),
							LabelField.FOCUSABLE);
					// commentLabel.setBorder(BorderFactory.createSimpleBorder(
					// edge, Border.STYLE_TRANSPARENT));
					commentLabel.setMargin(edge);
					commentLabel.isFix = true;
					commentsManager.add(commentLabel);
					commentLabel = new CrieLabelField(commentArr[0],
							MyColor.FONT_DESCRIPTION,
							Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT,
							LabelField.FOCUSABLE);
					// commentLabel.setBorder(BorderFactory.createSimpleBorder(
					// edge, Border.STYLE_TRANSPARENT));
					commentLabel.isFix = true;
					commentLabel.setMargin(edge);
					commentsManager.add(commentLabel);
				}
			} else if (commentList.size() == 0) {
				CrieLabelField commentLabel = new CrieLabelField(
						Wording.NO_COMMENT, MyColor.FONT_DESCRIPTION_TITLE,
						Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT
								- (Display.getWidth() > 350 ? 8 : 2),
						LabelField.FOCUSABLE);
				edge = new XYEdges(2, 35 * Display.getWidth() / 480, 2,
						35 * Display.getWidth() / 480);
				commentLabel.setMargin(edge);
				commentLabel.isFix = true;
				commentsManager.add(commentLabel);
			}
			FixMainScreen.processHaveComment(commentsManager, picinfo, this);
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

			LabelField postCommentLabel = new LabelFieldWithFullBG(
					"post comment", MyColor.COMMENT_LABEL_FONT,
					MyColor.COMMENT_LABEL_FONT_COLOR, MyColor.COMMENT_LABEL_BG,
					Display.getWidth() - 50 * Display.getWidth() / 480);// postCommentLabel.setBorder(BorderFactory.createSimpleBorder(edge,
			edge = new XYEdges(Scale.EDGE, 25 * Display.getWidth() / 480,
					Scale.EDGE, 25 * Display.getWidth() / 480);
			postCommentLabel.setMargin(edge);
			// postCommentLabel.setBorder(BorderFactory.createSimpleBorder(edge,
			// Border.STYLE_TRANSPARENT));
			postCommentLabel.setFont(Scale.FONT_DETAIL_TITLE);
			mainManager.add(postCommentLabel);

			postCommentEditField = new EditField("", "");
			postCommentEditField.setFont(MyColor.COMMENT_LABEL_FONT);
			// edit.setBorder(BorderFactory.createSimpleBorder(edge,
			// Border.STYLE_TRANSPARENT));
			postCommentEditField.setMargin(edge);
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
		// bf = new BitmapField(img, Field.FIELD_BOTTOM | Field.USE_ALL_HEIGHT);
		// bf.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
		// add(bf);

		addMenuItem(_mainMenuItem);
	}

	public void setDownloadButton(String status) {
		// 0= new| 2=downloading | 3=downloaded
		durationPlayManager.delete(playButtonImg);
		if (status.equals("0")) {
			// new video
			// / Listener for show download dialog
			playButtonImg = new CustomButtonField(null, imgStock.getDownload(),
					imgStock.getDownloadOn());
			playButtonImg.setChangeListener(new ButtonListener(picInfo, 31,
					this));
		} else if (status.equals("3")) {
			// video is downloaded
			playButtonImg.setChangeListener(new ButtonListener(picInfo, 311,
					this));
		} else if (status.equals("2")) {
			// video is downloading
			playButtonImg = new CustomButtonField(null, imgStock
					.getDownloading(), imgStock.getDownloadingOn());
			playButtonImg.setChangeListener(new ButtonListener(picInfo, 312,
					this));
		} else if (status.equals("1")) {
			// video is downloading
			playButtonImg = new CustomButtonField(null, imgStock.getSchedule(),
					imgStock.getScheduleOn());
			playButtonImg.setChangeListener(new ButtonListener(picInfo, 313,
					this));
		}
		durationPlayManager.add(playButtonImg);
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

	public int getCurrentCommentInd() {
		return currentComment;
	}

	public void setCurrentCommentInd(int currentComment) {
		this.currentComment = currentComment;
	}
}
