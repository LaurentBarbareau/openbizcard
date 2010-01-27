package com.tssoftgroup.tmobile.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.tssoftgroup.tmobile.utils.Img;

public class VideoVerticalFieldManager extends VerticalFieldManager {
	Img imgStock = Img.getInstance();
	Bitmap footerBmp = imgStock.getFooter();

	public VideoVerticalFieldManager() {
		super(net.rim.device.api.ui.Manager.NO_VERTICAL_SCROLL
				| net.rim.device.api.ui.Manager.NO_VERTICAL_SCROLLBAR);
	}

	protected void paintBackground(Graphics g) {
		/*
		 * if(bkgrndBmp != null){ g.rop(Graphics.ROP_SRC_ALPHA_GLOBALALPHA,
		 * (this.getWidth() / 2) - (bkgrndBmp .getWidth() /2), (this.getHeight()
		 * / 2) - (bkgrndBmp .getHeight() /2), bkgrndBmp .getWidth(), bkgrndBmp
		 * .getHeight(), bkgrndBmp ,0,0); }
		 */
		g.setBackgroundColor(0xcdcdcd);

		// Clears the entire graphic area to the current background
		g.clear();

		Bitmap videoBackground = imgStock.getVideoBackground();
		g.drawBitmap(24, 62, 432, 195, videoBackground, 0, 0);
		if (footerBmp != null) {
			g.rop(Graphics.ROP_SRC_ALPHA_GLOBALALPHA, 0, Display.getHeight()
					- footerBmp.getHeight(), Display.getWidth(), 25, footerBmp,
					0, 0);
		}

	}

	protected void sublayout(int width, int height) {
		super.sublayout(width, height);
		setExtent(width, height);
	}

}