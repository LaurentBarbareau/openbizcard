package com.tss.one.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.tss.one.MyTeamsTab;
import com.tss.one.R;
import com.tssoft.one.utils.ElementState;
import com.tssoft.one.webservice.ImageLoaderFactory;
import com.tssoft.one.webservice.WebServiceReader;
import com.tssoft.one.webservice.WebServiceReaderMyTeam;
import com.tssoft.one.webservice.model.ArticleBySubject;

public class MyTeamTabClickListener implements OnClickListener {
	private HashMap<View, ElementState> elements;
	private MyTeamsTab act;
    
	public MyTeamTabClickListener(HashMap<View, ElementState> e, MyTeamsTab act) {
		this.act = act;
		elements = e;
	}

	public void onClick(View v) {
		if (elements.containsKey(v)) {
			
			ElementState es = elements.get(v);
			ImageButton tab = (ImageButton) v;
			if (!es.isFocused()) {
				tab.setBackgroundResource(es.getFocusedIcon());
				es.setFocused(true);
				unFocusOtherTab(v);
				Log.e("tab", tab.getId() + "");
				// Right tab get user article 
				if (tab.getId() == R.id.my_teams_tab1) {
					
					act.setGameScore();
					
				} 
				// Left Tab get score
				else if (tab.getId() == R.id.my_teams_tab2) {
					act.setArticles();
				}
			}
			
		}
	}

	private void unFocusOtherTab(View stillFocus) {
		Set<Entry<View, ElementState>> s = elements.entrySet();
		for (Entry<View, ElementState> entry : s) {
			View tab = entry.getKey();
			ElementState tabProp = entry.getValue();
			if (tab != stillFocus) {
				ImageButton tabImg = (ImageButton) tab;
				tabImg.setBackgroundResource(tabProp.getNormalIcon());
				tabProp.setFocused(false);
			}
		}
	}
	
}
