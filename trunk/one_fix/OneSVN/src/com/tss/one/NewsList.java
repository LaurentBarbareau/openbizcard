package com.tss.one;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tssoft.one.utils.Utils;
import com.tssoft.one.webservice.ImageLoaderFactory;
import com.tssoft.one.webservice.WebServiceReader;
import com.tssoft.one.webservice.model.Article;
import com.tssoft.one.webservice.model.ArticleBySubject;

public class NewsList extends MyListActivity {
	public static NewsList instance;
	private HashMap<Integer, View> chkList = new HashMap<Integer, View>();
	private ArrayList<Object> newsList = null;
	private NewsAdapter newsAdapter;
	private Runnable viewNews;
	private ProgressBar progressBar;

	@Override 
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig); 
	}
	
	private Runnable displayNews = new Runnable() {
		public void run() {
			if (newsList != null && newsList.size() > 0) {
				newsAdapter.notifyDataSetChanged();
			}
			runOnUiThread(new Runnable(){
				public void run(){
					progressBar.setVisibility(8);
				}						
			});// jen added
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.news_list);
		
		instance = this;
		super.buildMenu(this, 3);

		// Set no line separator 
		getListView().setDivider( null ); 
		getListView().setDividerHeight(0); 
		
		newsList = new ArrayList<Object>();
		this.newsAdapter = new NewsAdapter(this, R.layout.news_list, newsList);
		setListAdapter(this.newsAdapter);

		viewNews = new Runnable() {
			public void run() {
				getNews();
			}
		};
		// Refresh icon
		ImageView refreshIcon = ((ImageView) findViewById(R.id.refrest_icon));
		final NewsList act = this;
		refreshIcon.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				Intent mainDetailIntent = new Intent(act, NewsList.class);
				// mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(mainDetailIntent);

			}
		});
		Thread thread = new Thread(null, viewNews, "MagentoBackground");
		thread.start();
		
		progressBar = (ProgressBar) findViewById(R.id.progressbar);// jen added
		
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
		Object o = newsList.get(position);
		if (o instanceof Article) {
			NewsDetail.currentArticle = (Article) newsList.get(position);
			Intent newsDetailIndent = new Intent(v.getContext(), NewsDetail.class);
			startActivityForResult(newsDetailIndent, 0);
		}
	}

	private void getNews() {
		try {
			ArrayList<ArticleBySubject> abs = WebServiceReader.getNews();
			for (ArticleBySubject a : abs) {
				newsList.add(a.subject);
				newsList.addAll(a.articles);
			}
			Log.i("ARRAY", "" + newsList.size());
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

	private class NewsAdapter extends ArrayAdapter<Object> {

		private ArrayList<Object> items;
		LayoutInflater vi;
		
		public NewsAdapter(Context context, int textViewResourceId,
				ArrayList<Object> items) {
			super(context, textViewResourceId, items);
			this.items = items;
			this.vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (chkList.containsKey(position))
				return chkList.get(position);

			View v = convertView;		
			Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Arial.ttf");

			TextView headline;
			TextView sc;
			Object i = items.get(position);

			if (i instanceof String) {
				v = vi.inflate(R.layout.red_list, null);
				
				String txt = Utils.reverseStringByPattern(Utils.NUMBER_PATTERN, (String)i);
				
				headline = (TextView) v.findViewById(R.id.subject_title);
				headline.setTypeface(face);
				headline.setText(txt);
			} else {
				Article article = (Article) i;
				if ((position % 2) == 1) {
					v = vi.inflate(R.layout.white_list, null);
					ImageView imgView = (ImageView) v.findViewById(R.id.small_main_image_w);

					headline = (TextView) v.findViewById(R.id.small_main_headline_w);
					sc = (TextView) v.findViewById(R.id.small_main_sc_w);

					headline.setTypeface(face, Typeface.BOLD);
					headline.setTextSize(headline.getTextSize() + 1);
					sc.setTypeface(face);

					String hdTxt = Utils.reverseStringByPattern(Utils.NUMBER_PATTERN, article.getTitle());
					String scTxt = Utils.reverseStringByPattern(Utils.NUMBER_PATTERN, article.getScTitle());
					
					headline.setText(hdTxt);
					sc.setText(scTxt);
					
					// if start with English unicode
					// toggle alignment
					String articleDesc = article.getScTitle();
					if(Utils.isStartWithEnglishUnicode(articleDesc)){
						sc.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
					}

					ImageLoaderFactory.createImageLoader(NewsList.this).setTask(article.getImageUrl(), imgView);
					ImageLoaderFactory.createImageLoader(NewsList.this).go();
				} else {
					v = vi.inflate(R.layout.gray_list, null);
					ImageView imgView = (ImageView) v.findViewById(R.id.small_main_image);

					headline = (TextView) v.findViewById(R.id.small_main_headline);
					sc = (TextView) v.findViewById(R.id.small_main_sc);

					headline.setTypeface(face, Typeface.BOLD);
					headline.setTextSize(headline.getTextSize() + 1);
					sc.setTypeface(face);

					String hdTxt = Utils.reverseStringByPattern(Utils.NUMBER_PATTERN, article.getTitle());
					String scTxt = Utils.reverseStringByPattern(Utils.NUMBER_PATTERN, article.getScTitle());
					
					headline.setText(hdTxt);
					sc.setText(scTxt);
					
					// if start with English unicode
					// toggle alignment
					String articleDesc = article.getScTitle();
					if(Utils.isStartWithEnglishUnicode(articleDesc)){
						sc.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
					}

					ImageLoaderFactory.createImageLoader(NewsList.this)
							.setTask(article.getImageUrl(), imgView);
					ImageLoaderFactory.createImageLoader(NewsList.this).go();
				}
			}
			chkList.put(position, v);

			return v;
		}
	}
}
