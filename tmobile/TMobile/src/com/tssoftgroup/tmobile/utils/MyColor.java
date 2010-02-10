package com.tssoftgroup.tmobile.utils;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;

public class MyColor {
	public static final int SEARCH_COLOR = 0xc5c5c5;
	public static final int TOPIC_BG = 0xffffff;
	public static final int FONT_TOPIC_COLOR = 0x000000;
	public static final int FONT_DESCRIPTION= 0xffffff;
	public static final int FONT_WAIT= 0xffffff;
	public static final int LIST_TITLE_BG = 0xffce00;
	public static final int LIST_TITLE_FONT_UNFOCUS = 0x898888;
	public static final int LIST_TITLE_FONT_FOCUS = 0xffffff;
	
	public static final int LIST_DESC_FONT_UNFOCUS = 0x898888;
	public static final int LIST_DESC_FONT_FOCUS = 0xffffff;
	public static final int COMMENT_LABEL_BG = 0xbfbdbd;
//	public static final Font COMMENT_LABEL_FONT = Font.getDefault().derive(
//			Font.BOLD, 20 * Display.getWidth() / 480);
//	public static final Font FONT_SEARCH = Font.getDefault().derive(Font.BOLD,
//			25 * Display.getWidth() / 480);
//	public static final Font FONT_TOPIC = Font.getDefault().derive(Font.BOLD,
//			25 * Display.getWidth() / 480);
//	public static final Font FONT_CHOICE = Font.getDefault().derive(Font.PLAIN,
//			15 * Display.getWidth() / 480);
	public static final Font COMMENT_LABEL_FONT = Font.getDefault().derive(
			Font.BOLD, 20 * Display.getWidth() / 480);
	public static final Font FONT_SEARCH = Font.getDefault().derive(Font.BOLD,
			25 * Display.getWidth() / 480);
	public static final Font FONT_TOPIC = Font.getDefault().derive(Font.BOLD,
			25 * Display.getWidth() / 480 );
	public static final Font FONT_CHOICE = Font.getDefault().derive(Font.PLAIN,
			15* Display.getWidth() / 480);
}
