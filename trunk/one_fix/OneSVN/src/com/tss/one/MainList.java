package com.tss.one;

import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tss.one.debug.LogTool;
import com.tssoft.one.utils.Utils;
import com.tssoft.one.webservice.ImageLoaderStringFactory;
import com.tssoft.one.webservice.WebServiceReader;
import com.tssoft.one.webservice.WebServiceText;
import com.tssoft.one.webservice.model.Article;
import com.tssoft.one.webservice.model.Game;

public class MainList extends MyListActivity {
	public static OneApplication splashScreen;
	
	public static MainList instance;
	private HashMap<Integer, View> chkList = new HashMap<Integer, View>();
//	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<Object> mainArticleList = null;
	private MainAdapter mainAdapter;
	private Runnable viewMain;
	public static boolean firstTime = true;
	public static boolean isShowDetail = false;
	private ProgressBar progressBar;
	private boolean score = false;
	
	private Runnable displayChanged = new Runnable() {
		public void run() {
			try{
				MainList.splashScreen.finish();
				}catch(Exception e){
					e.printStackTrace();
				}
			if (mainArticleList != null && mainArticleList.size() > 0) {
				mainAdapter.notifyDataSetChanged();
			}
//			if (m_ProgressDialog != null) {
//				m_ProgressDialog.dismiss();
//			}
			
			runOnUiThread(new Runnable(){
					public void run(){
						progressBar.setVisibility(View.GONE);
					}						
				});// jen added	
			
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

	@Override 
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig); 
	}
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_list);
		instance = this;
		if (firstTime) {
			((RelativeLayout) findViewById(R.id.relative_layout)).setVisibility(RelativeLayout.INVISIBLE);
			((LinearLayout) findViewById(R.id.linear_layout)).setVisibility(RelativeLayout.INVISIBLE);
			((ImageView) findViewById(R.id.news_header)).setVisibility(ImageView.VISIBLE);
		}
		super.buildMenu(this, 1); 
		
		System.setErr(new PrintStream(new LogTool("System.err")));
		System.setOut(new PrintStream(new LogTool("System.out")));

		mainArticleList = new ArrayList<Object>();
		this.mainAdapter = new MainAdapter(this, R.layout.main_list, mainArticleList);
		setListAdapter(this.mainAdapter);
		
		// Set no line separator 
		getListView().setDivider( null ); 
		getListView().setDividerHeight(0); 
		
		// Refresh icon
		ImageView refreshIcon = ((ImageView) findViewById(R.id.refrest_icon));
		final MainList act = this;
		refreshIcon.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
				Intent mainDetailIntent = new Intent(act, MainList.class);
				// mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(mainDetailIntent);

			}
		});

		viewMain = new Runnable() {
			public void run() {
				getMain();
			}
		};
		Thread thread = new Thread(null, viewMain, "MagentoBackground");
		thread.start();

		progressBar = (ProgressBar) findViewById(R.id.progressbar);// jen added
		
		if(firstTime){
			runOnUiThread(new Runnable(){
				public void run(){
					progressBar.setVisibility(View.INVISIBLE);
				}						
			});// jen added		
		}else{
			runOnUiThread(new Runnable(){
				public void run(){
					progressBar.setVisibility(View.VISIBLE);
				}						
			});// jen added	
		}	
	}

	private void getMain() {
		try {
			mainArticleList.addAll(WebServiceReader.getMain());
			mainArticleList.addAll(WebServiceText.mainStr);
			Log.i("ARRAY", "" + mainArticleList.size());
		} catch (UnknownHostException e) {
			Utils.showAlertWithExitProgram(this, "No internet connection. Please try again.");
		}
		catch (Exception e) {
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

//	@Override
//	public void finish() {
//		super.finish();
//		ImageLoaderFactory.clear(this);
//	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Object o = mainArticleList.get(position);
		if (o instanceof Article) {
			isShowDetail = true;
			MainDetail.article = (Article) mainArticleList.get(position);
			Intent mainDetailIndent = new Intent(v.getContext(), MainDetail.class);
			startActivity(mainDetailIndent);
		}
	}

	String ARTICLE_KEY = "articlemain";
	String SCORE_KEY = "scoremain";

	private class MainAdapter extends ArrayAdapter<Object> {
		
		private HashMap<Integer,Integer> viewType = new HashMap<Integer,Integer>();
		private ArrayList<Object> items;
		private LayoutInflater vi; 
		Context context;
		Typeface face;
		private TextView headline;
		private TextView sc;
		private View whiteList;
		
		public MainAdapter(Context context, int textViewResourceId,
				ArrayList<Object> items) {
			super(context, textViewResourceId, items);
			this.context = context;
			this.items = items;
			this.vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.face = Typeface.createFromAsset(getAssets(),"fonts/Arial.ttf");
		}
	
//		@Override
//		public int getViewTypeCount() {
//			// TODO Auto-generated method stub
//			return 5;
//		}
//		
//		@Override
//		public int getItemViewType(int position) {
//			Object item = mainArticleList.get(position);
//			
//			if(item instanceof String){
//				return 4;
//			}
//			if(item instanceof Game){
//				return 3;
//			}
//			if(item instanceof Article){
//				Article a = (Article) item;
//				if(a.getIsHighlight().equals("true")){
//					return 0;
//				}else{
//					if(position%2==0){
//						return 1;
//					}else{
//						return 2;
//					}
//				}
//			}
//			return -1;
//		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (chkList.containsKey(position))
				return chkList.get(position);

			View v = convertView;		
			System.out.println("convert view is "+v);
			
					
			Object i = items.get(position);

			if (i instanceof String) {
				v = vi.inflate(R.layout.ads_list, null);
				headline = (TextView) v.findViewById(R.id.main_ads);
				headline.setTypeface(face, Typeface.BOLD);
				headline.setText((String) i);
			} 
			else if (i instanceof Game) {
				final Game game = (Game) i;		
				v = vi.inflate(R.layout.my_teams_score_element, null);
				viewType.put(position, 1);
				if(game.getHasEvent().equals("true")){
					(v.findViewById(R.id.arrow_detail)).setVisibility(ImageButton.VISIBLE);
					v.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							GameDetail.gameId = game.getId();
							GameDetail.screenId = 1;
							Intent mainDetailIntent = new Intent(context,GameDetail.class);
							context.startActivity(mainDetailIntent);
						}
					});
				}
				else {
					// game does not start
					if (game.getHomeScore().equalsIgnoreCase("") || game.getGuestScore().equalsIgnoreCase("")) {
						v.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Utils.displayNoGameDetailNow(instance);
							}
						});
					}else{
						v.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Utils.displayNoGameDetail(instance);
							}
						});
					}
				}
				
				// / All prop
				TextView minute = (TextView) v.findViewById(R.id.my_teams_minute);
				ImageView teamLogo1 = (ImageView) v.findViewById(R.id.my_teams_logo1);
				TextView nameHome = (TextView) v.findViewById(R.id.my_teams_name1);
				TextView score = (TextView) v.findViewById(R.id.my_teams_score);
				TextView nameGuest = (TextView) v.findViewById(R.id.my_teams_name2);
				ImageView teamLogo2 = (ImageView) v.findViewById(R.id.my_teams_logo2);

				// set Value
				minute.setTypeface(face);
				nameHome.setTypeface(face);
				nameGuest.setTypeface(face);
				score.setTypeface(face);
				if(Utils.isEndGame(game.getStartTime())){
					minute.setTextColor(Color.GREEN);
					minute.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
				}
				minute.setText(Utils.toEndedHebrew(instance, game.getStartTime()));
				
				nameHome.setText(game.getHomeTeam());
				score.setText(game.getGuestScore() + " - "
						+ game.getHomeScore());
				nameGuest.setText(game.getGuestTeam());
				
				//== add team icon
				String fileName = "";
				fileName = game.getHomeIcon();
				fileName = fileName.substring(fileName.lastIndexOf("/")+1, fileName.lastIndexOf("."));
				fileName = "icon_"+fileName;
//				teamLogo1.setImageResource(Utils.getResourceIdFromPath(MainList.this, fileName));
				int rId = Utils.getResourceIdFromPath(MainList.this, fileName);
				if(rId == -1){
					ImageLoaderStringFactory.createImageLoader(MainList.this,
					SCORE_KEY).setTask(game.getHomeIcon(), teamLogo1);
					ImageLoaderStringFactory.createImageLoader(MainList.this,
					SCORE_KEY).go();
				}else{
					teamLogo1.setImageResource(rId);
				}
				
				
				fileName = game.getGuestIcon();
				fileName = fileName.substring(fileName.lastIndexOf("/")+1, fileName.lastIndexOf("."));
				fileName = "icon_"+fileName;
//				teamLogo2.setImageResource(Utils.getResourceIdFromPath(MainList.this, fileName));
				int rId_ = Utils.getResourceIdFromPath(MainList.this, fileName);
				if(rId_ == -1){
					ImageLoaderStringFactory.createImageLoader(MainList.this,
					SCORE_KEY).setTask(game.getGuestIcon(), teamLogo2);
					ImageLoaderStringFactory.createImageLoader(MainList.this,
					SCORE_KEY).go();
				}else{
					teamLogo2.setImageResource(rId_);
				}
				
//				ImageLoaderStringFactory.createImageLoader(MainList.this,
//						SCORE_KEY).setTask(game.getHomeIcon(), teamLogo1);
//				ImageLoaderStringFactory.createImageLoader(MainList.this,
//						SCORE_KEY).setTask(game.getGuestIcon(), teamLogo2);
//				ImageLoaderStringFactory.createImageLoader(MainList.this,
//						SCORE_KEY).go();

			} else {
				Article article = (Article) i;
				if (article.getIsHighlight().equals("true")) {
					v = vi.inflate(R.layout.blue_list, null);
					ImageView imgView = (ImageView) v.findViewById(R.id.main_image);
					headline = (TextView) v.findViewById(R.id.main_headline);
					sc = (TextView) v.findViewById(R.id.main_sc);
					sc.setGravity(Gravity.CENTER_VERTICAL);
					headline.setTypeface(face, Typeface.BOLD);
					sc.setTypeface(face);

					headline.setText(article.getTitle());
					sc.setText(article.getScTitle());

					ImageLoaderStringFactory.createImageLoader(MainList.this,
							ARTICLE_KEY)
							.setTask(article.getImageUrl(), imgView);
					ImageLoaderStringFactory.createImageLoader(MainList.this,
							ARTICLE_KEY).go();

				} else {

					if ((position % 2) == 1) {			
						v = vi.inflate(R.layout.white_list, null);
						ImageView imgView = (ImageView) v.findViewById(R.id.small_main_image_w);
						
						headline = (TextView) v.findViewById(R.id.small_main_headline_w);
						sc = (TextView) v.findViewById(R.id.small_main_sc_w);
						
						headline.setTypeface(face, Typeface.BOLD);
						headline.setTextSize(headline.getTextSize()+1);
						sc.setTypeface(face);
	
						headline.setText(article.getTitle());
						sc.setText(article.getScTitle());
						
						// if start with english unicode
						// toggle alignment
						String articleDesc = article.getScTitle();
						if(Utils.isStartWithEnglishUnicode(articleDesc)){
							sc.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
						}
	
						ImageLoaderStringFactory.createImageLoader(MainList.this,
								ARTICLE_KEY)
								.setTask(article.getImageUrl(), imgView);
						ImageLoaderStringFactory.createImageLoader(MainList.this,
								ARTICLE_KEY).go();
					} else {
						v = vi.inflate(R.layout.gray_list, null);			
						ImageView imgView = (ImageView) v.findViewById(R.id.small_main_image);
	
						headline = (TextView) v.findViewById(R.id.small_main_headline);
						sc = (TextView) v.findViewById(R.id.small_main_sc);
						
						headline.setTypeface(face, Typeface.BOLD);
						headline.setTextSize(headline.getTextSize()+1);
						sc.setTypeface(face);
	
						headline.setText(article.getTitle());
						sc.setText(article.getScTitle());
						
						// if start with english unicode
						// toggle alignment
						String articleDesc = article.getScTitle();
						if(Utils.isStartWithEnglishUnicode(articleDesc)){
							sc.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
						}
	
						ImageLoaderStringFactory.createImageLoader(MainList.this,
								ARTICLE_KEY)
								.setTask(article.getImageUrl(), imgView);
						ImageLoaderStringFactory.createImageLoader(MainList.this,
								ARTICLE_KEY).go();
					}
				}
			}
			chkList.put(position, v);
			return v;
		}
	}
}
