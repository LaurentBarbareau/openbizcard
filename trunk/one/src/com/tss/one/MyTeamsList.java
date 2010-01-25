package com.tss.one;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tss.one.listener.TabClickListener;
import com.tssoft.one.utils.Utils;
import com.tssoft.one.webservice.ImageLoaderFactory;
import com.tssoft.one.webservice.WebServiceReaderMyTeam;
import com.tssoft.one.webservice.model.Team;

public class MyTeamsList extends MyListActivity {
	public static MyTeamsList current = null;
	private TabClickListener tabClickListener = null;
	private EditTeamAdapter teamsAdapter;
	// private NewsAdapter newsAdapter;
	private HashMap<Integer, View> chkList = new HashMap<Integer, View>();
	public ProgressDialog m_ProgressDialog = null;
	private ArrayList<Team> teamsList = new ArrayList<Team>();
	private Runnable viewMyTeam;
	private Runnable displayNews = new Runnable() {
		public void run() {
			if (teamsList != null && teamsList.size() > 0) {
				teamsAdapter.notifyDataSetChanged();
			}
			m_ProgressDialog.dismiss();
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		current = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.my_teams_list);
		super.buildMenu(this);

		// ImageButton icon0 = (ImageButton) findViewById(R.id.main_button);
		// ImageButton icon2 = (ImageButton) findViewById(R.id.news_button);
		// ImageButton icon3 = (ImageButton)
		// findViewById(R.id.score_board_button);

		ImageButton addTeam = (ImageButton) findViewById(R.id.my_teams_add);
		ImageButton back2Tab = (ImageButton) findViewById(R.id.my_teams_back);

		// icon0.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View view) {
		// Intent mainDetailIntent = new Intent(view.getContext(),
		// MainList.class);
		// // mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		// startActivityForResult(mainDetailIntent, 0);
		// }
		// });
		//
		// icon2.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View view) {
		// Intent newsListIntent = new Intent(view.getContext(),
		// NewsList.class);
		// // newsListIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		// startActivityForResult(newsListIntent, 0);
		// }
		// });

		addTeam.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myTeamsSelectIntent = new Intent(view.getContext(),
						MyTeamsSelect.class);
				// newsListIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(myTeamsSelectIntent, 0);
			}
		});

		back2Tab.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myTeamsTabIntent = new Intent(view.getContext(),
						MyTeamsTab.class);
				// newsListIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(myTeamsTabIntent, 0);
			}
		});
		// / Init User Team List
		this.teamsAdapter = new EditTeamAdapter(this, R.layout.my_teams_list,
				teamsList);
		setListAdapter(this.teamsAdapter);
		viewMyTeam = new Runnable() {
			public void run() {
				getMyteam();
			}
		};
		Thread thread = new Thread(null, viewMyTeam, "MagentoBackground");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(MyTeamsList.this,
				"Please wait...", "Retrieving data ...", true);
	}

	private class EditTeamAdapter extends ArrayAdapter<Team> {

		private ArrayList<Team> items;
		MyTeamsList myTeamList;

		public EditTeamAdapter(MyTeamsList context, int textViewResourceId,
				ArrayList<Team> items) {
			super(context, textViewResourceId, items);
			this.myTeamList = context;
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (chkList.containsKey(position))
				return chkList.get(position);

			View v = convertView;
			LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			Typeface face = Typeface.createFromAsset(getAssets(),
					"fonts/Arial.ttf");

			TextView headline;
			Team team = items.get(position);

			v = vi.inflate(R.layout.my_teams_element, null);
			ImageView imgView = (ImageView) v.findViewById(R.id.my_teams_logo);
			headline = (TextView) v.findViewById(R.id.my_teams_element_text);
			ImageButton deleteButton = (ImageButton) v
					.findViewById(R.id.my_teams_element_remove);

			DeleteOnClickListener listener = new DeleteOnClickListener(team
					.getId(), myTeamList, position);
			deleteButton.setOnClickListener(listener);
			// Log.e(MyTeamsList.class + "", "getView");
			headline.setTypeface(face);

			headline.setText(team.getName());

			ImageLoaderFactory.createImageLoader(MyTeamsList.this).setTask(
					team.getImageURL(), imgView);
			ImageLoaderFactory.createImageLoader(MyTeamsList.this).go();

			chkList.put(position, v);

			return v;
		}
	}

	private class DeleteOnClickListener implements OnClickListener {
		String id = "";
		MyTeamsList list;
		int position;

		public DeleteOnClickListener(String id, MyTeamsList list, int position) {
			this.id = id;
			this.list = list;
			this.position = position;
		}

		public void onClick(View v) {
			AlertDialog alertDialog = new AlertDialog.Builder(list).create();
			alertDialog.setTitle("Confirm");
			alertDialog.setMessage("Do you really want to delete this team?");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

					runOnUiThread(new Runnable() {
						public void run() {
							teamsList.remove(position);

							// int a = teamsList.size();
							// int b= teamsAdapter.items.size();

							teamsAdapter.notifyDataSetChanged();
						}
					});
					new Thread(new Runnable() {

						public void run() {
							WebServiceReaderMyTeam.removeUserTeam(
									WebServiceReaderMyTeam.getDeviceId(list),
									id);
						}
					}).start();

				}
			});
			alertDialog.setButton2("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});
			alertDialog.show();
		}

	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		System.out.println("Aaaaa");
		// Object o = teamsList.get(position);
		// if (o instanceof Article) {
		// MyTeamsNewsDetail.currentArticle = (Article) teamsList.get(position);
		// Intent newsDetailIndent = new Intent(v.getContext(),
		// MyTeamsNewsDetail.class);
		// startActivityForResult(newsDetailIndent, 0);
		// }
	}

	private void getMyteam() {
		try {
			ArrayList<Team> abs = WebServiceReaderMyTeam
					.getUserTeam(WebServiceReaderMyTeam.getDeviceId(this));
			for (Team a : abs) {
				teamsList.add(a);
			}

			// Log.i("ARRAY", "" + newsList.size());
		} catch (UnknownHostException ex) {
			Log.e("Dont have internet ", ex.getMessage());
			Utils.showAlert(this, "No Internet Connection.");
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(displayNews);
		try {
			ImageLoaderFactory.createImageLoader(this).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void publicUpdateMyteam(Team team) {
		final Team myTeam = team;
		Runnable updateNews = new Runnable() {
			public void run() {
				teamsList.add(myTeam);
				if (teamsList != null && teamsList.size() > 0) {
					teamsAdapter.notifyDataSetChanged();
				}
			}
		};
		runOnUiThread(updateNews);
	}

	Runnable updateNews = new Runnable() {
		public void run() {

			if (teamsList != null && teamsList.size() > 0) {
				teamsAdapter.notifyDataSetChanged();
			}
			m_ProgressDialog.dismiss();
		}
	};
}
