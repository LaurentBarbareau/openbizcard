package com.tssoftgroup.tmobile.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

import com.tssoftgroup.tmobile.utils.Img;

public class CustomButtonField extends Field {
	Img imgstock = Img.getInstance();
	// private int backgroundColour = 0xcdcdcd;
	private Bitmap button;

	private Bitmap on = imgstock.getTrainingOn();
	private Bitmap off = imgstock.getTrainingOn();
	private int fieldWidth = Display.getWidth();// Graphics.getScreenWidth();
	private int fieldHeight = 26 * Display.getWidth() /480;
	private int buffer = 0;// (480/*Graphics.getScreenWidth()*/ - 105) / 2;

	public CustomButtonField(String _text, Bitmap on, Bitmap off) {
		super(Field.FOCUSABLE);
		// text = _text;
		// fieldFont = FieldFont();
		this.on = on;
		this.off = off;
		button = off;
	}

	public CustomButtonField(String _text, Bitmap on, Bitmap off, int buffer,
			int width, int height) {
		super(Field.FOCUSABLE);
		// text = _text;
		// fieldFont = FieldFont();
		this.on = on;
		this.off = off;
		button = off;
		this.buffer = buffer;
		this.fieldWidth = width;
		this.fieldHeight = height;
	}
	public void setMyBitmap(Bitmap on, Bitmap off){
		this.on = on;
		this.off = off;
		button = off;
	}
	protected boolean navigationClick(int status, int time) {
		fieldChangeNotify(0);
		return true;
	}

	protected void onFocus(int direction) {
		button = on;
		invalidate();
	}

	protected void onUnfocus() {
		button = off;
		invalidate();
	}

	public int getPreferredWidth() {
		return fieldWidth;
	}

	public int getPreferredHeight() {
		return fieldHeight;
	}

	public boolean isFocusable() {
		return true;
	}

	protected void layout(int arg0, int arg1) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	/*
	 * public static Font FieldFont() { try { FontFamily theFam =
	 * FontFamily.forName("SYSTEM"); return
	 * theFam.getFont(net.rim.device.api.ui.Font.BOLD, 14); } catch
	 * (ClassNotFoundException ex) { ex.printStackTrace(); } return null; }
	 */

	protected void drawFocus(Graphics graphics, boolean on) {
		//
	}

	protected void fieldChangeNotify(int context) {
		try {
			this.getChangeListener().fieldChanged(this, context);
		} catch (Exception exception) {
		}
	}

	protected void paint(Graphics graphics) {
		// graphics.setColor(backgroundColour);
		// graphics.fillRect(0, 0, fieldWidth, fieldHeight);
		graphics.drawBitmap(buffer, 0, fieldWidth, fieldHeight, button, 0, 0);
		// graphics.setColor(0x444444);
		// graphics.setFont(fieldFont);
		// graphics.drawText(text, buffer + ((fieldWidth -
		// fieldFont.getAdvance(text)) / 2) - 105, (fieldHeight -
		// fieldFont.getHeight()) / 2);
	}
}