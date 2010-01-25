package com.tssoft.one.webservice;

import java.util.HashMap;

import android.app.Activity;

public class ImageLoaderFactory {
	private static HashMap<Activity,ImageLoader> factory = new HashMap<Activity,ImageLoader>();
	
	public static ImageLoader createImageLoader(Activity act){
		ImageLoader result;
		if(factory.containsKey(act))result = factory.get(act);
		else{
			result = new ImageLoader(act);
			factory.put(act, result);
		}
		return result;
	}
}
