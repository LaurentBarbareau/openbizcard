package com.tss.one;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.tssoft.one.webservice.WebServiceReader;
import com.tssoft.one.webservice.model.Article;
import com.tssoft.one.webservice.model.cons.ArticleIndex;

public class MainDetail extends MyActivity {
	public static Article article = new Article("151154", "", "", "", "");
	public static ProgressDialog dig;
	public void loadArticle(Article article) {
		if (article != null) {
			MainDetail.dig = ProgressDialog.show(this,    
		              "Please wait...", "Retrieving data ...", true);
			final Article myArticle = article;
			final WebView webview = (WebView) findViewById(R.id.main_detail_webview);
			webview.loadData("Loading", "text/html", "utf-8");	
			new Thread(new Runnable(){

				public void run() {
					Article newArticle = WebServiceReader.getArticleByID(myArticle,
							ArticleIndex.MAIN);
					
					System.out.println("Num 3");
					
					// setContentView(webview);
					String summary = newArticle.getBody();
					webview.loadDataWithBaseURL (null, summary, "text/html", "utf-8", 
					"about:blank"); 
	
					MainDetail.dig.dismiss();
				}}).start();
			
		} else {
			WebView webview = (WebView) findViewById(R.id.main_detail_webview);
			// setContentView(webview);
			String summary = "You forget to set article first";
			webview.loadDataWithBaseURL (null, summary, "text/html", "utf-8", 
			"about:blank"); 

		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_detail);
		super.buildMenu(this);
		
		// Oak add
		loadArticle(article);
//		ImageButton refreshIcon = (ImageButton) findViewById(R.id.refrest_icon);
//		refreshIcon.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				loadArticle(article);
//			}
//		});
//		ImageButton zoominIcon = (ImageButton) findViewById(R.id.zoom_in_button);
//		ImageButton zoomoutIcon = (ImageButton) findViewById(R.id.zoom_out_button);
//		zoominIcon.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				WebView webview = (WebView) findViewById(R.id.main_detail_webview);
//				webview.zoomIn();
//			
//			}
//		});
//		zoomoutIcon.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				WebView webview = (WebView) findViewById(R.id.main_detail_webview);
//				webview.zoomOut();
//			}
//		});
//		//

//		ImageButton icon0 = (ImageButton) findViewById(R.id.main_button);
//		ImageButton icon1 = (ImageButton) findViewById(R.id.my_teams_button);
//		ImageButton icon2 = (ImageButton) findViewById(R.id.news_button);
//		ImageButton icon3 = (ImageButton) findViewById(R.id.score_board_button);
//		// Oak Add This part
//		// loadArticle(article);
//		icon2.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				Intent newsListIntent = new Intent(view.getContext(),
//						NewsList.class);
////				newsListIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				startActivityForResult(newsListIntent, 0);
//			}
//		});

	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
		Log.i("Oak", "Start Activity");
		Log.i("Oak", "article " + article);
		loadArticle(article);
		// loadArticle(article);
//		overridePendingTransition(0, 0);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
//		overridePendingTransition(0, 0);
	}
}
