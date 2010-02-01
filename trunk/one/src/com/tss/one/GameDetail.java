package com.tss.one;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tssoft.one.webservice.ImageLoader;
import com.tssoft.one.webservice.ImageLoaderFactory;
import com.tssoft.one.webservice.WebServiceReaderScoreBoard;
import com.tssoft.one.webservice.model.Game;
import com.tssoft.one.webservice.model.GameEvent;

public class GameDetail extends MyListActivity {
	public static String gameId;
	public static ProgressDialog dig;

	private HashMap<Integer, View> chkList = new HashMap<Integer, View>();
	// private ProgressDialog m_ProgressDialog = null;
	private ArrayList<GameEvent> eventsList = new ArrayList<GameEvent>();
	private ScorerAdapter adapter;
	public static boolean isShow = false;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

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

							public void run() {

								TextView gameDetailScoreTV = (TextView) findViewById(R.id.game_detail_score);
								TextView gameDetailFirstHalfScoreTV = (TextView) findViewById(R.id.game_detail_1st_half_score);
								TextView gameDetailMinuteTV = (TextView) findViewById(R.id.game_detail_minute);
								ImageView guestLogo = (ImageView) findViewById(R.id.game_detail_logo2);
								ImageView homeLogo = (ImageView) findViewById(R.id.game_detail_logo1);
								gameDetailScoreTV.setText(game.getGuestScore()
										+ " - " + game.getHomeScore());
								if (!(game.getGuestHalfScore().equals("-1"))) {
									gameDetailFirstHalfScoreTV.setText(game
											.getGuestHalfScore()
											+ " - " + game.getHomeHalfScore());
								} else {
									gameDetailFirstHalfScoreTV.setText("");
								}
								gameDetailMinuteTV
										.setText(game.getGameMinute());
								for (int i = 0; i < game.guestEvents.size(); i++) {
									GameEvent temp = game.guestEvents.get(i);
									temp.isHome = false;
									eventsList.add(temp);
								}
								for (int i = 0; i < game.homeEvents.size(); i++) {
									GameEvent temp = game.homeEvents.get(i);
									temp.isHome = true;
									eventsList.add(temp);
								}
								// // Dummy
								// GameEvent e = new GameEvent("score" , "oak");
								// GameEvent e2 = new GameEvent("score" ,
								// "oak2");
								// GameEvent e3 = new GameEvent("score" ,
								// "oak3");
								// GameEvent e4 = new GameEvent("score" ,
								// "oak4");
								// e3.isHome = false;
								// e4.isHome = false;
								// eventsList.add(e);
								// eventsList.add(e2);
								// eventsList.add(e3);
								// eventsList.add(e4);
								// // End Dummy
								adapter.notifyDataSetChanged();
								loader.setTask(game.getGuestIcon(), guestLogo);
								loader.setTask(game.getHomeIcon(), homeLogo);
								loader.go();
								GameDetail.dig.dismiss();
							}
						});
					}

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
		super.buildMenu(this);
		try {
			loader = ImageLoaderFactory.createImageLoader(this);
			loader.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.adapter = new ScorerAdapter(this, R.layout.game_detail, eventsList);
		setListAdapter(adapter);
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

	private class ScorerAdapter extends ArrayAdapter<GameEvent> {

		private ArrayList<GameEvent> items;
		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		public ScorerAdapter(Context context, int textViewResourceId,
				ArrayList<GameEvent> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (chkList.containsKey(position))
				return chkList.get(position);

			View v = convertView;

			Object i = items.get(position);

			if (i instanceof GameEvent) {
				GameEvent game = (GameEvent) i;
				v = vi.inflate(R.layout.game_detail_score_element, null);
				if (game.isHome) {
					((TextView) v.findViewById(R.id.home_scorer))
							.setText(game.description);
					((ImageView) v.findViewById(R.id.guest_ball))
							.setVisibility(TextView.INVISIBLE);
					((ImageView) v.findViewById(R.id.guest_line))
							.setVisibility(TextView.INVISIBLE);
					((TextView) v.findViewById(R.id.guest_scorer))
							.setVisibility(TextView.INVISIBLE);
				} else {
					((TextView) v.findViewById(R.id.guest_scorer))
							.setText(game.description);
					((ImageView) v.findViewById(R.id.home_ball))
							.setVisibility(TextView.INVISIBLE);
					((ImageView) v.findViewById(R.id.home_line))
							.setVisibility(TextView.INVISIBLE);
					((TextView) v.findViewById(R.id.home_scorer))
							.setVisibility(TextView.INVISIBLE);
				}

			}
			chkList.put(position, v);

			return v;
		}
	}
}
