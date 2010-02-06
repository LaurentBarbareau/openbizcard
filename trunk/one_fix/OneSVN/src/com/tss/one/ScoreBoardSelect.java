package com.tss.one;

import java.util.ArrayList;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.tssoft.one.webservice.WebServiceReaderScoreBoard;
import com.tssoft.one.webservice.model.LiveSubject;

public class ScoreBoardSelect extends MyActivity {
	private static ScoreBoardSelect instance;
	private ArrayList<LiveSubject> leaguesList = new ArrayList<LiveSubject>();
	private ArrayAdapter<Object> leagueAdapter;
//	private ProgressDialog m_ProgressDialog = null;
	private ProgressBar progressBar;
	public static String leagueId = "";
	private Runnable displayLeague = new Runnable() {
		public void run() {
			int i = 0;
			for (LiveSubject a : leaguesList) {
				leagueAdapter.add(a.getName());
				i++;
			}
			leagueAdapter.notifyDataSetChanged();
			progressBar.setVisibility(8);
		}
	};

	@Override 
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig); 
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.score_board_select_league);
		super.buildMenu(this, 4);
		
		instance = this;
		
		TextView tView = (TextView)findViewById(R.id.league__sel_title_text);
		Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Arial.ttf");
		tView.setTypeface(face, Typeface.BOLD);
		
		//Jen add
		ImageButton backBtn = (ImageButton) findViewById(R.id.score_detail_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {				
				Intent scoreIntent = new Intent(view.getContext(), ScoreBoard.class);
//				// mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(scoreIntent);
//				instance.finish();
			}
		});
		
		// Get dropdown
		Spinner leagueSpinner = (Spinner) findViewById(R.id.league_select);
		ImageButton addButton = (ImageButton) findViewById(R.id.team_select_btn);

		leagueAdapter = new ArrayAdapter<Object>(this,
				android.R.layout.simple_spinner_item);
		leagueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		leagueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		leagueSpinner.setAdapter(leagueAdapter);

		Runnable viewNews = new Runnable() {
			public void run() {
				getTeam();
			}
		};
		final Spinner myLeageSpinner = leagueSpinner;
		addButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//m_ProgressDialog.show();
				String leagueId = leaguesList.get(myLeageSpinner.getSelectedItemPosition()).getId();
				ScoreBoardSelect.leagueId = leagueId;
				finish();
			}
		});
		Thread thread = new Thread(null, viewNews, "MagentoBackground");
		thread.start();
		progressBar = (ProgressBar) findViewById(R.id.progressbar);// jen added
	}

	private void getTeam() {
		try {
			leaguesList = WebServiceReaderScoreBoard.getCurrentSubjects();
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(displayLeague);
	}

}
