package com.tssoftgroup.tmobile.component.engine;

import java.util.Vector;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.DownloadCombiner;

public class HttpDownloadVideoThread extends Thread {

	Vector conbiners = new Vector();
	public boolean mTrucking = true;

	HTTPHandler handler;
	public String currentDownloadName = "";
	public DownloadCombiner current = null;
	public HttpDownloadVideoThread() {
	}

	public void setTask(DownloadCombiner combiner) {
		conbiners.addElement(combiner);
	}

	public synchronized void run() {
		while (mTrucking) {
			if (conbiners.size() > 0) {
				// try {//TODO
				// Thread.sleep(1000);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				// boolean excep = false;
				DownloadCombiner com = (DownloadCombiner) conbiners
						.elementAt(0);
				current = com;
				currentDownloadName = com.fileName;
				conbiners.removeElementAt(0);
				try {
					com.run();
				}
				// catch (SecurityException e) {
				// // excep = true;
				// }
				catch (Exception e) {
					e.printStackTrace();
				}
				currentDownloadName = "";

			} else {
				try {
					waiting = true;
					wait();
				} catch (Exception e) {
					// System.out.println("ConnectURL : " + e + " " +
					// e.getMessage());
				}
			} // if
		}
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
