package com.tssoftgroup.tmobile.utils;

import com.tssoftgroup.tmobile.screen.MCastPlayerScreen;
import com.tssoftgroup.tmobile.screen.TrainingPlayerScreen;
import com.tssoftgroup.tmobile.screen.VideoConnectPlayerScreen;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Screen;

public class MyColor {
	public static final int SEARCH_COLOR = 0x000000;
	public static final int TOPIC_BG = 0xe20074;
	public static final int FONT_TOPIC_COLOR = 0xffffff;
	public static final int FONT_DESCRIPTION= 0xe20074;
	public static final int FONT_DESCRIPTION_TITLE = 0x898888;
	public static final int FONT_DESCRIPTION_PLAYER= 0x898888;
	public static final int FONT_DESCRIPTION_PLAYER_DETAIL= 0x00;
	public static final int FONT_DESCRIPTION_PLAYER_DETAIL_DIALOG= 0xffffff;
	
	public static final int FONT_WAIT= 0xe20074;
	public static final int LIST_TITLE_BG = 0xffce00;
	public static final int LIST_TITLE_FONT_UNFOCUS = 0xadacac;
	public static final int LIST_TITLE_FONT_FOCUS = 0xe20074;
	
	public static final int LIST_DESC_FONT_UNFOCUS = 0x898888;
	public static final int LIST_DESC_FONT_FOCUS = 0xe20074;
	
	public static final int COMMENT_LABEL_BG = 0xe20074;
	public static final int COMMENT_LABEL_FONT_COLOR = 0xffffff;
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
	public static int getFontColor(Screen scr){
		if(scr instanceof VideoConnectPlayerScreen || scr instanceof MCastPlayerScreen || scr instanceof TrainingPlayerScreen){
			return FONT_DESCRIPTION_PLAYER_DETAIL;
		}else{
			return FONT_DESCRIPTION;
		}
	}
}
