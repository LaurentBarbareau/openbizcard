package com.tssoftgroup.tmobile.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.tssoftgroup.tmobile.utils.Img;

public class NewVerticalFieldManager extends VerticalFieldManager {
	Img img = Img.getInstance();

	Bitmap bkgrndBmp = img.getFooter();

	public NewVerticalFieldManager() {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);
	}

	protected void paintBackground(Graphics g) {
		g.setBackgroundColor(0xcdcdcd);

		// Clears the entire graphic area to the current background
		g.clear();
		if (bkgrndBmp != null) {
			g.rop(Graphics.ROP_SRC_ALPHA_GLOBALALPHA, 0, Display.getHeight()
					- bkgrndBmp.getHeight(), Display.getWidth(), bkgrndBmp
					.getHeight(), bkgrndBmp, 0, 0);
		}
	}

	protected void sublayout(int width, int height) {
		super.sublayout(width, height);
		setExtent(width, height);
	}

}