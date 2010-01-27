package com.tssoftgroup.tmobile.screen;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;

public class LogScreen extends MainScreen {
	private static LogScreen instance = null;
	LabelField label = new LabelField("Log ");
	TextField tf = new TextField(FIELD_LEFT);
	long lastTime = 0;

	private LogScreen() {
		add(tf);
	}

	MenuItem clearMenu = new MenuItem("Clear", 0, 100) {

		public void run() {
			tf.setText("");
		}
	};

	protected void makeMenu(Menu menu, int instance) {
		menu.add(clearMenu);

	}

	public static LogScreen getInstance() {

		if (instance == null) {
			instance = new LogScreen();
		}
		return instance;
	}

	public void log(String text) {
		System.out.println(text);
		long current = System.currentTimeMillis();
		tf.setText(tf.getText() + "\r\n" + text);
		lastTime = current;
	}

	public static void debug(String text) {
		// System.out.println(text);
		final String tt = text;
		UiApplication.getUiApplication().invokeLater(new Runnable() {

			public void run() {
				getInstance().log(tt);
			}
		});

	}

	protected boolean onSavePrompt() {
		return true;
	}
}
