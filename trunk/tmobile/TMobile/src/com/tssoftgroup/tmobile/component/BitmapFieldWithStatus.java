package com.tssoftgroup.tmobile.component;

import com.tssoftgroup.tmobile.utils.MyColor;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BitmapField;

public class BitmapFieldWithStatus extends BitmapField {
	String status;
	int color = MyColor.LIST_TITLE_FONT_UNFOCUS;
	Font font = Font.getDefault().derive(Font.ITALIC,
			12 * Display.getWidth() / 480);

	public void setStatus(String status) {
		this.status = status;
		invalidate();
	}

	public BitmapFieldWithStatus(Bitmap bitmap, long style, String status) {
		super(bitmap, style);
		this.status = status;
	}

	protected void paint(Graphics g) {
		super.paint(g);
		g.setColor(color);
		g.setFont(font);
		setFont(font);
		g.drawText(status, (getBitmapWidth() - font.getAdvance(status)) / 2,
				(getBitmapHeight() - font.getHeight()) / 2);
		// g.drawText(status, 0, 0);
	}

}
