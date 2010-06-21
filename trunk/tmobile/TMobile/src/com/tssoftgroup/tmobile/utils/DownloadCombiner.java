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

import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.component.engine.HttpUtilUploadThread;
import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.model.Video;

public class DownloadCombiner extends Thread {

	private String remoteName;
	private String localName;
	private int chunksize;
	private boolean fromVideoDownload = false;
	public String fileName;
	private String videoname;
//	public boolean isCancel = false;
	public boolean cancel = false;
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
			System.out.println("from video downlo0ad");
			// Add new Video to download queue
			ProfileEntry profile = ProfileEntry.getInstance();
			System.out.println("after get instance ");
			Vector videos = Video.convertStringToVector(profile.videos);
			System.out.println("profile.videos " + profile.videos);
			// check the old video have current name
			boolean haveThisName = false;
			for (int i = 0; i < videos.size(); i++) {
				Video vid = (Video) videos.elementAt(i);
				if (vid.getName().equals(fileName)) {
					haveThisName = true;
				}
			}
			if (!haveThisName) {
				System.out.println("dont' have this name");
				Video newVideo = new Video();
				newVideo.setName(fileName);
				newVideo.setStatus("2");
				newVideo.setTitle(videoname);
				//
				videos.addElement(newVideo);
				profile.videos = Video.convertVectorToString(videos);
				profile.saveProfile();
			} else {
				System.out.println("have this name");
			}
		}
		try {
			int chunkIndex = 0;
			int totalSize = 0;

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
			// if video download resume from last chunk

			
			// / end if

			while (true) {
				if(cancel){
					return;
				}
				ProfileEntry profile = ProfileEntry.getInstance();
				Vector videos = Video.convertStringToVector(profile.videos);
				
				System.out.println("profile.video " + profile.videos);
				// check resume
				boolean isResume = false;
				for (int i = 0; i < videos.size(); i++) {
					Video video = (Video) videos.elementAt(i);
					System.out.println("video.getname :" + video.getName());
					System.out.println("filename :" + fileName);
					
					if (video.getName().equals(fileName)) {
						System.out.println("video current chunk " + video.getCurrentChunk());
						if (!video.getCurrentChunk().equals("0")) {
							try {
								isResume = true;
								chunkIndex = Integer.parseInt(video
										.getCurrentChunk());
							} catch (Exception e) {
								isResume = false;
							}
						}
					}
				}
//				if (isCancel) {
//					break;
//				}
				System.out.println("Starting while .....");
				System.out.println("Opening Chunk: " + chunkIndex);
				conn = (HttpConnection) Connector.open(currentFile
						+ ";ConnectionTimeout=10000"
						+ HttpUtilUploadThread.getConnectionSuffix(),
						Connector.READ_WRITE, true);
				rangeStart = chunkIndex * chunksize;
				rangeEnd = rangeStart + chunksize - 1;
				System.out.println("Requesting Range: " + rangeStart + "-"
						+ rangeEnd);
				conn.setRequestProperty("Range", "bytes=" + rangeStart + "-"
						+ rangeEnd);
				System.out.println("before get responsecode");
				int responseCode = conn.getResponseCode();
				System.out.println("after get responsecode");

				myResponseCode = responseCode;
				if (responseCode != 200 && responseCode != 206) {
					System.out.println("Response Code = "
							+ conn.getResponseCode());
					break;
				}
				final String r = conn.getHeaderField("Content-Range");
				boolean updatePercentBool = false;
				int updatePercent = 0;
				if (r != null) {
					try {
						int indMinus = r.indexOf("-");
						int indSlash = r.indexOf("/");
						String current = r.substring(indMinus + 1, indSlash);
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
							if (!fromVideoDownload) {
								UiApplication.getUiApplication().invokeLater(
										new Runnable() {

											public void run() {
												Status.show("" + percent
														+ "% Completed", 1000);
											}
										});

							} else {
								updatePercentBool = true;
								updatePercent = percent;
							}
						}
					} catch (Exception e) {
						System.out.println("error " + e.getMessage());
					}

				} else {
					break;
				}

				System.out.println(r);
				in = conn.openInputStream();
				int length = -1;
				byte[] readBlock = new byte[256];
				int fileSize = 0;
				/*
				 * File connection
				 */
				FileConnection file = (FileConnection) Connector
						.open(localName);
				if (!file.exists()) {
					try {
						// System.out.println("1 file is open " +
						// file.isOpen());
						// while(file.isOpen()){
						// Thread.sleep(5000);
						// }
						file.create();
						file.setWritable(true);
					} catch (Exception e) {
						System.out.println("exception e" + e.getMessage());
					}
				} else {
					// System.out.println("File already exists...");
					// UiApplication.getUiApplication().invokeLater(new
					// Runnable() {
					//
					// public void run() {
					// Dialog.alert("File already exists...");
					// }
					// });
					try {
						System.out.println("2 file is open " + file.isOpen());
						// while (!file.canWrite()) {
						// try {
						// System.out.println("file cannot write");
						// Thread.sleep(5000);
						// } catch (Exception e) {
						// System.out.println("exception on thread.skeep");
						// }
						// }
						System.out.println("is resume " + isResume);
						if (!isResume) {
							file.delete();
							file.create();
						}
						System.out.println("is resume 2");
						file.setWritable(true);
					} catch (Exception e) {
						System.out.println("exception2 e" + e.getMessage());
					}
				}
				System.out.println("before open output");
				OutputStream out = null;
				try {
					length = in.read(readBlock);
					System.out.println("file size " + file.fileSize());
					System.out.println("skip " +(chunkIndex * chunksize) );
					out = file.openOutputStream(chunkIndex * chunksize);

					while (length != -1) {
						out.write(readBlock, 0, length);
						fileSize += length;
						// Thread.yield(); // Try not to get cut off
						length = in.read(readBlock);
					}
				} catch (IOException ef) {
					// time out
					System.out.println("error IOException" + ef.getMessage());
					in.close();
					conn.close();
					in = null;
					conn = null;
					if (out != null) {
						out.close();
					}
					file.close();
					System.out.println("Finish close everything");
					continue;
				} catch (Exception ef2){
					System.out.println("Ohter exception " + ef2.getMessage());
				}
				System.out.println("before close output");
				if (out != null) {
					out.close();
				}
				System.out.println("file size after close out " +file.fileSize() );
				
				file.close();
				totalSize += fileSize;
				System.out.println("Chunk Downloaded: " + fileSize + " Bytes");

				chunkIndex++; // index (range) increase
				// / save video chunk current
				if (fromVideoDownload) {
					boolean isDelete = true;
					Vector videos2 = Video
							.convertStringToVector(profile.videos);
					for (int i = 0; i < videos2.size(); i++) {
						Video video = (Video) videos2.elementAt(i);
						if (video.getName().equals(fileName)) {
							isDelete = false;
							video.setCurrentChunk(chunkIndex + "");
							System.out.println("video.setCurrentChunk " + chunkIndex);
							if (updatePercentBool) {
								video.setPercent(updatePercent + "");
							}
						}
					}
					profile.videos = Video.convertVectorToString(videos2);
					profile.saveProfile();
					if (isDelete) {
						// maybe cancel
						break;
					}
				}
				// 
				//
				in.close();
				conn.close();
				in = null;
				conn = null;
				/*
				 * Pause to allow connections to close and other Threads to run.
				 */
				Thread.sleep(2000);
			}
			System.out.println("Video " + videoname + " downloaded: "
					+ totalSize + " Bytes");
			final int mySize = totalSize;
			final int fiResponseCode = myResponseCode;

			if (fiResponseCode == 404) {
				final String choices[] = { "Done" };
				final int values[] = { Dialog.OK };
				Dialog dia = new Dialog("The file was removed", choices,
						values, Dialog.OK, Bitmap
								.getPredefinedBitmap(Bitmap.INFORMATION), 0);
				int result = dia.doModal();

			} else {
				if (!fromVideoDownload) {
					UiApplication.getUiApplication().invokeLater(
							new Runnable() {

								public void run() {
									final String choices[] = { "Open", "Done" };
									final int values[] = { Dialog.OK,
											Dialog.CANCEL };
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
								}
							});

				} else {
					ProfileEntry profile = ProfileEntry.getInstance();
					// Update Status of Video to
					Vector myvideos = Video
							.convertStringToVector(profile.videos);
					for (int i = 0; i < myvideos.size(); i++) {
						Video v = (Video) myvideos.elementAt(i);
						if (v.getName().equals(fileName)
								&& v.getPercent().equals("100")) {
							v.setStatus("3");
							String profileVideoString = Video
									.convertVectorToString(myvideos);
							profile.videos = profileVideoString;
							profile.saveProfile();
						}
					}
				}
			}

		} catch (InterruptedException ex) {
			System.out.println("InterruptedException" + ex.getMessage());
			ex.printStackTrace();
		} catch (IOException ex) {
			System.out.println("IOException" + ex.getMessage());
			// TCP receive timed out
			ex.printStackTrace();
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

		}

	}
}