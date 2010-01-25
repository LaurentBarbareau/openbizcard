package com.tss.one;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.tssoft.one.webservice.model.Table;
import com.tssoft.one.webservice.model.Team;

public class MyTeamsSelect extends MyActivity {
	private ArrayList<Table> leaguesList = new ArrayList<Table>();
	private ArrayAdapter leagueAdapter;
	private ArrayAdapter teamsAdapter;
	private ProgressDialog m_ProgressDialog = null;
	private Runnable displayLeague = new Runnable() {
		public void run() {
			int i = 0;
			for (Table a : leaguesList) {
				if (i == 0) {
					for (Team t : a.teams) {
						teamsAdapter.add(t.getName());
					}
				}
				leagueAdapter.add(a.name);
				i++;
			}
			leagueAdapter.notifyDataSetChanged();
			teamsAdapter.notifyDataSetChanged();
			m_ProgressDialog.dismiss();
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.my_teams_select);
		super.buildMenu(this);
		
		// Get dropdown
		Spinner leagueSpinner = (Spinner) findViewById(R.id.league_select);
		Spinner teamSpinner = (Spinner) findViewById(R.id.team_select);

		ImageButton addButton = (ImageButton) findViewById(R.id.team_select_btn);

		leagueAdapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item);
		leagueAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		teamsAdapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item);
		leagueAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		leagueSpinner.setAdapter(leagueAdapter);
		teamSpinner.setAdapter(teamsAdapter);
		leagueSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				// your code here
				System.out.println("aa");
				Table a = leaguesList.get(position);
				teamsAdapter.clear();
				for (Team t : a.teams) {
					teamsAdapter.add(t.getName());
				}
				leagueAdapter.notifyDataSetChanged();
				teamsAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				System.out.println("aa");
			}

		});

		Runnable viewNews = new Runnable() {
			public void run() {
				getTeam();
			}
		};
		final Spinner myLeageSpinner = leagueSpinner;
		final Spinner myTeamSpinner = teamSpinner;
		final Activity myAct = this;
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_ProgressDialog.show();
				final Team selectedTeam = (leaguesList.get(myLeageSpinner
						.getSelectedItemPosition())).teams.get(myTeamSpinner
						.getSelectedItemPosition());

				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						Looper.prepare();
						Log.i("AddingTeam", "add");
						boolean success = WebServiceReaderMyTeam.addUserTeam(
								WebServiceReaderMyTeam.getDeviceId(myAct),
								selectedTeam.getId());
						Log.i("AddingTeam", "finish add");
						m_ProgressDialog.dismiss();
						myAct.finish();
						Log.i("AddingTeam", "show dialog");
						if (success) {
							MyTeamsList.current
									.publicUpdateMyteam(selectedTeam);
						}
						Log.i("AddingTeam", "dismiss progress");
						Looper.loop();
					}
				});
				thread.start();
			}
		});
		Thread thread = new Thread(null, viewNews, "MagentoBackground");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(MyTeamsSelect.this,
				"Please wait...", "Retrieving data ...", true);
	}

	private void getTeam() {
		try {
			leaguesList = WebServiceReaderMyTeam.getTablesTeams();

		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(displayLeague);
	}

}
