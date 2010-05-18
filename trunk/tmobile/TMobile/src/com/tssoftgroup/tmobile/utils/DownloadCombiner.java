package com.tssoftgroup.tmobile.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;

import com.tssoftgroup.tmobile.component.engine.HttpUtilUploadThread;
import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.model.Video;

public class DownloadCombiner extends Thread {

	private String remoteName;
	private String localName;
	private int chunksize;
	private boolean fromVideoDownload = false;
	private String fileName;
	private String videoname;

	public DownloadCombiner(String remoteName, String localName, int chunksize) {
		this.remoteName = remoteName;
		this.localName = localName;
		this.chunksize = chunksize;
	}

	public DownloadCombiner(String remoteName, String localName, int chunksize,
			boolean fromVideoDownload, String fileName, String videoname) {
		this.remoteName = remoteName;
		this.localName = localName;
		this.chunksize = chunksize;
		this.fromVideoDownload = fromVideoDownload;
		this.fileName = fileName;
		this.videoname = videoname;
	}

	public void run() {
		if (fromVideoDownload) {
			// Add new Video to download queue
			ProfileEntry profile = ProfileEntry.getInstance();
			Vector videos = Video.convertStringToVector(profile.videos);
			Video newVideo = new Video();
			newVideo.setName(fileName);
			newVideo.setStatus("2");
			newVideo.setTitle(videoname);
			//
			videos.addElement(newVideo);
			profile.videos = Video.convertVectorToString(videos);
			profile.saveProfile();
		}
		try {
			int chunkIndex = 0;
			int totalSize = 0;
			/*
			 * File connection
			 */
			FileConnection file = (FileConnection) Connector.open(localName);
			if (!file.exists()) {
				file.create();
				file.setWritable(true);
				OutputStream out = file.openOutputStream();
				/*
				 * HTTP Connections
				 */
				String currentFile = remoteName;
				System.out.println("urlfile|" + currentFile + "|");
				HttpConnection conn;
				InputStream in;
				int rangeStart = 0;
				int rangeEnd = 0;
				int myResponseCode = 0;
				while (true) {
					System.out.println("Opening Chunk: " + chunkIndex);
					conn = (HttpConnection) Connector.open(currentFile
							+ HttpUtilUploadThread.getConnectionSuffix(),
							Connector.READ_WRITE, true);
					rangeStart = chunkIndex * chunksize;
					rangeEnd = rangeStart + chunksize - 1;
					System.out.println("Requesting Range: " + rangeStart + "-"
							+ rangeEnd);
					conn.setRequestProperty("Range", "bytes=" + rangeStart
							+ "-" + rangeEnd);
					int responseCode = conn.getResponseCode();
					myResponseCode = responseCode;
					if (responseCode != 200 && responseCode != 206) {
						System.out.println("Response Code = "
								+ conn.getResponseCode());
						break;
					}
					final String r = conn.getHeaderField("Content-Range");
					if (r != null) {
						try {
							int indMinus = r.indexOf("-");
							int indSlash = r.indexOf("/");
							String current = r
									.substring(indMinus + 1, indSlash);
							String all = r.substring(indSlash + 1, r.length());
							System.out.println("current " + current);
							System.out.println("all " + all);
							int intCurrent = Integer.parseInt(current);
							intCurrent = intCurrent + 1;
							int intAll = Integer.parseInt(all);
							final int percent = intCurrent * 100 / intAll;
							if (intAll == 0) {
								// If enter this case it mean file is too small
								break;
							} else {
								UiApplication.getUiApplication().invokeLater(
										new Runnable() {
											public void run() {
												if (!fromVideoDownload) {
													Status.show("" + percent
															+ "% Completed",
															1000);
												}else{
													ProfileEntry profile = ProfileEntry.getInstance();
													Vector videos =  Video.convertStringToVector(profile.videos);
													for (int i = 0; i < videos.size(); i++) {
														Video video = (Video)videos.elementAt(i);
														if(video.getName().equals(fileName)){
															video.setPercent(percent + " %");
														}
													}
													profile.videos = Video.convertVectorToString(videos);
												}
											}
										});
							}
						} catch (Exception e) {

						}

					} else {
						// System.out.println("r is equal null");
						// if (chunksize > 1000) {
						// try {
						// conn.close();
						// } catch (Exception e) {
						//
						// }
						// new DownloadCombiner(remoteName, localName, 1000)
						// .start();
						// return;
						// } else {
						// break;
						// }
						break;
					}

					System.out.println(r);
					in = conn.openInputStream();
					int length = -1;
					byte[] readBlock = new byte[256];
					int fileSize = 0;
					while ((length = in.read(readBlock)) != -1) {
						out.write(readBlock, 0, length);
						fileSize += length;
						Thread.yield(); // Try not to get cut off
					}
					totalSize += fileSize;
					System.out.println("Chunk Downloaded: " + fileSize
							+ " Bytes");

					chunkIndex++; // index (range) increase
					in.close();
					conn.close();
					in = null;
					conn = null;
					/*
					 * Pause to allow connections to close and other Threads to
					 * run.
					 */
					Thread.sleep(1000);
				}
				System.out.println("Video " + videoname + " downloaded: "
						+ totalSize + " Bytes");
				final int mySize = totalSize;
				final int fiResponseCode = myResponseCode;
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						if (fiResponseCode == 404) {
							final String choices[] = { "Done" };
							final int values[] = { Dialog.OK };
							Dialog dia = new Dialog(
									"The file was removed",
									choices,
									values,
									Dialog.OK,
									Bitmap
											.getPredefinedBitmap(Bitmap.INFORMATION),
									0);
							int result = dia.doModal();

						} else {
							if (!fromVideoDownload) {
								final String choices[] = { "Open", "Done" };
								final int values[] = { Dialog.OK, Dialog.CANCEL };
								Dialog dia = new Dialog(
										"Full file downloaded: " + mySize
												+ " Bytes",
										choices,
										values,
										Dialog.OK,
										Bitmap
												.getPredefinedBitmap(Bitmap.INFORMATION),
										0);
								int result = dia.doModal();
								if (result == Dialog.OK) {
									CrieUtils.browserURL(localName);
								} else {
								}
							} else {
								// Update Status of Video to
								ProfileEntry profile = ProfileEntry
										.getInstance();
								Vector videos = Video
										.convertStringToVector(profile.videos);
								for (int i = 0; i < videos.size(); i++) {
									Video v = (Video) videos.elementAt(i);
									if (v.getName().equals(fileName)) {
										v.setStatus("3");
									}
								}
								String profileVideoString = Video
										.convertVectorToString(videos);
								profile.videos = profileVideoString;
								profile.saveProfile();
								// from video download
								final String choices[] = { "Done" };
								final int values[] = { Dialog.OK };
								Dialog dia = new Dialog(
										"Video "
												+ videoname
												+ " downloaded: "
												+ mySize
												+ " Bytes. Please go back to first page and choose this video again to play the downloaded file.",
										choices,
										values,
										Dialog.OK,
										Bitmap
												.getPredefinedBitmap(Bitmap.INFORMATION),
										0);
								int result = dia.doModal();
								if (result == Dialog.OK) {
								} else {
								}
							}
						}
					}
				});
				out.close();
				file.close();
			} else {
				System.out.println("File already exists...");
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						Dialog.alert("File already exists...");
					}
				});
			}
		} catch (InterruptedException ex) {
			System.out.println("InterruptedException");
			ex.printStackTrace();
		} catch (IOException ex) {
			System.out.println("IOException");
			ex.printStackTrace();
		}

	}
}