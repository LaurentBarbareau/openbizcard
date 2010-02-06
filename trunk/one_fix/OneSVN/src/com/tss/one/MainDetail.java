package com.tss.one;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.tssoft.one.webservice.WebServiceReader;
import com.tssoft.one.webservice.model.Article;
import com.tssoft.one.webservice.model.cons.ArticleIndex;

public class MainDetail extends MyActivity {
	public static MainDetail instance;
	public static Article article = new Article("151154", "", "", "", "");
	//public static ProgressDialog dig;
	private ProgressBar progressBar;
	
	@Override 
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig); 
	}
	
	public void loadArticle(Article article) {
		if (article != null) {
						
			final Article myArticle = article;
			final WebView webview = (WebView) findViewById(R.id.main_detail_webview);
			webview.loadData("", "text/html", "utf-8");	
			new Thread(new Runnable(){
				public void run() {
					Article newArticle = WebServiceReader.getArticleByID(myArticle,
							ArticleIndex.MAIN);
					
					System.out.println("Num 3");
					
					// setContentView(webview);
					String summary = newArticle.getBody();
					webview.loadDataWithBaseURL (null, summary, "text/html", "utf-8", 
					"about:blank"); 
					
					runOnUiThread(new Runnable(){
						public void run(){
							progressBar.setVisibility(View.GONE);
						}						
					});// jen added
					
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
		
		instance = this;
		
		super.buildMenu(this, 1);
		
		progressBar = (ProgressBar) findViewById(R.id.progressbar);// jen added
		
		// Oak add
		loadArticle(article);
		//Jen add
		ImageButton backBtn = (ImageButton) findViewById(R.id.main_detail_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {				
//				Intent mainListIntent = new Intent(view.getContext(),
//						MainList.class);
//				// mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				startActivity(mainListIntent);
				instance.finish();
			}
		});
		
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
