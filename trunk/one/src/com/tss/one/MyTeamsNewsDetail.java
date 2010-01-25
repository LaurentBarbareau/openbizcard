package com.tss.one;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.tssoft.one.webservice.OakHandler;
import com.tssoft.one.webservice.WebServiceReader;
import com.tssoft.one.webservice.model.Article;
import com.tssoft.one.webservice.model.cons.ArticleIndex;

public class MyTeamsNewsDetail extends MyActivity {
	public static Article currentArticle = new Article("151154", "", "", "", "");
	public static ProgressDialog dig;

	public void loadArticle(Article article) {
		if (article != null) {
			MyTeamsNewsDetail.dig = ProgressDialog.show(this, "Please wait...",
					"Retrieving data ...", true);
			final Article myArticle = article;
			final WebView webview = (WebView) findViewById(R.id.news_detail_webview);
			final Activity act = this;
			webview.loadData("Loading", "text/html", "utf-8");
			new Thread(new Runnable() {
				
				public void run() {
					Looper.prepare();
					currentArticle = WebServiceReader.getArticleByID(myArticle,
							ArticleIndex.MAIN);
					if (currentArticle.getPrevId() == null) {
						OakHandler hand = new OakHandler() {
							public void doJob() {
								act.runOnUiThread(new Runnable() {

									public void run() {
										ImageButton prevBtn = (ImageButton) findViewById(R.id.previous_button);
										prevBtn
												.setVisibility(ImageButton.INVISIBLE);
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
					}
					System.out.println("Num 3");

					// setContentView(webview);
					String summary = currentArticle.getBody();
					webview.loadDataWithBaseURL (null, summary, "text/html", "utf-8", 
					"about:blank"); 
					MyTeamsNewsDetail.dig.dismiss();
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
		super.buildMenu(this);
		
		loadArticle(currentArticle);
//		ImageButton icon0 = (ImageButton) findViewById(R.id.main_button);
//		ImageButton icon1 = (ImageButton) findViewById(R.id.my_teams_button);
//		ImageButton icon2 = (ImageButton) findViewById(R.id.news_button);
//		ImageButton icon3 = (ImageButton) findViewById(R.id.score_board_button);
//		ImageButton backicon = (ImageButton) findViewById(R.id.back_button);
//		backicon.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				finish();
//			}
//		});
//		icon0.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				// MainDetail.article = article;
//				Intent mainDetailIntent = new Intent(view.getContext(),
//						MainDetail.class);
////				mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				startActivityForResult(mainDetailIntent, 0);
//			}
//		});
//
//		icon2.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				Intent newsListIntent = new Intent(view.getContext(),
//						NewsList.class);
////				newsListIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				startActivityForResult(newsListIntent, 0);
//			}
//		});
		
		// / Icon Next and Previous
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
