package com.tssoftgroup.tmobile.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;

public class CrieLabelField extends LabelField {
	int fontColor;
	int MARGIN = 5;
	int bgColor = 0x00000000;
	String text;
	Font font = null;
	boolean transparentBG = true;
	public boolean isFix = false;
	public int otherMinusWidth= 0;
	public CrieLabelField(String text, int fontColor) {
		super(text);
		this.text = text;
		this.setMargin(MARGIN, MARGIN, MARGIN, MARGIN);
		this.fontColor = fontColor;
	}

	public CrieLabelField(String text, int fontColor, int fontHeight, long style) {
		super(text, style);
		init(text, fontColor, fontHeight);
	}

	public CrieLabelField(String text, int fontColor, int bgColor,
			int fontHeight) {
		super(text);
		init(text, fontColor, bgColor, fontHeight);
	}

	public CrieLabelField(String text, int fontColor, long style) {
		super(text, style);
		this.text = text;
		this.setMargin(MARGIN, MARGIN, MARGIN, MARGIN);
		this.fontColor = fontColor;
	}

	public CrieLabelField(String text, int fontColor, int bgColor,
			int fontHeight, long style) {
		super(text, style);
		init(text, fontColor, bgColor, fontHeight);
	}

	public void init(String text, int fontColor, int bgColor, int fontHeight) {
		this.text = text;
		this.setMargin(MARGIN, MARGIN, MARGIN, MARGIN);
		this.fontColor = fontColor;
		this.bgColor = bgColor;
		font = Font.getDefault().derive(Font.PLAIN, fontHeight);
	}

	public void init(String text, int fontColor, int fontHeight) {
		this.text = text;
		this.setMargin(MARGIN, MARGIN, MARGIN, MARGIN);
		this.fontColor = fontColor;
		font = Font.getDefault().derive(Font.PLAIN, fontHeight);
	}

	protected void paintBackground(net.rim.device.api.ui.Graphics g) {
		if (!transparentBG) {
			g.clear();
			g.getColor();
			g.setColor(bgColor);
			g.fillRect(0, 0, Display.getWidth(), Display.getHeight());
			g.setColor(fontColor);
			if (font != null) {
				setFont(font);
				g.setFont(font);
			}
		} else {
			g.setColor(fontColor);
			if (font != null) {
				setFont(font);
				g.setFont(font);
			}
		}
	}

	public void setTransparentBG(boolean boo) {
		this.transparentBG = boo;
	}

//	protected void layout(int width, int height) {
//		if (isFix) {
//			layout(getPreferredWidth(), getPreferredHeight());
//		} else {
//			super.layout(width, height);
//		}
//	}
//
//	//
	public int getPreferredWidth() {
		if (isFix && font != null) {
			return Display.getWidth() - getMarginLeft() - getMarginRight() - otherMinusWidth;
		} else {
			return super.getPreferredWidth();
		}
	}
//
//	//
	public int getPreferredHeight() {
		if (isFix && font != null) {
			return font.getHeight();
		} else {
			return super.getPreferredHeight();
		}
	}
}
