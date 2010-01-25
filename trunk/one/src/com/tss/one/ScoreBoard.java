package com.tss.one;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import com.tss.one.adapter.ScoreBoardAdapter;
import com.tss.one.listener.ScoreBoardTabCL;
import com.tssoft.one.utils.ElementState;
import com.tssoft.one.utils.Utils;
import com.tssoft.one.webservice.ImageLoaderFactory;
import com.tssoft.one.webservice.WebServiceReaderScoreBoard;
import com.tssoft.one.webservice.model.Game;
import com.tssoft.one.webservice.model.GameBySubject;

public class ScoreBoard extends MyListActivity {

	public final static int TODAY_GAME_TAB = 3;
	public final static int LIVE_GAME_TAB = 2;
	public final static int LEAGUE_TAB = 1;

	public ArrayList<Object> scoreBoardList = null;
	public ArrayList<Object> liveList = null;
	public ArrayAdapter<Object> scoreBoardAdapter = null;

	private ScoreBoardTabCL tabClickListener = null;
	private ProgressDialog m_ProgressDialog = null;

	private String dayOffset = "0";
	private int currentTab = TODAY_GAME_TAB;
	private String spinnerId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.score_board_tab);
		super.buildMenu(this);
		ImageButton icon0 = (ImageButton) findViewById(R.id.main_button);
		ImageButton icon1 = (ImageButton) findViewById(R.id.my_teams_button);
		ImageButton icon2 = (ImageButton) findViewById(R.id.news_button);

		ImageButton tab1 = (ImageButton) findViewById(R.id.score_board_tab1);
		ImageButton tab2 = (ImageButton) findViewById(R.id.score_board_tab2);
		ImageButton tab3 = (ImageButton) findViewById(R.id.score_board_tab3);

		icon0.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent mainDetailIntent = new Intent(view.getContext(),
						MainList.class);
				// mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(mainDetailIntent, 0);
			}
		});

		icon1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myTeamsTabIntent = new Intent(view.getContext(),
						MyTeamsTab.class);
				// mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(myTeamsTabIntent, 0);
			}
		});

		icon2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent newsListIntent = new Intent(view.getContext(),
						NewsList.class);
				// newsListIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(newsListIntent, 0);
			}
		});

		// add Tab's Listener to change tab

		ElementState e1 = new ElementState(R.drawable.score_board_tab1_over,
				R.drawable.score_board_tab1, false);
		ElementState e2 = new ElementState(R.drawable.score_board_tab2_over,
				R.drawable.score_board_tab2, false);
		ElementState e3 = new ElementState(R.drawable.score_board_tab3_over,
				R.drawable.score_board_tab3, true);
		HashMap<View, ElementState> elements = new HashMap<View, ElementState>();

		elements.put(findViewById(R.id.score_board_tab1), e1);
		elements.put(findViewById(R.id.score_board_tab2), e2);
		elements.put(findViewById(R.id.score_board_tab3), e3);

		tabClickListener = new ScoreBoardTabCL(elements, this);
		tab1.setOnClickListener(tabClickListener);
		tab2.setOnClickListener(tabClickListener);
		tab3.setOnClickListener(tabClickListener);

		scoreBoardList = new ArrayList<Object>();

		scoreBoardAdapter = new ScoreBoardAdapter(ScoreBoard.this,
				R.layout.score_board_tab, scoreBoardList);
		setListAdapter(scoreBoardAdapter);

		Thread thread = new Thread(null, viewScoreBoard, "MagentoBackground");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(ScoreBoard.this,
				"Please wait...", "Retrieving data ...", true);

	}

	public void setCurrentTab(int t) {
		currentTab = t;
	}

	public int getCurrentTab() {
		return currentTab;
	}

	public void setDayOffset(String dayOffset) {
		this.dayOffset = dayOffset;
		scoreBoardList.clear();
	}

	public String getDayOffset() {
		return dayOffset;
	}

	public void getValueFromSpinner(View view) {
		startActivityForResult(new Intent(view.getContext(),
				ScoreBoardSelect.class), 0);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		spinnerId = ScoreBoardSelect.leagueId;
	}

	public void setScoreBoard() {
		Thread t = new Thread(viewScoreBoard);
		t.start();
		m_ProgressDialog = ProgressDialog.show(ScoreBoard.this,
				"Please wait...", "Retrieving data ...", true);
	}

	public void setLiveGame() {

		Thread t = new Thread(viewLiveGame);
		t.start();
		m_ProgressDialog = ProgressDialog.show(ScoreBoard.this,
				"Please wait...", "Retrieving data ...", true);

	}

	public void setLeagueGame() {

		Thread t = new Thread(viewLeagueGame);
		t.start();
		m_ProgressDialog = ProgressDialog.show(ScoreBoard.this,
				"Please wait...", "Retrieving data ...", true);

	}

	private Runnable viewScoreBoard = new Runnable() {
		public void run() {
			ArrayList<GameBySubject> gbs = WebServiceReaderScoreBoard
					.getGamesByDay(dayOffset);
			for (GameBySubject g : gbs) {
				scoreBoardList.add(g.subject);
				scoreBoardList.addAll(g.games);
			}
			runOnUiThread(displayChanged);
			if (!ImageLoaderFactory.createImageLoader(ScoreBoard.this).isRunning) {
				ImageLoaderFactory.createImageLoader(ScoreBoard.this).start();
			}

		}
	};

	public Runnable viewLiveGame = new Runnable() {
		public void run() {

			ArrayList<GameBySubject> gbs = WebServiceReaderScoreBoard
					.getLiveGames();
			for (GameBySubject g : gbs) {
				scoreBoardList.add(g.subject);
				scoreBoardList.addAll(g.games);
			}
			runOnUiThread(displayChanged);
			if (!ImageLoaderFactory.createImageLoader(ScoreBoard.this).isRunning) {
				ImageLoaderFactory.createImageLoader(ScoreBoard.this).start();
			}
		}
	};

	public Runnable viewLeagueGame = new Runnable() {
		public void run() {
			System.out.println("l run lready");
			try {
				ArrayList<GameBySubject> gbs = WebServiceReaderScoreBoard
						.getUserGames(spinnerId);
		
				for (GameBySubject g : gbs) {
					scoreBoardList.add(g.subject);
					scoreBoardList.addAll(g.games);
				}
			} catch (UnknownHostException e) {
				Log.e("Dont have internet ", e.getMessage());
//				Utils.showAlert(this, "No Internet Connection.");
			}
				runOnUiThread(displayChanged);
				if (!ImageLoaderFactory.createImageLoader(ScoreBoard.this).isRunning) {
					ImageLoaderFactory.createImageLoader(ScoreBoard.this)
							.start();
				}
			
		}
	};

	private Runnable displayChanged = new Runnable() {
		public void run() {
			if (currentTab == TODAY_GAME_TAB) {
				if (scoreBoardList != null && scoreBoardList.size() > 0) {
					scoreBoardAdapter.notifyDataSetChanged();
				}
			}
			if (currentTab == LIVE_GAME_TAB) {
				if (scoreBoardList != null && scoreBoardList.size() > 0) {
					scoreBoardAdapter.notifyDataSetChanged();
				}
			}
			if (currentTab == LEAGUE_TAB) {
				if (scoreBoardList != null && scoreBoardList.size() > 0) {
					scoreBoardAdapter.notifyDataSetChanged();
				}
			}
			m_ProgressDialog.dismiss();

		}
	};
}
