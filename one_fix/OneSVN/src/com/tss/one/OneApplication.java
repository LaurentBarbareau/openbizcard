package com.tss.one;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

public class OneApplication extends Activity {

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
//	AnimationDrawable rocketAnimation;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
//		rocketAnimation.start();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_page);
//		ImageView rocketImage = (ImageView) findViewById(R.id.loading);
//		rocketImage.setBackgroundResource(R.anim.loading);
//		rocketAnimation = (AnimationDrawable) rocketImage.getBackground();

		MainList.splashScreen = this;
		Intent mainDetailIntent = new Intent(this, MainList.class);
		startActivity(mainDetailIntent);

		// super.buildMenu(this);
		// ImageButton icon0 = (ImageButton) findViewById(R.id.main_button);
		// ImageButton icon1 = (ImageButton) findViewById(R.id.my_teams_button);
		// ImageButton icon2 = (ImageButton) findViewById(R.id.news_button);
		// ImageButton icon3 = (ImageButton)
		// findViewById(R.id.score_board_button);
		//        
		// icon0.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View view) {
		// Intent mainDetailIntent = new Intent(view.getContext(),
		// MainList.class);
		// // mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		// startActivityForResult(mainDetailIntent, 0);
		// }
		// });
		//        
		// icon1.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View view) {
		// Intent myTeamsTabIntent = new Intent(view.getContext(),
		// MyTeamsTab.class);
		// // mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		// startActivityForResult(myTeamsTabIntent, 0);
		// }
		// });
		//        
		// icon2.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View view) {
		// Intent newsListIntent = new Intent(view.getContext(),
		// NewsList.class);
		// // newsListIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		// startActivityForResult(newsListIntent, 0);
		// }
		// });
		// icon3.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View view) {
		// Intent newsListIntent = new Intent(view.getContext(),
		// ScoreBoardSelect.class);
		// // newsListIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		// startActivityForResult(newsListIntent, 0);
		// }
		// });
	}
	//
	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// System.out.println("aaaaaa" + data);
	// super.onActivityResult(requestCode, resultCode, data);
	// // finish();
	// }

}
