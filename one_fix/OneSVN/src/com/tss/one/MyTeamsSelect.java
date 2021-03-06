package com.tss.one;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.tssoft.one.webservice.WebServiceReaderMyTeam;
import com.tssoft.one.webservice.model.Table;
import com.tssoft.one.webservice.model.Team;

public class MyTeamsSelect extends MyActivity {
	private ArrayList<Table> leaguesList = new ArrayList<Table>();
	private ArrayAdapter<Object> leagueAdapter;
	private ArrayAdapter<Object> teamsAdapter;
	//private ProgressDialog m_ProgressDialog = null;
	private ProgressBar progressBar;
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
//			m_ProgressDialog.dismiss();
			
			runOnUiThread(new Runnable(){
				public void run(){
					progressBar.setVisibility(8);
				}						
			});// jen added
			
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
		setContentView(R.layout.my_teams_select);
		super.buildMenu(this, 2);
	
		 
		// Get dropdown
		Spinner leagueSpinner = (Spinner) findViewById(R.id.league_select);
		Spinner teamSpinner = (Spinner) findViewById(R.id.team_select);
		
		ImageButton addButton = (ImageButton) findViewById(R.id.team_select_btn);
		ImageButton cancelButton = (ImageButton) findViewById(R.id.team_select_cancel_btn);

		leagueAdapter = new ArrayAdapter<Object>(this, android.R.layout.simple_spinner_item);
		leagueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		teamsAdapter = new ArrayAdapter<Object>(this,android.R.layout.simple_spinner_item);
		teamsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		leagueSpinner.setAdapter(leagueAdapter);
		teamSpinner.setAdapter(teamsAdapter);
		
		
		leagueSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {	
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
			
			public void onClick(View v) {
				
				if(progressBar!=null){
					runOnUiThread(new Runnable(){
						public void run(){
							progressBar.setVisibility(0);
						}						
					});// jen added
				}else{
					progressBar = (ProgressBar) findViewById(R.id.progressbar);// jen added
				}

				final Team selectedTeam = (leaguesList.get(myLeageSpinner
						.getSelectedItemPosition())).teams.get(myTeamSpinner
						.getSelectedItemPosition());

				Thread thread = new Thread(new Runnable() {

					public void run() {
						Looper.prepare();
						Log.i("AddingTeam", "add");
						MyTeamsTab.needRefresh = true;
						boolean success = WebServiceReaderMyTeam.addUserTeam(
								WebServiceReaderMyTeam.getDeviceId(myAct),
								selectedTeam.getId());
						Log.i("AddingTeam", "finish add");
//						m_ProgressDialog.dismiss();
						
						runOnUiThread(new Runnable(){
							public void run(){
								progressBar.setVisibility(8);
							}						
						});// jen added
						
						myAct.finish();
						Log.i("AddingTeam", "show dialog");
						if (success) {
							MyTeamsList.current.publicUpdateMyteam(selectedTeam);
						}
						Log.i("AddingTeam", "dismiss progress");
						Looper.loop();
					}
				});
				thread.start();
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myTeamsListIntent = new Intent(view.getContext(),
						MyTeamsList.class);
				// newsListIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(myTeamsListIntent, 0);
			}
		});
		Thread thread = new Thread(null, viewNews, "MagentoBackground");
		thread.start();
		
//		m_ProgressDialog = ProgressDialog.show(MyTeamsSelect.this,
//				"Please wait...", "Retrieving data ...", true);
		
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

	private void getTeam() {
		try {
			leaguesList = WebServiceReaderMyTeam.getTablesTeams();

		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(displayLeague);
	}

}
