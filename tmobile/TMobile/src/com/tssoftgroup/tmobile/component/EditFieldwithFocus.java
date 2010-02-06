package com.tssoftgroup.tmobile.component;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.TextField;

public class EditFieldwithFocus extends EditField {
	Font font;
	int fontColor = -1;

	public EditFieldwithFocus(long arg0) {
		super("", "", TextField.DEFAULT_MAXCHARS, arg0);
	}

	public EditFieldwithFocus(String a, String b, int c, long d) {
		super(a, b, c, d);
	}

	public EditFieldwithFocus(String a, String b, int c, long d, Font f,
			int color) {
		super(a, b, c, d);
		this.fontColor = color;
		this.font = f;
	}

	public boolean isFocus = false;

	protected void onFocus(int direction) {
		isFocus = true;
		super.onFocus(direction);
	}

	protected void onUnfocus() {
		isFocus = false;
		super.onUnfocus();
	}

	protected void paintBackground(net.rim.device.api.ui.Graphics g) {
		if (fontColor != -1) {
			g.setColor(fontColor);
		}
		if (font != null) {
			setFont(font);
		}
	}
}
