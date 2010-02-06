package com.tssoft.one.webservice;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import android.util.Log;

import com.tssoft.one.webservice.model.Article;
import com.tssoft.one.webservice.model.ArticleBySubject;
import com.tssoft.one.webservice.model.Game;
import com.tssoft.one.webservice.model.GameBySubject;

public class WebServiceReader {
	public static final String ENDPOINT_URL = "http://one.mobile1.co.il/webservices/appservices.asmx";

	public static ArrayList<Object> getMain() throws UnknownHostException{
		WebServiceText.mainStr.clear();
		String TAG = "SOAPConnected";
		String SOAP_ACTION = "http://tempuri.org/GetFirstPage";
		String METHOD_NAME = "GetFirstPage";
		String NAMESPACE = "http://tempuri.org/";
		String URL = ENDPOINT_URL;
		SoapObject resultRequestSOAP = null;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		// SoapObject
		// request.addProperty("firstname", "John");
		// request.addProperty("lastname", "Williams");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		Log.v(TAG, "Request" + request.toString());
		AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
				URL);
		ArrayList<Object> articlesArr = new ArrayList<Object>();
		ArrayList<Object> highlightArr = new ArrayList<Object>();
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultRequestSOAP = (SoapObject) envelope.getResponse();
			try {
				SoapObject subjects = (SoapObject) resultRequestSOAP
						.getProperty("Subjects");

				subjects = (SoapObject) subjects.getProperty("GamesBySubject");
				SoapPrimitive subject = (SoapPrimitive) subjects
						.getProperty("Subject");
				SoapObject games = (SoapObject) subjects.getProperty("Games");
				ArrayList<Game> gamesArr = new ArrayList<Game>();
				for (int i = 0; i < games.getPropertyCount(); i++) {

					SoapObject article = (SoapObject) games.getProperty(i);
					Game game = WebServiceReaderScoreBoard
							.getGameFromSoapObj(article);
					gamesArr.add(game);
					articlesArr.add(game);
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			SoapObject articles = (SoapObject) resultRequestSOAP
					.getProperty("Articles");
			for (int i = 0; i < articles.getPropertyCount(); i++) {

				SoapObject article = (SoapObject) articles.getProperty(i);
				SoapPrimitive id = (SoapPrimitive) article.getProperty("ID");

				SoapPrimitive title = (SoapPrimitive) article
						.getProperty("Title");
				if (id.toString().equals("-1")) {
					WebServiceText.mainStr.add(title.toString());
					WebServiceText.firstArticleText = title.toString();
					continue;
				}
				SoapPrimitive scTitle = (SoapPrimitive) article
						.getProperty("sc_Title");
				SoapPrimitive imageUrl = (SoapPrimitive) article
						.getProperty("ImageURL");
				SoapPrimitive isHighlight = (SoapPrimitive) article
						.getProperty("IsHighlighted");
				Article ar = new Article(id != null ? id.toString() : null,
						title != null ? title.toString() : null,
						scTitle != null ? scTitle.toString() : null,
						imageUrl != null ? imageUrl.toString() : null,
						isHighlight != null ? isHighlight.toString() : null);
				if (isHighlight.toString().equals("true")) {
					highlightArr.add(ar);
				} else {
					articlesArr.add(ar);
				}

			}

			Log.v(TAG, "Response from servcer:" + resultRequestSOAP.toString());

		}  catch (UnknownHostException ae) {
			throw ae;
		}catch (Exception aE) {
			Log.d(TAG, "Error", aE);
			aE.printStackTrace();
		}
		for (Iterator iterator = highlightArr.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			articlesArr.add(0, object);
		}
		return articlesArr;
	}

	public static ArrayList<ArticleBySubject> getNews() {
		WebServiceText.newsStr.clear();
		String TAG = "SOAPConnected";
		String SOAP_ACTION = "http://tempuri.org/GetArticles";
		String METHOD_NAME = "GetArticles";
		String NAMESPACE = "http://tempuri.org/";
		String URL = ENDPOINT_URL;
		SoapObject resultRequestSOAP = null;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		// SoapObject
		// request.addProperty("firstname", "John");
		// request.addProperty("lastname", "Williams");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		Log.v(TAG, "Request" + request.toString());
		AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
				URL);
		ArrayList<ArticleBySubject> articlesSubjectArr = new ArrayList<ArticleBySubject>();
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultRequestSOAP = (SoapObject) envelope.getResponse();
			for (int i = 0; i < resultRequestSOAP.getPropertyCount(); i++) {

				SoapObject articleBySubject = (SoapObject) resultRequestSOAP
						.getProperty(i);
				SoapPrimitive subject = (SoapPrimitive) articleBySubject
						.getProperty("Subject");
				SoapObject articles = (SoapObject) articleBySubject
						.getProperty("Articles");
				ArrayList<Article> articlesArr = new ArrayList<Article>();
				for (int j = 0; j < articles.getPropertyCount(); j++) {

					SoapObject article = (SoapObject) articles.getProperty(j);
					SoapPrimitive id = (SoapPrimitive) article
							.getProperty("ID");
					SoapPrimitive title = (SoapPrimitive) article
							.getProperty("Title");
					if (id.toString().equals("-1")) {
						WebServiceText.firstArticleText = title.toString();
						WebServiceText.newsStr.add(title.toString());
						continue;
					}
					SoapPrimitive scTitle = (SoapPrimitive) article
							.getProperty("sc_Title");
					SoapPrimitive imageUrl = (SoapPrimitive) article
							.getProperty("ImageURL");
					SoapPrimitive isHighlight = (SoapPrimitive) article
							.getProperty("IsHighlighted");
					Article ar = new Article(id != null ? id.toString() : null,
							title != null ? title.toString() : null,
							scTitle != null ? scTitle.toString() : null,
							imageUrl != null ? imageUrl.toString() : null,
							isHighlight != null ? isHighlight.toString() : null);
					articlesArr.add(ar);
				}
				articlesSubjectArr.add(new ArticleBySubject(subject.toString(),
						articlesArr));
			}
			Log.v(TAG, "Response from servcer:" + resultRequestSOAP.toString());

		} catch (Exception aE) {
			Log.d(TAG, "Error", aE);
			aE.printStackTrace();
		}
		return articlesSubjectArr;
	}

	public static Article getArticleByID(Article article, String indexType) {
		String USER_ID = "Cellcity";

		String TAG = "SOAPConnected";
		String SOAP_ACTION = "http://tempuri.org/GetArticle";
		String METHOD_NAME = "GetArticle";
		String NAMESPACE = "http://tempuri.org/";
		String URL = ENDPOINT_URL;
		SoapObject resultRequestSOAP = null;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		// SoapObject
		PropertyInfo arinfo = new PropertyInfo();
		arinfo.setNamespace(NAMESPACE);
		arinfo.setName("i_ArticleID");
		arinfo.setValue(article.getId());

		PropertyInfo indexinfo = new PropertyInfo();
		indexinfo.setNamespace(NAMESPACE);
		indexinfo.setName("i_IndexType");
		indexinfo.setValue(indexType);

		PropertyInfo usrinfo = new PropertyInfo();
		usrinfo.setNamespace(NAMESPACE);
		usrinfo.setName("i_UserID");
		usrinfo.setValue(USER_ID);

		request.addProperty(arinfo);
		request.addProperty(indexinfo);
		request.addProperty(usrinfo);
		//		
		// SoapPrimitive arid = new
		// SoapPrimitive(NAMESPACE,"i_ArticleID",article.getId() );
		// SoapPrimitive indexTypeSoap = new
		// SoapPrimitive(NAMESPACE,"i_IndexType", indexType );
		// SoapPrimitive iUserID = new
		// SoapPrimitive(NAMESPACE,"i_UserID",USER_ID );
		// request.setProperty(0, arid);
		// request.setProperty(1, indexTypeSoap);
		// request.setProperty(2, iUserID);
		// request.addProperty("http://tempuri.org/i_ArticleID",
		// article.getId());
		// request.addProperty("http://tempuri.org/i_IndexType", indexType);
		// request.addProperty("http://tempuri.org/i_UserID", USER_ID);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		Log.v(TAG, "Request" + request.toString());
		AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
				URL);
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultRequestSOAP = (SoapObject) envelope.getResponse();
			SoapPrimitive body = (SoapPrimitive) resultRequestSOAP.getProperty("Body");
			SoapPrimitive nextId = (SoapPrimitive) resultRequestSOAP.getProperty("NextID");
			SoapPrimitive prevId = (SoapPrimitive) resultRequestSOAP.getProperty("PrevID");
			Log.v(TAG, "Response from servcer:" + resultRequestSOAP.toString());
			// assign value to old article
			article.setBody(body.toString());
			article.setPrevId(prevId == null ? null : prevId.toString());
			article.setNextId(nextId == null ? null : nextId.toString());
		} catch (Exception aE) {
			Log.d(TAG, "Error", aE);
			aE.printStackTrace();
		}
		return article;

	}

}
