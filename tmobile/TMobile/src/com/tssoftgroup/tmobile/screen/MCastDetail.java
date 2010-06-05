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
import net.rim.device.api.ui.Screen;
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
import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.model.Comment;
import com.tssoftgroup.tmobile.model.MoreInfo;
import com.tssoftgroup.tmobile.model.PicInfo;
import com.tssoftgroup.tmobile.model.Video;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.MyColor;
import com.tssoftgroup.tmobile.utils.Scale;
import com.tssoftgroup.tmobile.utils.Wording;

public class MCastDetail extends FixMainScreen implements FieldChangeListener,
		ScreenWithComment {
	public static final String LOADING = "Downloading ";
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
	HorizontalFieldManager durationPlayManager;
	PicInfo picInfo = null;

	CrieLabelField percent = new CrieLabelField(LOADING + "10%",
			MyColor.FONT_DESCRIPTION,
			Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT
					- (Display.getWidth() > 350 ? 5 : 0),
			LabelField.NON_FOCUSABLE);
	String fileName = "";

	public MCastDetail(PicInfo picinfo) {
		super(MODE_MCAST);
		this.picInfo = picinfo;
		VideoDownloadDialog.filename = picinfo.getFilename();
		fileName = picinfo.getFilename();
		VideoDownloadDialog.fileURL = picinfo.getUrlDownloadVideo();
		VideoDownloadDialog.videoname = picinfo.getTitle();
		System.out.println("video name " + VideoDownloadDialog.videoname);
		System.out.println(picinfo.getFilename());
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
					picinfo.getTitle(), MyColor.FONT_TOPIC,
					MyColor.FONT_TOPIC_COLOR, MyColor.TOPIC_BG, Display
							.getWidth()
							- 50 * Display.getWidth() / 480);
			edge = new XYEdges(2, 25 * Display.getWidth() / 480, 2,
					25 * Display.getWidth() / 480);
			titleLabel.setMargin(edge);
			mainManager.add(titleLabel);
			// Check video status
			String videoStatus = Video.getVideoStatus(picinfo.getFilename());
			if (videoStatus.equals("0")) {
				// new video
				// / Listener for show download dialog
				playButtonImg = new CustomButtonField(null, imgStock
						.getDownload(), imgStock.getDownloadOn());
				playButtonImg.setChangeListener(new ButtonListener(picinfo, 32,
						this));
			} else if (videoStatus.equals("3")) {
				// video is downloaded
				playButtonImg = new CustomButtonField(null, imgStock
						.getPlay(), imgStock.getPlayOn());
				playButtonImg.setChangeListener(new ButtonListener(picinfo,
						321, this));
			} else if (videoStatus.equals("2")) {
				// video is downloading
				playButtonImg = new CustomButtonField(null, imgStock
						.getDownloading(), imgStock.getDownloadingOn());
				playButtonImg.setChangeListener(new ButtonListener(picinfo,
						322, this));
			} else if (videoStatus.equals("1")) {
				// video is downloading
				playButtonImg = new CustomButtonField(null, imgStock
						.getSchedule(), imgStock.getScheduleOn());
				playButtonImg.setChangeListener(new ButtonListener(picinfo,
						323, this));
			}

			int thumbWidth = 0;
			if (picinfo.getThumbnail() != null) {
				BitmapField thumb = new BitmapField(picinfo.getThumbnail());
				thumb.setMargin(5, 0, 0, 0);
				thumnailDescDurationPlayManager.add(thumb);
				thumbWidth = thumb.getWidth() + 10 * Display.getWidth() / 480;
			}

			durationPlayManager = new HorizontalFieldManager();
			// /
			String durationString = "";
			if (!picinfo.getDuration().equals("")) {
				durationString = "duration " + picinfo.getDuration();
			}
			CrieLabelField durLabel = new CrieLabelField(durationString,
					MyColor.FONT_DESCRIPTION_TITLE,
					Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT
							- (Display.getWidth() > 350 ? 5 : 0),
					LabelField.NON_FOCUSABLE);
			durationPlayManager.add(durLabel);
			// playButtonImg.setMargin(0, 0, 0, Display.getWidth() - 50
			// - durLabel.getWidth() - playButtonImg.getWidth());
			playButtonImg.setMargin(0, 0, 0, 20);
			if (videoStatus.equals("2")) {
				percent.setText(LOADING + "0%");
				updateStatus();
			} else {
				durationPlayManager.add(playButtonImg);
			}
			durationPlayManager.add(percent);
			CrieLabelField descriptionLabel = new CrieLabelField(picinfo
					.getDescription(), MyColor.FONT_DESCRIPTION,
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
					MyColor.COMMENT_LABEL_FONT,
					MyColor.COMMENT_LABEL_FONT_COLOR, MyColor.COMMENT_LABEL_BG,
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
							MyColor.FONT_DESCRIPTION_TITLE,
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
					commentLabel = new CrieLabelField(commentArr[0],
							MyColor.FONT_DESCRIPTION,
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
						Wording.NO_COMMENT, MyColor.FONT_DESCRIPTION_TITLE,
						Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT
								- (Display.getWidth() > 350 ? 8 : 2),
						LabelField.FOCUSABLE);
				commentLabel.isFix = true;
				edge = new XYEdges(2, 35 * Display.getWidth() / 480, 2,
						35 * Display.getWidth() / 480);
				commentLabel.setMargin(edge);
				commentsManager.add(commentLabel);
			}
			FixMainScreen.processHaveComment(commentsManager, picinfo, this);
			// Deal with more info
			VerticalFieldManager listVerticalManager = new VerticalFieldManager();
			// LabelField moreinfoLB = new LabelField("More Info:");
			LabelField moreinfoLB = new LabelFieldWithFullBG("more info",
					MyColor.COMMENT_LABEL_FONT,
					MyColor.COMMENT_LABEL_FONT_COLOR, MyColor.COMMENT_LABEL_BG,
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
							moreinfoArr[0], MyColor.FONT_DESCRIPTION,
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
						Wording.NO_MORE_INFO, MyColor.FONT_DESCRIPTION_TITLE,
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
					"post comment", MyColor.COMMENT_LABEL_FONT,
					MyColor.COMMENT_LABEL_FONT_COLOR, MyColor.COMMENT_LABEL_BG,
					Display.getWidth() - 50 * Display.getWidth() / 480);
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
		new Thread(new Runnable() {

			public void run() {
				while (mTrucking) {
					if (MCastDetail.this.isDisplayed()) {
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						updateStatus();
					}
				}
			}
		}).start();
	}
	boolean mTrucking = true;
	public void setDownloadButton(String status) {
		// 0= new| 2=downloading | 3=downloaded
		durationPlayManager.delete(playButtonImg);
		if (status.equals("0")) {
			// new video
			// / Listener for show download dialog
			playButtonImg = new CustomButtonField(null, imgStock.getDownload(),
					imgStock.getDownloadOn());
			playButtonImg.setChangeListener(new ButtonListener(picInfo, 32,
					this));
		} else if (status.equals("3")) {
			// video is downloaded
			playButtonImg = new CustomButtonField(null, imgStock.getPlay(),
					imgStock.getPlayOn());
			playButtonImg.setChangeListener(new ButtonListener(picInfo, 321,
					this));
		} else if (status.equals("2")) {
			// video is downloading
			playButtonImg = new CustomButtonField(null, imgStock
					.getDownloading(), imgStock.getDownloadingOn());
			playButtonImg.setChangeListener(new ButtonListener(picInfo, 322,
					this));
		} else if (status.equals("1")) {
			// video is downloading
			playButtonImg = new CustomButtonField(null, imgStock.getSchedule(),
					imgStock.getScheduleOn());
			playButtonImg.setChangeListener(new ButtonListener(picInfo, 323,
					this));
		}
		if (!status.equals("2")) {
			System.out.println("Add Play button");
			durationPlayManager.add(playButtonImg);
		} else {
			System.out.println("before add percent");
			if (status.equals("2")) {
				percent.setText(LOADING + "0%");
			}
		}
	}

	private void updateStatus() {
		try {
			// System.out.println("update status");
			ProfileEntry profile = ProfileEntry.getInstance();
			Vector videos = Video.convertStringToVector(profile.videos);
			for (int i = 0; i < videos.size(); i++) {
				final Video v = (Video) videos.elementAt(i);
				if(v.getName().equals(fileName)){
					System.out.println("Found Current File");
					System.out.println("title " + v.getTitle());
					System.out.println("status " + v.getStatus());
					System.out.println("percent " + v.getPercent());
				}
				if (v.getStatus().equals("2") && v.getName().equals(fileName)) {
					UiApplication.getUiApplication().invokeLater(
							new Runnable() {
								public void run() {
									try {
										percent.setText(LOADING
												+ v.getPercent() + "%");
									} catch (Exception e) {
										System.out
												.println("Error when updating download label");
									}
								}
							});
				} else if ( v.getName().equals(fileName)
						&&v.getStatus().equals("3") && !isFinish) {
					System.out.println("Enter in 3");
					UiApplication.getUiApplication().invokeLater(
							new Runnable() {
								public void run() {
									try {
										System.out.println("Enter in 4");
										isFinish = true;
										percent.setText("");
										setDownloadButton("3");
									} catch (Exception e) {
										System.out
												.println("Error when updating download label");
									}
								}
							});
					
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	boolean isFinish = false;
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
