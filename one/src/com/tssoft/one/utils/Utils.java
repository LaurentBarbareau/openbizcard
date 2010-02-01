package com.tssoft.one.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class Utils {
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
				AlertDialog alertDialog = new AlertDialog.Builder(myact).create();
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
				AlertDialog alertDialog = new AlertDialog.Builder(myact).create();
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
}
