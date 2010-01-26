package com.tssoft.one.webservice;

import java.util.HashMap;

import android.app.Activity;

public class ImageLoaderStringFactory {
	private static HashMap<String,ImageLoader> factory = new HashMap<String,ImageLoader>();
	
	public static ImageLoader createImageLoader(Activity act , String str){
		ImageLoader result;
		if(factory.containsKey(str))result = factory.get(str);
		else{
			result = new ImageLoader(act);
			factory.put(str, result);
		}
		return result;
	}
	public static void clear( String str) {
		if (factory.containsKey(str)) {
			factory.get(str).isRunning = false;
			factory.remove(str);
		}
	}
}
