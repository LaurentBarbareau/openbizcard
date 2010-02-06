package com.tss.one;

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

public class NewsDetail extends MyActivity {
	public static NewsDetail instance;
	
	public static Article currentArticle = new Article("151154", "", "", "", "");
	//public static ProgressDialog dig;
	private ProgressBar progressBar;

	@Override 
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig); 
	}
	
	public void loadArticle(Article article) {
		if (article != null) {
//			NewsDetail.dig = ProgressDialog.show(this, "Please wait...",
//					"Retrieving data ...", true);
			final Article myArticle = article;
			final WebView webview = (WebView) findViewById(R.id.news_detail_webview);
			final Activity act = this;
			webview.loadData("", "text/html", "utf-8");
			new Thread(new Runnable() {
				
				public void run() {
					Looper.prepare();
					currentArticle = WebServiceReader.getArticleByID(myArticle,ArticleIndex.MAIN);
					System.out.println("currentArticle.getPrevId() ==================== " + currentArticle.getPrevId() + " " + currentArticle.getNextId());
					
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
						
					}else{
						OakHandler hand = new OakHandler() {
							public void doJob() {
								act.runOnUiThread(new Runnable() {

									public void run() {
										ImageButton prevBtn = (ImageButton) findViewById(R.id.previous_button);
										prevBtn
												.setVisibility(ImageButton.VISIBLE);
									}
								});
							}
						};
						hand.doJob();
					}
					if (currentArticle.getNextId() == null) {

						OakHandler hand = new OakHandler() {
							public void doJob() {
								act.runOnUiThread(new Runnable() {

									public void run() {
										ImageButton nextBtn = (ImageButton) findViewById(R.id.next_button);
										nextBtn
												.setVisibility(ImageButton.INVISIBLE);
									}
								});
							}
						};
						hand.doJob();
					}else{


						OakHandler hand = new OakHandler() {
							public void doJob() {
								act.runOnUiThread(new Runnable() {

									public void run() {
										ImageButton nextBtn = (ImageButton) findViewById(R.id.next_button);
										nextBtn
												.setVisibility(ImageButton.VISIBLE);
									}
								});
							}
						};
						hand.doJob();
					
					}
					System.out.println("Num 3");

					// setContentView(webview);
					String summary = currentArticle.getBody();
					webview.loadDataWithBaseURL (null, summary, "text/html", "utf-8", 
					"about:blank"); 
//					NewsDetail.dig.dismiss();
					
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
		instance = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.news_detail);
		super.buildMenu(this, 3);
		
		progressBar = (ProgressBar) findViewById(R.id.progressbar);// jen added
		
		loadArticle(currentArticle);

		// Icon Next and Previous
		ImageButton nextBtn = (ImageButton) findViewById(R.id.next_button);
		ImageButton prevBtn = (ImageButton) findViewById(R.id.previous_button);
		nextBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// currentArticle.setNextId("151146");
				// currentArticle.setPrevId("151166");
				if (currentArticle.getNextId() != null) {
					currentArticle = new Article(currentArticle.getNextId(),
							"", "", "", "");
					loadArticle(currentArticle);
				}

			}
		});
		prevBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// currentArticle.setNextId("151146");
				// currentArticle.setPrevId("151166");
				if (currentArticle.getPrevId() != null) {
					currentArticle = new Article(currentArticle.getPrevId(),
							"", "", "", "");
					loadArticle(currentArticle);
				}
			}
		});
		
		//Jen add
		ImageButton backBtn = (ImageButton) findViewById(R.id.news_detail_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {				
//				Intent newsListIntent = new Intent(view.getContext(),
//						NewsList.class);
//				// mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				startActivity(newsListIntent);

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
