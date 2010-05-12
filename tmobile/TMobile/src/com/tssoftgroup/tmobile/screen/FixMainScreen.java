package com.tssoftgroup.tmobile.screen;

import java.util.Vector;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.tssoftgroup.tmobile.component.CommmentNextPrevListener;
import com.tssoftgroup.tmobile.component.CrieLabelField;
import com.tssoftgroup.tmobile.component.MyButtonField;
import com.tssoftgroup.tmobile.component.NewVerticalFieldManager;
import com.tssoftgroup.tmobile.component.ScreenWithComment;
import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.model.Comment;
import com.tssoftgroup.tmobile.model.PicInfo;
import com.tssoftgroup.tmobile.model.Video;
import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.MyColor;
import com.tssoftgroup.tmobile.utils.Scale;
import com.tssoftgroup.tmobile.utils.Wording;

/* package */public class FixMainScreen extends MainScreen implements
		FieldChangeListener {
	NewVerticalFieldManager manager = new NewVerticalFieldManager();
	boolean haveNext = false;
	boolean havePrevious = false;
	int currentIndex = 0;
	VerticalFieldManager pagingManager = new VerticalFieldManager();
	HorizontalFieldManager previousNextManager = new HorizontalFieldManager();
	MyButtonField nextBT = new MyButtonField(Const.NEXT_LABEL,
			ButtonField.ELLIPSIS);
	MyButtonField previousBT = new MyButtonField(Const.PREVIOUS_LABEL,
			ButtonField.ELLIPSIS);

	// Vector of label field
	LabelField numPage = new CrieLabelField("", 0x00);
	private Font pageFont = getFont().derive(Font.PLAIN,
			17 * Display.getWidth() / 480);
	String[] pageString = { "1", "2", "3" };
	private ChoiceField pageChoice = null;
	// String[] teststr = {"a", "b","c"};
	// private ObjectChoiceField testChoice = new ObjectChoiceField("sss"
	// ,teststr);
	static XYEdges edge = new XYEdges(5, 25 * Display.getWidth() / 480, 5,
			25 * Display.getWidth() / 480);
	public static final int MODE_MCAST = 0;
	public static final int MODE_VIDEOCONNECT = 1;
	public static final int MODE_DOC = 2;
	public static final int MODE_TRAIN = 3;
	public static final int MODE_CONTACT = 4;
	public static final int MODE_POLL = 5;
	int mode;
	int currentComment = 0;
	public String search = "";
	MyButtonField searchBT = new MyButtonField("Search", ButtonField.ELLIPSIS);
	int numItem;

	FixMainScreen(int mode) {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);
		super.add(manager);
		searchBT.setMargin(10, 15, 0, 0);
		searchBT.setChangeListener(this);
		this.mode = mode;
		pagingManager.add(previousNextManager);
		pageChoice = new ChoiceField("Go to :", pageString.length, 0) {
			public Object getChoice(int index) throws IllegalArgumentException {
				return pageString[index];
			}
		};
		pageChoice.setPadding(0, 350 * Display.getWidth() / 480, 0, 0);
		// testChoice.setPadding(0, 450 * Display.getWidth() / 480, 0, 0);

		pagingManager.add(pageChoice);
	}

	public void processHaveNext(int numItem) {
		this.numItem = numItem;
		calculateNumpageLabel(currentIndex, numItem);
		int numItemIndex = numItem - 1;
		if (numItemIndex > currentIndex + Const.NUM_LIST - 1) {
			haveNext = true;
		} else {
			haveNext = false;
		}
		if (currentIndex >= Const.NUM_LIST) {
			havePrevious = true;
		} else {
			havePrevious = false;
		}
		// Add Remove previous next Button
		previousNextManager.deleteAll();
		if (havePrevious) {
			previousNextManager.add(previousBT);
		}
		numPage.setMargin(6, 0, 0, 0);
		numPage.setFont(pageFont);

		if (haveNext) {
			previousNextManager.add(nextBT);
		}
		// set listener
		// pageChoice.setChangeListener(this);
		//
		if (haveNext || havePrevious) {
			previousNextManager.add(numPage);
		}
		// / Set new Choice Field
		if (pageChoice != null) {
			try {
				pagingManager.delete(pageChoice);
			} catch (IllegalArgumentException e) {
				// Delete item that is not belong to
			}
		}
		pageString = new String[allPage];
		for (int i = 0; i < pageString.length; i++) {
			pageString[i] = "" + (i + 1);
		}
		pageChoice = new ChoiceField("Go to :", pageString.length, 0) {
			public Object getChoice(int index) throws IllegalArgumentException {
				return pageString[index];
			}
		};
		pageChoice.setPadding(0, 350 * Display.getWidth() / 480, 0, 0);
		pageChoice.setSelectedIndex(currentPage - 1);
		pageChoice.setFont(pageFont);
		// pageChoice.setMargin(edge);
		pageChoice.setChangeListener(this);
		if (haveNext || havePrevious) {
			pagingManager.add(pageChoice);
		}
	}

	static int allComment = 0;

	public static void processHaveComment(VerticalFieldManager commentManager,
			PicInfo picinfo, ScreenWithComment scr) {
		Vector commentList = new Vector();
		allComment = picinfo.comments.size();
		commentManager.deleteAll();
		for (int i = scr.getCurrentCommentInd(); i < picinfo.comments.size(); i++) {
			if (i >= Const.NUM_LIST + scr.getCurrentCommentInd()) {
				break;
			}
			Comment commment = (Comment) picinfo.comments.elementAt(i);
			String[] comment = { commment.getComment(), commment.getTime(),
					commment.getUser() };
			commentList.addElement(comment);
		}
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
				commentManager.add(commentLabel);
				commentLabel = new CrieLabelField(commentArr[0], MyColor
						.getFontColor(scr.getScreen()),
						Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT,
						LabelField.FOCUSABLE);
				commentLabel.isFix = true;
				// commentLabel.setBorder(BorderFactory.createSimpleBorder(
				// edge, Border.STYLE_TRANSPARENT));
				commentLabel.setMargin(edge);
				commentManager.add(commentLabel);
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
			commentManager.add(commentLabel);
		}
		int numItemIndex = picinfo.comments.size() - 1;
		boolean haveNext;
		boolean havePrevious;
		if (numItemIndex > scr.getCurrentCommentInd() + Const.NUM_LIST - 1) {
			haveNext = true;
		} else {
			haveNext = false;
		}
		if (scr.getCurrentCommentInd() >= Const.NUM_LIST) {
			havePrevious = true;
		} else {
			havePrevious = false;
		}
		// Add Remove previous next Button
		HorizontalFieldManager commentPreviousNextManager = new HorizontalFieldManager();

		commentPreviousNextManager.deleteAll();
		commentPreviousNextManager.setMargin(edge);
		MyButtonField commentNextBT;
		MyButtonField commentPreviousBT;
		if (scr instanceof VideoConnectPlayerScreen
				|| scr instanceof MCastPlayerScreen) {
			commentNextBT = new MyButtonField(Const.NEXT_LABEL,
					ButtonField.ELLIPSIS, true);
			commentPreviousBT = new MyButtonField(Const.PREVIOUS_LABEL,
					ButtonField.ELLIPSIS, true);
		} else {
			commentNextBT = new MyButtonField(Const.NEXT_LABEL,
					ButtonField.ELLIPSIS);
			commentPreviousBT = new MyButtonField(Const.PREVIOUS_LABEL,
					ButtonField.ELLIPSIS);
		}

		CommmentNextPrevListener listener = new CommmentNextPrevListener(
				commentManager, picinfo, scr);
		if (havePrevious) {
			commentPreviousBT.setChangeListener(listener);
			commentPreviousNextManager.add(commentPreviousBT);
		}
		if (haveNext) {
			commentNextBT.setChangeListener(listener);
			commentPreviousNextManager.add(commentNextBT);
		}
		commentManager.add(commentPreviousNextManager);
	}

	int allPage;
	int currentPage;

	private void calculateNumpageLabel(int currentIndex, int numItem) {
		allPage = numItem / Const.NUM_LIST;
		allPage = allPage + (numItem % Const.NUM_LIST == 0 ? 0 : 1);
		currentPage = (currentIndex / Const.NUM_LIST) + 1;
		String pageString = " " + currentPage + "/" + allPage + " ";
		numPage.setText(pageString);
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
		// / If have download queue
		ProfileEntry profile = ProfileEntry.getInstance();
		Vector videos = Video.convertStringToVector(profile.videos);
		Vector sceduleVideos = Video.getScheduleVideo(videos);
		Vector downloadingVideos = Video.getDownloadingVideo(videos);
		if (sceduleVideos.size() > 0 || downloadingVideos.size() > 0) {
			UiApplication.getUiApplication().requestBackground();
		} else {
			try {
				TMobile.downloadThread.isRunning = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			Dialog.alert("good bye");
			System.exit(0);
			// / Don't have download Queue
			super.close();
		}
	}

	public void fieldChanged(Field field, int context) {
		if (field == pageChoice) {
			if (mode == MODE_MCAST) {
				currentIndex = Const.NUM_LIST * pageChoice.getSelectedIndex();
				UiApplication.getUiApplication().pushScreen(
						WaitScreen.getInstance());
				Engine.getInstance().viewVideoMCast(currentIndex, "");
			}
			if (mode == MODE_VIDEOCONNECT) {
				currentIndex = Const.NUM_LIST * pageChoice.getSelectedIndex();
				UiApplication.getUiApplication().pushScreen(
						WaitScreen.getInstance());
				Engine.getInstance().viewVideoConnect(currentIndex, "");
			}
			if (mode == MODE_TRAIN) {
				currentIndex = Const.NUM_LIST * pageChoice.getSelectedIndex();
				UiApplication.getUiApplication().pushScreen(
						WaitScreen.getInstance());
				Engine.getInstance().getTraining(currentIndex, "");
			}
			if (mode == MODE_DOC) {
				currentIndex = Const.NUM_LIST * pageChoice.getSelectedIndex();
				UiApplication.getUiApplication().pushScreen(
						WaitScreen.getInstance());
				Engine.getInstance().getDocument(currentIndex, "");
			}
			if (mode == MODE_CONTACT) {
				currentIndex = Const.NUM_LIST * pageChoice.getSelectedIndex();
				UiApplication.getUiApplication().pushScreen(
						WaitScreen.getInstance());
				Engine.getInstance().getProject(currentIndex, "");
			}
			if (mode == MODE_POLL) {
				currentIndex = Const.NUM_LIST * pageChoice.getSelectedIndex();
				UiApplication.getUiApplication().pushScreen(
						WaitScreen.getInstance());
				Engine.getInstance().getPoll(currentIndex, "");
			}
		}
	}
}