package com.tss.one;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.tssoft.one.webservice.OakHandler;
import com.tssoft.one.webservice.WebServiceReader;
import com.tssoft.one.webservice.model.Article;
import com.tssoft.one.webservice.model.cons.ArticleIndex;

public class MyTeamsNewsDetail extends MyActivity {
	public static MyTeamsNewsDetail instance;
	public static Article currentArticle = new Article("151154", "", "", "", "");
	public static ArrayList<Object> newsList;
	public static int position;
	private ProgressBar progressBar;
	
	public Article setNextPreviousArticle() {
		System.out.println("======================>>  " + position);
		// next article
		try {
			Article nextArt = (Article) newsList.get(position + 1);
			currentArticle.setNextId(nextArt.getId());
		} 
		catch(ClassCastException e){
			if(position-2 < newsList.size()){
				System.out.println("xxxxxx " + position);
				Article nextArt = (Article) newsList.get(position + 1 + 1);
				currentArticle.setNextId(nextArt.getId());
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("==========================>>>>>  Exception next id " + e.getMessage());
			currentArticle.setNextId(null);
		}
		System.out.println(" ====================== ||||||||||||   " + currentArticle.getNextId());
		
		
		// last
		try {
			Article lastArt = (Article) newsList.get(position - 1);
			currentArticle.setPrevId(lastArt.getId());
		} 
		catch(ClassCastException e){
			if(position > 1){
				System.out.println("xxxxxx " + position);
				Article lastArt = (Article) newsList.get(position - 1 - 1);
				currentArticle.setPrevId(lastArt.getId());
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("==========================>>>>> Exception last id " + e.getMessage());
			currentArticle.setPrevId(null);
		}
		System.out.println(" ====================== ||||||||||||   " + currentArticle.getPrevId());
		
		return currentArticle;
	}

	@Override 
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig); 
	}
	
	public void loadArticle(Article article) {
		if (article != null) {
			
			progressBar = (ProgressBar) findViewById(R.id.progressbar);// jen added
			
			final Article myArticle = article;
			final WebView webview = (WebView) findViewById(R.id.news_detail_webview);
			final Activity act = this;
			webview.loadData("", "text/html", "utf-8");
			new Thread(new Runnable() {
				
				public void run() {
					Looper.prepare();
					currentArticle = WebServiceReader.getArticleByID(myArticle,ArticleIndex.MAIN);
					setNextPreviousArticle();
					
					System.out.println("currentArticle.getPrevId() ==================== " + currentArticle.getPrevId() + " " + currentArticle.getNextId());
					
					/**
					 * previous button
					 */
					if (currentArticle.getPrevId() == null) {
						OakHandler hand = new OakHandler() {
							public void doJob() {
								act.runOnUiThread(new Runnable() {

									public void run() {
										ImageButton prevBtn = (ImageButton) findViewById(R.id.previous_button);
										prevBtn.setVisibility(ImageButton.INVISIBLE);
									}
								});
							}
						};
						hand.doJob();
					} else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								ImageButton prevBtn = (ImageButton) findViewById(R.id.previous_button);
								prevBtn.setVisibility(ImageButton.VISIBLE);
							}
						});
					}
					
					/**
					 * next button
					 */
					if (currentArticle.getNextId() == null) {
						OakHandler hand = new OakHandler() {
							public void doJob() {
								act.runOnUiThread(new Runnable() {

									public void run() {
										ImageButton nextBtn = (ImageButton) findViewById(R.id.next_button);
										nextBtn.setVisibility(ImageButton.INVISIBLE);
									}
								});
							}
						};
						hand.doJob();
					}else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								ImageButton nextBtn = (ImageButton) findViewById(R.id.next_button);
								nextBtn.setVisibility(ImageButton.VISIBLE);
							}
						});
					}
					System.out.println("Num 3");

					String summary = currentArticle.getBody();
					webview.loadDataWithBaseURL (null, summary, "text/html", "utf-8", 
					"about:blank"); 
					
					runOnUiThread(new Runnable(){
						public void run(){
							progressBar.setVisibility(8);
						}						
					});// jen added
					Looper.loop();
				}
			}).start();

		} else {
			WebView webview = (WebView) findViewById(R.id.news_detail_webview);
			// setContentView(webview);
			String summary = "You forget to set article first";
			webview.loadData(summary, "text/html", "utf-8");
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// currentArticle = new Article("151154", "", "", "", "");
		// currentArticle.setNextId("151146");
		// currentArticle.setPrevId("151166");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.my_teams_news_detail);
		
		instance = this;
		
		super.buildMenu(this, 2);
		
		loadArticle(currentArticle);
		
		// / Icon Next and Previous
		ImageButton nextBtn = (ImageButton) findViewById(R.id.next_button);
		ImageButton prevBtn = (ImageButton) findViewById(R.id.previous_button);
		nextBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// currentArticle.setNextId("151146");
				// currentArticle.setPrevId("151166");
				if (currentArticle.getNextId() != null) {
					currentArticle = new Article(currentArticle.getNextId(),"", "", "", "");
					position++;
					loadArticle(currentArticle);
				}

			}
		});
		prevBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (currentArticle.getPrevId() != null) {
					currentArticle = new Article(currentArticle.getPrevId(), "", "", "", "");
					position--;
					loadArticle(currentArticle);
				}
			}
		});
		
		//Jen add
		ImageButton backBtn = (ImageButton) findViewById(R.id.news_detail_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {				
//				Intent myteamListIntent = new Intent(view.getContext(),
//						MyTeamsTab.class);
//				// mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				startActivity(myteamListIntent);
				
				instance.finish();
			}
		});
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
//		overridePendingTransition(0, 0);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
//		overridePendingTransition(0, 0);
	}
}
