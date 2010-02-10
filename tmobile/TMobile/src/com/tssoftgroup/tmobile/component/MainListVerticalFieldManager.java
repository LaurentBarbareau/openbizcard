package com.tssoftgroup.tmobile.component;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.tssoftgroup.tmobile.screen.MyMainScreen;
import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.Img;

public class MainListVerticalFieldManager extends VerticalFieldManager {
	// Bitmap bkgrndBmp = Bitmap.getBitmapResource("TMobile.png");
	Img img = Img.getInstance();

	public MainListVerticalFieldManager() {
		super(VerticalFieldManager.VERTICAL_SCROLL
				| VerticalFieldManager.VERTICAL_SCROLLBAR);
	}

	protected void paintBackground(Graphics g) {
		/*
		 * if(bkgrndBmp != null){ g.rop(Graphics.ROP_SRC_ALPHA_GLOBALALPHA,
		 * (this.getWidth() / 2) - (bkgrndBmp .getWidth() /2), (this.getHeight()
		 * / 2) - (bkgrndBmp .getHeight() /2), bkgrndBmp .getWidth(), bkgrndBmp
		 * .getHeight(), bkgrndBmp ,0,0); }
		 */
		if (getScreen() instanceof MyMainScreen) {
			g.setBackgroundColor(Const.BG_COLOR_MAIN);
		} else {
			g.setBackgroundColor(Const.BG_COLOR);
		}
		// Clears the entire graphic area to the current background
		g.clear();

	}

	protected void sublayout(int width, int height) {
		super.sublayout(width, height);
		// setExtent(width, Scale.VIDEO_CONNECT_SCREEN_LIST_HEIGHT);
		setExtent(width, Display.getHeight() - img.getHeader().getHeight()
				- img.getFooter().getHeight());
	}

}