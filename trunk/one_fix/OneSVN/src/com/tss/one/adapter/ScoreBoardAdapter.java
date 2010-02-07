package com.tss.one.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tss.one.GameDetail;
import com.tss.one.R;
import com.tss.one.ScoreBoard;
import com.tssoft.one.utils.Utils;
import com.tssoft.one.webservice.ImageLoaderFactory;
import com.tssoft.one.webservice.model.Game;

public class ScoreBoardAdapter extends ArrayAdapter<Object> {
	private HashMap<Integer, View> chkListT3 = new HashMap<Integer, View>();
	private HashMap<Integer, View> chkListT2 = new HashMap<Integer, View>();
	private HashMap<Integer, View> chkListT1 = new HashMap<Integer, View>();
	private ScoreBoard scoreBoard;
	private ArrayList<Object> items;

	private LayoutInflater vi;
	private Typeface face;
		
	public int currentDate = 0; // can be 0 -1 1

	public ScoreBoardAdapter(ScoreBoard context, int textViewResourceId,
			ArrayList<Object> items) {
		super(context, textViewResourceId, items);
		this.scoreBoard = context;
		this.items = items;
		this.vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.face = Typeface.createFromAsset(context.getAssets(),"fonts/Arial.ttf");
	}

	public void setList(ArrayList<Object> i) {
		this.items = i;
	}

	public void clearItem() {
		items.clear();
	}

	public void clickPrev() {
		items.clear();
		chkListT3.clear();
		scoreBoard.setDayOffset(scoreBoard.getDayOffset() - 1);
		scoreBoard.setScoreBoard();
	}

	public void clickNext() {
		items.clear();
		chkListT3.clear();
		scoreBoard.setDayOffset(scoreBoard.getDayOffset() + 1);
		scoreBoard.setScoreBoard();
	}

	// /
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHH:mm:ssZ");
	int oneDay = 1000 * 60 * 60 * 24;

	// 2010-01-25T00:00:00+02:00
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("enter View");

		View v = convertView;
		TextView subject;
		TextView minute;
		TextView teamH, teamG;
		TextView score;
		Object i = items.get(position);
		
		/**
		 * ===================== TODAY GAME TAB ========================
		 */
		if (((ScoreBoard) scoreBoard).getCurrentTab() == ScoreBoard.TODAY_GAME_TAB) {
			System.out.println("enter View TAB today");
			if (chkListT3.containsKey(position))
				return chkListT3.get(position);
			
			/**
			 * ======= TITLE ========
			 */
			if (position == 0) {
//				View todayGame = vi.inflate(R.layout.score_board_today_game, null);
				
				TextView todayTitle = (TextView) scoreBoard.findViewById(R.id.day_header);
//				subject = (TextView) todayGame.findViewById(R.id.subject_title);

				ImageButton prev = (ImageButton) scoreBoard.findViewById(R.id.previous_day);
				ImageButton next = (ImageButton) scoreBoard.findViewById(R.id.next_day);
//				if (scoreBoard.getDayOffset() == 2) {
//					next.setVisibility(ImageButton.INVISIBLE);
//				}
//				if (scoreBoard.getDayOffset() == -2) {
//					prev.setVisibility(ImageButton.INVISIBLE);
//				}
				prev.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						clickPrev();
					}
				});

				next.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						clickNext();
					}
				});

				String cDate = scoreBoard.cDate;
				try {
					Date d = formatter.parse(cDate);
					// next and prev
					d.setTime(d.getTime()+ (scoreBoard.getDayOffset() * oneDay));
					SimpleDateFormat newformatter = new SimpleDateFormat("dd/MM");
					String s = newformatter.format(d);
					todayTitle.setTypeface(face, Typeface.BOLD);
					todayTitle.setTextColor(Color.WHITE);
					todayTitle.setText(s);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

//				subject.setTypeface(face);
//				subject.setText((String) i);
//				v = todayGame;
			} 
			
			/**
			 * ===== MATCH LIST ======
			 */
//			else {
				if (i instanceof String) {
					v = vi.inflate(R.layout.score_board_red_list, null);
					
					String txt = Utils.reverseStringByPattern(Utils.NUMBER_PATTERN, (String)i);
					
					subject = (TextView) v.findViewById(R.id.subject_title);
					subject.setTypeface(face, Typeface.BOLD);
					subject.setText(txt);
					
				} else {
					final Game game = (Game) i;
					v = vi.inflate(R.layout.score_board_match, null);
					
					// can see detail
					if(game.getHasEvent().equals("true")){
						v.findViewById(R.id.arrow_detail).setVisibility(ImageButton.VISIBLE);
						v.setOnClickListener(new OnClickListener() {

							public void onClick(View v) {
								GameDetail.isShow = true;
								GameDetail.gameId = game.getId();
								GameDetail.screenId = 4;
								Intent mainDetailIntent = new Intent(scoreBoard, GameDetail.class);
								scoreBoard.startActivity(mainDetailIntent);
							}
						});
				} else {
					// game does not start
					if (game.getHomeScore().equalsIgnoreCase("") || game.getGuestScore().equalsIgnoreCase("")) {
						v.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Utils.displayNoGameDetailNow(scoreBoard);
							}
						});
					}else{
						v.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Utils.displayNoGameDetail(scoreBoard);
							}
						});
					}
				}
										
					minute = (TextView) v
							.findViewById(R.id.score_board_schedule_minute);
					teamG = (TextView) v
							.findViewById(R.id.score_board_schedule_name2);
					teamH = (TextView) v
							.findViewById(R.id.score_board_schedule_name1);
					score = (TextView) v
							.findViewById(R.id.score_board_schedule_score);
					ImageView hIcon = (ImageView) v
							.findViewById(R.id.score_board_schedule_logo1);
					ImageView gIcon = (ImageView) v
							.findViewById(R.id.score_board_schedule_logo2);
					
					
					minute.setTypeface(face);
					teamH.setTypeface(face);
					teamG.setTypeface(face);
					score.setTypeface(face, Typeface.BOLD);

					System.out.println("===========>>>>>>>>>>>>  " + game.getStartTime());
					
					if(Utils.isEndGame(game.getStartTime())){
						minute.setTextColor(Color.GREEN);
						minute.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
						minute.setText(Utils.toEndedHebrew(scoreBoard, game.getStartTime()));
					}else if(game.getCondition().equals("Active") && !game.getGameType().equals("Basketball")){
						minute.setTextColor(Color.GREEN);
						minute.setText(scoreBoard.getText(R.string.minute) + " " + game.getStartTime() );
					}else if(game.getCondition().equals("Active")  && game.getGameType().equals("Basketball")){
						minute.setTextColor(Color.GREEN);
						minute.setText(game.getStartTime());
					}
					else{
						minute.setText(game.getStartTime());
					}
					
					String hTxt = Utils.reverseStringByPattern(Utils.NUMBER_PATTERN, game.getHomeTeam());
					String gTxt = Utils.reverseStringByPattern(Utils.NUMBER_PATTERN, game.getGuestTeam());
					
					teamH.setText(hTxt);
					teamG.setText(gTxt);
					
					String scoreStr = game.getCondition().equals("NotStarted")?"":game.getGuestScore() + " - "
							+ game.getHomeScore();
					
					score.setText(scoreStr);

					System.out.println("url icon ==================== " + game.getHomeIcon());
					
					String fileName = "";
					fileName = game.getHomeIcon();
					fileName = fileName.substring(fileName.lastIndexOf("/")+1, fileName.lastIndexOf("."));
					fileName = "icon_"+fileName;
					int rId = Utils.getResourceIdFromPath(scoreBoard, fileName);
					if(rId == -1){
						System.out.println("xxxxxxxxxxxxxx " + fileName);
						ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard).setTask(game.getHomeIcon(), hIcon);
						ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard).go();
					}else{
						System.out.println("yyyyyyyyyyyyyy " + fileName);
						hIcon.setImageResource(rId);
					}
					
					fileName = game.getGuestIcon();
					fileName = fileName.substring(fileName.lastIndexOf("/")+1, fileName.lastIndexOf("."));
					fileName = "icon_"+fileName;
					int rId_ = Utils.getResourceIdFromPath(scoreBoard, fileName);
					if(rId_ == -1){
						ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard).setTask(game.getGuestIcon(), gIcon);
						ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard).go();
					}else{
						gIcon.setImageResource(rId_);
					}
					
					
//				ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard)
//						.setTask(game.getHomeIcon(), hIcon);
//				ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard)
//						.setTask(game.getGuestIcon(), gIcon);
//				ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard)
//						.go();
				}
//			}
			chkListT3.put(position, v);
		}

		/**
		 * ========================== LIVE GAME TAB ================================
		 */
		if (((ScoreBoard) scoreBoard).getCurrentTab() == ScoreBoard.LIVE_GAME_TAB) {
			if (chkListT2.containsKey(position))
				return chkListT2.get(position);
			if (i instanceof String) {
				v = vi.inflate(R.layout.score_board_red_list, null);
				String txt = Utils.reverseStringByPattern(Utils.NUMBER_PATTERN, (String) i );
				subject = (TextView) v.findViewById(R.id.subject_title);
				subject.setTypeface(face);
				subject.setText(txt);

			} else {
				final Game game = (Game) i;
				v = vi.inflate(R.layout.score_board_live_match, null);
				if(game.getHasEvent().equals("true")){
					(v.findViewById(R.id.arrow_detail)).setVisibility(ImageButton.VISIBLE);
					v.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							GameDetail.isShow = true;
							GameDetail.gameId = game.getId();
							GameDetail.screenId = 4;
							Intent mainDetailIntent = new Intent(scoreBoard,
									GameDetail.class);
							scoreBoard.startActivity(mainDetailIntent);
						}
					});
				}else{
						// game does not start
						if (game.getHomeScore().equalsIgnoreCase("") || game.getGuestScore().equalsIgnoreCase("")) {
							v.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									Utils.displayNoGameDetailNow(scoreBoard);
								}
							});
						}else{
							v.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									Utils.displayNoGameDetail(scoreBoard);
								}
							});
						}
					
				}
				minute = (TextView) v.findViewById(R.id.score_board_live_minute);
				teamH = (TextView) v.findViewById(R.id.score_board_live_name1);
				teamG = (TextView) v.findViewById(R.id.score_board_live_name2);
				score = (TextView) v.findViewById(R.id.score_board_live_score);
				ImageView homeIcon = (ImageView) v
						.findViewById(R.id.score_board_live_logo1);
				ImageView guestIcon = (ImageView) v
						.findViewById(R.id.score_board_live_logo2);

				minute.setTypeface(face);
				teamH.setTypeface(face);
				teamG.setTypeface(face);
				score.setTypeface(face);

//				if(Utils.isEndGame(game.getGameMinute())){
//					minute.setTextColor(Color.GREEN);
//					minute.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
//				}
//				
//				minute.setText(Utils.toEndedHebrew(scoreBoard, game.getGameMinute()));
				if(Utils.isEndGame(game.getStartTime())){
					minute.setTextColor(Color.GREEN);
					minute.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
					minute.setText(Utils.toEndedHebrew(scoreBoard, game.getStartTime()));
				}else if(game.getCondition().equals("Active") && !game.getGameType().equals("Basketball")){
					minute.setTextColor(Color.GREEN);
					minute.setText(scoreBoard.getText(R.string.minute) + " " + game.getStartTime() );
				}else if(game.getCondition().equals("Active")  && game.getGameType().equals("Basketball")){
					minute.setTextColor(Color.GREEN);
					minute.setText(game.getStartTime());
				}
				else{
					minute.setText(game.getStartTime());
				}
				
				String hTxt = Utils.reverseStringByPattern(Utils.NUMBER_PATTERN, game.getHomeTeam());
				String gTxt = Utils.reverseStringByPattern(Utils.NUMBER_PATTERN, game.getGuestTeam());
				
				teamH.setText(hTxt);
				teamG.setText(gTxt);
				String scoreStr = game.getCondition().equals("NotStarted")?"":game.getGuestScore() + " - "
						+ game.getHomeScore();
				
				score.setText(scoreStr);
				
				
				String fileName = "";
				fileName = game.getHomeIcon();
				fileName = fileName.substring(fileName.lastIndexOf("/")+1, fileName.lastIndexOf("."));
				fileName = "icon_"+fileName;

				int rId = Utils.getResourceIdFromPath(scoreBoard, fileName);
				if(rId == -1){
					ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard).setTask(game.getHomeIcon(), homeIcon);
					ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard).go();
				}else{
					homeIcon.setImageResource(rId);
				}
				
				
				fileName = game.getGuestIcon();
				fileName = fileName.substring(fileName.lastIndexOf("/")+1, fileName.lastIndexOf("."));
				fileName = "icon_"+fileName;
//				guestIcon.setImageResource(Utils.getResourceIdFromPath(scoreBoard, fileName));
				int rId_ = Utils.getResourceIdFromPath(scoreBoard, fileName);
				if(rId_ == -1){
					ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard).setTask(game.getGuestIcon(), guestIcon);
					ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard).go();
				}else{
					guestIcon.setImageResource(rId_);
				}
				
				
//				ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard)
//						.setTask(game.getHomeIcon(), homeIcon);
//				ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard)
//						.setTask(game.getGuestIcon(), guestIcon);
//				ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard)
//						.go();
			}
			chkListT2.put(position, v);
		}

		if (((ScoreBoard) scoreBoard).getCurrentTab() == ScoreBoard.LEAGUE_TAB) {
			if (chkListT1.containsKey(position))
				return chkListT1.get(position);
			if (i instanceof String) {
				v = vi.inflate(R.layout.score_board_red_list, null);
				
				String txt = Utils.reverseStringByPattern(Utils.NUMBER_PATTERN, (String)i);
				
				subject = (TextView) v.findViewById(R.id.subject_title);
				subject.setTypeface(face);
				subject.setText(txt);

			} else {
				final Game game = (Game) i;
				v = vi.inflate(R.layout.score_board_league, null);
				if(game.getHasEvent().equals("true")){
					(v.findViewById(R.id.arrow_detail)).setVisibility(ImageButton.VISIBLE);
					v.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							GameDetail.isShow = true;
							GameDetail.gameId = game.getId();
							GameDetail.screenId = 4;
							Intent mainDetailIntent = new Intent(scoreBoard,
									GameDetail.class);
							scoreBoard.startActivity(mainDetailIntent);
						}
					});
				}else {
					// game does not start
					if (game.getHomeScore().equalsIgnoreCase("") || game.getGuestScore().equalsIgnoreCase("")) {
						v.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Utils.displayNoGameDetailNow(scoreBoard);
							}
						});
					}else{
						v.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Utils.displayNoGameDetail(scoreBoard);
							}
						});
					}
				}
				minute = (TextView) v
						.findViewById(R.id.score_board_league_minute);
				teamH = (TextView) v
						.findViewById(R.id.score_board_league_name1);
				teamG = (TextView) v
						.findViewById(R.id.score_board_league_name2);
				score = (TextView) v
						.findViewById(R.id.score_board_league_score);
				ImageView homeIcon = (ImageView) v
						.findViewById(R.id.score_board_league_logo1);
				ImageView guestIcon = (ImageView) v
						.findViewById(R.id.score_board_league_logo2);

				minute.setTypeface(face);
				teamH.setTypeface(face);
				teamG.setTypeface(face);
				score.setTypeface(face);

				if(Utils.isEndGame(game.getStartTime())){
					minute.setTextColor(Color.GREEN);
					minute.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
					minute.setText(Utils.toEndedHebrew(scoreBoard, game.getStartTime()));
				}else if(game.getCondition().equals("Active") && !game.getGameType().equals("Basketball")){
					minute.setTextColor(Color.GREEN);
					minute.setText(scoreBoard.getText(R.string.minute) + " " + game.getStartTime() );
				}else if(game.getCondition().equals("Active")  && game.getGameType().equals("Basketball")){
					minute.setTextColor(Color.GREEN);
					minute.setText(game.getStartTime());
				}
				else{
					minute.setText(game.getStartTime());
				}
				
				String hTxt = Utils.reverseStringByPattern(Utils.NUMBER_PATTERN, game.getHomeTeam());
				String gTxt = Utils.reverseStringByPattern(Utils.NUMBER_PATTERN, game.getGuestTeam());
				
				teamH.setText(hTxt);
				teamG.setText(gTxt);
				
				String scoreStr = game.getCondition().equals("NotStarted")?"":game.getGuestScore() + " - "
						+ game.getHomeScore();
				
				score.setText(scoreStr);
				
				
//				ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard)
//						.setTask(game.getHomeIcon(), homeIcon);
//				ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard)
//						.setTask(game.getGuestIcon(), guestIcon);
//				ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard)
//						.go();
				
				String fileName = "";
				fileName = game.getHomeIcon();
				fileName = fileName.substring(fileName.lastIndexOf("/")+1, fileName.lastIndexOf("."));
				fileName = "icon_"+fileName;
//				homeIcon.setImageResource(Utils.getResourceIdFromPath(scoreBoard, fileName));
				int rId = Utils.getResourceIdFromPath(scoreBoard, fileName);
				if(rId == -1){
					ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard).setTask(game.getHomeIcon(), homeIcon);
					ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard).go();
				}else{
					homeIcon.setImageResource(rId);
				}
				
				
				fileName = game.getGuestIcon();
				fileName = fileName.substring(fileName.lastIndexOf("/")+1, fileName.lastIndexOf("."));
				fileName = "icon_"+fileName;
//				guestIcon.setImageResource(Utils.getResourceIdFromPath(scoreBoard, fileName));
				int rId_ = Utils.getResourceIdFromPath(scoreBoard, fileName);
				if(rId_ == -1){
					ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard).setTask(game.getGuestIcon(), guestIcon);
					ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard).go();
				}else{
					guestIcon.setImageResource(rId_);
				}
			}
			chkListT1.put(position, v);
		}
		return v;
	}
}
