package com.tssoftgroup.tmobile.utils;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;

public class Scale {
	// / Video Connect Detail Screen
	public static final int VIDEO_CONNECT_DETAIL_LIST_COMMENT_HEIGHT = 150 * Display
			.getWidth() / 480;
	public static final int VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT = 20 * Display
			.getWidth() / 480;
	
	public static final int VIDEO_CONNECT_DETAIL_COMMENT_TITLE_FONT_HEIGHT = 30 * Display
			.getWidth() / 480;
	// Video Connect Screen
	public static final int VIDEO_CONNECT_SCREEN_LIST_HEIGHT = 200 * Display
			.getWidth() / 480;
	// Video PLayre all
	public static final int VIDEO_PLAYER_TIME_HEIGHT = 10 * Display.getWidth() / 480;
	// 
	public static final int WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD = 80 * Display
			.getWidth() / 480;

	public static final int INDENT_LEFT_RIGHT_TOPIC = 25 * Display.getWidth() / 480;
	public static final int FONT_HEIGHT_LISTFIELD = 20 * Display.getWidth() / 480;

	public static final int FONT_HEIGHT_DETAIL_TITLE = 30 * Display.getWidth() / 480;
	public static final int FONT_HEIGHT_DETAIL_DESC = 20 * Display.getWidth() / 480;

	public static final Font FONT_DETAIL_TITLE = Font.getDefault().derive(
			Font.PLAIN, FONT_HEIGHT_DETAIL_TITLE);
	public static final Font FONT_DETAIL_DESC = Font.getDefault().derive(
			Font.PLAIN, FONT_HEIGHT_DETAIL_DESC);
	public static final int EDGE = 2;

}
