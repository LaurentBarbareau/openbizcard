package com.tssoft.one.webservice;

import java.util.ArrayList;

import com.tss.one.R;
import com.tssoft.one.utils.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ImageLoader extends Thread {

	public boolean isRunning = false;
	ArrayList<String> urls = new ArrayList<String>();
	ArrayList<ImageView> imgViews = new ArrayList<ImageView>();
	// public static ImageLoader instance;
	Activity act;

	public void setTask(String str, ImageView view) {
		urls.add(str);
		imgViews.add(view);
	}

	// public static ImageLoader getInstance(Activity act) {
	// if (instance == null) {
	// instance = new ImageLoader();
	// }
	// instance.act = act;
	// return instance;
	// }

	public ImageLoader(Activity act) {
		this.act = act;
	}

	@Override
	public synchronized void run() {
		Looper.prepare();
		isRunning = true;
		while (isRunning) {
			if (urls.size() > 0) {// upload
				String link = urls.remove(0);
				ImageView imgView = imgViews.remove(0);
				byte[] data = Utils.getByteImageData(link);
				if (data != null) {
					Bitmap bmp = convertByteToBitmap(data);
					final ImageView myImgView = imgView;
					final Bitmap myBmp = bmp;

					OakHandler hand = new OakHandler() {
						public void doJob() {
							act.runOnUiThread(new Runnable() {
								public void run() {
									myImgView.setImageBitmap(myBmp);
								}
							});
						}
					};
					hand.doJob();
				}

			} else {// wait
				try {
					waiting = true;
					wait();
				} catch (Exception e) {
					// System.out.println("ConnectURL : " + e + " " +
					// e.getMessage());
				}
			}
			System.gc();
		}
		Looper.loop();
	}

	private Bitmap convertByteToBitmap(byte[] data) {
		Bitmap photo = BitmapFactory.decodeByteArray(data, 0, data.length);
		return photo;
	}

	boolean waiting = false;

	public void go() {
		// System.out.println("Start Go");
		if (waiting) {
			// System.out.println("In Go");
			mynotify();
			waiting = false;
		}
		// System.out.println("End Go");
	}

	private synchronized void mynotify() {
		notify();
	}

	
}
