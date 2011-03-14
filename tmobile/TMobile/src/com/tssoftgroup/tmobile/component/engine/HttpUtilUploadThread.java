package com.tssoftgroup.tmobile.component.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

import com.tssoftgroup.tmobile.utils.Base64;
import com.tssoftgroup.tmobile.utils.CrieUtils;
import com.tssoftgroup.tmobile.utils.StringUtil;

/**
 * 
 * @author Tommi Laukkanen
 */
public class HttpUtilUploadThread extends HttpAbstractUtil {
	/** Total bytes transfered */
	private static String userAgent = "curl/7.18.0 (i486-pc-linux-gnu) libcurl/7.18.0 OpenSSL/0.9.8g zlib/1.2.3.3 libidn/1.1";
	// private static String userAgent = "UNTRUSTED/1.0";
	private static boolean alternateAuthen = false;
	private static String contentType = "application/x-www-form-urlencoded";
	public static HttpConnection con = null;
	public static String suffix = null;

	/** Creates a new instance of HttpUtil */
	public HttpUtilUploadThread() {
	}

	public static void closeConnection() {
		if (con != null) {
			try {
				con.close();
			} catch (IOException e) {
				e.printStackTrace();
				// System.out.println("\n---------\n error httpcon.close() "+e.getMessage()+e.toString()+"\n");
			}
		}
		// System.out.println("\n---------\n finish closeConnection \n");
	}

	public static void setContentType(String contentType) {
		if (contentType == null) {
			HttpUtilUploadThread.contentType = "application/x-www-form-urlencoded";
		} else {
			HttpUtilUploadThread.contentType = contentType;
		}
	}

	public static void setUserAgent(String userAgent) {
		HttpUtilUploadThread.userAgent = userAgent;
	}

	public static void setAlternateAuthentication(boolean flag) {
		HttpUtilUploadThread.alternateAuthen = flag;
	}

	public static String doPost(String url, String query, boolean cancelable)
			throws IOException, Exception {
		return doRequest(url, prepareQuery(query), HttpConnection.POST,
				cancelable);
	}

	public static String doPost(String url, byte[] query, boolean cancelable)
			throws IOException, Exception {
		return doRequest(url, query, HttpConnection.POST, cancelable);
	}

	public static String doGet(String url, String query, boolean cancelable)
			throws IOException, Exception {
		String fullUrl = url;
		query = prepareQuery(query);
		if (query.length() > 0) {
			fullUrl += "?" + query;
		}
		return doRequest(fullUrl, "", HttpConnection.GET, cancelable);
	}

	public static String doRequest(String url, String query,
			String requestMethod, boolean cancelable) throws IOException,
			Exception {
		if (requestMethod.equals(HttpConnection.GET)) {
			return doRequest(url, CrieUtils.UTF8Encode(query), requestMethod,
					cancelable);
		} else {
			return doRequest(url, CrieUtils.UTF8Encode(query), requestMethod,
					cancelable);
		}
	}

	public static String doRequest(String url, byte[] query,
			String requestMethod, boolean cancelable) throws IOException,
			Exception {
		String response = "";
		int status = -1;
		String message = null;
		int depth = 0;
		boolean redirected = false;
		String auth = null;
		InputStream is = null;
		OutputStream os = null;
		con = null;
		// final String platform = Device.getPlatform();
		while (con == null) {
			try {
				con = (HttpConnection) Connector.open(url
						+ getConnectionSuffix());
			} catch (Exception e) {
				System.out.println("error " + e.getMessage());
				System.out.println("class " + e.getClass());
				e.printStackTrace();
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						CrieUtils.removeCurrent();
						Dialog
								.alert("Cannot connect to internet. Please check your internet connection");
					}
				});
				try {
					if (con != null) {
						con.close();
					}
				} catch (Exception ioe) {
				}
				return "NOCONNECTION";
			}

			if (cancelable)
				HttpUtilUploadThread.con = con;
			// Log.setState("connected");

			con.setRequestMethod(requestMethod);

			// con.setRequestProperty("Connection", "close");
			// If no useragent Python server will not accept
			// con.setRequestProperty("User-Agent", Device.getUserAgent());

			if (query.length > 0) {
				// con.setRequestProperty("Content-Type", contentType);
				con.setRequestProperty("Content-Type", contentType);
				// if (Device.isIMobile()) {
				// con.setRequestProperty("Transfer-Encoding", "chunked");
				// }
				con.setRequestProperty(
						HttpProtocolConstants.HEADER_CONTENT_LENGTH, String
								.valueOf(query.length));
				// con.setRequestProperty("x-rim-transcode-content", "none");
				// System.out.println("Length = " + query.length);
				os = con.openOutputStream();
				// ////Edited by oak show progress in 10 20 30 40 50 60 70 80 90
				// Progress of send part use only 50 %
				// os.write(query);
				// for (int i = 0; i < query.length; i++) {
				// os.write(query[i]);
				// }
				os.write(query);
				os.flush();
				// os.close();
				// os = null;
			}

			status = con.getResponseCode();
			System.gc();

			message = con.getResponseMessage();
			// System.out.println("response " + message);
			// Log.info(status + " " + message);
			switch (status) {
			case HttpConnection.HTTP_OK:
			case HttpConnection.HTTP_NOT_MODIFIED:
			case HttpConnection.HTTP_BAD_REQUEST:
				break;
			case HttpConnection.HTTP_MOVED_TEMP:
			case HttpConnection.HTTP_TEMP_REDIRECT:
			case HttpConnection.HTTP_MOVED_PERM:
				if (depth > 2) {
					throw new IOException("Too many redirect");
				}
				redirected = true;
				url = con.getHeaderField("location");
				depth++;
				break;
			case 100:
				throw new IOException("unexpected 100 Continue");
			default:
				throw new IOException("Response status not OK:" + status + " "
						+ message);
			}
		}
		is = con.openInputStream();
		// Log.setState("receiving data");

		if (!redirected) {
			response = getUpdates(con, is, os);
		} else {

		}
		try {
			if (is != null) {
				is.close();
			}
		} catch (Exception ioe) {
			throw ioe;
		}
		try {
			if (os != null) {
				os.close();
			}
		} catch (Exception ioe) {
			throw ioe;
		}
		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception ioe) {
			throw ioe;
		}

		if (status == HttpConnection.HTTP_BAD_REQUEST) {
			// System.out.println(response);
			throw new IOException("Response status not OK:");
		}
		return response;
	}

	private static String getUpdates(HttpConnection con, InputStream is,
			OutputStream os) throws IOException {
		StringBuffer stb = new StringBuffer();
		int ch = 0;
		try {
			int n = (int) con.getLength();
			// Log.info("Size: " + n);
				while ((ch = is.read()) != -1) {
					n = is.available();
					stb.append((char) ch);
				}
			
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (IOException ioe) {
				throw ioe;
			}
		}
		return stb.toString();
	}

	private static String prepareQuery(String query) {
		if (alternateAuthen && username != null && password != null
				&& username.length() > 0) {
			String userPass;
			Base64 b64 = new Base64();
			userPass = username + ":" + password;
			userPass = Base64.encode(userPass.getBytes());
			if (query.length() > 0) {
				query += "&";
			}
			query += "__token__=" + StringUtil.urlEncode(userPass);
		}
		return query;
	}

	public static String getConnectionSuffix() {
		// if (suffix == null) {
		if (DeviceInfo.isSimulator()) {
			suffix = ";deviceside=true";
		} else if (CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_MDS)) {
			suffix = ";deviceside=false";
		} else if ((WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED)
				&& RadioInfo.areWAFsSupported(RadioInfo.WAF_WLAN)) {
			suffix = ";interface=wifi";
		} else {
			ServiceBook sb = ServiceBook.getSB();
			ServiceRecord[] records = sb.getRecords();
			String myuid = null;
			for (int i = 0; i < records.length; i++) {
				ServiceRecord myRecord = records[i];
				String cid, uid;

				if (myRecord.isValid() && !myRecord.isDisabled()) {
					cid = myRecord.getCid().toLowerCase();
					uid = myRecord.getUid().toLowerCase();
					// BIS
					// Wap2.0
					if (cid.indexOf("wptcp") != -1 && uid.indexOf("wifi") == -1
							&& uid.indexOf("mms") == -1) {
						myuid = myRecord.getUid();
						break;
					}
				}
			}
			if (myuid != null) {
				// WAP2 Connection
				suffix = ";deviceside=true;ConnectionUID=" + myuid;
			} else {
				suffix = ";deviceside=true";
			}
		}
		// }
		return suffix;

	}
}