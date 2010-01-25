package com.tss.one.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tss.one.R;
import com.tss.one.ScoreBoard;
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
		this.vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.face = Typeface.createFromAsset(context.getAssets(),
				"fonts/Arial.ttf");
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
		TextView team1, team2;
		TextView score;
		Object i = items.get(position);

		if (((ScoreBoard) scoreBoard).getCurrentTab() == ScoreBoard.TODAY_GAME_TAB) {
			System.out.println("enter View TAB today");
			if (chkListT3.containsKey(position))
				return chkListT3.get(position);
			if (position == 0) {
				View todayGame = vi.inflate(R.layout.score_board_today_game,
						null);
				TextView todayTitle = (TextView) todayGame
						.findViewById(R.id.day_header);
				subject = (TextView) todayGame.findViewById(R.id.subject_title);

				ImageButton prev = (ImageButton) todayGame
						.findViewById(R.id.previous_day);
				ImageButton next = (ImageButton) todayGame
						.findViewById(R.id.next_day);
				if (scoreBoard.getDayOffset() == 2) {
					next.setVisibility(ImageButton.INVISIBLE);
				}
				if (scoreBoard.getDayOffset() == -2) {
					prev.setVisibility(ImageButton.INVISIBLE);
				}
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
					d.setTime(d.getTime()
							+ (scoreBoard.getDayOffset() * oneDay));
					SimpleDateFormat newformatter = new SimpleDateFormat(
							"dd/MM");
					String s = newformatter.format(d);
					todayTitle.setTypeface(face);
					todayTitle.setText(s);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				subject.setTypeface(face);
				subject.setText((String) i);

				v = todayGame;
			} else {
				if (i instanceof String) {

					v = vi.inflate(R.layout.score_board_red_list, null);
					subject = (TextView) v.findViewById(R.id.subject_title);
					subject.setTypeface(face);
					subject.setText((String) i);

				} else {

					Game game = (Game) i;
					v = vi.inflate(R.layout.score_board_match, null);
					minute = (TextView) v
							.findViewById(R.id.score_board_schedule_minute);
					team2 = (TextView) v
							.findViewById(R.id.score_board_schedule_name1);
					team1 = (TextView) v
							.findViewById(R.id.score_board_schedule_name2);
					score = (TextView) v
							.findViewById(R.id.score_board_schedule_score);
					ImageView guestIcon = (ImageView) v
							.findViewById(R.id.score_board_schedule_logo1);
					ImageView homeIcon = (ImageView) v
							.findViewById(R.id.score_board_schedule_logo2);

					minute.setTypeface(face);
					team1.setTypeface(face);
					team2.setTypeface(face);
					score.setTypeface(face);

					minute.setText(game.getStartTime());
					team1.setText(game.getHomeTeam());
					team2.setText(game.getGuestTeam());
					score.setText(game.getGuestScore() + " - "
							+ game.getHomeScore());

					System.out.println("url = " + game.getHomeIcon());

					ImageLoaderFactory.createImageLoader(
							(ListActivity) scoreBoard).setTask(
							game.getHomeIcon(), homeIcon);
					ImageLoaderFactory.createImageLoader(
							(ListActivity) scoreBoard).setTask(
							game.getGuestIcon(), guestIcon);
					ImageLoaderFactory.createImageLoader(
							(ListActivity) scoreBoard).go();
				}
			}
			chkListT3.put(position, v);
		}

		if (((ScoreBoard) scoreBoard).getCurrentTab() == ScoreBoard.LIVE_GAME_TAB) {
			if (chkListT2.containsKey(position))
				return chkListT2.get(position);
			if (i instanceof String) {
				v = vi.inflate(R.layout.score_board_red_list, null);
				subject = (TextView) v.findViewById(R.id.subject_title);
				subject.setTypeface(face);
				subject.setText((String) i);

			} else {
				Game game = (Game) i;
				v = vi.inflate(R.layout.score_board_live_match, null);
				minute = (TextView) v
						.findViewById(R.id.score_board_live_minute);
				team1 = (TextView) v.findViewById(R.id.score_board_live_name1);
				team2 = (TextView) v.findViewById(R.id.score_board_live_name2);
				score = (TextView) v.findViewById(R.id.score_board_live_score);
				ImageView homeIcon = (ImageView) v
						.findViewById(R.id.score_board_live_logo1);
				ImageView guestIcon = (ImageView) v
						.findViewById(R.id.score_board_live_logo2);

				minute.setTypeface(face);
				team1.setTypeface(face);
				team2.setTypeface(face);
				score.setTypeface(face);

				minute.setText(game.getGameMinute());
				team1.setText(game.getHomeTeam());
				team2.setText(game.getGuestTeam());
				score.setText(game.getHomeScore() + ":" + game.getGuestScore());

				ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard)
						.setTask(game.getHomeIcon(), homeIcon);
				ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard)
						.setTask(game.getGuestIcon(), guestIcon);
				ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard)
						.go();
			}
			chkListT2.put(position, v);
		}

		if (((ScoreBoard) scoreBoard).getCurrentTab() == ScoreBoard.LEAGUE_TAB) {
			if (chkListT1.containsKey(position))
				return chkListT1.get(position);
			if (i instanceof String) {
				v = vi.inflate(R.layout.score_board_red_list, null);
				subject = (TextView) v.findViewById(R.id.subject_title);
				subject.setTypeface(face);
				subject.setText((String) i);

			} else {
				Game game = (Game) i;
				v = vi.inflate(R.layout.score_board_league, null);
				minute = (TextView) v
						.findViewById(R.id.score_board_league_minute);
				team1 = (TextView) v
						.findViewById(R.id.score_board_league_name1);
				team2 = (TextView) v
						.findViewById(R.id.score_board_league_name2);
				score = (TextView) v
						.findViewById(R.id.score_board_league_score);
				ImageView homeIcon = (ImageView) v
						.findViewById(R.id.score_board_league_logo1);
				ImageView guestIcon = (ImageView) v
						.findViewById(R.id.score_board_league_logo2);

				minute.setTypeface(face);
				team1.setTypeface(face);
				team2.setTypeface(face);
				score.setTypeface(face);

				minute.setText(game.getGameMinute());
				team1.setText(game.getHomeTeam());
				team2.setText(game.getGuestTeam());
				score.setText(game.getHomeScore() + ":" + game.getGuestScore());

				ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard)
						.setTask(game.getHomeIcon(), homeIcon);
				ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard)
						.setTask(game.getGuestIcon(), guestIcon);
				ImageLoaderFactory.createImageLoader((ListActivity) scoreBoard)
						.go();
			}
			chkListT1.put(position, v);
		}
		return v;
	}
}
