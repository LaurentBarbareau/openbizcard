package com.tssoftgroup.tmobile.component.engine;

import java.util.Vector;

import com.tssoftgroup.tmobile.utils.Const;


public class HttpThread extends Thread {

	Vector bodies = new Vector();
	Vector urls = new Vector();
	Vector modes = new Vector();
	boolean mTrucking = true;
	
	HTTPHandler handler ;
	public HttpThread(HTTPHandler handler) {
		this.handler = handler;
	}

	public void setTask(String url, String body, int mode) {
		urls.addElement(url);
		bodies.addElement(body);
		modes.addElement(new Integer(mode));

	}

	public synchronized void run() {
		while (mTrucking) {
			if (urls.size() > 0) {
//				try {//TODO
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				boolean excep = false;
				String url = (String) urls.elementAt(0);
				String body = (String) bodies.elementAt(0);
				int mode = ((Integer) modes.elementAt(0)).intValue();
				System.out.println("-----");
				System.out.println("url "+ url);
				System.out.println("body "+ body);
				
				urls.removeElementAt(0);
				bodies.removeElementAt(0);
				modes.removeElementAt(0);
				String res = "";
				try {
					res = getContent(url, body, true);
					System.out.println("myresponse " + res);
				}
//				catch (SecurityException e) {
////					excep = true;
//				}
				catch (Exception e) {
					e.printStackTrace();
					for (int i = 0; i < Const.NUM_RETRY_HTTP; i++) {
						try {
							res = getContent(url, body, true);
							
							System.out.println("myresponse " + res);
						}catch (Exception ex) {
							ex.printStackTrace();
//							System.out.println("\n---------\n error httpcon.close() "+e.getMessage()+e.toString()+"\n");
						}
						break;
					}
//					System.out.println("\n---------\n error httpcon.close() "+e.getMessage()+e.toString()+"\n");
				}
				try {
					handler.finishCallback(res, mode);
				}
					catch (Exception ex) {
						ex.printStackTrace();
//						System.out.println("\n---------\n error httpcon.close() "+e.getMessage()+e.toString()+"\n");
					}
				
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
//		System.out.println("Start Go");
		if(waiting){
//			System.out.println("In Go");
			mynotify();
			waiting = false;
		}
//		System.out.println("End Go");
	}
	private synchronized void mynotify(){
		notify();
	}

	public String getContent(String url, String body, boolean cancelable) throws Exception {
		String response = "";
		try {

			HttpAbstractUtil.setBasicAuthentication("", "");
			response = HttpUtilUploadThread.doGet(url, body, cancelable);
			// response = HttpUtil.doPost(url, body);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			// throw new TwitterException("post " + ex.toString());
		}

		return response;
	}
}
