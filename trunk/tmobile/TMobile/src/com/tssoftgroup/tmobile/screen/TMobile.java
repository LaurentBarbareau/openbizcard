package com.tssoftgroup.tmobile.screen;

import java.util.Timer;
import java.util.TimerTask;

import net.rim.blackberry.api.homescreen.HomeScreen;
import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;

import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.ScheduleRunable;

/**
 * Create a new screen that extends MainScreen, which provides default standard
 * behavior for BlackBerry applications.
 */
/*
 * BlackBerry applications that provide a user interface must extend
 * UiApplication.
 */
public class TMobile extends UiApplication {
	Img img = Img.getInstance();

	/**
	 * Entry point for application.
	 */
	public static void main(String[] args) {
		Bitmap icon = Bitmap.getBitmapResource("icon.png");
		Bitmap iconover = Bitmap.getBitmapResource("icon-focus.png");
		HomeScreen.setRolloverIcon(iconover);
		HomeScreen.updateIcon(icon);
		TMobile theApp = new TMobile();

		// To make the application enter the event thread and start
		// processing messages,
		// we invoke the enterEventDispatcher() method.
		theApp.enterEventDispatcher();
	}

	static class BacklightTimeout extends TimerTask {

		public void run() {
			Backlight.setTimeout(255);
			Backlight.enable(true, 255);
		}

	}

	public static ScheduleRunable downloadThread = null;

	public static void start() {
		try {
			BacklightTimeout timeout = new BacklightTimeout();
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(timeout, 0, 60000);

		} catch (Exception e) {

		}
		ProfileEntry profile = ProfileEntry.getInstance();
		try {
			profile.loadProfile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		downloadThread = new ScheduleRunable();
		new Thread(downloadThread).start();

		if (!Engine.isLogin) {
			LoginScreen app = new LoginScreen(false);

		} else {
			// Create a new instance of the application.
			UiApplication.getUiApplication().pushScreen(
					MyMainScreen.getInstance());
			// UserDetails app = new UserDetails();
			// app.enterEventDispatcher();
		}
		// Push the main screen instance onto the UI stack for rendering.

	}

	/**
	 * <p>
	 * The default constructor. Creates all of the RIM UI components and pushes
	 * the application's root screen onto the UI stack.
	 */
	private TMobile() {

		ApplicationPermissionsManager apm = ApplicationPermissionsManager
				.getInstance();
		ApplicationPermissions original = apm.getApplicationPermissions();
		if (original.getPermission(ApplicationPermissions.PERMISSION_MEDIA) == ApplicationPermissions.VALUE_ALLOW
				&& original
						.getPermission(ApplicationPermissions.PERMISSION_FILE_API) == ApplicationPermissions.VALUE_ALLOW

				&& original
						.getPermission(ApplicationPermissions.PERMISSION_EXTERNAL_CONNECTIONS) == ApplicationPermissions.VALUE_ALLOW
				&& original
						.getPermission(ApplicationPermissions.PERMISSION_INTERNAL_CONNECTIONS) == ApplicationPermissions.VALUE_ALLOW
				&& original
						.getPermission(ApplicationPermissions.PERMISSION_EVENT_INJECTOR) == ApplicationPermissions.VALUE_ALLOW
				&& original
						.getPermission(ApplicationPermissions.PERMISSION_IDLE_TIMER) == ApplicationPermissions.VALUE_ALLOW) {
			start();
		} else {
			ApplicationPermissions permRequest = new ApplicationPermissions();

			permRequest.addPermission(ApplicationPermissions.PERMISSION_MEDIA);
			permRequest
					.addPermission(ApplicationPermissions.PERMISSION_FILE_API);
			permRequest
					.addPermission(ApplicationPermissions.PERMISSION_EXTERNAL_CONNECTIONS);
			permRequest
					.addPermission(ApplicationPermissions.PERMISSION_INTERNAL_CONNECTIONS);
			permRequest
					.addPermission(ApplicationPermissions.PERMISSION_EVENT_INJECTOR);
			permRequest
					.addPermission(ApplicationPermissions.PERMISSION_IDLE_TIMER);
			boolean acceptance = ApplicationPermissionsManager.getInstance()
					.invokePermissionsRequest(permRequest);

			if (acceptance) {
				start();
			} else {
				System.exit(0);
			}
		}

	}

	// public void deactivate() {
	// TODO Auto-generated method stub
	// super.deactivate();
	// }
}
