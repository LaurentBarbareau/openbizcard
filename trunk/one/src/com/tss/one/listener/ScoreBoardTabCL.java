package com.tss.one.listener;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.tss.one.MainList;
import com.tss.one.R;
import com.tss.one.ScoreBoard;
import com.tss.one.adapter.ScoreBoardAdapter;
import com.tssoft.one.utils.ElementState;

public class ScoreBoardTabCL extends TabClickListener{
	
	private ScoreBoard sb;
	
	public ScoreBoardTabCL(HashMap<View, ElementState> e,Activity a) {
		super(e,a);
		sb=(ScoreBoard)a;
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		int thisViewId = v.getId();
	
		if(thisViewId ==R.id.score_board_tab3){
			sb.setCurrentTab(ScoreBoard.TODAY_GAME_TAB);
			((ScoreBoardAdapter)sb.scoreBoardAdapter).clearItem();
			sb.scoreBoardList.clear();
			sb.setScoreBoard();
			}
		if(thisViewId ==R.id.score_board_tab2){	
				//CHANGE TO APPROPRIATE ADAPTER
			sb.setCurrentTab(ScoreBoard.LIVE_GAME_TAB);
			((ScoreBoardAdapter)sb.scoreBoardAdapter).clearItem();
			sb.scoreBoardList.clear();
			sb.setLiveGame();
		}
		if(thisViewId ==R.id.score_board_tab1){	
			//CHANGE TO APPROPRIATE ADAPTER
			sb.getValueFromSpinner(v);
			sb.setCurrentTab(ScoreBoard.LEAGUE_TAB);
			((ScoreBoardAdapter)sb.scoreBoardAdapter).clearItem();
			sb.scoreBoardList.clear();
			sb.setLeagueGame();
	}
	}
}
