package com.tssoftgroup.tmobile.screen;

/**
 *
 * HelloWorld.java
 * The sentinal sample!
 *
 * Copyright © 1998-2008 Research In Motion Ltd.
 *
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

import java.util.Date;

import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;

import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

import com.tssoftgroup.tmobile.component.ButtonListener;
import com.tssoftgroup.tmobile.component.CrieLabelField;
import com.tssoftgroup.tmobile.component.MyButtonField;
import com.tssoftgroup.tmobile.component.MyPlayer;
import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.component.engine.HttpUtilUploadThread;
import com.tssoftgroup.tmobile.model.TrainingInfo;
import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.Scale;

public class TrainingPlayerScreen extends MainScreen implements
		FieldChangeListener, PlayerListener, MyPlayer {
	private MainItem _mainMenuItem = new MainItem();
	private MovieItem _videoItem = new MovieItem();
	private CrieLabelField _currentTime = null;
	private CrieLabelField _duration = null;
	private LabelField _volumeDisplay = null;
	private Player player = null;
	private TimerUpdateThread _timerUpdateThread = null;
	private String videoPath = "";
	private VideoControl videoControl = null;
	private VolumeControl volumeControl = null;
	Img imgStock = Img.getInstance();
	TrainingInfo traininfo;
	boolean isFullScreen = false;

	public void setFullScreen(boolean bool) {
		isFullScreen = bool;
	}
	MyButtonField fullButton ;
	public TrainingPlayerScreen(TrainingInfo trainInfo) {
		this.traininfo = trainInfo;
		XYEdges edge = new XYEdges(2, 0, 2, 0);

		Bitmap img = imgStock.getHeader();
		BitmapField bf = new BitmapField(img, BitmapField.FIELD_HCENTER
				| BitmapField.USE_ALL_WIDTH);
		add(bf);

		edge = new XYEdges(5, 25, 2, 10);
		HorizontalFieldManager buttonHorizontalManager = new HorizontalFieldManager();

		edge = new XYEdges(2, 0, 0, 0);
		// HorizontalFieldManager videoHorizontalManager = new
		// HorizontalFieldManager(
		// HorizontalFieldManager.FIELD_HCENTER
		// | HorizontalFieldManager.USE_ALL_WIDTH);

		try {
			/*
             *
             */
			// boolean playerSupported = true;
			int volumeLevel;
			Field videoField;
			this.videoPath = trainInfo.getVideoUrl();
			player = javax.microedition.media.Manager.createPlayer(trainInfo
					.getVideoUrl()
					+ HttpUtilUploadThread.getConnectionSuffix());
			// }

			// if the player has been created; checking method for
			// initialization above
			if (player != null /* && player.getState() != Player.CLOSED */) {
				player.addPlayerListener(this);
				player.realize();
				player.prefetch();
				if ((videoControl = (VideoControl) player
						.getControl("VideoControl")) != null) {
					// Dialog.alert("videoControl not null");
					videoField = (Field) videoControl.initDisplayMode(
							VideoControl.USE_GUI_PRIMITIVE,
							"net.rim.device.api.ui.Field");
					// videoControl.setDisplaySize(300 * Display.getWidth() /
					// 480,
					// 200 * Display.getHeight() / 360);// edge = new
					videoControl.setDisplaySize(Const.VIDEO_WIDTH,
							Const.VIDEO_HEIGHT);// edge = new
					// videoControl.setDisplaySize(videoControl.getSourceWidth(),
					// videoControl.getSourceHeight());// edge = new
					// XYEdges(2, 0,
					// 0, 97);
					edge = new XYEdges(2, 0, 0,
							(Display.getWidth() - Const.VIDEO_WIDTH) / 2);
					// videoField.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
					videoField.setMargin(edge);
					_currentTime = new CrieLabelField("00:00", 0x00,
							Scale.VIDEO_PLAYER_TIME_HEIGHT, Field.FOCUSABLE);
					edge = new XYEdges(260 * Display.getWidth() / 480, 5, 0,
							15 * Display.getWidth() / 480);
					// _currentTime.setMargin(edge);
					_duration = new CrieLabelField("00:00", 0x00,
							Scale.VIDEO_PLAYER_TIME_HEIGHT, Field.FOCUSABLE);
					edge = new XYEdges(260 * Display.getWidth() / 480, 0, 0,
							5 * Display.getWidth() / 480);
					// _duration.setMargin(edge);
					add(videoField);
					videoControl.setVisible(true);
				} else {
				}
				if ((volumeControl = (VolumeControl) player
						.getControl("VolumeControl")) != null) {
					volumeControl.setMute(false);
					volumeControl.setLevel(Const.DEFAULT_VOLUMN);
					volumeLevel = volumeControl.getLevel();
				}

				// _volumeDisplay = new LabelField ("Volume : " +
				// _volumeControl.getLevel());
				if (player.getState() != player.STARTED) {
					player.start();
					_timerUpdateThread = new TimerUpdateThread();
					_timerUpdateThread.start();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
              *
              */
		// CustomButtonField playButton = new
		MyButtonField stopButton = null;
		// if(player.getState()==player.STARTED){
		// playButton = new MyButtonField("Pause",ButtonField.ELLIPSIS);
		// }else{
		stopButton = new MyButtonField("Stop", ButtonField.ELLIPSIS, true);
		// }
		// playButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
		stopButton.setChangeListener(this);

		buttonHorizontalManager.add(stopButton);

		// edge = new XYEdges(206, 5, 6, 24);
		// CustomButtonField fullButton = new
		fullButton = new MyButtonField("Full Screen",
				ButtonField.ELLIPSIS, true);
		// fullButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
		fullButton.setChangeListener(new ButtonListener(player, 9, this));
		buttonHorizontalManager.add(fullButton);
		fullButton.setFocusable(false);
		// / Play button
		MyButtonField nextButton = new MyButtonField("Next",
				ButtonField.ELLIPSIS, true);
		nextButton.setChangeListener(this);
		buttonHorizontalManager.add(nextButton);

		// edge = new XYEdges(206, 5, 6, 5);
		// CustomButtonField stopButton = new
		// MyButtonField infoButton = new MyButtonField("More Info",
		// ButtonField.ELLIPSIS);
		// stopButton.setBorder(BorderFactory.createSimpleBorder(edge,Border.STYLE_TRANSPARENT));
		// infoButton.setChangeListener(this);
		// mainHorizontalManager.add(infoButton);

		// edge = new XYEdges(206, 24, 6, 24);
		// CustomButtonField commentButton = new

		edge = new XYEdges(Const.DURATION_MARGIN_TOP, 0, 0, (Display.getWidth()
				- _currentTime.getPreferredWidth() - 3) / 2);
		HorizontalFieldManager timeManager = new HorizontalFieldManager();
		timeManager.add(_currentTime);
		timeManager.add(new CrieLabelField("/", 0x00,
				Scale.VIDEO_PLAYER_TIME_HEIGHT, Field.NON_FOCUSABLE));
		timeManager.add(_duration);
		timeManager.setMargin(edge);
		edge = new XYEdges(5, 0, 0,
				(Display.getWidth() - buttonHorizontalManager
						.getPreferredWidth()) / 2);
		buttonHorizontalManager.setMargin(edge);
		add(timeManager);
		add(buttonHorizontalManager);
		// bf = new BitmapField(img, Field.FIELD_BOTTOM | Field.USE_ALL_HEIGHT);
		// add(bf);

		addMenuItem(_mainMenuItem);
//		addMenuItem(_videoItem);
		// addMenuItem( _exitFullItem );

	}

	protected void paintBackground(Graphics g) {
		/*
		 * if(bkgrndBmp != null){ g.rop(Graphics.ROP_SRC_ALPHA_GLOBALALPHA,
		 * (this.getWidth() / 2) - (bkgrndBmp .getWidth() /2), (this.getHeight()
		 * / 2) - (bkgrndBmp .getHeight() /2), bkgrndBmp .getWidth(), bkgrndBmp
		 * .getHeight(), bkgrndBmp ,0,0); }
		 */
		g.setBackgroundColor(Const.BG_COLOR);

		// Clears the entire graphic area to the current background
		g.clear();
		g.fillRect(0, 0, Display.getWidth(), Display.getHeight());
	}
	boolean resumeFromFull = false;
	protected boolean keyDown(int arg0, int arg1) {
		// TODO Auto-generated method stub

		return super.keyDown(arg0, arg1);
	}

	public boolean keyChar(char c, int status, int time) {
		boolean bool = false;
		try {
			// player.stop();
			// VideoControl videoControl = (VideoControl)
			// player.getControl("VideoControl");
			if (player != null && player.getState() == player.STARTED
					&& isFullScreen) {
				bool = true;
				isFullScreen = false;
				videoControl.setDisplayFullScreen(false);
			}
			// player.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch (c) {
		case Characters.ENTER:
		case Characters.DELETE:
//		case Characters.BACKSPACE:
//			return true;
		case Characters.ESCAPE:
			if (!bool) {

				try {
					player.stop();
					// player.deallocate();
					// player.close();
					_timerUpdateThread.stop();
				} catch (Exception e) {
					System.out.println("" + e.toString());
				}
				UiApplication.getUiApplication().popScreen(
						UiApplication.getUiApplication().getActiveScreen());
			}

			return true;
		default:
			return super.keyChar(c, status, time);
		}
	}

	protected boolean keyControl(char c, int status, int time) {
		// Capture volume control key press and adjust volume accordingly.
		switch (c) {
		case Characters.CONTROL_VOLUME_DOWN:
			volumeControl.setLevel(volumeControl.getLevel() - 10);
			return true;

		case Characters.CONTROL_VOLUME_UP:
			volumeControl.setLevel(volumeControl.getLevel() + 10);
			return true;
		}

		return super.keyControl(c, status, time);
	}

	private final class MainItem extends MenuItem {
		/**
		 * Constructor.
		 */
		private MainItem() {
			super("Main Menu", 100, 1);
		}

		/**
		 * Attempts to save the screen's data to its associated memo. If
		 * successful, the edit screen is popped from the display stack.
		 */
		public void run() {
			boolean bool = false;
			try {
				// player.stop();
				// VideoControl videoControl = (VideoControl)
				// player.getControl("VideoControl");
				if (player != null && player.getState() == player.STARTED && isFullScreen) {
					bool = true;
					isFullScreen = false;
					videoControl.setDisplayFullScreen(false);
				}
				// player.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(!bool){
			try {
				player.stop();
				_timerUpdateThread.stop();
			} catch (Exception e) {
				System.out.println("" + e.toString());
			}
			UiApplication.getUiApplication().popScreen(
					TrainingPlayerScreen.this);
			UiApplication.getUiApplication().popScreen(
					UiApplication.getUiApplication().getActiveScreen());
			}
		}
	}

	private final class MovieItem extends MenuItem {
		/**
		 * Constructor.
		 */
		private MovieItem() {
			super("Video Connect", 100, 1);
		}

		/**
		 * Attempts to save the screen's data to its associated memo. If
		 * successful, the edit screen is popped from the display stack.
		 */
		public void run() {
			try {
				player.stop();
				_timerUpdateThread.stop();
			} catch (Exception e) {
				System.out.println("" + e.toString());
			}
			UiApplication.getUiApplication().popScreen(
					TrainingPlayerScreen.this);
			// UiApplication.getUiApplication().popScreen(
			// UiApplication.getUiApplication().getActiveScreen() );
		}
	}

	public void fieldChanged(Field field, int context) {
		MyButtonField btnField = (MyButtonField) field;
		if (btnField.getLabel().equals("Start")) {
			try {
				// Start/resume the media player.
				player.start();

				_timerUpdateThread = new TimerUpdateThread();
				_timerUpdateThread.start();
				btnField.setLabel("Stop");
			} catch (MediaException pe) {
				System.out.println(pe.toString());
			}
		} else if (btnField.getLabel().equals("Stop")) {
			try {
				// Stop/pause the media player.
				player.stop();
				_timerUpdateThread.stop();
				btnField.setLabel("Start");
			} catch (MediaException pe) {
				System.out.println(pe.toString());
			}
		}else if (btnField.getLabel().equals("Next")) {
			try {
				// Stop/pause the media player.
				player.stop();
				_timerUpdateThread.stop();
				UiApplication.getUiApplication().pushScreen(
						WaitScreen.getInstance());
				Engine.getInstance().getTrainingAns(traininfo);
			} catch (MediaException pe) {
				System.out.println(pe.toString());
			}
		}
	}

	public void playerUpdate(Player _player, final String event,
			Object eventData) {
		new Thread(new Runnable() {

			public void run() {
				UiApplication.getUiApplication().invokeLater(new Runnable() {
					public void run() {
						try{
						if (event.equals(VOLUME_CHANGED)) {
							// _volumeDisplay.setText("Volume : " +
							// volumeControl.getLevel());
						} else if (event.equals(STARTED)) {
							fullButton.setFocusable(true);
							try {
								if (!isFullScreen) {
									
									videoControl.setDisplaySize(
											Const.VIDEO_WIDTH, videoControl
													.getSourceHeight()
													* Const.VIDEO_WIDTH
													/ videoControl
															.getSourceWidth());
								}
							} catch (MediaException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}// edge = new
							Date date = new Date(player.getMediaTime() / 1000);
							SimpleDateFormat df = new SimpleDateFormat("mm:ss");
							_currentTime.setText(df.format(date));
							// _controlButton.setLabel("Pause");
						} else if (event.equals(STOPPED)) {
							fullButton.setFocusable(false);
							Date date = new Date(player.getMediaTime() / 1000);
							SimpleDateFormat df = new SimpleDateFormat("mm:ss");
							_currentTime.setText(df.format(date));
							// _controlButton.setLabel("Start");
						} else if (event.equals(DURATION_UPDATED)) {
							Date date = new Date(player.getDuration() / 1000);
							SimpleDateFormat df = new SimpleDateFormat("mm:ss");
							_duration.setText(df.format(date));
						} else if (event.equals(END_OF_MEDIA)) {
							// _controlButton.setLabel("Start");try{
							try {
								if (player != null
										&& player.getState() == player.STARTED) {
									fullButton.setFocusable(false);
									videoControl.setDisplayFullScreen(false);
									isFullScreen = false;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						//cat
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		}).start();

	}

	private class TimerUpdateThread extends Thread {
		private boolean _threadCanRun;

		public void run() {
			_threadCanRun = true;
			while (_threadCanRun) {
				UiApplication.getUiApplication().invokeLater(new Runnable() {
					public void run() {
						if (player != null
								&& player.getState() == Player.STARTED) {
							Date date = new Date(player.getMediaTime() / 1000);
							SimpleDateFormat df = new SimpleDateFormat("mm:ss");
							_currentTime.setText(df.format(date));
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
