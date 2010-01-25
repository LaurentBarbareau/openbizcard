package com.tssoft.one.webservice;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import android.provider.LiveFolders;
import android.util.Log;

import com.tssoft.one.webservice.model.Article;
import com.tssoft.one.webservice.model.ArticleBySubject;
import com.tssoft.one.webservice.model.Game;
import com.tssoft.one.webservice.model.GameBySubject;
import com.tssoft.one.webservice.model.LiveSubject;

public class WebServiceReaderScoreBoard {
	// //////////////////////// Game
	// get user game
	public static ArrayList<GameBySubject> getUserGames(String userId)
			throws UnknownHostException {
		WebServiceText.newsStr.clear();
		String TAG = "SOAPConnected";
		String SOAP_ACTION = "http://tempuri.org/GetUserGames";
		String METHOD_NAME = "GetUserGames";
		String NAMESPACE = "http://tempuri.org/";
		String URL = WebServiceReader.ENDPOINT_URL;
		SoapObject resultRequestSOAP = null;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		// SoapObject
		PropertyInfo userid = new PropertyInfo();
		userid.setNamespace(NAMESPACE);
		userid.setName("i_UserID");
		userid.setValue(userId);

		request.addProperty(userid);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		Log.v(TAG, "Request" + request.toString());
		AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
				URL);
		ArrayList<GameBySubject> gameSubjectArr = new ArrayList<GameBySubject>();
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultRequestSOAP = (SoapObject) envelope.getResponse();
			String hasteam = ((SoapPrimitive) resultRequestSOAP
					.getProperty("UserHasTeams")).toString();
			if (hasteam.equals("false")) {
				return gameSubjectArr;
			}
			resultRequestSOAP = (SoapObject) resultRequestSOAP
					.getProperty("Games");
			for (int i = 0; i < resultRequestSOAP.getPropertyCount(); i++) {

				SoapObject articleBySubject = (SoapObject) resultRequestSOAP
						.getProperty(i);
				SoapPrimitive subject = (SoapPrimitive) articleBySubject
						.getProperty("Subject");
				SoapObject articles = (SoapObject) articleBySubject
						.getProperty("Games");
				ArrayList<Game> articlesArr = new ArrayList<Game>();
				for (int j = 0; j < articles.getPropertyCount(); j++) {

					SoapObject article = (SoapObject) articles.getProperty(j);
					Game game = getGameFromSoapObj(article);
					articlesArr.add(game);
				}
				gameSubjectArr.add(new GameBySubject(subject.toString(),
						articlesArr));
			}
			Log.v(TAG, "Response from servcer:" + resultRequestSOAP.toString());

		} catch (UnknownHostException aE2) {
			Log.d(TAG, "Error", aE2);
			aE2.printStackTrace();
			throw aE2;
		} catch (Exception aE) {
			Log.d(TAG, "Error", aE);
			aE.printStackTrace();
		}
		return gameSubjectArr;
	}

	// current date
	public static String getCurrentDate() {
		String TAG = "SOAPConnected";
		String SOAP_ACTION = "http://tempuri.org/CurrentDate";
		String METHOD_NAME = "CurrentDate";
		String NAMESPACE = "http://tempuri.org/";
		String URL = WebServiceReader.ENDPOINT_URL;
		SoapPrimitive resultRequestSOAP = null;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		Log.v(TAG, "Request" + request.toString());
		AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
				URL);
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultRequestSOAP = (SoapPrimitive) envelope.getResponse();
		} catch (Exception aE) {
			Log.d(TAG, "Error", aE);
			aE.printStackTrace();
		}
		return resultRequestSOAP.toString();
	}

	// get game by day
	// Day can be -3 to 3
	public static ArrayList<GameBySubject> getGamesByDay(String dayOffset) {
		WebServiceText.newsStr.clear();
		String TAG = "SOAPConnected";
		String SOAP_ACTION = "http://tempuri.org/GetGamesByDay";
		String METHOD_NAME = "GetGamesByDay";
		String NAMESPACE = "http://tempuri.org/";
		String URL = WebServiceReader.ENDPOINT_URL;
		SoapObject resultRequestSOAP = null;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		// SoapObject
		PropertyInfo userid = new PropertyInfo();
		userid.setNamespace(NAMESPACE);
		userid.setName("i_Offset");
		userid.setValue(dayOffset);

		request.addProperty(userid);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		Log.v(TAG, "Request" + request.toString());
		AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
				URL);
		ArrayList<GameBySubject> gameSubjectArr = new ArrayList<GameBySubject>();
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultRequestSOAP = (SoapObject) envelope.getResponse();
			for (int i = 0; i < resultRequestSOAP.getPropertyCount(); i++) {
				SoapObject articleBySubject = (SoapObject) resultRequestSOAP
						.getProperty(i);
				SoapPrimitive subject = (SoapPrimitive) articleBySubject
						.getProperty("Subject");
				SoapObject articles = (SoapObject) articleBySubject
						.getProperty("Games");
				ArrayList<Game> articlesArr = new ArrayList<Game>();
				for (int j = 0; j < articles.getPropertyCount(); j++) {

					SoapObject article = (SoapObject) articles.getProperty(j);
					Game game = getGameFromSoapObj(article);
					articlesArr.add(game);
				}
				gameSubjectArr.add(new GameBySubject(subject.toString(),
						articlesArr));
			}
			Log.v(TAG, "Response from servcer:" + resultRequestSOAP.toString());

		} catch (Exception aE) {
			Log.d(TAG, "Error", aE);
			aE.printStackTrace();
		}
		return gameSubjectArr;
	}

	// get live games
	public static ArrayList<GameBySubject> getLiveGames() {
		WebServiceText.newsStr.clear();
		String TAG = "SOAPConnected";
		String SOAP_ACTION = "http://tempuri.org/GetLiveGames";
		String METHOD_NAME = "GetLiveGames";
		String NAMESPACE = "http://tempuri.org/";
		String URL = WebServiceReader.ENDPOINT_URL;
		SoapObject resultRequestSOAP = null;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		Log.v(TAG, "Request" + request.toString());
		AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
				URL);
		ArrayList<GameBySubject> gameSubjectArr = new ArrayList<GameBySubject>();
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultRequestSOAP = (SoapObject) envelope.getResponse();
			for (int i = 0; i < resultRequestSOAP.getPropertyCount(); i++) {
				SoapObject articleBySubject = (SoapObject) resultRequestSOAP
						.getProperty(i);
				SoapPrimitive subject = (SoapPrimitive) articleBySubject
						.getProperty("Subject");
				SoapObject articles = (SoapObject) articleBySubject
						.getProperty("Games");
				ArrayList<Game> articlesArr = new ArrayList<Game>();
				for (int j = 0; j < articles.getPropertyCount(); j++) {

					SoapObject article = (SoapObject) articles.getProperty(j);
					Game game = getGameFromSoapObj(article);
					articlesArr.add(game);
				}
				gameSubjectArr.add(new GameBySubject(subject.toString(),
						articlesArr));
			}
			Log.v(TAG, "Response from servcer:" + resultRequestSOAP.toString());

		} catch (Exception aE) {
			Log.d(TAG, "Error", aE);
			aE.printStackTrace();
		}
		return gameSubjectArr;
	}

	// get current subject
	public static ArrayList<LiveSubject> getCurrentSubjects() {
		WebServiceText.mainStr.clear();
		String TAG = "SOAPConnected";
		String SOAP_ACTION = "http://tempuri.org/GetCurrentSubjects";
		String METHOD_NAME = "GetCurrentSubjects";
		String NAMESPACE = "http://tempuri.org/";
		String URL = WebServiceReader.ENDPOINT_URL;
		SoapObject resultRequestSOAP = null;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		Log.v(TAG, "Request" + request.toString());
		AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
				URL);
		ArrayList<LiveSubject> articlesArr = new ArrayList<LiveSubject>();
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultRequestSOAP = (SoapObject) envelope.getResponse();
			for (int i = 0; i < resultRequestSOAP.getPropertyCount(); i++) {

				SoapObject article = (SoapObject) resultRequestSOAP
						.getProperty(i);
				SoapPrimitive name = (SoapPrimitive) article
						.getProperty("Name");

				SoapPrimitive id = (SoapPrimitive) article.getProperty("ID");
				LiveSubject ar = new LiveSubject(name != null ? name.toString()
						: null, id != null ? id.toString() : null);
				articlesArr.add(ar);
			}
			Log.v(TAG, "Response from servcer:" + resultRequestSOAP.toString());

		} catch (Exception aE) {
			Log.d(TAG, "Error", aE);
			aE.printStackTrace();
		}
		return articlesArr;
	}

	// get game by subject
	public static GameBySubject getGamesBySubject(String subjectId) {

		String TAG = "SOAPConnected";
		String SOAP_ACTION = "http://tempuri.org/GetGamesBySubject";
		String METHOD_NAME = "GetGamesBySubject";
		String NAMESPACE = "http://tempuri.org/";
		String URL = WebServiceReader.ENDPOINT_URL;
		SoapObject resultRequestSOAP = null;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		// SoapObject
		PropertyInfo arinfo = new PropertyInfo();
		arinfo.setNamespace(NAMESPACE);
		arinfo.setName("i_SubjectID");
		arinfo.setValue(subjectId);

		request.addProperty(arinfo);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		Log.v(TAG, "Request" + request.toString());
		AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
				URL);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultRequestSOAP = (SoapObject) envelope.getResponse();
			resultRequestSOAP = (SoapObject) resultRequestSOAP
					.getProperty("GamesBySubject");
			SoapPrimitive subject = (SoapPrimitive) resultRequestSOAP
					.getProperty("Subject");
			SoapObject games = (SoapObject) resultRequestSOAP
					.getProperty("Games");
			ArrayList<Game> gamesArr = new ArrayList<Game>();
			for (int i = 0; i < games.getPropertyCount(); i++) {

				SoapObject article = (SoapObject) games.getProperty(i);
				Game game = getGameFromSoapObj(article);
				gamesArr.add(game);
			}
			GameBySubject gameBySubject = new GameBySubject(subject.toString(),
					gamesArr);
			return gameBySubject;
		} catch (Exception aE) {
			Log.d(TAG, "Error", aE);
			aE.printStackTrace();
		}
		return null;

	}

	public static Game getGameFromSoapObj(SoapObject article) {
		SoapPrimitive id = (SoapPrimitive) article.getProperty("LiveID");
		SoapPrimitive gameMinute = (SoapPrimitive) article
				.getProperty("GameMinute");
		SoapPrimitive condition = (SoapPrimitive) article
				.getProperty("Condition");
		SoapPrimitive periodType = (SoapPrimitive) article
				.getProperty("PeriodType");
		SoapPrimitive gameType = (SoapPrimitive) article
				.getProperty("GameType");
		SoapPrimitive homeScore = (SoapPrimitive) article
				.getProperty("HomeScore");
		SoapPrimitive guestScore = (SoapPrimitive) article
				.getProperty("GuestScore");
		SoapPrimitive homeHalfScore = (SoapPrimitive) article
				.getProperty("HomeHalfScore");
		SoapPrimitive guestHalfScore = (SoapPrimitive) article
				.getProperty("GuestHalfScore");
		SoapPrimitive penaltyHomeScore = (SoapPrimitive) article
				.getProperty("PenaltyHomeScore");
		SoapPrimitive penaltyGuestScore = (SoapPrimitive) article
				.getProperty("PenaltyGuestScore");
		SoapPrimitive homeIcon = (SoapPrimitive) article
				.getProperty("HomeIcon");
		SoapPrimitive guestIcon = (SoapPrimitive) article
				.getProperty("GuestIcon");
		SoapPrimitive startTime = null;
		if (article.getProperty("StartTime") instanceof SoapPrimitive) {
			startTime = (SoapPrimitive) article.getProperty("StartTime");
		}
		SoapPrimitive homeTeam = (SoapPrimitive) article
				.getProperty("HomeTeam");
		SoapPrimitive guestTeam = (SoapPrimitive) article
				.getProperty("GuestTeam");
		SoapPrimitive gameDate = (SoapPrimitive) article
				.getProperty("GameDate");
		SoapPrimitive hasEvents = (SoapPrimitive) article
				.getProperty("HasEvents");
		Game game = new Game(
				id.toString() == null ? null : id.toString(),
				gameMinute == null ? null : gameMinute.toString(),
				condition == null ? null : condition.toString(),
				periodType == null ? null : periodType.toString(),
				gameType == null ? null : gameType.toString(),
				homeScore == null ? null : homeScore.toString(),
				guestScore == null ? null : guestScore.toString(),
				homeHalfScore == null ? null : homeHalfScore.toString(),
				guestHalfScore == null ? null : guestHalfScore.toString(),
				penaltyHomeScore == null ? null : penaltyHomeScore.toString(),
				penaltyGuestScore == null ? null : penaltyGuestScore.toString(),
				homeIcon == null ? null : homeIcon.toString(),
				guestIcon == null ? null : guestIcon.toString(),
				startTime == null ? null : startTime.toString(),
				homeTeam == null ? null : homeTeam.toString(),
				guestTeam == null ? null : guestTeam.toString(),
				gameDate == null ? null : gameDate.toString(),
				hasEvents == null ? null : hasEvents.toString());
		return game;

	}

}
