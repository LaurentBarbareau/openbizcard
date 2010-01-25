package com.tssoft.one.webservice;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation.ActivityMonitor;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.tssoft.one.webservice.model.Article;
import com.tssoft.one.webservice.model.ArticleBySubject;
import com.tssoft.one.webservice.model.Table;
import com.tssoft.one.webservice.model.Team;

public class WebServiceReaderMyTeam {
	// user : oakoakoak
	// Get Device Id
	static TelephonyManager mTelephonyMgr = null;
	private static String deviceId = "oakoakoak";

	public static String getDeviceId(Activity act) {
		 if (mTelephonyMgr == null) {
		 mTelephonyMgr = (TelephonyManager) act
		 .getSystemService(Activity.TELEPHONY_SERVICE);
		 deviceId = mTelephonyMgr.getDeviceId();
		 }
		return deviceId;
	}

	public static ArrayList<Table> getTablesTeams() {
		String TAG = "SOAPConnected";
		String SOAP_ACTION = "http://tempuri.org/GetTablesAndTeams";
		String METHOD_NAME = "GetTablesAndTeams";
		String NAMESPACE = "http://tempuri.org/";
		String URL = WebServiceReader.ENDPOINT_URL;
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
		ArrayList<Table> tables = new ArrayList<Table>();
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultRequestSOAP = (SoapObject) envelope.getResponse();
			for (int i = 0; i < resultRequestSOAP.getPropertyCount(); i++) {

				SoapObject articleBySubject = (SoapObject) resultRequestSOAP
						.getProperty(i);
				SoapPrimitive subject = (SoapPrimitive) articleBySubject
						.getProperty("Name");
				SoapObject articles = (SoapObject) articleBySubject
						.getProperty("Teams");
				ArrayList<Team> teams = new ArrayList<Team>();
				for (int j = 0; j < articles.getPropertyCount(); j++) {

					SoapObject article = (SoapObject) articles.getProperty(j);
					SoapPrimitive id = (SoapPrimitive) article
							.getProperty("ID");
					SoapPrimitive name = (SoapPrimitive) article
							.getProperty("Name");
					SoapPrimitive imgUrl = (SoapPrimitive) article
							.getProperty("ImageURL");
					Team ar = new Team(id != null ? id.toString() : null,
							name != null ? name.toString() : null,
							imgUrl != null ? imgUrl.toString() : null);
					teams.add(ar);
				}
				tables.add(new Table(subject.toString(), teams));
			}
			Log.v(TAG, "Response from servcer:" + resultRequestSOAP.toString());

		} catch (Exception aE) {
			Log.d(TAG, "Error", aE);
			aE.printStackTrace();
		}
		return tables;
	}

	public static boolean addUserTeam(String userId, String teamId) {
		String TAG = "SOAPConnected";
		String SOAP_ACTION = "http://tempuri.org/AddUserTeam";
		String METHOD_NAME = "AddUserTeam";
		String NAMESPACE = "http://tempuri.org/";
		String URL = WebServiceReader.ENDPOINT_URL;
		SoapPrimitive resultRequestSOAP = null;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		// SoapObject
		PropertyInfo userid = new PropertyInfo();
		userid.setNamespace(NAMESPACE);
		userid.setName("i_UserID");
		userid.setValue(userId);

		PropertyInfo teamid = new PropertyInfo();
		teamid.setNamespace(NAMESPACE);
		teamid.setName("i_TeamID");
		teamid.setValue(teamId);

		request.addProperty(userid);
		request.addProperty(teamid);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		Log.v(TAG, "Request" + request.toString());
		AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
				URL);
		boolean ret = true;
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultRequestSOAP = (SoapPrimitive) envelope.getResponse();
			ret = resultRequestSOAP.toString().equals("true");
		} catch (Exception aE) {
			Log.d(TAG, "Error", aE);
			aE.printStackTrace();
		}
		return ret;
	}

	// get user team
	public static ArrayList<Team> getUserTeam(String deviceId)
			throws UnknownHostException {
		String TAG = "SOAPConnected";
		String SOAP_ACTION = "http://tempuri.org/GetUserTeams";
		String METHOD_NAME = "GetUserTeams";
		String NAMESPACE = "http://tempuri.org/";
		String URL = WebServiceReader.ENDPOINT_URL;
		SoapObject resultRequestSOAP = null;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		// SoapObject
		PropertyInfo userid = new PropertyInfo();
		userid.setNamespace(NAMESPACE);
		userid.setName("i_UserID");
		userid.setValue(deviceId);

		request.addProperty(userid);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		Log.v(TAG, "Request" + request.toString());
		AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
				URL);
		ArrayList<Team> teams = new ArrayList<Team>();
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultRequestSOAP = (SoapObject) envelope.getResponse();
			for (int j = 0; j < resultRequestSOAP.getPropertyCount(); j++) {
				SoapObject team = (SoapObject) resultRequestSOAP.getProperty(j);
				SoapPrimitive id = (SoapPrimitive) team.getProperty("ID");
				SoapPrimitive name = (SoapPrimitive) team.getProperty("Name");
				SoapPrimitive imgUrl = (SoapPrimitive) team
						.getProperty("ImageURL");
				Team ar = new Team(id != null ? id.toString() : null,
						name != null ? name.toString() : null,
						imgUrl != null ? imgUrl.toString() : null);
				teams.add(ar);
			}
			Log.v(TAG, "Response from servcer:" + resultRequestSOAP.toString());

		} catch (UnknownHostException ae) {
			throw ae;
		} catch (Exception aE) {
			Log.d(TAG, "Error", aE);
			aE.printStackTrace();
		}
		return teams;
	}

	// remove user team
	public static boolean removeUserTeam(String userId, String teamId) {
		String TAG = "SOAPConnected";
		String SOAP_ACTION = "http://tempuri.org/RemoveUserTeam";
		String METHOD_NAME = "RemoveUserTeam";
		String NAMESPACE = "http://tempuri.org/";
		String URL = WebServiceReader.ENDPOINT_URL;
		SoapPrimitive resultRequestSOAP = null;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		// SoapObject
		PropertyInfo userid = new PropertyInfo();
		userid.setNamespace(NAMESPACE);
		userid.setName("i_UserID");
		userid.setValue(userId);

		PropertyInfo teamid = new PropertyInfo();
		teamid.setNamespace(NAMESPACE);
		teamid.setName("i_TeamID");
		teamid.setValue(teamId);

		request.addProperty(userid);
		request.addProperty(teamid);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		Log.v(TAG, "Request" + request.toString());
		AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
				URL);
		boolean ret = true;
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultRequestSOAP = (SoapPrimitive) envelope.getResponse();
			ret = resultRequestSOAP.toString().equals("true");
		} catch (Exception aE) {
			Log.d(TAG, "Error", aE);
			aE.printStackTrace();
		}
		return ret;
	}

	// getuserarticle
	public static ArrayList<ArticleBySubject> getUserArrticles(String userId)
			throws UnknownHostException {
		String TAG = "SOAPConnected";
		String SOAP_ACTION = "http://tempuri.org/GetUserArticles";
		String METHOD_NAME = "GetUserArticles";
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
					if (ar != null) {
						articlesArr.add(ar);
					}
				}
				articlesSubjectArr.add(new ArticleBySubject(subject.toString(),
						articlesArr));
			}
			Log.v(TAG, "Response from servcer:" + resultRequestSOAP.toString());

		} catch (UnknownHostException ae) {
			throw ae;
		} catch (Exception aE) {
			Log.d(TAG, "Error", aE);
			aE.printStackTrace();
		}
		return articlesSubjectArr;
	}
	// public static ArrayList<ArticleBySubject> getUserArrticles(String userId)
	// {
	// ArrayList<ArticleBySubject> arr =new ArrayList<ArticleBySubject>();
	// ArrayList<Article> article1 = new ArrayList<Article>();
	// article1.add(new Article("1","title1","ssdsdssdsdds","",""));
	// article1.add(new Article("2","title1","ssdsdssdsdds","",""));
	// article1.add(new Article("3","title1","ssdsdssdsdds","",""));
	// article1.add(new Article("4","title1","ssdsdssdsdds","",""));
	// ArticleBySubject a1 = new ArticleBySubject("test1",article1);
	// arr.add(a1);
	// arr =new ArrayList<ArticleBySubject>();
	// article1 = new ArrayList<Article>();
	// article1.add(new Article("1","title1","ssdsdssdsdds","",""));
	// article1.add(new Article("2","title1","ssdsdssdsdds","",""));
	// article1.add(new Article("3","title1","ssdsdssdsdds","",""));
	// article1.add(new Article("4","title1","ssdsdssdsdds","",""));
	// a1 = new ArticleBySubject("test1",article1);
	// arr.add(a1);
	// return arr;
	// }
	// //////////////////////// Game
	// get user game

	// current date
	// get game by day
	// get live games
	// get current subject
	// get game by subject
}
