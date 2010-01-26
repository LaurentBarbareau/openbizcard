package com.tss.one;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tssoft.one.webservice.ImageLoader;
import com.tssoft.one.webservice.ImageLoaderFactory;
import com.tssoft.one.webservice.WebServiceReaderScoreBoard;
import com.tssoft.one.webservice.model.Article;
import com.tssoft.one.webservice.model.Game;

public class GameDetail extends MyListActivity {
	public static String gameId;
	public static ProgressDialog dig;

	public void loadGame(String gameId) {
		if (gameId != null) {
			GameDetail.dig = ProgressDialog.show(this, "Please wait...",
					"Retrieving data ...", true);
			final String myGameId = gameId;
			new Thread(new Runnable() {

				public void run() {
					ArrayList<Game> games = WebServiceReaderScoreBoard
							.getGameByID(myGameId);
					if (games.size() > 0) {
						final Game game = games.get(0);

						System.out.println("Num 3");
						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								TextView gameDetailScoreTV = (TextView) findViewById(R.id.game_detail_score);
								TextView gameDetailFirstHalfScoreTV = (TextView) findViewById(R.id.game_detail_1st_half_score);
								TextView gameDetailMinuteTV = (TextView) findViewById(R.id.game_detail_minute);
								ImageView guestLogo = (ImageView) findViewById(R.id.game_detail_logo2);
								ImageView homeLogo = (ImageView) findViewById(R.id.game_detail_logo1);
								gameDetailScoreTV.setText(game.getGuestScore() + " - " + game.getHomeScore());
								gameDetailFirstHalfScoreTV.setText(game.getGuestHalfScore() + " - " + game.getHomeHalfScore());
								gameDetailMinuteTV.setText(game.getGameMinute());
								loader.setTask(game.getGuestIcon(), guestLogo);
								loader.setTask(game.getHomeIcon(), homeLogo);
								loader.go();
							}
						});
					}
					GameDetail.dig.dismiss();
				}
			}).start();

		} else {

		}
	}
	ImageLoader loader;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game_detail);
		try {
			loader = ImageLoaderFactory.createImageLoader(this);
			loader.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadGame(gameId);
	}
	
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
		Log.i("Oak", "Start Activity");

		// Log.i("Oak", "article " + article);
		// loadArticle(article);
		// loadArticle(article);
		// overridePendingTransition(0, 0);
	}
//	private class ScorerAdapter extends ArrayAdapter<Object> {
//
//		private ArrayList<Object> items;
//
//		public NewsAdapter(Context context, int textViewResourceId,
//				ArrayList<Object> items) {
//			super(context, textViewResourceId, items);
//			this.items = items;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			if (chkList.containsKey(position))
//				return chkList.get(position);
//
//			View v = convertView;
//			LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//			Typeface face = Typeface.createFromAsset(getAssets(),
//					"fonts/Arial.ttf");
//
//			TextView headline;
//			TextView sc;
//			Object i = items.get(position);
//
//			if (i instanceof String) {
//				v = vi.inflate(R.layout.red_list, null);
//				headline = (TextView) v.findViewById(R.id.subject_title);
//				headline.setTypeface(face);
//				headline.setText((String) i);
//			} else {
//				Article article = (Article) i;
//
//				v = vi.inflate(R.layout.white_list, null);
//				ImageView imgView = (ImageView) v
//						.findViewById(R.id.small_main_image);
//				headline = (TextView) v.findViewById(R.id.small_main_headline);
//				sc = (TextView) v.findViewById(R.id.small_main_sc);
//				headline.setTypeface(face);
//				sc.setTypeface(face);
//
//				headline.setText(article.getTitle());
//				sc.setText(article.getScTitle());
//
//				ImageLoaderFactory.createImageLoader(NewsList.this).setTask(
//						article.getImageUrl(), imgView);
//				ImageLoaderFactory.createImageLoader(NewsList.this).go();
//
//			}
//			chkList.put(position, v);
//
//			return v;
//		}
//	}
}
