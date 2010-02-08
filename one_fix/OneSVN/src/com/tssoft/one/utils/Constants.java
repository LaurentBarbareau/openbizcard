package com.tssoft.one.utils;

import android.app.Activity;

import com.tss.one.GameDetail;
import com.tss.one.MainDetail;
import com.tss.one.MainList;
import com.tss.one.MyTeamsList;
import com.tss.one.MyTeamsNewsDetail;
import com.tss.one.MyTeamsSelect;
import com.tss.one.MyTeamsTab;
import com.tss.one.NewsDetail;
import com.tss.one.NewsList;
import com.tss.one.ScoreBoard;
import com.tss.one.ScoreBoardSelect;

public class Constants {
	// public static final String BANNER_URL =
	// "http://www.adservxt.com/cellcity/adservxt_iphone.php?zoneid=126439567251";
	private static final String BANNER_URL = "http://www.adservxt.com/thinkmobile/adservxt_iphone.php?zoneid=37";
	private static final String BANNER_URL_MAIN = "http://www.adservxt.com/thinkmobile/adservxt_iphone.php?zoneid=44";
	private static final String BANNER_URL_MYTEAM = "http://www.adservxt.com/thinkmobile/adservxt_iphone.php?zoneid=47";
	private static final String BANNER_URL_NEWS = "http://www.adservxt.com/thinkmobile/adservxt_iphone.php?zoneid=50";
	private static final String BANNER_URL_GAME_SCORES = "http://www.adservxt.com/thinkmobile/adservxt_iphone.php?zoneid=45";
	private static final String BANNER_URL_GAME_DETAIL = "http://www.adservxt.com/thinkmobile/adservxt_iphone.php?zoneid=49";
	private static final String BANNER_URL_NEWS_ARTICLE = "http://www.adservxt.com/thinkmobile/adservxt_iphone.php?zoneid=48";

	public static final String getBannerURL(Activity act) {
		String url = "";
		if (act instanceof GameDetail) {
			url = BANNER_URL_GAME_DETAIL;
		} else if (act instanceof MainDetail
				|| act instanceof MyTeamsNewsDetail
				|| act instanceof NewsDetail) {
			url = BANNER_URL_NEWS_ARTICLE;
		} else if (act instanceof MainList) {
			url = BANNER_URL_MAIN;
		} else if (act instanceof MyTeamsList || act instanceof MyTeamsTab) {
			url = BANNER_URL_MYTEAM;
		} else if (act instanceof MyTeamsSelect || act instanceof ScoreBoardSelect) {
			url = BANNER_URL;
		}  else if (act instanceof NewsList) {
			url = BANNER_URL_NEWS;
		} else if (act instanceof ScoreBoard) {
			url = BANNER_URL_GAME_SCORES;
		} 
		return url;
	}

	public static final int COUNT_DOWN_THREAD = 59; // 59;
}
