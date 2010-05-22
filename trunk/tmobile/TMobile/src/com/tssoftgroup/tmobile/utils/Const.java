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

	public static int VIDEO_WIDTH = 270 * Display.getWidth() / 480;// 3
	public static int VIDEO_HEIGHT = 180 * Display.getWidth() / 480; // 2

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
	public static final String URL_SEND_VIDEO_CHUNK = "http://173.203.97.162/web/api/postvidnew.php";
	public static final String URL_SEND_VIDEO = "http://173.203.97.162/web/api/postvidnew.php";
	public static final String URL_SEND_DOC_CHUNK = "http://173.203.97.162/web/api/postdoc.php";
	public static final String URL_SEND_DOC = "http://173.203.97.162/web/api/postdoc.php";
	public static final String URL_VIEW_VIDEO = "http://173.203.97.162/web/viewmcast.php";
	public static final String URL_LOGIN = "http://173.203.97.162/web/userlogin.php";
	public static final String URL_COMMENT_VIDEOCONNECT = "http://173.203.97.162/web/addvideoconnectcomment.php";
	public static final String URL_COMMENT_MCAST = "http://173.203.97.162/web/addmcastcomment.php";
	public static final String URL_SEND_MOREINFO_EMAIL = "http://173.203.97.162/web/sendmoreinfoemail.php";
	public static final String URL_VIEW_TRAINING = "http://173.203.97.162/web/gettraining.php";
	public static final String URL_VIEW_TRAINING_ANS = "http://173.203.97.162/web/gettrainingans.php";
	public static final String URL_ADD_TRAINING_RESULT = "http://173.203.97.162/web/addtraininghistory.php";
	public static final String URL_VIEW_PROJECT = "http://173.203.97.162/web/getproject.php";
	public static final String URL_VIEW_DOCUMENT = "http://173.203.97.162/web/getdocument.php";
	public static final String URL_VIEW_POLL = "http://173.203.97.162/web/getpoll.php";
	public static final String URL_VIEW_POLL_CHOICE = "http://173.203.97.162/web/getpollchoice.php";
	public static final String URL_INCREASE_POLL_COUNT = "http://173.203.97.162/web/increasepollcount.php";
	public static final String URL_EMAIL_DOC = "http://173.203.97.162/web/senddocemail.php";
	public static final String DOCUMENT_PATH = "http://173.203.97.162/web/api/docs/";//Microfunding Proposal Template.doc
	// 
	public static final String URL_VIDEO_DOWNLOAD= "http://173.203.97.162/movies/";
	public static final int BG_COLOR = 0xeeeeee;
	public static final int BG_COLOR_MAIN = 0xcdcdcd;
	public static final int LIST_BG_COLOR = 0xffffff;
	public static final int LIST_BG_COLOR_UNFOCUS = 0xf7f7f7;
	public static final int GRAY_COLOR = 0xcccccc;
	public static final int BLUE_COLOR = 0x0000ff;

	public static final int NUM_LIST = 10;
	public static final String NEXT_LABEL = "Next";
	public static final String PREVIOUS_LABEL = "Previous";
	public static final String DELETE_LABEL = "Delete";
	public static final int DEFAULT_VOLUMN = 60;
	public static final int NUM_RETRY_HTTP = 5;
	public static final int DURATION_MARGIN_TOP = 15;
}
