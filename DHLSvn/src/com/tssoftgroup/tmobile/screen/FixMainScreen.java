package com.tssoftgroup.tmobile.screen;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

import com.tssoftgroup.tmobile.component.MyButtonField;
import com.tssoftgroup.tmobile.component.NewVerticalFieldManager;
import com.tssoftgroup.tmobile.utils.Const;

/* package */class FixMainScreen extends MainScreen {
	NewVerticalFieldManager manager = new NewVerticalFieldManager();
	boolean haveNext = false;
	boolean havePrevious = false;
	int currentIndex = 0;
	HorizontalFieldManager previousNextManager = new HorizontalFieldManager();
	MyButtonField nextBT = new MyButtonField(Const.NEXT_LABEL,
			ButtonField.ELLIPSIS);
	MyButtonField previousBT = new MyButtonField(Const.PREVIOUS_LABEL,
			ButtonField.ELLIPSIS);
	/**
	 * HelloWorldScreen constructor.
	 */
	FixMainScreen() {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);
		super.add(manager);

	}
	public void processHaveNext(int numItem) {
		int numItemIndex = numItem - 1;
		if (numItemIndex > currentIndex + Const.NUM_LIST -1) {
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
		if (haveNext) {
			previousNextManager.add(nextBT);
		}
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
		Dialog.alert("good bye");
		System.exit(0);

		super.close();
	}
}