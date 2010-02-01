package com.tssoft.one.webservice;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.widget.ImageView;

import com.tss.one.R;
import com.tssoft.one.utils.Utils;

public class ImageLoader extends Thread {

	private Bitmap rounder;
	private Bitmap bmp;
	private Canvas canvas;
	public boolean isRunning = false;
	ArrayList<String> urls = new ArrayList<String>();
	ArrayList<ImageView> imgViews = new ArrayList<ImageView>();
	Activity act;

	public void setTask(String str, ImageView view) {
		urls.add(str);
		imgViews.add(view);
	}

	public ImageLoader(Activity act) {
		this.act = act;
	}

	@Override
	public synchronized void run() {
		
		isRunning = true;
		while (isRunning) {
			if (urls.size() > 0) {// upload
				String link = urls.remove(0);
				ImageView imgView = imgViews.remove(0);
				byte[] data = Utils.getByteImageData(link);
				if (data != null) {
					bmp = convertByteToBitmap(data);
					final ImageView myImgView = imgView;
					final Bitmap myBmp = bmp;

					act.runOnUiThread(new Runnable() {
						public void run() {
							if(isRunning){
//								if(myImgView.findViewById(R.id.main_image)!=null){
//									int w = 120;
//									int h = 107;
//									rounder = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
//									canvas = new Canvas(rounder);  
//									
//									Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//									xferPaint.setColor(Color.RED);
//
//									canvas.drawRoundRect(new RectF(0,0,w - 20,h - 20), 120.0f, 120.0f, xferPaint);
//
//									xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//									
//									canvas.drawBitmap(bmp, 0,0, null);
//									canvas.drawBitmap(rounder, 0, 0, xferPaint);	
//									//canvas.drawBitmap(bmp, 0,0, xferPaint);
//									
//									myImgView.setImageBitmap(bmp);
//									
//									System.out.println("draw rounded=======================");
//								}else{
								
									myImgView.setImageBitmap(myBmp);
									
								//}								
							}
						}
					});
				}

			} else {// wait
				try {
					waiting = true;
					wait();
				} catch (Exception e) {
				}
			}
			System.gc();
		}
	}

	private Bitmap convertByteToBitmap(byte[] data) {
		Bitmap photo = BitmapFactory.decodeByteArray(data, 0, data.length);
		return photo;
	}

	boolean waiting = false;

	public void go() {
		if (waiting) {
			mynotify();
			waiting = false;
		}
	}

	private synchronized void mynotify() {
		notify();
	}

	
}
