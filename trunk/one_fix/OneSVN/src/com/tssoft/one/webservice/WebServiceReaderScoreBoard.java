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
import com.tssoft.one.webservice.model.GameEvent;
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
		SoapPrimitive id = null;
		SoapPrimitive gameMinute = null;
		SoapPrimitive condition = null;
		SoapPrimitive periodType = null;
		SoapPrimitive gameType = null;
		SoapPrimitive homeScore = null;
		SoapPrimitive guestScore = null;
		SoapPrimitive homeHalfScore = null;
		SoapPrimitive guestHalfScore = null;
		SoapPrimitive penaltyHomeScore = null;
		SoapPrimitive penaltyGuestScore = null;
		SoapPrimitive homeIcon = null;
		SoapPrimitive guestIcon = null;
		SoapPrimitive homeTeam = null;
		SoapPrimitive guestTeam = null;
		SoapPrimitive gameDate = null;
		SoapPrimitive hasEvents = null;
		try {
			id = (SoapPrimitive) article.getProperty("LiveID");
		} catch (Exception e) {

		}

		try {
			gameMinute = (SoapPrimitive) article.getProperty("GameMinute");
		} catch (Exception e) {

		}
		try {
			condition = (SoapPrimitive) article.getProperty("Condition");
		} catch (Exception e) {

		}
		try {
			periodType = (SoapPrimitive) article.getProperty("PeriodType");
		} catch (Exception e) {

		}
		try {
			gameType = (SoapPrimitive) article.getProperty("GameType");
		} catch (Exception e) {

		}
		try {
			homeScore = (SoapPrimitive) article.getProperty("HomeScore");
		} catch (Exception e) {

		}
		try {
			guestScore = (SoapPrimitive) article.getProperty("GuestScore");
		} catch (Exception e) {

		}
		try {
			homeHalfScore = (SoapPrimitive) article
					.getProperty("HomeHalfScore");
		} catch (Exception e) {

		}
		try {
			guestHalfScore = (SoapPrimitive) article
					.getProperty("GuestHalfScore");
		} catch (Exception e) {

		}
		try {
			penaltyHomeScore = (SoapPrimitive) article
					.getProperty("PenaltyHomeScore");
		} catch (Exception e) {

		}
		try {
			penaltyGuestScore = (SoapPrimitive) article
					.getProperty("PenaltyGuestScore");
		} catch (Exception e) {

		}
		try {
			homeIcon = (SoapPrimitive) article.getProperty("HomeIcon");
		} catch (Exception e) {

		}
		try {
			guestIcon = (SoapPrimitive) article.getProperty("GuestIcon");
		} catch (Exception e) {

		}
		SoapPrimitive startTime = null;
		if (article.getProperty("StartTime") instanceof SoapPrimitive) {
			startTime = (SoapPrimitive) article.getProperty("StartTime");
		}
		try {
			homeTeam = (SoapPrimitive) article.getProperty("HomeTeam");
		} catch (Exception e) {

		}
		try {
			guestTeam = (SoapPrimitive) article.getProperty("GuestTeam");
		} catch (Exception e) {

		}
		try {
			gameDate = (SoapPrimitive) article.getProperty("GameDate");
		} catch (Exception e) {

		}
		try {
			hasEvents = (SoapPrimitive) article.getProperty("HasEvents");
		} catch (Exception e) {

		}

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
		try {
			Object homeEvent = article.getProperty("HomeEvents");
			if (homeEvent != null && homeEvent instanceof SoapObject) {
				SoapObject homeSoap = (SoapObject) homeEvent;
				ArrayList<GameEvent> events = new ArrayList<GameEvent>();
				for (int j = 0; j < homeSoap.getPropertyCount(); j++) {
					SoapObject gameEvent = (SoapObject) homeSoap.getProperty(j);
					events.add(getGameEventFromSoapObj(gameEvent));
				}
				game.homeEvents = events;
			}
		} catch (Exception e) {

		}
		try {
			Object guestEvent = article.getProperty("GuestEvents");
			if (guestEvent != null && guestEvent instanceof SoapObject) {
				SoapObject guestSoap = (SoapObject) guestEvent;
				ArrayList<GameEvent> events = new ArrayList<GameEvent>();
				for (int j = 0; j < guestSoap.getPropertyCount(); j++) {
					SoapObject gameEvent = (SoapObject) guestSoap
							.getProperty(j);
					events.add(getGameEventFromSoapObj(gameEvent));
				}
				game.guestEvents = events;
			}
		} catch (Exception e) {

		}
		return game;

	}

	public static GameEvent getGameEventFromSoapObj(SoapObject article) {
		SoapPrimitive eventType = (SoapPrimitive) article
				.getProperty("EventType");
		SoapPrimitive desc = (SoapPrimitive) article.getProperty("Description");
		GameEvent event = new GameEvent(eventType.toString() == null ? null
				: eventType.toString(), desc == null ? null : desc.toString());
		return event;

	}

	public static ArrayList<Game> getGameByID(String gameId) {
		String TAG = "SOAPConnected";
		String SOAP_ACTION = "http://tempuri.org/GetGame";
		String METHOD_NAME = "GetGame";
		String NAMESPACE = "http://tempuri.org/";
		String URL = WebServiceReader.ENDPOINT_URL;
		SoapObject resultRequestSOAP = null;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		// SoapObject
		PropertyInfo arinfo = new PropertyInfo();
		arinfo.setNamespace(NAMESPACE);
		arinfo.setName("i_LiveID");
		arinfo.setValue(gameId);

		request.addProperty(arinfo);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		Log.v(TAG, "Request" + request.toString());
		AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
				URL);
		ArrayList<Game> articlesArr = new ArrayList<Game>();
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultRequestSOAP = (SoapObject) envelope.getResponse();

			SoapObject articles = (SoapObject) resultRequestSOAP
					.getProperty("Games");

			for (int j = 0; j < articles.getPropertyCount(); j++) {

				SoapObject article = (SoapObject) articles.getProperty(j);
				Game game = getGameFromSoapObj(article);
				articlesArr.add(game);
			}
		} catch (Exception aE) {
			Log.d(TAG, "Error", aE);
			aE.printStackTrace();
		}
		return articlesArr;

	}
}
