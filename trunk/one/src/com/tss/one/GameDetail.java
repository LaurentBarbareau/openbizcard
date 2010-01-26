package com.tss.one;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
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
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tssoft.one.webservice.ImageLoader;
import com.tssoft.one.webservice.ImageLoaderFactory;
import com.tssoft.one.webservice.WebServiceReader;
import com.tssoft.one.webservice.WebServiceReaderScoreBoard;
import com.tssoft.one.webservice.model.Article;
import com.tssoft.one.webservice.model.ArticleBySubject;
import com.tssoft.one.webservice.model.cons.ArticleIndex;

public class GameDetail extends MyListActivity {
	public static String gameId; 
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
		setContentView(R.layout.game_detail);
		WebServiceReaderScoreBoard.getGameByID(gameId);
		// Oak add
//		loadArticle(article);

	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
		Log.i("Oak", "Start Activity");
//		Log.i("Oak", "article " + article);
//		loadArticle(article);
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
