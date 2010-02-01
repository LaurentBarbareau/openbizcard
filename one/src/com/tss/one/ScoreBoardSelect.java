package com.tss.one;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.tssoft.one.webservice.WebServiceReaderMyTeam;
import com.tssoft.one.webservice.WebServiceReaderScoreBoard;
import com.tssoft.one.webservice.model.LiveSubject;
import com.tssoft.one.webservice.model.Table;
import com.tssoft.one.webservice.model.Team;

public class ScoreBoardSelect extends MyActivity {
	private ArrayList<LiveSubject> leaguesList = new ArrayList<LiveSubject>();
	private ArrayAdapter leagueAdapter;
	private ProgressDialog m_ProgressDialog = null;
	public static String leagueId = "";
	private Runnable displayLeague = new Runnable() {
		public void run() {
			int i = 0;
			for (LiveSubject a : leaguesList) {
				leagueAdapter.add(a.getName());
				i++;
			}
			leagueAdapter.notifyDataSetChanged();
			m_ProgressDialog.dismiss();
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
		super.buildMenu(this);
		
		// Get dropdown
		Spinner leagueSpinner = (Spinner) findViewById(R.id.league_select);

		ImageButton addButton = (ImageButton) findViewById(R.id.team_select_btn);

		leagueAdapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item);
		leagueAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		leagueAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		leagueSpinner.setAdapter(leagueAdapter);

		Runnable viewNews = new Runnable() {
			public void run() {
				getTeam();
			}
		};
		final Spinner myLeageSpinner = leagueSpinner;
		final Activity myAct = this;
		addButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//m_ProgressDialog.show();
				String leagueId = leaguesList.get(
						myLeageSpinner.getSelectedItemPosition()).getId();
				ScoreBoardSelect.leagueId = leagueId;
				finish();
			}
		});
		Thread thread = new Thread(null, viewNews, "MagentoBackground");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(ScoreBoardSelect.this,
				"Please wait...", "Retrieving data ...", true);
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
