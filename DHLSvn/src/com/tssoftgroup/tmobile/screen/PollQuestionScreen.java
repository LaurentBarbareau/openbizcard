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
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.tssoftgroup.tmobile.component.CrieLabelField;
import com.tssoftgroup.tmobile.component.LabelFieldWithFullBG;
import com.tssoftgroup.tmobile.component.MainListVerticalFieldManager;
import com.tssoftgroup.tmobile.component.MyButtonField;
import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.model.Choice;
import com.tssoftgroup.tmobile.model.PollInfo;
import com.tssoftgroup.tmobile.model.Question;
import com.tssoftgroup.tmobile.utils.CrieUtils;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.MyColor;
import com.tssoftgroup.tmobile.utils.Scale;

/**
 * Create a new screen that extends MainScreen, which provides default standard
 * behavior for BlackBerry applications.
 */
/*
 * BlackBerry applications that provide a user interface must extend
 * UiApplication.
 */
public class PollQuestionScreen extends FixMainScreen implements
		FieldChangeListener {
	Img imgstock = Img.getInstance();
	private MainItem _mainMenuItem = new MainItem();

	LabelField titleLabel = new LabelFieldWithFullBG("Question",
			MyColor.FONT_TOPIC, 0xffffff, MyColor.TOPIC_BG, Display.getWidth()
					- 50 * Display.getWidth() / 480);
	CrieLabelField questionLabel = new CrieLabelField("", 0x00,
			Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT,
			LabelField.NON_FOCUSABLE);
	RadioButtonGroup radioGroup = new RadioButtonGroup();
	MainListVerticalFieldManager mainManager = new MainListVerticalFieldManager();
	VerticalFieldManager radioManager = new VerticalFieldManager();
	MyButtonField submitButton = new MyButtonField("Next", ButtonField.ELLIPSIS);
	PollInfo info;
	int rightQuestion = 0;
	int allQuestion = 0;
	int currentQuestion = 0;

	public PollQuestionScreen(PollInfo info) {
		this.info = info;
		allQuestion = info.questions.size();
		createVideoMain();
	}

	public void setQuestion(String question) {
		titleLabel.setText("Question " + (currentQuestion + 1));
		questionLabel.setText(question);
	}

	public void setRadio(String[] choice) {
		radioGroup = new RadioButtonGroup();
		radioManager.deleteAll();
		for (int i = 0; i < choice.length; i++) {
			RadioButtonField radio = new RadioButtonField(choice[i],
					radioGroup, false);
			if (i == 0) {
				radio.setSelected(true);
			}
			radioManager.add(radio);
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
			edge = new XYEdges(2, 25, 2, 25);
			// submitButton.setChangeListener(new ButtonListener());
			// Topic Photo and List field
			titleLabel.setMargin(edge);
			questionLabel.setMargin(edge);
			questionLabel.isFix=true;
			radioManager.setMargin(edge);
			submitButton.setMargin(edge);
			// submitButton.setChangeListener(new ButtonListener());
			// Topic Photo and List field
			mainManager.add(titleLabel);
			mainManager.add(questionLabel);
			mainManager.add(radioManager);
			submitButton.setChangeListener(this);
			mainManager.add(submitButton);
			// button
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
		System.out.println("Click Next");
		System.out.println("currentQuestion" + currentQuestion);

		Question currentQ = (Question) info.questions
				.elementAt(currentQuestion);
		System.out.println("a");

		Choice selectedChoice = (Choice) currentQ.choices.elementAt(radioGroup
				.getSelectedIndex());
		System.out.println("b");
		String sumPoll = "";
		int all = 0;
		for (int i = 0; i < currentQ.choices.size(); i++) {
			Choice temp = (Choice) currentQ.choices.elementAt(i);
			int mycount = temp.count;
			if (selectedChoice == temp) {
				mycount = mycount + 1;
			}
			all = all + mycount;
		}
		for (int i = 0; i < currentQ.choices.size(); i++) {
			Choice temp = (Choice) currentQ.choices.elementAt(i);
			int mycount = temp.count;
			if (selectedChoice == temp) {
				mycount = mycount + 1;
			}
			sumPoll = sumPoll + temp.key + " = " + (mycount * 100 / all)  + "%\n";
		}
		// Send new poll count to server
		sumPoll = "All voted " + all + ":\n" + sumPoll;
		Engine.getInstance().increasePollCount(info.getId(),
				"" + currentQ.numQuestion, selectedChoice.key);
		Dialog.alert(sumPoll);
		System.out.println("c");

		currentQuestion = currentQuestion + 1;
		// set new value of question

		if (currentQuestion == allQuestion) {
			// It is the last question
			// Dialog.alert("Score " + rightQuestion + "/" + allQuestion);//
			Object[] choice = { "OK" };
			int[] value = { 1 };

			Dialog finish = new Dialog("Finish.", choice, value, 1, Bitmap
					.getPredefinedBitmap(Bitmap.INFORMATION));
			finish.doModal();
			// CrieUtils.removeCurrent();
			CrieUtils.removeCurrent();
			// WaitScreen.getInstance().label.setText("Finish. Sending Score "
			// + rightQuestion + "/" + allQuestion);
			// UiApplication.getUiApplication().pushScreen(
			// WaitScreen.getInstance());
			// Engine.getInstance().addTrainingResult(info, rightQuestion,
			// allQuestion);
		} else {
			Question newQ = (Question) info.questions
					.elementAt(currentQuestion);
			setQuestion(newQ.question);
			setRadio(Engine.composeRadioFromQuestion(newQ));
		}
		// Chick new

	}

}
