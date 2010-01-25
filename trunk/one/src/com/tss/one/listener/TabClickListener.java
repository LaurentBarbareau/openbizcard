package com.tss.one.listener;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.tssoft.one.utils.ElementState;

public class TabClickListener implements OnClickListener{
	protected HashMap<View,ElementState> elements;
	protected Activity act;
	
	public TabClickListener(HashMap<View,ElementState> e,  Activity a){
		elements = e;
		act = a;
	}
	
	public void onClick(View v) {
		if(elements.containsKey(v)){
			ElementState es = elements.get(v);
			ImageButton tab = (ImageButton) v;
			if(!es.isFocused()){
				tab.setBackgroundResource(es.getFocusedIcon());
				es.setFocused(true);
				unFocusOtherTab(v);
			}
		}
	}
	
	private void unFocusOtherTab(View stillFocus){
		Set<Entry<View,ElementState>> s = elements.entrySet();
		for(Entry<View,ElementState> entry : s){
			View tab = entry.getKey();
			ElementState tabProp = entry.getValue();
			if(tab!=stillFocus){
				ImageButton tabImg = (ImageButton) tab;
				tabImg.setBackgroundResource(tabProp.getNormalIcon());
				tabProp.setFocused(false);
			}
		}
	}

}
