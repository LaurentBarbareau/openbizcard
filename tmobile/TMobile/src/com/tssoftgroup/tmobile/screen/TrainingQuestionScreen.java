package com.tssoftgroup.tmobile.screen;

/**
 *
 * HelloWorld.java
 * The sentinal sample!
 *
 * Copyright � 1998-2008 Research In Motion Ltd.
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
import com.tssoftgroup.tmobile.model.Question;
import com.tssoftgroup.tmobile.model.TrainingInfo;
import com.tssoftgroup.tmobile.utils.Const;
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
public class TrainingQuestionScreen extends FixMainScreen implements
		FieldChangeListener {
	Img imgstock = Img.getInstance();
	private MainItem _mainMenuItem = new MainItem();
	LabelField titleLabel = new LabelFieldWithFullBG("Question",
			MyColor.FONT_TOPIC, MyColor.FONT_TOPIC_COLOR, MyColor.TOPIC_BG, Const.LABEL_WIDTH);
	CrieLabelField questionLabel = new CrieLabelField("",MyColor.FONT_DESCRIPTION,
			Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT,
			LabelField.NON_FOCUSABLE);
	RadioButtonGroup radioGroup = new RadioButtonGroup();
	MainListVerticalFieldManager mainManager = new MainListVerticalFieldManager();
	VerticalFieldManager radioManager = new VerticalFieldManager();
	MyButtonField submitButton = new MyButtonField("Next", ButtonField.ELLIPSIS);
	TrainingInfo info;
	int rightQuestion = 0;
	int allQuestion = 0;
	int currentQuestion = 0;

	public TrainingQuestionScreen(TrainingInfo info) {
		super(MODE_TRAIN);
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
			radio.setFont(MyColor.FONT_TOPIC);
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
			edge = new XYEdges(2, 25 * Display.getWidth()/ 480, 2, 25 * Display.getWidth()/ 480);
			// submitButton.setChangeListener(new ButtonListener());
			// Topic Photo and List field
			titleLabel.setMargin(edge);
			questionLabel.setMargin(edge);
			questionLabel.isFix = true;
			radioManager.setMargin(edge);
			submitButton.setMargin(edge);

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

		if (selectedChoice.key.equals(currentQ.answer)) {
			rightQuestion = rightQuestion + 1;
			Dialog.alert("Right! current score is " + rightQuestion + "/"
					+ allQuestion);
		} else {
			Dialog.alert("Wrong! current score is " + rightQuestion + "/"
					+ allQuestion + " The answer is " + currentQ.answer + ".");
		}
		System.out.println("c");

		currentQuestion = currentQuestion + 1;
		// set new value of question

		if (currentQuestion == allQuestion) {
			Object[] choice = { "OK" };
			int[] value = { 1 };

			Dialog finish = new Dialog("Finish. Your score is " + rightQuestion
					+ "/" + allQuestion + ". " + info.getExplanation(), choice,
					value, 1, Bitmap.getPredefinedBitmap(Bitmap.INFORMATION));
			finish.doModal();
			// It is the last question
			// Dialog.alert("Score " + rightQuestion + "/" + allQuestion);//
			WaitScreen.getInstance().label.setText("Finish. Sending Score "
					+ rightQuestion + "/" + allQuestion);
			UiApplication.getUiApplication().pushScreen(
					WaitScreen.getInstance());
			Engine.getInstance().addTrainingResult(info, rightQuestion,
					allQuestion);
		} else {
			Question newQ = (Question) info.questions
					.elementAt(currentQuestion);
			setQuestion(newQ.question);
			setRadio(Engine.composeRadioFromQuestion(newQ));
		}
		// Chick new

	}

}
