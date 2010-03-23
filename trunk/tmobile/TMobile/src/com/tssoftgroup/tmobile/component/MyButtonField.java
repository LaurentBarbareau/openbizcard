package com.tssoftgroup.tmobile.component;

import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.MyColor;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.ButtonField;

public class MyButtonField extends Field {
	private int backgroundColour = 0xffffff;
	// private int highlightColour = 0xe20074;
	private int highlightColour = MyColor.FONT_DESCRIPTION;
	private int fontColor = 0x333333;
	private boolean _bIsFocusable = true;
	private Font font = getFont().derive(Font.PLAIN,
			17 * Display.getWidth() / 480);
	private int fieldWidth;
	private int fieldHeight;
	private int padding = 8;
	private String label;
	private int borderColor = 0x666666;
	boolean isWhiteBG = false;
	public boolean isBlackBG = false;
	int BORDER = 20;

	public MyButtonField(String label, long style) {
		super(ButtonField.CONSUME_CLICK | style);
		this.label = label;
		fieldHeight = font.getHeight() + padding;
		fieldWidth = font.getAdvance(label) + (padding * 4);
		this.setPadding(2, 2, 2, 2);
	}

	public MyButtonField(String label, long style, boolean isWhiteBG) {
		super(ButtonField.CONSUME_CLICK | style);
		this.label = label;
		fieldHeight = font.getHeight() + padding;
		fieldWidth = font.getAdvance(label) + (padding * 4);
		this.setPadding(2, 2, 2, 2);
		this.isWhiteBG = isWhiteBG;
	}

	public boolean isFocusable() {
		return _bIsFocusable;
	}

	protected void onFocus(int direction) {
		backgroundColour = highlightColour;
		fontColor = 0xffffff;
		borderColor = 0x666666;
		invalidate();
	}

	protected void onUnfocus() {
		backgroundColour =0xffffff;
		fontColor = 0x313131;
		borderColor = 0x313131;
		invalidate();
	}

	public void setLabel(String label) {
		this.label = label;
		invalidate();
	}

	public String getLabel() {
		return label;
	}

	public void setFocusable(boolean value) {
		_bIsFocusable = value;
	}

	protected void paint(Graphics graphics) {
		if (!_bIsFocusable)
			graphics.setGlobalAlpha(50);
		if(isWhiteBG){
			graphics.setColor(0xffffff);
		}else{
			graphics.setColor(Const.BG_COLOR);
		}
		if(isBlackBG){
			graphics.setColor(0x000000);
		}
		graphics.fillRect(0, 0, fieldWidth, fieldHeight);
		graphics.setFont(font);
		graphics.setColor(backgroundColour);
		graphics.fillRoundRect(0, 0, fieldWidth, fieldHeight, BORDER, BORDER);
		graphics.setColor(borderColor);
		graphics.drawRoundRect(0, 0, fieldWidth, fieldHeight, BORDER, BORDER);
		graphics.setColor(fontColor);
		graphics.drawText(label, padding * 2 - 1, padding / 2 + 1);
		// super.paint(graphics);
	}

	protected void paintBackground(net.rim.device.api.ui.Graphics g) {
	}

	protected boolean navigationClick(int status, int time) {
		return onClick();
	}

	// to support 8700
	protected boolean trackwheelClick(int status, int time) {
		return onClick();
	}

	protected boolean onClick() {
		// TO override by derived class
		fieldChangeNotify(0);
		return true;
	}

	protected void layout(int arg0, int arg1) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	protected void fieldChangeNotify(int context) {
		try {
			this.getChangeListener().fieldChanged(this, context);
		} catch (Exception exception) {
		}
	}

	public int getPreferredWidth() {
		return fieldWidth;
	}

	public int getPreferredHeight() {
		return fieldHeight;
	}

	/*
	 * public boolean keyDown(int keycode, int time) { int keycode2 = keycode
	 * >>> 16; if (keycode2 == Keypad.KEY_MENU) { return true; }
	 * 
	 * return super.keyDown(keycode, time); }
	 * 
	 * public boolean keyChar(char c, int status, int time) { if (c ==
	 * Characters.ENTER || c == Characters.SPACE) { if (isFocusable()) return
	 * onClick(); else return false; }
	 * 
	 * return super.keyChar(c, status, time); }
	 */
}