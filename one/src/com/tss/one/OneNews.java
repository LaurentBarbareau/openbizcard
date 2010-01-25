package com.tss.one;

import java.util.ArrayList;

import com.tssoft.one.webservice.WebServiceReader;
import com.tssoft.one.webservice.model.Article;
import com.tssoft.one.webservice.model.ArticleBySubject;
import com.tssoft.one.webservice.model.cons.ArticleIndex;

import android.app.Activity;
import android.os.Bundle;

public class OneNews extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		ArrayList<Article> arr = WebServiceReader.getMain();
//		System.out.println("Num " + arr.size());
//		ArrayList<ArticleBySubject> arr2 = WebServiceReader.getNews();
//		System.out.println("Num2 " + arr2.size());
//		Article article = arr.get(0);
//		Article newArticle = WebServiceReader.getArticleByID(article, ArticleIndex.MAIN);
//		System.out.println("Num 3");
	}
}