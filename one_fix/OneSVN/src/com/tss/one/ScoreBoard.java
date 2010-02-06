package com.tss.one;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tss.one.adapter.ScoreBoardAdapter;
import com.tss.one.listener.ScoreBoardTabCL;
import com.tssoft.one.utils.Constants;
import com.tssoft.one.utils.ElementState;
import com.tssoft.one.webservice.ImageLoaderFactory;
import com.tssoft.one.webservice.WebServiceReaderScoreBoard;
import com.tssoft.one.webservice.model.GameBySubject;

public class ScoreBoard extends MyListActivity {
	public static ScoreBoard instance; 
	public final static int TODAY_GAME_TAB = 3;
	public final static int LIVE_GAME_TAB = 2;
	public final static int LEAGUE_TAB = 1;

	public ArrayList<Object> scoreBoardList = null;
	public ArrayList<GameBySubject> liveList = null;
	public ArrayAdapter<Object> scoreBoardAdapter = null;

	private Timer timer=null;
	private ScoreBoardTabCL tabClickListener = null;
//	private ProgressDialog m_ProgressDialog = null;
	private ProgressBar progressBar;

	private int dayOffset = 0;
	private int currentTab = TODAY_GAME_TAB;
	private String spinnerId = "";
	
	public CountDownThread cdThread ;

	@Override 
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig); 
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.score_board_tab);
		cdThread = new CountDownThread(this, Constants.COUNT_DOWN_THREAD);
		super.buildMenu(this, 4);
		
		// test navigate text
		TextView score_board_sec_label = (TextView)findViewById(R.id.score_board_sec_label);
		TextView score_board_next_label = (TextView)findViewById(R.id.score_board_next_label);
		TextView score_board_date_label2 = (TextView)findViewById(R.id.score_board_date_label2);
		TextView score_board_date_label1 = (TextView)findViewById(R.id.score_board_date_label1);
		float textSize = score_board_sec_label.getTextSize()-1;
		score_board_sec_label.setTextSize(textSize);
		score_board_next_label.setTextSize(textSize);
		score_board_date_label2.setTextSize(textSize);
		score_board_date_label1.setTextSize(textSize);
		
		// set layout frame list
//		FrameLayout f = (FrameLayout)findViewById(R.id.score_list_frame_layout);
//		f.setLayoutParams(new LayoutParams(320, 376));
//		ListView lView = (ListView)getListView();
//		lView.getLayoutParams().height = 224;
		
		ImageButton tab1 = (ImageButton) findViewById(R.id.score_board_tab1);
		ImageButton tab2 = (ImageButton) findViewById(R.id.score_board_tab2);
		ImageButton tab3 = (ImageButton) findViewById(R.id.score_board_tab3);

		ImageView refreshIcon = ((ImageView) findViewById(R.id.refrest_icon));
		final ScoreBoard act = this;
		refreshIcon.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				Intent mainDetailIntent = new Intent(act, ScoreBoard.class);
				// mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(mainDetailIntent);

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
		scoreBoardAdapter = new ScoreBoardAdapter(this, R.layout.score_board_tab, scoreBoardList);
		setListAdapter(scoreBoardAdapter);
		
		timer = new Timer();
		timer.scheduleAtFixedRate(cdThread, 0, 1000);
		
		Thread thread = new Thread(null, viewScoreBoard, "MagentoBackground");
		thread.start();
		if(progressBar!=null){
			runOnUiThread(new Runnable(){
				public void run(){
					progressBar.setVisibility(0);
				}						
			});// jen added
		}else{
			progressBar = (ProgressBar) findViewById(R.id.progressbar);// jen added
		}	
	}

	public void setCurrentTab(int t) {
		currentTab = t;
	}

	public int getCurrentTab() {
		return currentTab;
	}

	public void setDayOffset(int dayOffset) {
		this.dayOffset = dayOffset;
		scoreBoardList.clear();
	}

	public int getDayOffset() {
		return dayOffset;
	}

	public void getValueFromSpinner(View view) {
		startActivityForResult(new Intent(view.getContext(),
				ScoreBoardSelect.class), 0);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		spinnerId = ScoreBoardSelect.leagueId;
		setCurrentTab(ScoreBoard.LEAGUE_TAB);
		((ScoreBoardAdapter) scoreBoardAdapter).clearItem();
		scoreBoardList.clear();
		setLeagueGame();
	}
//	public void finish() {
//		super.finish();
//		ImageLoaderFactory.clear(this);
//	}
	public void updateScore(){
		if(currentTab == TODAY_GAME_TAB){
//			scoreBoardList.clear();
//			scoreBoardAdapter.clear();
			setScoreBoard();
		}
		if(currentTab == LIVE_GAME_TAB){			
//			scoreBoardList.clear();
//			scoreBoardAdapter.clear();
			setLiveGame();
		}
		if(currentTab == LEAGUE_TAB){
//			scoreBoardList.clear();
//			scoreBoardAdapter.clear();
			setLeagueGame();

		}
	}
	
	public void setScoreBoard() {
		Thread t = new Thread(viewScoreBoard);
		t.start();
		if(progressBar!=null){
			runOnUiThread(new Runnable(){
				public void run(){
					progressBar.setVisibility(View.VISIBLE);
				}						
			});// jen added
		}else{
			progressBar = (ProgressBar) findViewById(R.id.progressbar);// jen added
		}
	}

	public void setLiveGame() {
		Thread t = new Thread(viewLiveGame);
		t.start();
		if(progressBar!=null){
			runOnUiThread(new Runnable(){
				public void run(){
					progressBar.setVisibility(0);
				}						
			});// jen added
		}else{
			progressBar = (ProgressBar) findViewById(R.id.progressbar);// jen added
		}

	}

	public void setLeagueGame() {
		Thread t = new Thread(viewLeagueGame);
		t.start();
		if(progressBar!=null){
			runOnUiThread(new Runnable(){
				public void run(){
					progressBar.setVisibility(0);
				}						
			});// jen added
		}else{
			progressBar = (ProgressBar) findViewById(R.id.progressbar);// jen added
		}
	}

	public String cDate = null;
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHH:mm:ssZ");
	private Runnable viewScoreBoard = new Runnable() {
		public void run() {
			if (cDate == null) {
				System.out.println("================>>>>>>>>> start date");
				cDate = WebServiceReaderScoreBoard.getCurrentDate();
				cDate = cDate.replace("T", "");
				try {
					Date d = formatter.parse(cDate);
					// next and previous
					SimpleDateFormat newformatter = new SimpleDateFormat("dd/MM/yyy");
					final String date = newformatter.format(d);
					newformatter = new SimpleDateFormat("HH:mm");
					final String time = newformatter.format(d);
					runOnUiThread(new Runnable() {
						public void run() {
							TextView dateTv = (TextView) findViewById(R.id.score_board_date);
							dateTv.setText(date);
							TextView timeTv = (TextView) findViewById(R.id.score_board_next);
							timeTv.setText(time);							
						}
					});
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("================>>>>>>>>> end date");
			}
			
			/**
			 * add new score board
			 */			
			runOnUiThread(new Runnable() {
				public void run() {
					scoreBoardList.clear();
					scoreBoardAdapter.clear();						
				}
			});
			ArrayList<GameBySubject> gbs = WebServiceReaderScoreBoard.getGamesByDay(dayOffset + "");
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
			/**
			 * add new score board
			 */			
			runOnUiThread(new Runnable() {
				public void run() {
					scoreBoardList.clear();
					scoreBoardAdapter.clear();						
				}
			});
			ArrayList<GameBySubject> gbs = WebServiceReaderScoreBoard.getLiveGames();
			liveList = gbs;
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
			try {
				/**
				 * add new score board
				 */			
				runOnUiThread(new Runnable() {
					public void run() {
						scoreBoardList.clear();
						scoreBoardAdapter.clear();						
					}
				});
				GameBySubject gbs = WebServiceReaderScoreBoard.getGamesBySubject(spinnerId);
				scoreBoardList.add(gbs.subject);
				scoreBoardList.addAll(gbs.games);
			} catch (Exception e) {
				if (e != null && e.getMessage() != null) {
					Log.e("Dont have internet ", e.getMessage());
				}
				// Utils.showAlert(this, "No Internet Connection.");
			}
			runOnUiThread(displayChanged);
			if (!ImageLoaderFactory.createImageLoader(ScoreBoard.this).isRunning) {
				ImageLoaderFactory.createImageLoader(ScoreBoard.this).start();
			}

		}
	};

	private Runnable displayChanged = new Runnable() {
		public void run() {
			if (tabClickListener.tabId == 2) {
				if (liveList == null || liveList.size() == 0) {
					displayNoLiveGamesDialog();
					setFocusTab(3);
				} else {
					scoreBoardAdapter.notifyDataSetChanged();
					progressBar.setVisibility(View.GONE);
				}
			}
			else if (tabClickListener.tabId == 3) {
				scoreBoardAdapter.notifyDataSetChanged();
				progressBar.setVisibility(View.GONE);
			}
		}
	};
	
	
	/**
	 * Display dialog
	 * No games.
	 */
	public void displayNoLiveGamesDialog(){

		// display dialog box
		// when no news
		AlertDialog alertDialog = new AlertDialog.Builder(ScoreBoard.this).create();
		alertDialog.setTitle(getText(R.string.no_live_game_title));
		alertDialog.setMessage(getText(R.string.no_live_game_popup));
		alertDialog.setButton(getText(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
		alertDialog.show();			
	}
	
	
	
	public void setFocusTab(int displayTab){
		ImageButton tab1 = (ImageButton) findViewById(R.id.score_board_tab1);
		ImageButton tab2 = (ImageButton) findViewById(R.id.score_board_tab2);
		ImageButton tab3 = (ImageButton) findViewById(R.id.score_board_tab3);
		
		progressBar.setVisibility(View.VISIBLE);
		switch(displayTab){
		case 1:
			tabClickListener.onClick(tab1);
			break;
		case 2:
			tabClickListener.onClick(tab2);
			break;
		case 3:
			tabClickListener.onClick(tab3);
			break;
		}
	}
}
