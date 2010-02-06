package com.tss.one.listener;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.tss.one.MyTeamsTab;
import com.tss.one.R;
import com.tssoft.one.utils.ElementState;

public class MyTeamTabClickListener implements OnClickListener {
	private HashMap<View, ElementState> elements;
	private MyTeamsTab act;
	
	private int pabIndex;

	public MyTeamTabClickListener(HashMap<View, ElementState> e, MyTeamsTab act) {
		this.act = act;
		elements = e;
	}

	public void onClick(View v) {
		if (elements.containsKey(v)) {

			ElementState es = elements.get(v);
			ImageButton tab = (ImageButton) v;
			if (!es.isFocused()) {
//				tab.setBackgroundResource(es.getFocusedIcon());
//				es.setFocused(true);
//				unFocusOtherTab(v);
				Log.e("tab", tab.getId() + "");
				// Right tab get user article
				if (tab.getId() == R.id.my_teams_tab1) {
					pabIndex = 1;
					if(act.getTeamList() == null | act.getTeamList().size() == 0 ){
						act.displayNoTeamDialog();
					}
					else{
						tab.setBackgroundResource(es.getFocusedIcon());
						es.setFocused(true);
						unFocusOtherTab(v);
						act.setGameScore();
					}
				}
				// Left Tab get score
				else if (tab.getId() == R.id.my_teams_tab2) {
					pabIndex = 2;
					tab.setBackgroundResource(es.getFocusedIcon());
					es.setFocused(true);
					unFocusOtherTab(v);
					act.setArticles();
				}
			}
		}
	}
	

	public void unFocusOtherTab(View stillFocus) {
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
	
	public int getTabIndex(){
		return pabIndex;
	}

}
