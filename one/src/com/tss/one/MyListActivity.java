package com.tss.one;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;
import android.widget.ImageButton;

public class MyListActivity extends ListActivity {

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	protected void buildMenu(Activity act) {
		final Activity myAct = act;
		ImageButton icon0 = (ImageButton) act.findViewById(R.id.main_button);
		ImageButton icon1 = (ImageButton) act
				.findViewById(R.id.my_teams_button);
		ImageButton icon2 = (ImageButton) act.findViewById(R.id.news_button);
		ImageButton icon3 = (ImageButton) act
				.findViewById(R.id.score_board_button);

		icon0.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (GameDetail.isShow) {
					GameDetail.isShow = false;
				}
				myAct.finish();
				Intent mainDetailIntent = new Intent(view.getContext(),
						MainList.class);
				// mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(mainDetailIntent);
			}
		});

		icon1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (GameDetail.isShow) {
					GameDetail.isShow = false;
				}
				myAct.finish();
				Intent myTeamsTabIntent = new Intent(view.getContext(),
						MyTeamsTab.class);
				// mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(myTeamsTabIntent);
			}
		});

		icon2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (GameDetail.isShow) {
					GameDetail.isShow = false;
				}
				myAct.finish();
				Intent newsListIntent = new Intent(view.getContext(),
						NewsList.class);
				// newsListIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(newsListIntent);
			}
		});
		icon3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (GameDetail.isShow) {
					GameDetail.isShow = false;
					myAct.finish();
					return;
				}
				myAct.finish();
				Intent newsListIntent = new Intent(view.getContext(),
						ScoreBoard.class);
				// newsListIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(newsListIntent);
			}
		});
	}
}
