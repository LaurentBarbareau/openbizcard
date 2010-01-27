package com.tssoftgroup.tmobile.component;

import com.tssoftgroup.tmobile.utils.CrieUtils;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;

public class ImageButtonListener implements FocusChangeListener {
	String link = "";
	int id = 0;
	public ImageButtonListener(String link, int id) {
		this.link  = link;
		this.id = id;
	}

	public void focusChanged(Field field, int eventType) {

	}

	public boolean imageButtonClicked(ImageButton imgButton) {
		switch (id) {
		case 0:
			CrieUtils.browserURL(link);
			break;

		default:
			break;
		}
		return true;
	}

}
