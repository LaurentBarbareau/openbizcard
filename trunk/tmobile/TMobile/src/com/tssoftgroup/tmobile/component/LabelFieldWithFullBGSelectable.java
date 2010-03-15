package com.tssoftgroup.tmobile.component;

import com.tssoftgroup.tmobile.utils.CrieUtils;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;

public class LabelFieldWithFullBGSelectable extends LabelField {
	int bgColor = 0x00000000;
	Font font = null;
	int fontColor;
	int bgWidth;
	public boolean hasFocus = false;
	
	public LabelFieldWithFullBGSelectable(String text, Font font, int fontColor,
			int bgColor, int bgWidth) {
		super(" "+text, Field.FOCUSABLE);
		if (font.getAdvance(text) > bgWidth) {
			setText(CrieUtils.cutString(font, text, bgWidth) + "...");
		}
		this.font = font;
		this.fontColor = fontColor;
		this.bgWidth = bgWidth;
		this.bgColor = bgColor;
	}

	public int getPreferredWidth() {
		return bgWidth;

	}
	protected void onFocus(int direction) {
		hasFocus = true;
		invalidate();
	}

	protected void onUnfocus() {
		hasFocus = false;
		invalidate();
	}
	public int getPreferredHeight() {
		return font.getHeight() ;
	}

	protected void paintBackground(net.rim.device.api.ui.Graphics g) {
		g.setColor(bgColor);
		g.fillRect(0, 0, bgWidth, getHeight());
		g.setColor(fontColor);
		if (font != null) {
			setFont(font);
			g.setFont(font);
			g.drawText(getText(), 0, 0);
		}
	}
	
	protected void paint(Graphics g) {
		super.paint(g);
		if (hasFocus) {

			g.setColor(bgColor);
			g.fillRect(0, 0, bgWidth, getHeight());
			g.setColor(fontColor);
			if (font != null) {
				setFont(font);
				g.setFont(font);
				g.drawText(getText(), 0, 0);
			}
		
		}
	}

	protected void layout(int arg0, int arg1) {
//		setExtent(getPreferredWidth(), getPreferredHeight()+ (Display.getWidth() > 350 ? 10 :5));
		setExtent(getPreferredWidth(), getPreferredHeight()+ (Display.getWidth() > 350 ? 0 :0));
	}
}
