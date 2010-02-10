package com.tssoftgroup.tmobile.component;

import net.rim.device.api.ui.Screen;

public interface ScreenWithComment {
	public int getCurrentCommentInd();
	public void setCurrentCommentInd(int commentInd);
	public Screen getScreen();
}
