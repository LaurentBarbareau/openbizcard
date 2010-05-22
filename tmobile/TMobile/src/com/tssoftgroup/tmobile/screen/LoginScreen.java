package com.tssoftgroup.tmobile.screen;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

import com.tssoftgroup.tmobile.component.MainListVerticalFieldManager;
import com.tssoftgroup.tmobile.component.MyButtonField;
import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.Wording;

public class LoginScreen {
	/* declaring Strings to store the data of the user */

	String getName;
	String getEmail;
	String getMobile;
	String getPasscode;
	/* declaring text fields for user input */
	// private AutoTextEditField name;
	// private AutoTextEditField mobile;
	private PasswordEditField passCode;
	// private EmailAddressEditField email;
	/* declaring choice field for user input */
	// private ObjectChoiceField gender;
	/* declaring check box field for user input */
	// private CheckboxField status;
	// Declaring button fields
	private MyButtonField save;
	private MyButtonField close;

	/* declaring vector */
	Img imgStock = Img.getInstance();

	/* declaring persistent object */

	/* creating an entry point */
	// public static void main(String[] args) {
	/* creating instance of the class */
	// Display app = new Display();
	// app.enterEventDispatcher();
	// }
	/* creating default constructor */
	
	public LoginScreen(boolean needLogin) {
		
		/*
		 * Creating an object of the main screen class to use its
		 * functionalities
		 */
		FixMainScreen mainScreen = new FixMainScreen(FixMainScreen.MODE_MCAST);

		// setting title of the main screen
		// mainScreen.setTitle(new LabelField(Wording.WELCOME_MESSAGE));

		Bitmap img = imgStock.getHeader();
		BitmapField bf = new BitmapField(img, BitmapField.FIELD_HCENTER
				| BitmapField.USE_ALL_WIDTH);
		mainScreen.add(bf);
		// creating text fields for user input
		// name = new AutoTextEditField(Wording.NAME, "");
		// mobile = new AutoTextEditField(Wording.MOBILE, "");
		passCode = new PasswordEditField(Wording.PASSCODE, "", 6,
				EditField.FILTER_INTEGER);

		// email = new EmailAddressEditField(Wording.EMAIL, "");
		ProfileEntry profile = ProfileEntry.getInstance();
		// name.setText(profile.name);
		// mobile.setText(profile.mobile);
		Font passCodeFont = Font.getDefault().derive(Font.PLAIN,
				15 * Display.getWidth() / 480);
		passCode.setFont(passCodeFont);
		// passCode.setText(profile.passCode);
		// email.setText(profile.email);

		// creating choice field for user input
		// String[] items = {"Male", "Female"};
		// gender = new ObjectChoiceField("Gender", items);
		// creating Check box field
		// status = new CheckboxField("Active", true);
		// creating Button fields and adding functionality using listeners

		save = new MyButtonField(Wording.LOGIN, ButtonField.CONSUME_CLICK);
		save.setChangeListener(new FieldChangeListener() {

			public void fieldChanged(Field field, int context) {
				login();
			}
		});
		close = new MyButtonField(Wording.CLOSE, ButtonField.CONSUME_CLICK);
		close.setChangeListener(new FieldChangeListener() {

			public void fieldChanged(Field field, int context) {
				onClose();
			}
		});
		close.setMargin(0, 0, 0, 5);
		// adding the input fields to the main screen
		MainListVerticalFieldManager mainManager = new MainListVerticalFieldManager();
		BitmapField loginPic = new BitmapField(imgStock.getLogin());
		loginPic.setMargin(5 * Display.getWidth() / 480, 0, 0, 13 * Display
				.getWidth() / 480);
		mainManager.add(loginPic);

		HorizontalFieldManager passCodeManager = new HorizontalFieldManager();
		passCodeManager.setMargin(60 * Display.getWidth() / 480, 0, 0,
				34 * Display.getWidth() / 480);
		BitmapField passCodePic = new BitmapField(imgStock.getPasscode());

		passCodeManager.add(passCodePic);
		passCode.setMargin(0, 0, 0, 10);
		passCodeManager.add(passCode);
		// mainManager.add(name);
		// mainManager.add(email);
		// mainManager.add(mobile);
		mainManager.add(passCodeManager);
		// adding buttons to the main screen
		HorizontalFieldManager buttonManager = new HorizontalFieldManager();
		buttonManager.setMargin(20 * Display.getWidth() / 480, 0, 0,
				250 * Display.getWidth() / 480);
		buttonManager.add(save);
		buttonManager.add(close);

		mainManager.add(buttonManager);
		// adding menu items
		mainScreen.add(mainManager);
//		mainScreen.addMenuItem(saveItem);
//		mainScreen.addMenuItem(getItem);
		// pushing the main screen
		UiApplication.getUiApplication().pushScreen(mainScreen);
		// add
		if (!profile.passCode.equals("") && ! needLogin) {
			loginFirst();
		}
	}

	// adding functionality to menu item "saveItem"
//	private MenuItem saveItem = new MenuItem(Wording.LOGIN, 110, 10) {
//
//		public void run() {
//			// Calling save method
//			login();
//		}
//	};
	// adding functionality to menu item "saveItem"
//	private MenuItem getItem = new MenuItem(Wording.GET, 110, 11) {
//		// running thread for this menu item
//
//		public void run() {
//			// synchronizing thread
//			// getting contents of the persistent object
//			// checking for empty object
//			// if not empty
//			// create a new object of Store Info class
//			ProfileEntry profile = ProfileEntry.getInstance();
//			// storing information retrieved in strings
//			getName = profile.name;
//			getEmail = profile.email;
//			getMobile = profile.mobile;
//			getPasscode = profile.passCode;
//
//			// calling the show method
//			show();
//		}
//	};

	// coding for persistent store
	// new class store info implementing persistable
	// details for show method
	public void show() {
		Dialog.alert("Name is " + getName + "\nE-mail is " + getEmail
				+ "\nJobDesc is " + getMobile + "\nAddress is " + getPasscode);
	}

	// creating save method
	public void login() {
		ProfileEntry profile = ProfileEntry.getInstance();
		// getting the test entered in the input fields
		// profile.name = name.getText();
		// profile.email = "hideoaki@gmail.com";
		// profile.mobile = mobile.getText();
		profile.passCode = passCode.getText();

		profile.saveProfile();
		if (profile.passCode.equals("")) {
			Dialog.alert("Email and Passcode can not be empty");
		} else {
			UiApplication.getUiApplication().pushScreen(
					WaitScreen.getInstance());
			Engine.getInstance().login(profile.email, profile.passCode);
		}
		// gender.setSelectedIndex("Male");
		// status.setChecked(true);
	}

	public void loginFirst() {
		ProfileEntry profile = ProfileEntry.getInstance();
		UiApplication.getUiApplication().pushScreen(WaitScreen.getInstance());
		Engine.getInstance().login(profile.email, profile.passCode);
		// gender.setSelectedIndex("Male");
		// status.setChecked(true);
	}

	// overriding onClose method
	public boolean onClose() {
		System.exit(0);
		return true;
	}
}
