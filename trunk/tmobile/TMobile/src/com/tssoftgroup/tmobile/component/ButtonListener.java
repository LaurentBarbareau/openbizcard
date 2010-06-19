package com.tssoftgroup.tmobile.component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;

import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RadioButtonGroup;

import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.model.Comment;
import com.tssoftgroup.tmobile.model.PicInfo;
import com.tssoftgroup.tmobile.model.TrainingInfo;
import com.tssoftgroup.tmobile.movieexplorer.MovieExplorerDemoScreen;
import com.tssoftgroup.tmobile.screen.DocumentListScreen;
import com.tssoftgroup.tmobile.screen.MCastPlayerScreen;
import com.tssoftgroup.tmobile.screen.MCastScreen;
import com.tssoftgroup.tmobile.screen.TrainingPlayerScreen;
import com.tssoftgroup.tmobile.screen.VideoConnectPlayerScreen;
import com.tssoftgroup.tmobile.screen.WaitScreen;
import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.CrieUtils;

public class ButtonListener implements FieldChangeListener {
	Player player;
	LabelField labelField;
	Vector itemList;
	EditField editField;
	EditField descriptionField;
	RadioButtonGroup rbgrp;
	MyButtonField buttonField;
	int buttonID = 0;
	TimerUpdateThread _timerUpdateThread;
	public int DELAY_BEFORE_KEY = 300;
	PicInfo picInfo;
	TrainingInfo trainInfo;
	String link;
	 Screen screen;
	public ButtonListener(String link, int buttonID) {
		super();
		this.link = link;
		this.buttonID = buttonID;
	}

	public ButtonListener(PicInfo picInfo, int buttonID, Screen screen) {
		super();
		this.picInfo = picInfo;
		this.buttonID = buttonID;
		this.screen = screen;
	}

	public ButtonListener(TrainingInfo trainInfo, int buttonID, Screen screen) {
		super();
		this.trainInfo = trainInfo;
		this.buttonID = buttonID;
		this.screen = screen;
	}

	public ButtonListener(int buttonID) {
		super();
		this.buttonID = buttonID;
	}

	MyPlayer myPlayer;

	public ButtonListener(Player player, int buttonID, MyPlayer m) {
		super();
		this.player = player;
		this.buttonID = buttonID;
		this.myPlayer = m;
	}

	public ButtonListener(Player player, LabelField labelField, int buttonID) {
		super();
		this.player = player;
		this.labelField = labelField;
		this.buttonID = buttonID;
	}

	public ButtonListener(RadioButtonGroup rbgrp, EditField editField,
			int buttonID) {
		super();
		this.rbgrp = rbgrp;
		this.editField = editField;
		this.buttonID = buttonID;
	}

	public ButtonListener(RadioButtonGroup rbgrp, EditField editField,
			EditField descriptionField, int buttonID) {
		super();
		this.rbgrp = rbgrp;
		this.editField = editField;
		this.descriptionField = descriptionField;
		this.buttonID = buttonID;
	}

	public ButtonListener(RadioButtonGroup rbgrp, Vector itemList, int buttonID) {
		super();
		this.rbgrp = rbgrp;
		this.itemList = itemList;
		this.buttonID = buttonID;
	}

	public ButtonListener(RadioButtonGroup rbgrp, int buttonID) {
		super();
		this.rbgrp = rbgrp;
		this.buttonID = buttonID;
	}

	public ButtonListener(Player player, MyButtonField buttonField, int buttonID) {
		super();
		this.player = player;
		this.buttonField = buttonField;
		this.buttonID = buttonID;
	}

	public ButtonListener(EditField editField, PicInfo picinfo, int buttonID) {
		super();
		this.editField = editField;
		this.picInfo = picinfo;
		this.buttonID = buttonID;
	}

	public long lastFull = 0;

	public void fieldChanged(Field field, int context) {
		try {
			switch (buttonID) {
			case 1:// MyVideoMain.Play
				String[] arr = (String[]) itemList.elementAt(rbgrp
						.getSelectedIndex());
				// UiApplication.getUiApplication().pushModalScreen( /*new
				// MyVideoScreen(rbgrp.getSelectedIndex())*/new
				// MCastPlayerScreen("") );
				break;
			case 2:// MyVideoMain.Refresh
				UiApplication.getUiApplication().popScreen(
						UiApplication.getUiApplication().getActiveScreen());
				UiApplication.getUiApplication().pushModalScreen(
						new MCastScreen(editField.getText()));
				break;
			case 3:// HelloWorld.Video
				// Edit by oak
				// UiApplication.getUiApplication().pushModalScreen( new
				// MyVideoMain() );
				UiApplication.getUiApplication().pushScreen(
						WaitScreen.getInstance());
				Engine.getInstance().viewVideoMCast(0, "");
				break;
			case 4:// HelloWorld.Document
				UiApplication.getUiApplication().pushScreen(
						WaitScreen.getInstance());
				Engine.getInstance().getDocument(0, "");
				break;
			case 5:// Press Training at Main Screen
				// UiApplication.getUiApplication().pushScreen(TrainingVideoScreen.getInstance());
				// TrainingListScreen scr = TrainingListScreen.getInstance();
				// Vector vec = new Vector();
				// TrainingInfo info1 = new TrainingInfo("Title 1 ",
				// "Description 1", "URL 1");
				// TrainingInfo info2 = new TrainingInfo("Title 2 ",
				// "Description 2", "URL 2");
				// TrainingInfo info3 = new TrainingInfo("Title 3 ",
				// "Description 3", "URL 3");
				// TrainingInfo info4 = new TrainingInfo("Title 4 ",
				// "Description 4", "URL 4");
				// vec.addElement(info1);
				// vec.addElement(info2);
				// vec.addElement(info3);
				// vec.addElement(info4);
				// scr.setList(vec);
				// UiApplication.getUiApplication().pushScreen(scr);
				UiApplication.getUiApplication().pushScreen(
						WaitScreen.getInstance());
				Engine.getInstance().getTraining(0, "");
				break;
			case 6:// HelloWorld.Contact
				UiApplication.getUiApplication().pushScreen(
						WaitScreen.getInstance());
				Engine.getInstance().getProject(0, "");
				break;
			case 7:// MyVideoScreen.Play
				if (player.getState() == player.STARTED) {
					// player.stop();
					// buttonField.setLabel("Play");
				} else {
					player.start();

					_timerUpdateThread = new TimerUpdateThread();
					_timerUpdateThread.start();
					// buttonField.setLabel("Pause");
				}
				break;
			case 8:// MyVideoScreen.Stop
				player.stop();
				_timerUpdateThread.stop();
				break;
			case 9:// MyVideoScreen.FullScreen
				long now = System.currentTimeMillis();
				if (now - lastFull > 2000) {

					try {
						if (player.getState() == player.STARTED) {
							player.stop();
							VideoControl videoControl = (VideoControl) player
									.getControl("VideoControl");
							videoControl.setDisplayFullScreen(true);
							myPlayer.setFullScreen(true);
							player.start();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					lastFull = now ;
				}
				break;
			case 10:// MyVideoScreen.Comment
				Dialog.alert("CommentButtonListener, MediaTime:"
						+ player.getMediaTime() + ",Duration:"
						+ player.getDuration());
				break;
			case 11:// MyDocumentMain.Download
				/*
				 * String path =
				 * HttpConn.getDocumentPath(((String[])(this.itemList
				 * .elementAt(this.rbgrp.getSelectedIndex())))[0]);
				 * 
				 * if(WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED )
				 * path = path+";interface=wifi";
				 * 
				 * BrowserSession browserSession = Browser.getDefaultSession();
				 * // now launch the URL browserSession.displayPage(path); //
				 * The following line is a work around to the issue found in //
				 * version 4.2.0 browserSession.showBrowser();
				 */

				// Dialog.alert("this.rbgrp.getSelectedIndex():"+this.rbgrp.getSelectedIndex());
				// UiApplication.getUiApplication().invokeLater(new Runnable() {
				// public void run() {
				Const.fileName = ((String[]) (this.itemList
						.elementAt(this.rbgrp.getSelectedIndex())))[0];
				// UiApplication.getUiApplication().pushScreen(new
				// DownloadPopupScreen());
				// }
				// });
				UiApplication.getUiApplication().invokeLater(new Runnable() {
					public void run() {
						// HttpConn.downlodFile(Const.fileName);
						// UiApplication.getUiApplication().popScreen(
						// UiApplication.getUiApplication()
						// .getActiveScreen());
					}
				});
				break;
			case 12:// MyDocumentMain.Sharing
				ShareDocVideoTitleDescriptionDialog.getInstance().myshow();

				break;
			case 13:// MyDocumentMain.Refresh
				UiApplication.getUiApplication().pushModalScreen(
						DocumentListScreen.getInstance());
				break;
			case 14:// MyDocumentShare.Submit
			// if (Const.fileToUpload.trim().equals("")
			// || this.editField.getText().trim().equals(""))
			// Dialog.alert("Please fill all the field");
			// else {
			// /*
			// * Dialog.alert("Sending document... Please wait"); byte
			// * result = HttpConn.uploadFile(Const.fileToUpload,
			// * this.editField.getText()); if(result ==
			// * Const.status_fail) Dialog.alert("Submission failed");
			// * else Dialog.alert("Submission success");
			// */
			//
			// Status.show("Sending document (0%)... Please wait");
			// int noOfFiles = splitFile(Const.fileToUpload);
			// byte result = Const.status_fail;
			// for (int i = 0; i < noOfFiles; i++) {
			// Status.show("Sending document (" + (i + 1) * 100
			// / noOfFiles + "%)... Please wait");
			// result = HttpConn.uploadFile(Const.fileToUpload + "."
			// + i, this.editField.getText(), noOfFiles);
			// deleteFile(Const.fileToUpload + "." + i);
			// if (result == Const.status_fail)
			// break;
			// }
			// if (result == Const.status_fail)
			// Dialog.alert("Submission failed");
			// else {
			// Status.show("Sending document (100%)... Please wait");
			// Dialog.alert("Submission success");
			// }
			// }
				break;
			case 15:// MyDocumentShare.Cancel
				UiApplication.getUiApplication().popScreen(
						UiApplication.getUiApplication().getActiveScreen());
				break;
			case 16:// MyTrainingMain.Download
				Dialog.alert("Downloading!");
				break;
			case 17:// MyTrainingMain.View
				Dialog.alert("Viewing!");
				break;
			case 18:// MyTrainingMain.Refresh
				Dialog.alert("Refreshing!");
				break;
			case 19:// MyDocumentMain.Browse
			// UiApplication.getUiApplication().pushModalScreen(
			// new FileExplorerDemoScreen());
				break;
			case 20:// MyVideoComment.Cancel
				UiApplication.getUiApplication().popScreen(
						UiApplication.getUiApplication().getActiveScreen());
				break;
			case 21:// Click upload a video in video connect
				// UiApplication.getUiApplication().pushModalScreen(
				// MyMovieShare.getInstance());
				BrowseVideoTitleDescriptionDialog.getInstance().myshow();
				break;
			case 22:// MyVideoShare.Browse
				UiApplication.getUiApplication().pushModalScreen(
						new MovieExplorerDemoScreen());
				break;
			case 23:// MyMovieShare.Submit
				System.out.println("MyMovieShare.Submit");
				if (Const.videoToUpload.trim().equals("")
						|| this.editField.getText().trim().equals(""))
					Dialog.alert("Please choose video and fill Title");
				else {
					// Status.show("Sending video (0%)... Please wait");
					// int noOfFiles = splitFile(Const.videoToUpload);
					// byte result = Const.status_fail;
					// for(int i=0; i<noOfFiles; i++){
					// Status.show("Sending video ("+(i+1)*100/noOfFiles+"%)... Please wait");
					// Upload Video
					System.out.println("file name " + (Const.videoToUpload));

					Engine engine = Engine.getInstance();
					engine.picInfo.setLocalFilename("file:///"
							+ Const.videoToUpload);
					System.out.println("2");
					engine.picInfo.setTitle(this.editField.getText());
					System.out.println("3");
					engine.picInfo.setDescription(this.descriptionField
							.getText());
					System.out.println("file name " + (Const.videoToUpload));

					engine.sendVideo();
					CrieUtils.removeCurrent();
					// result = HttpConn.uploadVideo(Const.videoToUpload + "." +
					// i, this.editField.getText(), noOfFiles);
					// deleteFile(Const.videoToUpload + "." + i);
					// if(result == Const.status_fail)
					// break;
					// }
					// if(result == Const.status_fail)
					// Dialog.alert("Submission failed");
					// else{
					// Status.show("Sending video (100%)... Please wait");
					// Dialog.alert("Submission success");
					// }
				}
				break;
			case 24:// HelloWorld.Movie
				UiApplication.getUiApplication().pushScreen(
						WaitScreen.getInstance());
				Engine.getInstance().viewVideoConnect(0, "");
				break;
			case 25:// HelloWorld.Poll
			// UiApplication.getUiApplication().pushModalScreen(
			// new MyPollMain());
				UiApplication.getUiApplication().pushScreen(
						WaitScreen.getInstance());
				Engine.getInstance().getPoll(0, "");
				break;
			case 26:// MyMovieMain.Play
				// String[] arr = (String[])
				// itemList.elementAt(rbgrp.getSelectedIndex());
				// UiApplication.getUiApplication().pushModalScreen( /*new
				// MyVideoScreen(rbgrp.getSelectedIndex())*/new
				// VideoConnectPlayerScreen(rbgrp.getSelectedIndex(),itemList)
				// );
				break;
			case 27:// MyMovieMain.Refresh
				UiApplication.getUiApplication().popScreen(
						UiApplication.getUiApplication().getActiveScreen());
				// UiApplication.getUiApplication().pushModalScreen(
				// VideoConnectScreen.getInstance(editField.getText()));
				break;
			// Capture Video in Video Connect Screen
			case 28:// MyMovieShare.Record
				VideoTitleDescriptionDialog.getInstance().myshow();
				break;
			case 29:// MyPollMain.Back
				UiApplication.getUiApplication().popScreen(
						UiApplication.getUiApplication().getActiveScreen());
				break;
			case 30:// MyPollMain.Select
				UiApplication.getUiApplication().popScreen(
						UiApplication.getUiApplication().getActiveScreen());
				break;
			case 31:// Video Connect Download button
				VideoDownloadDialog dialog2=  new VideoDownloadDialog(screen);
				dialog2.myshow();
				break;
			case 311:// Video Connect Play button
				UiApplication.getUiApplication().pushScreen(
						new VideoConnectPlayerScreen(picInfo));
				break;
			case 312:// Video Connect Downloadings button
				Dialog.alert("You are downloading this video. Please wait ....");
				break;
				
			case 313:// Video Connect Downloadings button
				Dialog.alert("It has been scheduled to download.");
				break;
			case 32:// MCast Detail Download Button
//				UiApplication.getUiApplication().pushScreen(
//						new MCastPlayerScreen(picInfo));
				VideoDownloadDialog dialog=  new VideoDownloadDialog(screen);
				dialog.myshow();
				break;
			case 321:// MCast Detail Play Button
				UiApplication.getUiApplication().pushScreen(
						new MCastPlayerScreen(picInfo));
				break;
			case 322:// MCast Detail Downloading
				Dialog.alert("You are downloading this video. Please wait ....");
				break;
			case 323:// MCast Detail Downloading
				Dialog.alert("It has been scheduled to download.");
				break;
			case 33:// MyPollMain.Select
				Engine.getInstance().sendMoreInfo(link);
				break;
			case 34:// MyPollMain.Select

				// String[] questions = { "a. Oak", "b. O", "c. A", "d. OK" };
				// TrainingQuestionScreen screen = TrainingQuestionScreen
				// .getInstance();
				// screen.setQuestion("Who are you ?");
				// screen.setRadio(questions);
				// UiApplication.getUiApplication().pushScreen(screen);
				UiApplication.getUiApplication().pushScreen(
						WaitScreen.getInstance());
				Engine.getInstance().getTrainingAns(trainInfo);

				break;
			case 35:// MyPollMain.Select
				Engine.commnetPicInfo = picInfo;
				if (editField.getText().equals("")) {
					Dialog.alert("Please input comment");
					return;
				}
				Comment comment = new Comment();
				comment.setComment(editField.getText());
				comment.setTime("a moment ago");
				comment.setUser(ProfileEntry.getInstance().name);
				Engine.comment = comment;
				picInfo.comments.addElement(comment);
				Engine.getInstance().addComment(picInfo.getId(), Engine.userId,
						editField.getText(), picInfo.isMCast());

				break;
			case 36:
				CrieUtils.browserURL(link);
				break;
			case 42:// MCast Detail Download Button
//				UiApplication.getUiApplication().pushScreen(
//						new MCastPlayerScreen(picInfo));
				
				VideoDownloadDialog dialog3=  new VideoDownloadDialog(screen);
				dialog3.myshow();
				break;
			case 421:// MCast Detail Play Button

				TrainingPlayerScreen scr = new TrainingPlayerScreen(trainInfo);
				UiApplication.getUiApplication().pushScreen(scr);
			
				break;
			case 422:// MCast Detail Downloading
				Dialog.alert("You are downloading this video. Please wait ....");
				break;
			case 423:// MCast Detail Downloading
				Dialog.alert("It has been scheduled to download.");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int splitFile(String _file) throws Exception {

		FileConnection file = null;
		InputStream input = null;
		OutputStream output = null;

		if ((file = (FileConnection) Connector.open("file:///" + _file,
				Connector.READ)) != null) {
			if ((input = file.openInputStream()) != null) {
				byte[] readBlock = new byte[1048576];
				int chunkFileId = 0;
				int length = -1;
				while ((length = input.read(readBlock)) != -1) {
					String chunkFileName = _file + "." + chunkFileId++;
					file = (FileConnection) Connector.open("file:///"
							+ chunkFileName, Connector.READ_WRITE);
					if (file == null || !file.exists()) {
						file.create();
					}
					file.setWritable(true);
					output = file.openOutputStream();
					output.write(readBlock, 0, length);
					output.flush();
					output.close();
					System.out.println("Wrote Chunk file " + chunkFileName
							+ " of " + length + " Bytes");
				}
				System.out.println("Completed Splitting " + _file);
				return chunkFileId;
			}
		}
		return 0;
	}

	public void deleteFile(String _file) throws Exception {
		FileConnection file = null;

		if ((file = (FileConnection) Connector.open("file:///" + _file,
				Connector.READ_WRITE)) != null) {
			if (file != null && file.exists()) {
				file.delete();
			}
		}
	}

	private class TimerUpdateThread extends Thread {
		private boolean _threadCanRun;

		public void run() {
			_threadCanRun = true;
			while (_threadCanRun) {
				UiApplication.getUiApplication().invokeLater(new Runnable() {
					public void run() {
						if (player != null
								&& player.getState() != Player.CLOSED) {
							Date date = new Date(player.getMediaTime() / 1000);
							SimpleDateFormat df = new SimpleDateFormat("mm:ss");
							labelField.setText(df.format(date));
						} else {
							_threadCanRun = false;
						}
					}
				});

				try {
					Thread.sleep(500L);
				} catch (InterruptedException e) {
				}
			}
		}

		public void stop() {
			_threadCanRun = false;
		}
	}
}