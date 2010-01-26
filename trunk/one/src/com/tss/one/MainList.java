package com.tss.one;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tss.one.debug.LogTool;
import com.tssoft.one.webservice.ImageLoaderStringFactory;
import com.tssoft.one.webservice.WebServiceReader;
import com.tssoft.one.webservice.WebServiceText;
import com.tssoft.one.webservice.model.Article;
import com.tssoft.one.webservice.model.Game;

public class MainList extends MyListActivity {

	private HashMap<Integer, View> chkList = new HashMap<Integer, View>();
	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<Object> mainArticleList = null;
	private MainAdapter mainAdapter;
	private Runnable viewMain;
	public static boolean firstTime = true;
	private Runnable displayChanged = new Runnable() {
		public synchronized void run() {
			if (mainArticleList != null && mainArticleList.size() > 0) {
				mainAdapter.notifyDataSetChanged();
			}
			if (m_ProgressDialog != null) {
				m_ProgressDialog.dismiss();
			}
			if (firstTime) {
				((RelativeLayout) findViewById(R.id.relative_layout))
						.setVisibility(RelativeLayout.VISIBLE);
				((LinearLayout) findViewById(R.id.linear_layout))
						.setVisibility(RelativeLayout.VISIBLE);
				((ImageView) findViewById(R.id.news_header))
						.setVisibility(ImageView.INVISIBLE);
				firstTime = false;

			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_list);
		if (firstTime) {

			((RelativeLayout) findViewById(R.id.relative_layout))
					.setVisibility(RelativeLayout.INVISIBLE);
			((LinearLayout) findViewById(R.id.linear_layout))
					.setVisibility(RelativeLayout.INVISIBLE);
			((ImageView) findViewById(R.id.news_header))
					.setVisibility(ImageView.VISIBLE);
		}
		super.buildMenu(this);

		System.setErr(new PrintStream(new LogTool("System.err")));
		System.setOut(new PrintStream(new LogTool("System.out")));

		// ImageButton icon1 = (ImageButton) findViewById(R.id.my_teams_button);
		// ImageButton icon2 = (ImageButton) findViewById(R.id.news_button);
		// ImageButton icon3 = (ImageButton)
		// findViewById(R.id.score_board_button);

		// icon1.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View view) {
		// Intent myTeamsTabIntent = new Intent(view.getContext(),
		// MyTeamsTab.class);
		// // mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		// startActivityForResult(myTeamsTabIntent, 0);
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

		mainArticleList = new ArrayList<Object>();
		this.mainAdapter = new MainAdapter(this, R.layout.main_list,
				mainArticleList);
		setListAdapter(this.mainAdapter);

		viewMain = new Runnable() {
			public void run() {
				getMain();
			}
		};
		Thread thread = new Thread(null, viewMain, "MagentoBackground");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(MainList.this, "Please wait...",
				"Retrieving data ...", true);

	}

	private void getMain() {
		try {
			mainArticleList.addAll(WebServiceReader.getMain());
			mainArticleList.addAll(WebServiceText.mainStr);
			Log.i("ARRAY", "" + mainArticleList.size());
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(displayChanged);
		try {
			ImageLoaderStringFactory.createImageLoader(this, SCORE_KEY).start();
		} catch (IllegalThreadStateException e) {
			e.printStackTrace();
		}
		try {
			ImageLoaderStringFactory.createImageLoader(this, ARTICLE_KEY)
					.start();
		} catch (IllegalThreadStateException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
		// overridePendingTransition(0, 0);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		// overridePendingTransition(0, 0);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Object o = mainArticleList.get(position);
		if (o instanceof Article) {
			MainDetail.article = (Article) mainArticleList.get(position);
			Intent mainDetailIndent = new Intent(v.getContext(),
					MainDetail.class);
			startActivityForResult(mainDetailIndent, 0);
		}
	}

	String ARTICLE_KEY = "articlemain";
	String SCORE_KEY = "scoremain";

	private class MainAdapter extends ArrayAdapter<Object> {

		private ArrayList<Object> items;

		public MainAdapter(Context context, int textViewResourceId,
				ArrayList<Object> items) {
			super(context, textViewResourceId, items);
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
			TextView sc;
			Object i = items.get(position);

			if (i instanceof String) {
				v = vi.inflate(R.layout.ads_list, null);
				headline = (TextView) v.findViewById(R.id.main_ads);
				headline.setTypeface(face);
				headline.setText((String) i);

			} else if (i instanceof Game) {

				Game game = (Game) i;
				v = vi.inflate(R.layout.my_teams_score_element, null);
				// / All prop
				TextView minute = (TextView) v
						.findViewById(R.id.my_teams_minute);
				ImageView teamLogo1 = (ImageView) v
						.findViewById(R.id.my_teams_logo1);
				TextView name1 = (TextView) v.findViewById(R.id.my_teams_name1);
				TextView score = (TextView) v.findViewById(R.id.my_teams_score);
				TextView name2 = (TextView) v.findViewById(R.id.my_teams_name2);
				ImageView teamLogo2 = (ImageView) v
						.findViewById(R.id.my_teams_logo2);

				// set Value
				minute.setTypeface(face);
				name1.setTypeface(face);
				name2.setTypeface(face);
				score.setTypeface(face);

				minute.setText(game.getStartTime());
				name1.setText(game.getGuestTeam());
				score.setText(game.getGuestScore() + " - "
						+ game.getHomeScore());
				name2.setText(game.getHomeTeam());
				ImageLoaderStringFactory.createImageLoader(MainList.this,
						SCORE_KEY).setTask(game.getGuestIcon(), teamLogo1);
				ImageLoaderStringFactory.createImageLoader(MainList.this,
						SCORE_KEY).setTask(game.getHomeIcon(), teamLogo2);
				ImageLoaderStringFactory.createImageLoader(MainList.this,
						SCORE_KEY).go();

			} else {
				Article article = (Article) i;
				if (article.getIsHighlight().equals("true")) {

					v = vi.inflate(R.layout.blue_list, null);
					ImageView imgView = (ImageView) v
							.findViewById(R.id.main_image);
					headline = (TextView) v.findViewById(R.id.main_headline);
					sc = (TextView) v.findViewById(R.id.main_sc);
					headline.setTypeface(face);
					sc.setTypeface(face);

					headline.setText(article.getTitle());
					sc.setText(article.getScTitle());

					ImageLoaderStringFactory.createImageLoader(MainList.this,
							ARTICLE_KEY)
							.setTask(article.getImageUrl(), imgView);
					ImageLoaderStringFactory.createImageLoader(MainList.this,
							ARTICLE_KEY).go();

				} else {
					
					if((position % 2) ==0){
						v = vi.inflate(R.layout.white_list, null);
					}else{
						v = vi.inflate(R.layout.gray_list, null);
					}
					
					ImageView imgView = (ImageView) v
					.findViewById(R.id.small_main_image);
					
					headline = (TextView) v
							.findViewById(R.id.small_main_headline);
					sc = (TextView) v.findViewById(R.id.small_main_sc);
					headline.setTypeface(face);
					sc.setTypeface(face);

					headline.setText(article.getTitle());
					sc.setText(article.getScTitle());

					ImageLoaderStringFactory.createImageLoader(MainList.this,
							ARTICLE_KEY)
							.setTask(article.getImageUrl(), imgView);
					ImageLoaderStringFactory.createImageLoader(MainList.this,
							ARTICLE_KEY).go();
				}
			}
			chkList.put(position, v);
			return v;
		}
	}
}
