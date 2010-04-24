package com.tssoftgroup.tmobile.component.engine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Status;

import com.tssoftgroup.tmobile.model.PicInfo;
import com.tssoftgroup.tmobile.screen.LogScreen;
import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.CrieUtils;

public class UploadThread implements Runnable {

	private static UploadThread instance;

	Vector pics = new Vector();
	public boolean mTrucking = true;
	public boolean waiting = false;
	public boolean started = false;
	private final int limitNum = 20;
	private int all = 0;
	private int currentUploading = 0;
	public static final int LIMIT_PIC_BYTE = 81920;
	// int LIMIT_PIC_BYTE = 163840;
	// int LIMIT_PIC_BYTE = 4000;
	private PicInfo picInfoItem;

	private UploadThread() {
	}

	public int getCurrentUploadingInd() {
		return currentUploading;
	}

	public String getRemainString() {
		return pics.size() + " Uploading Images " + "/ " + (all - pics.size())
				+ " Uploaded Images";
	}

	public int getRemainPic() {
		return pics.size();
	}

	public Vector getRemainPicsVector() {
		return pics;
	}

	public void upLoad(PicInfo pf) {
		System.out.println("upLoad1");
		all = all + 1;
		addLink(pf);
		System.out.println("upLoad2");

		if (!this.started) {
			this.started = true;
			Thread threadload = new Thread(this);
			threadload.start();
			// System.out.println("upLoad3");

		} else {
			try {
				if (waiting) {
					this.go();
					waiting = false;
				} else {

				}
				// System.out.println("upLoad4");

			} catch (Exception e) {
				// System.out.println("upLoad5");

				e.printStackTrace();
			}
		}
	}

	public static UploadThread getInstance() {
		if (instance == null) {
			instance = new UploadThread();
		}
		return instance;
	}

	public void clearAll() {
		pics.removeAllElements();
	}

	private void addLink(PicInfo picInfo) {
		if (pics.size() >= limitNum) {
			pics.removeElementAt(limitNum - 1);
		}

		pics.addElement(picInfo);
	}

	private synchronized void go() {
		try {
			notify();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void run() {
		// started = true;

		while (mTrucking) {
			try {

				if (pics.size() > 0) {
					PicInfo picInfo = (PicInfo) pics.elementAt(0);

					picInfoItem = picInfo;

					try {
						String res = "";
						// read Image from file path
						byte[] data = CrieUtils.readByteFileSystem(picInfo
								.getLocalFilename());
						System.out.println("data " + data.length);

						picInfo.setFileSize(data.length);

						// Thread.sleep(10000);
						try {
							LogScreen.debug("Post video");
							System.out.println("Post video");
							res = postVideoByteChunk(data, picInfo.getTitle(),
									picInfo.getDescription(),
									Const.URL_SEND_VIDEO,
									Const.URL_SEND_VIDEO_CHUNK);
							LogScreen.debug("Finish Post video " + res);
							// Finish upload
							UiApplication.getUiApplication().invokeLater(
									new Runnable() {
										public void run() {
											Engine.getInstance().uploadingIndex++;
											Engine
													.getInstance()
													.updateStatus(
															"Uploaded ("
																	+ Engine
																			.getInstance().uploadingIndex
																	+ "/"
																	+ Engine
																			.getInstance().statusVector
																			.size()
																	+ ")");

										}
									});
							currentUploading++;
							pics.removeElementAt(0);
							// picInfo.setFinish(true);
						} catch (IOException e) {
							// IO Exception in Timeout exception
							// CrieUtils.showAlert(e.toString());
							e.printStackTrace();
							// CrieConstant.sending = false;
							System.out.println("error Upload thread 1 ");
							// try{
							// reload();
							// }catch(Exception ex){
							// ex.printStackTrace();
							// }
							return;
						} catch (Exception e) {
							// CrieUtils.showAlert(e.toString());
							e.printStackTrace();
							System.out.println("error Upload thread 2 ");
							// CrieConstant.sending = false;
							return;
						} catch (OutOfMemoryError e) {
							// CrieUtils.showAlert(e.toString());
							e.printStackTrace();
							System.out.println("error Upload thread 3 ");
							return;
						}

						data = null;
						System.gc();
					}

					catch (SecurityException e) {
						// CrieUtils.showAlert(e.toString());
						System.out.println("error Upload thread 4 ");
						return;
					} catch (Exception e) {
						System.out.println("error Upload thread 5 ");
						// CrieUtils.showAlert(e.toString());
						return;
					}

				}
				try {
					if (pics.size() > 0) {
					} else {
						waiting = true;
						wait();
					}

				} catch (InterruptedException e) {
					// CrieUtils.showAlert(e.toString());
					System.out.println("error Upload thread 6 ");
					return;
				}
			} catch (NullPointerException e) {
				System.out.println("error Upload thread 7 ");
				// CrieUtils.showAlert(e.toString());
				return;
			}
		}
	}

	private String postVideoByteChunk(byte[] picture, String title,
			String description, String url, String urlChunk) throws Exception {
		String response = "";
		if (picture.length > LIMIT_PIC_BYTE) {
			int numChunk = (picture.length / LIMIT_PIC_BYTE)
					+ (picture.length % LIMIT_PIC_BYTE == 0 ? 0 : 1);
			System.out.println(" numChunk  " + numChunk);
			int numSent = LIMIT_PIC_BYTE;
			int rangeStart = 0;
			int rangeEnd = 0;
			int chunkIndex = 0;

			try {
				long chunkid = System.currentTimeMillis();
				int pin = DeviceInfo.getDeviceId();
				System.out.println(" query length " + picture.length);
				System.out.println(" numChunk " + numChunk);
				System.out.println(" chunkid " + chunkid);
				for (int i = 0; i < numChunk; i++) {
					String boundary = MultiPartFormOutputStream
							.createBoundary();
					ByteArrayOutputStream data = new ByteArrayOutputStream();

					rangeStart = chunkIndex * LIMIT_PIC_BYTE;
					rangeEnd = rangeStart + LIMIT_PIC_BYTE - 1;
					// if it is the last chunk
					if (rangeStart < picture.length
							&& rangeEnd > picture.length) {
						numSent = picture.length - rangeStart;
						System.out.println("i " + i + " numsent " + numSent);
					}
					MultiPartFormOutputStream out = new MultiPartFormOutputStream(
							data, boundary);
					ByteArrayOutputStream b = new ByteArrayOutputStream();
					b.write(picture, rangeStart, numSent);
					byte[] byteSent = b.toByteArray();
					out.writeFile("video", "video/quicktime", "video.3gp",
							byteSent, null);
					out.writeField("currentchunk", "" + i, null);
					out.writeField("numchunk", "" + numChunk, null);
					out.writeField("chunkid", pin + "" + chunkid, null);

					out.writeField("title", title, null);
					out.writeField("description", description, null);
					out.close();

					HttpAbstractUtil.setBasicAuthentication("", "");
					HttpUtilUploadThread
							.setContentType(MultiPartFormOutputStream
									.getContentType(boundary));
					byte[] ba = data.toByteArray();

					String request = new String(ba);
					// System.out.println("request " + request);
					//response = HttpUtilUploadThread.doPost(urlChunk, ba, false);
					response = HttpUtilUploadThread.doPost(urlChunk, "currentchunk=10&numchunk=10", false);
					// System.out.println("myresponse " + response);
					chunkIndex++;
					final int myi = i;
					final int mynumchunk = numChunk;
					
					UiApplication.getUiApplication().invokeLater(new Runnable(){
						public void run() {
							Engine engine = Engine.getInstance();
							Engine.getInstance().updateStatus(
									"Uploading (" + engine.uploadingIndex + "/"
											+ engine.statusVector.size() + ") "+ (100 * (myi + 1) / mynumchunk) + "%");
							LogScreen.debug("Uploading ... "
									+ (100 * (myi + 1) / mynumchunk) + "%");
						}});
				

					// picInfoItem.setPercent(100 * (i + 1) / numChunk);
				}

				/*
				 * byte[] raw = data.toByteArray();
				 * System.out.println(boundary); System.out.println(raw.length);
				 * for(int i=0;i<2000;i++) { System.out.print(raw[i]+" "); }
				 * System.out.println();
				 */

				// UiApplication.getUiApplication().pushScreen(screen);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw ex;
				// throw new TwitterException("post " + ex.toString());
			}
			return "OK";
		}
		try {
			long chunkid = System.currentTimeMillis();
			int pin = DeviceInfo.getDeviceId();
			String boundary = MultiPartFormOutputStream.createBoundary();
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			MultiPartFormOutputStream out = new MultiPartFormOutputStream(data,
					boundary);

			out.writeFile("video", "video/quicktime", "video.3gp", picture,
					null);
			out.writeField("chunkid", pin + "" + chunkid, null);
			out.writeField("title", title, null);
			out.writeField("description", description, null);
			out.close();

			HttpAbstractUtil.setBasicAuthentication("", "");
			HttpUtilUploadThread.setContentType(MultiPartFormOutputStream
					.getContentType(boundary));
			byte[] ba = data.toByteArray();

			response = HttpUtilUploadThread.doPost(url, ba, false);
			// picLength = ba.length;
			// picInfoItem.setPercent(100);

		} catch (Exception ex) {
			HttpUtilUploadThread.setContentType(null);
			ex.printStackTrace();
			// CrieUtils.showAlert(ex.toString());
			throw ex;
			// throw new TwitterException("post " + ex.toString());
		}
		HttpUtilUploadThread.setContentType(null);
		return response;
	}
}
