package com.tssoftgroup.tmobile.component.engine;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;

import com.tssoftgroup.tmobile.model.PicInfo;
import com.tssoftgroup.tmobile.model.TrainingInfo;
import com.tssoftgroup.tmobile.utils.CrieUtils;
import com.tssoftgroup.tmobile.utils.Scale;

public class LoadPicThread implements Runnable {
	private boolean running = true;
	int BUFFER_SIZE = 512;
	// Vector imgButtons = new Vector();
	Vector items = new Vector();
	boolean isFinish = false;
	boolean pausing = false;
	MainScreen screen;

	public LoadPicThread(Vector timelines, MainScreen screen) {
		// this.imgButtons = imgButtons;
		System.out.println("set size " + timelines.size());
		this.items = timelines;
		this.screen = screen;
	}

	public void run() {
		System.out.println("Running load pic thread" + items.size());
		for (int i = 0; i < items.size(); i++) {
			if (running) {
				while (pausing) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (items.elementAt(i) instanceof PicInfo) {
					PicInfo picInfo = (PicInfo) items.elementAt(i);
					if (picInfo.getThumbnail() == null) {
						try {
//							System.out.println("a1");

							byte[] result = getImageByte(picInfo
									.getThumbnailURL());
							if (result != null) {
//								System.out.println("a2");
								EncodedImage encodeImg = EncodedImage
										.createEncodedImage(result, 0,
												result.length);
//								System.out.println("a3");
								EncodedImage scaledImg = CrieUtils
										.scaleImageToWidth(
												encodeImg,
												Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD);
								final Bitmap bmp = scaledImg.getBitmap();
								picInfo.setThumbnail(bmp);
								if (UiApplication.getUiApplication()
										.getActiveScreen() == screen) {
									screen.invalidate();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				if (items.elementAt(i) instanceof TrainingInfo) {
					TrainingInfo trainInfo = (TrainingInfo) items.elementAt(i);
					if (trainInfo.getThumbnail() == null) {
						try {
							byte[] result = getImageByte(trainInfo
									.getThumbnailUrl());
							if (result != null) {

								EncodedImage encodeImg = EncodedImage
										.createEncodedImage(result, 0,
												result.length);
								EncodedImage scaledImg = CrieUtils
										.scaleImageToWidth(
												encodeImg,
												Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD);
								final Bitmap bmp = scaledImg.getBitmap();
								trainInfo.setThumbnail(bmp);
								if (UiApplication.getUiApplication()
										.getActiveScreen() == screen) {
									screen.invalidate();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				return;
			}
		}
		System.out.println("finish  ");
		isFinish = true;
	}

	public void myWait() {
		if (!isFinish) {
			pausing = true;
		}
	}

	public void myResume() {
		if (!isFinish) {
			pausing = false;
		}
	}

	public void stop() {
		running = false;
	}

	public static byte[] getImageByte(String url) {
		HttpConnection httpcon = null;
		DataInputStream dInputS = null;
		ByteArrayOutputStream bStrm = null;
		byte imageData[] = null;
		try {
			httpcon = (HttpConnection) Connector.open(url
					+ HttpUtilUploadThread.getConnectionSuffix());
			httpcon.setRequestMethod(HttpConnection.GET);
			// httpcon.setRequestProperty("User-Agent",
			// Device.getUserAgent());
			// httpcon.setRequestProperty("Content-Language", "en-US");
			// httpcon.setRequestProperty("Content-Type",
			// "application/x-www-form-urlencoded");

			// retrieve the response
			dInputS = new DataInputStream(httpcon.openInputStream());

			int len = (int) httpcon.getLength();

			if (len != -1) {
				imageData = new byte[len];

				// Read the png into an array
				dInputS.readFully(imageData);
			} else // Length not available...
			{
				bStrm = new ByteArrayOutputStream();

				int ch;
				while ((ch = dInputS.read()) != -1)
					bStrm.write(ch);

				imageData = bStrm.toByteArray();
				bStrm.close();
			}

		} catch (Exception e) {
			System.out.println("error "  +e.getMessage());
			System.out.println("class "  +e.getClass());
			e.printStackTrace();
			UiApplication.getUiApplication().invokeLater(new Runnable() {

				public void run() {
					CrieUtils.removeCurrent();
					Dialog
							.alert("Cannot connect to internet. Please check your internet connection");
				}
			});

		}finally {
			// Free up i/o streams and http connection
			try {
				if (httpcon != null)
					httpcon.close();
			} catch (Exception ignored) {
			}
			try {
				if (dInputS != null)
					dInputS.close();
			} catch (Exception ignored) {
			}

		}
		return imageData;
	}
}
