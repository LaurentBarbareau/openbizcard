package com.tss.one;

import java.io.InputStream;
import java.net.URLDecoder;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.tssoft.one.utils.Constants;
import com.tssoft.one.utils.Utils;

public class MyActivity extends Activity {
	public static MyActivity instance;
	private Activity myAct;
	private int menuId;
	
	// ================= [ banner ] ==================//
	protected ImageView banner;
	protected String bannerImageUrl;
	protected String targetUrl;
	protected Runnable bannerR = new Runnable() {
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("===============================>>>>>>>>>> ===== "
							+ bannerImageUrl);
			System.out.println("===============================>>>>>>>>>> ===== "
							+ targetUrl);
			try {
				InputStream is = Utils.getBitmap_(bannerImageUrl);
				Drawable bd = BitmapDrawable.createFromStream(is, "");
				banner.setBackgroundDrawable(bd);
				banner.invalidate();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("[ Banner ] : InputStream is null");
			}
			if(bannerImageUrl == null)
				banner.setVisibility(View.GONE);
		}
	};

	protected void getJSON() {
		try {
			// read data
			String result = Utils.getHttpConn(Constants.BANNER_URL);
			result = Utils.toJSONString(result);
			System.out.println("==========================================>>>>>>>>> :: "+ result);

			String prefix = "src='";
			int indexSrc = result.indexOf(prefix) + prefix.length();
			bannerImageUrl = result.substring(indexSrc, result.indexOf("'",
					indexSrc));

			String prefixTargetUrl = "href='";
			int indexSrcTarget = result.indexOf(prefixTargetUrl)
					+ prefixTargetUrl.length();
			targetUrl = result.substring(indexSrcTarget, result.indexOf("'",
					indexSrcTarget));
			targetUrl = URLDecoder.decode(targetUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		runOnUiThread(bannerR);
	}

	// ========================================//

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		instance = this;
	}

	protected void buildMenu(Activity act, int menuId) {
		instance = this;
		
		this.myAct = act;
		this.menuId = menuId;
		
//		ImageButton icon0 = (ImageButton) act.findViewById(R.id.main_button);
//		ImageButton icon1 = (ImageButton) act
//				.findViewById(R.id.my_teams_button);
//		ImageButton icon2 = (ImageButton) act.findViewById(R.id.news_button);
//		ImageButton icon3 = (ImageButton) act
//				.findViewById(R.id.score_board_button);
//
//		icon0.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				if (GameDetail.isShow) {
//					GameDetail.isShow = false;
//				}
//				myAct.finish();
//				Intent mainDetailIntent = new Intent(view.getContext(),
//						MainList.class);
//				// mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				startActivity(mainDetailIntent);
//			}
//		});
//
//		icon1.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				if (GameDetail.isShow) {
//					GameDetail.isShow = false;
//				}
//				myAct.finish();
//				Intent myTeamsTabIntent = new Intent(view.getContext(),
//						MyTeamsTab.class);
//				// mainDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				startActivity(myTeamsTabIntent);
//			}
//		});
//
//		icon2.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				if (GameDetail.isShow) {
//					GameDetail.isShow = false;
//				}
//				myAct.finish();
//				Intent newsListIntent = new Intent(view.getContext(),
//						NewsList.class);
//				// newsListIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				startActivity(newsListIntent);
//			}
//		});
//		icon3.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				if (GameDetail.isShow) {
//					GameDetail.isShow = false;
//					myAct.finish();
//					return;
//				}
//				myAct.finish();
//				Intent newsListIntent = new Intent(view.getContext(),
//						ScoreBoard.class);
//				// newsListIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				startActivity(newsListIntent);
//			}
//		});

		
		
		
		
		/**
		 * ================== [ START banner ] ====================
		 */
		// set banner listener
		banner = (ImageView) findViewById(R.id.bannerIView);
		if (banner != null) {
			banner.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (targetUrl.startsWith("http://"))
						Utils.openBrowser(myAct, targetUrl);
				}
			});

			// =============== [ runnable ] ==================
			Runnable bannerRunnable = new Runnable() {
				public void run() {
					getJSON();
				}
			};
			Thread bannerThread = new Thread(null, bannerRunnable, "bannerRunnable");
			bannerThread.start();
		}
		/**
		 * ================== [ END banner ] ====================
		 */
	}
	
	
	// new menu
	private Menu mMenu;
	public boolean onCreateOptionsMenu(Menu menu) {
		this.mMenu = menu;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return true;
	}
	
	public boolean	onPrepareOptionsMenu(Menu menu){
		MenuItem menuItem = menu.getItem(menuId-1);
		switch(menuId){
		case 1:
			menuItem.setIcon(R.drawable.main_page_rolver);
			break;
		case 2:
			menuItem.setIcon(R.drawable.my_team_rolver);
			break;
		case 3:
			menuItem.setIcon(R.drawable.news_rolver);
			break;
		case 4:
			menuItem.setIcon(R.drawable.score_rolver);
			break;
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_main:
			if (GameDetail.isShow) {
				GameDetail.isShow = false;
			}
			myAct.finish();
			Intent mainDetailIntent = new Intent(myAct, MainList.class);
			startActivity(mainDetailIntent);
			return true;
		case R.id.menu_team:
			if (GameDetail.isShow) {
				GameDetail.isShow = false;
			}
			myAct.finish();
			Intent myTeamsTabIntent = new Intent(myAct, MyTeamsTab.class);
			startActivity(myTeamsTabIntent);
			return true;
		case R.id.menu_news:
			if (GameDetail.isShow) {
				GameDetail.isShow = false;
			}
			myAct.finish();
			Intent newsListIntent = new Intent(myAct, NewsList.class);
			startActivity(newsListIntent);
			return true;
		case R.id.menu_score:
			if (GameDetail.isShow) {
				GameDetail.isShow = false;
			}
			myAct.finish();
			Intent scoreListIntent = new Intent(myAct, ScoreBoard.class);
			startActivity(scoreListIntent);
			return true;
		case R.id.menu_exit:
			try{
				if(MyActivity.instance != null)
					MyActivity.instance.finish();
				if(MainList.instance != null)
					MainList.instance.finish();
				if(MyTeamsTab.instance != null)
					MyTeamsTab.instance.finish();
				if(NewsList.instance != null)
					NewsList.instance.finish();
				if(ScoreBoard.instance != null)
					ScoreBoard.instance.finish();
				if(OneApplication.instance != null)
					OneApplication.instance.finish();
				if(MyTeamsList.instance != null)
					MyTeamsList.instance.finish();
			}catch(Exception ex){
				System.out.println("===========>>> " + ex.getMessage());
			}
			this.finish();
			return true;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
}
