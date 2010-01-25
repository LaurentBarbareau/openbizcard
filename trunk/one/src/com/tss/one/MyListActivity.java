package com.tss.one;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MyListActivity extends ListActivity{

	protected void buildMenu(Activity act) {
		ImageButton icon0 = (ImageButton) act.findViewById(R.id.main_button);
		ImageButton icon1 = (ImageButton) act
				.findViewById(R.id.my_teams_button);
		ImageButton icon2 = (ImageButton) act.findViewById(R.id.news_button);
		ImageButton icon3 = (ImageButton) act
				.findViewById(R.id.score_board_button);

		icon0.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent mainDetailIntent = new Intent(view.getContext(),
						MainList.class);
				// mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(mainDetailIntent, 0);
			}
		});

		icon1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myTeamsTabIntent = new Intent(view.getContext(),
						MyTeamsTab.class);
				// mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(myTeamsTabIntent, 0);
			}
		});

		icon2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent newsListIntent = new Intent(view.getContext(),
						NewsList.class);
				// newsListIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(newsListIntent, 0);
			}
		});
		icon3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent newsListIntent = new Intent(view.getContext(),
						ScoreBoardSelect.class);
				// newsListIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(newsListIntent, 0);
			}
		});
	}
}
