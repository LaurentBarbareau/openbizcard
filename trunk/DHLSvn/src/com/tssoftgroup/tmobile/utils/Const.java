package com.tssoftgroup.tmobile.utils;

import net.rim.device.api.system.Display;

public class Const {
	public static final String success = "ok";
	public static final String fail = "fail";
	public static final byte type_video = 0;
	public static final byte type_document = 1;
	public static final byte type_video_comment = 2;
	public static final byte type_movie = 3;
	public static final byte type_movie_comment = 4;
	public static final byte status_success = 0;
	public static final byte status_fail = 1;
	public static String fileToUpload = "";
	public static String videoToUpload = "";
	public static String fileName = "";

	public static int VIDEO_WIDTH = 300 * Display.getWidth() / 480;// 3
	public static int VIDEO_HEIGHT = 200 * Display.getWidth() / 480; // 2

	public static int LABEL_WIDTH = 432 * Display.getWidth() / 480;
	public static final String url_list = "http://64.150.181.241/tmobile/List";
	public static final String url_upload = "http://64.150.181.241/tmobile/Receiver";
	public static final String url_video_upload = "http://64.150.181.241/tmobile/VideoReceiver";
	public static final String url_download = "http://64.150.181.241/tmobile/Sender";
	public static final String url_comment = "http://64.150.181.241/tmobile/c";
	public static final String url_download_name = "http://64.150.181.241/tmobile/LookUpName";

	/*
	 * public static final String url_list =
	 * "http://203.142.17.217/tmobile/List"; public static final String
	 * url_upload = "http://203.142.17.217/tmobile/Receiver"; public static
	 * final String url_video_upload =
	 * "http://203.142.17.217/tmobile/VideoReceiver"; public static final String
	 * url_download = "http://203.142.17.217/tmobile/Sender"; public static
	 * final String url_comment = "http://203.142.17.217/tmobile/c"; public
	 * static final String url_download_name =
	 * "http://203.142.17.217/tmobile/LookUpName";
	 */

	// public static final String URL_SEND_VIDEO_CHUNK =
	// "http://www.dhlknowledge.com/demo/postvid.php";
	// public static final String URL_SEND_VIDEO =
	// "http://www.dhlknowledge.com/demo/postvid.php";
	// public static final String URL_SEND_VIDEO_CHUNK =
	// "http://www.dhlknowledge.com/web/api/postvid.php";
	// public static final String URL_SEND_VIDEO =
	// "http://www.dhlknowledge.com/web/api/postvid.php";
	public static final String URL_SEND_VIDEO_CHUNK = "http://www.dhlknowledge.com/web/api/postvidnew.php";
	public static final String URL_SEND_VIDEO = "http://www.dhlknowledge.com/web/api/postvidnew.php";
	public static final String URL_SEND_DOC_CHUNK = "http://www.dhlknowledge.com/web/api/postdoc.php";
	public static final String URL_SEND_DOC = "http://www.dhlknowledge.com/web/api/postdoc.php";
	public static final String URL_VIEW_VIDEO = "http://www.dhlknowledge.com/web/viewmcast.php";
	public static final String URL_LOGIN = "http://www.dhlknowledge.com/web/userlogin.php";
	public static final String URL_COMMENT_VIDEOCONNECT = "http://www.dhlknowledge.com/web/addvideoconnectcomment.php";
	public static final String URL_COMMENT_MCAST = "http://www.dhlknowledge.com/web/addmcastcomment.php";
	public static final String URL_SEND_MOREINFO_EMAIL = "http://www.dhlknowledge.com/web/sendmoreinfoemail.php";
	public static final String URL_VIEW_TRAINING = "http://www.dhlknowledge.com/web/gettraining.php";
	public static final String URL_VIEW_TRAINING_ANS = "http://www.dhlknowledge.com/web/gettrainingans.php";
	public static final String URL_ADD_TRAINING_RESULT = "http://www.dhlknowledge.com/web/addtraininghistory.php";
	public static final String URL_VIEW_PROJECT = "http://www.dhlknowledge.com/web/getproject.php";
	public static final String URL_VIEW_DOCUMENT = "http://www.dhlknowledge.com/web/getdocument.php";
	public static final String URL_VIEW_POLL = "http://www.dhlknowledge.com/web/getpoll.php";
	public static final String URL_VIEW_POLL_CHOICE = "http://www.dhlknowledge.com/web/getpollchoice.php";
	public static final String URL_INCREASE_POLL_COUNT = "http://www.dhlknowledge.com/web/increasepollcount.php";
	public static final String URL_EMAIL_DOC = "http://dhlknowledge.com/web/senddocemail.php";
	public static final String DOCUMENT_PATH = "http://www.dhlknowledge.com/web/api/docs/";//Microfunding Proposal Template.doc
	public static final int BG_COLOR = 0xcdcdcd;
	public static final int LIST_BG_COLOR = 0xffd66b;
	public static final int GRAY_COLOR = 0xcccccc;
	public static final int BLUE_COLOR = 0x0000ff;

	public static final int NUM_LIST = 20;
	public static final String NEXT_LABEL = "Next";
	public static final String PREVIOUS_LABEL = "Previous";
	public static final int DEFAULT_VOLUMN = 100;

}
