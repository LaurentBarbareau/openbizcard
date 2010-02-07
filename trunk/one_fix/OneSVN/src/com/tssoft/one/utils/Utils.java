package com.tssoft.one.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.tss.one.R;

public class Utils {

//	public static String NUMBER_PATTERN = "[[\\p{InHebrew}]&&\\d*]";
	public static String NUMBER_PATTERN = "\\d*";
	private static String SCORE_PATTERN = "\\d*:\\d*";

	public static final String[] splitString(String original, String separator) {
		Vector nodes = new Vector();

		// Parse nodes into vector
		int index = original.indexOf(separator);
		while (index >= 0) {
			nodes.addElement(original.substring(0, index));
			original = original.substring(index + separator.length());
			index = original.indexOf(separator);
		}
		// Get the last node
		nodes.addElement(original);

		// Create splitted string array
		String[] result = new String[nodes.size()];
		if (nodes.size() > 0) {
			for (int loop = 0; loop < nodes.size(); loop++)
				result[loop] = (String) nodes.elementAt(loop);
		}
		return result;
	}

	public static String readAsset(Activity ac, String filename) {
		try {
			InputStream is = ac.getAssets().open(filename); //

			int size = is.available();

			// Read the entire asset into a local byte buffer.
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();

			// Convert the buffer into a string.
			String text = new String(buffer);

			return text;
		} catch (IOException e) {
			// Should never happen!
			throw new RuntimeException(e);
		}

	}

	// Save file
	public static void WriteSettings(Context context, String data,
			String filename, Activity ac) {
		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;

		try {
			fOut = ac.openFileOutput(filename, ac.MODE_PRIVATE);
			osw = new OutputStreamWriter(fOut);
			osw.write(data);
			osw.flush();
			Toast.makeText(context, "File saved", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "File not saved", Toast.LENGTH_SHORT)
					.show();
		} finally {
			try {
				osw.close();
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// read file
	public static String ReadSettings(Context context, String filename,
			Activity ac) {
		FileInputStream fIn = null;
		InputStreamReader isr = null;

		char[] inputBuffer = new char[255];
		String data = null;

		try {
			fIn = ac.openFileInput(filename);
			isr = new InputStreamReader(fIn);
			isr.read(inputBuffer);
			data = new String(inputBuffer);
			Toast.makeText(context, "File read", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "File not read", Toast.LENGTH_SHORT).show();
		} finally {
			try {
				isr.close();
				fIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	// http request
	public static String getUrlData(String url) {
		String websiteData = null;
		int timeOutMS = 3000;
		try {
			HttpParams my_httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(my_httpParams, timeOutMS);
			HttpConnectionParams.setSoTimeout(my_httpParams, timeOutMS);
			// HttpClient httpClient = new DefaultHttpClient(my_httpParams); //

			DefaultHttpClient client = new DefaultHttpClient(my_httpParams);
			URI uri = new URI(url);
			HttpGet method = new HttpGet(uri);
			HttpResponse res = client.execute(method);
			InputStream data = res.getEntity().getContent();
			websiteData = generateString(data);

		} catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return "408";

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		return websiteData;

	}

	public static byte[] getByteImageData(String url) {
		byte[] image = null;
		int timeOutMS = 6000;
		try {
			HttpParams my_httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(my_httpParams, timeOutMS);
			HttpConnectionParams.setSoTimeout(my_httpParams, timeOutMS);
			// HttpClient httpClient = new DefaultHttpClient(my_httpParams); //

			DefaultHttpClient client = new DefaultHttpClient(my_httpParams);
			URI uri = new URI(url);
			HttpGet method = new HttpGet(uri);
			HttpResponse res = client.execute(method);
			InputStream data = res.getEntity().getContent();
			// websiteData = generateString(data);

			// convert stream .. into byteArray
			ByteArrayOutputStream bStrm = new ByteArrayOutputStream();

			int ch;
			while ((ch = data.read()) != -1)
				bStrm.write(ch);

			image = bStrm.toByteArray();

		} catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return null;

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		return image;

	}

	public static String generateString(InputStream stream) {
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader buffer = new BufferedReader(reader);
		StringBuilder sb = new StringBuilder();
		try {
			String cur;
			while ((cur = buffer.readLine()) != null) {
				sb.append(cur + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		try {
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();

	}

	public static void showAlert(Activity act, String msg) {
		final Activity myact = act;
		final String mymsg = msg;

		act.runOnUiThread(new Runnable() {

			public void run() {
				AlertDialog alertDialog = new AlertDialog.Builder(myact)
						.create();
				alertDialog.setTitle("Error");
				alertDialog.setMessage(mymsg);
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				alertDialog.show();
			}
		});
	}

	public static void showAlertWithExitProgram(Activity act, String msg) {
		final Activity myact = act;
		final String mymsg = msg;

		act.runOnUiThread(new Runnable() {

			public void run() {
				AlertDialog alertDialog = new AlertDialog.Builder(myact)
						.create();
				alertDialog.setTitle("Error");
				alertDialog.setMessage(mymsg);
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								myact.finish();
								return;
							}
						});
				alertDialog.show();
			}
		});
	}

	public static final String SAVEPIC_FOLDER = "temp";

	public static void saveFileOnSD(Activity act, byte[] b, String fileName) {

		try {
			FileOutputStream stream = act.openFileOutput(fileName, 0);
			stream.write(b);
			Log.e("savePicOnSDCard", "Current file name: " + fileName);
			stream.flush();
			stream.close();
		} catch (Exception e) {
			Log.e("error", "exception while writing image", e);
		}

	}

	public static byte[] getByteData(Activity act, String filename) {
		// Auto-generated method stub
		try {
			FileInputStream fs = act.openFileInput(filename);
			// FileInputStream fs = new
			// FileInputStream(CrieConstant.getCurrentPhotoName());
			int size = fs.available();
			byte[] data = new byte[size];
			int result = fs.read(data);
			fs.close();
			Log.e("getByteData", "result: " + result + "," + size);
			if (result == size) {// OK
				return data;
			} else {
				return new byte[1024];
			}

		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			// Auto-generated catch block
			return null;
		}
	}

	public static InputStream getBitmap_(String fileUrl) {
		InputStream is = null;
		URL myFileUrl = null;
		try {
			myFileUrl = new URL(fileUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			int length = conn.getContentLength();
			// int[] bitmapData = new int[length];
			// byte[] bitmapData2 = new byte[length];
			is = conn.getInputStream();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("Error getBitmap_ : " + e.getMessage());
		}
		return is;
	}

	public static String getHttpConn(String url) {
		String responseStr = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGetRequest = new HttpGet(url);
			HttpResponse response = client.execute(httpGetRequest);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				response.getEntity().writeTo(os);
				responseStr = os.toString();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return responseStr;
	}

	public static String toJSONString(String result) {
		String res = result;
		if (result.startsWith("("))
			res = result.substring(1);
		if (result.indexOf("});") > -1)
			res = res.substring(0, res.lastIndexOf("});") + 3);

		return res;
	}

	public static void openBrowser(Context ct, String url) {
		Intent viewIntent = new Intent("android.intent.action.VIEW", Uri
				.parse(url));
		ct.startActivity(viewIntent);
	}

	public static boolean isStartWithEnglishUnicode(String str) {
		str = str.toLowerCase();
		String abc = "abcdefghijklmnopqrstuvwxyz";
		for (int i = 0; i < abc.length(); i++) {
			if (str.charAt(0) == abc.charAt(i)) {
				return true;
			}
		}
		return false;
	}

	public static int getResourceIdFromPath(Context context, String fileName) {
		int rId = -1;
		try {
			rId = context.getResources().getIdentifier(fileName, "drawable",
					"com.tss.one");
			// Bitmap image = BitmapFactory.decodeResource(Resources.getSystem()
			// , rId);
			// System.out.println("image ::: " + image);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			rId = -1;
			e.printStackTrace();
		}
		return rId;
	}

	public static String toEndedHebrew(Context context, String gameMinute) {
		if (gameMinute.toLowerCase().indexOf("end") > -1)
			gameMinute = context.getText(R.string.finishminute).toString();
		return gameMinute;
	}

	public static boolean isEndGame(String gameMinute) {
		if (gameMinute.toLowerCase().indexOf("end") > -1)
			return true;
		else
			return false;
	}

	public static String timenow(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		cal.get(Calendar.MINUTE);
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	public static String getCurrentMinute() {
		Calendar cal = Calendar.getInstance();
		String minute = cal.get(Calendar.MINUTE) + "";
		if (minute.length() == 1)
			minute = "0" + minute;
		return minute;
	}

	public static String getCurrentHour() {
		Calendar cal = Calendar.getInstance();
		String hour = cal.get(Calendar.HOUR_OF_DAY) + "";
		if (hour.length() == 1)
			hour = "0" + hour;
		return hour;
	}

	public static void displayNoGameDetail(Context context) {
		// display dialog box
		// when no news
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(context.getText(R.string.no_game_desc_title));
		alertDialog.setMessage(context.getText(R.string.no_game_detail_popup));
		alertDialog.setButton(context.getText(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
		alertDialog.show();
	}

	public static void displayNoGameDetailNow(Context context) {
		// display dialog box
		// when no news
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(context.getText(R.string.no_game_desc_title));
		alertDialog.setMessage(context
				.getText(R.string.no_game_desc_not_start_popup));
		alertDialog.setButton(context.getText(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
		alertDialog.show();
	}

	public static String reverseStringByPattern(String strPattern,
			String oldString) {

		String txt = oldString;
		String subTxt = txt;
		char[] c;

		Pattern pattern = Pattern.compile(strPattern);
		Matcher matcher = pattern.matcher(subTxt);

		while (matcher.find()) {
			if (matcher.group().length() > 1) {
				int start = matcher.start();
				int end = matcher.end();
				subTxt = txt.substring(start, end);
				// number pattern
				if (!strPattern.equals(SCORE_PATTERN)) {
					c = subTxt.toCharArray();
					subTxt = "";
					for (int j = c.length - 1; j >= 0; j--) {
						subTxt += c[j];
					}
					txt = txt.substring(0, start) + subTxt
							+ txt.substring(end, txt.length());
				} else {
					String first = subTxt.substring(0, subTxt.indexOf(":"));
					String second= subTxt.substring( subTxt.indexOf(":") +1 , subTxt.length());
					txt =  txt.substring(0, start)+  second +":" + first  + txt.substring(end, txt.length());
				}
				// score pattern
			}
		}
		if (strPattern.equals(SCORE_PATTERN)) {
			return txt;
		} else {
			return reverseStringByPattern(SCORE_PATTERN, txt);
		}
	}
	
}
