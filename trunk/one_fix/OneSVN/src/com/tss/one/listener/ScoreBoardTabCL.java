package com.tss.one.listener;

import java.util.HashMap;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.tss.one.R;
import com.tss.one.ScoreBoard;
import com.tss.one.adapter.ScoreBoardAdapter;
import com.tssoft.one.utils.ElementState;

public class ScoreBoardTabCL extends TabClickListener {
	public int tabId = 3;
	private ScoreBoard sb;

	public ScoreBoardTabCL(HashMap<View, ElementState> e, Activity a) {
		super(e, a);
		sb = (ScoreBoard) a;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		int thisViewId = v.getId();

		if (thisViewId == R.id.score_board_tab3) {
			tabId = 3;
			sb.setCurrentTab(ScoreBoard.TODAY_GAME_TAB);
			((ScoreBoardAdapter) sb.scoreBoardAdapter).clearItem();
			sb.findViewById(R.id.includeToday).setVisibility(View.VISIBLE);
			
			ListView lView = (ListView)sb.getListView();
			lView.getLayoutParams().height = 224;
			FrameLayout fLayout = (FrameLayout)sb.findViewById(R.id.score_list_frame_layout);
			fLayout.getLayoutParams().height = 224;
			fLayout.requestLayout();
			
			sb.scoreBoardList.clear();
			sb.setScoreBoard();
		}
		if (thisViewId == R.id.score_board_tab2) {
			tabId = 2;
			// CHANGE TO APPROPRIATE ADAPTER
			sb.findViewById(R.id.includeToday).setVisibility(View.GONE);
			sb.setCurrentTab(ScoreBoard.LIVE_GAME_TAB);
			((ScoreBoardAdapter) sb.scoreBoardAdapter).clearItem();

			ListView lView = (ListView)sb.getListView();
			lView.getLayoutParams().height = 276;
			FrameLayout fLayout = (FrameLayout)sb.findViewById(R.id.score_list_frame_layout);
			fLayout.getLayoutParams().height = 276;
			fLayout.requestLayout();
			
			sb.scoreBoardList.clear();
			sb.setLiveGame();
		}
		if (thisViewId == R.id.score_board_tab1) {
			tabId = 1;
			// CHANGE TO APPROPRIATE ADAPTER
			sb.findViewById(R.id.includeToday).setVisibility(View.GONE);
			
			ListView lView = (ListView)sb.getListView();
			lView.getLayoutParams().height = 276;
			FrameLayout fLayout = (FrameLayout)sb.findViewById(R.id.score_list_frame_layout);
			fLayout.getLayoutParams().height = 276;
			fLayout.requestLayout();
			
			sb.getValueFromSpinner(v);
		}
	}
}
