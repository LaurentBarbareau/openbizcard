package com.tss.one.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tss.one.R;
import com.tssoft.one.webservice.ImageLoaderFactory;
import com.tssoft.one.webservice.model.Game;

public class LiveAdapter extends ArrayAdapter<Object>{
	private HashMap<Integer,View> chkList = new HashMap<Integer,View>();
	private Context context;
	private ArrayList<Object> items;
	
	public LiveAdapter(Context context, int textViewResourceId,ArrayList<Object> items) {
		super(context, textViewResourceId, items);
		this.context = context;
		this.items = items;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(chkList.containsKey(position))return chkList.get(position);
		System.out.println("======================= viewed");
		
		View v = convertView;
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		Typeface face = Typeface.createFromAsset(context.getAssets(),"fonts/Arial.ttf");

		TextView subject;
		TextView minute;
		TextView team1,team2;
		TextView score;
		Object i = items.get(position);
				
		if(i instanceof String){			

			v = vi.inflate(R.layout.red_list, null);
			subject = (TextView) v.findViewById(R.id.subject_title);
			subject.setTypeface(face);
			subject.setText((String)i);
			
		}else{			

			Game game = (Game)i;
			v = vi.inflate(R.layout.score_board_live_match, null);
			minute = (TextView) v.findViewById(R.id.score_board_live_minute);
			team1 = (TextView) v.findViewById(R.id.score_board_live_name1);
			team2 = (TextView) v.findViewById(R.id.score_board_live_name2);
			score = (TextView) v.findViewById(R.id.score_board_live_score);
			ImageView homeIcon = (ImageView) v.findViewById(R.id.score_board_live_logo1);
			ImageView guestIcon = (ImageView) v.findViewById(R.id.score_board_live_logo2);
			
			minute.setTypeface(face);
			team1.setTypeface(face);
			team2.setTypeface(face);
			score.setTypeface(face);
			
			minute.setText(game.getStartTime());
			team1.setText(game.getHomeTeam());
			team2.setText(game.getGuestTeam());
			score.setText(game.getHomeScore()+":"+game.getGuestScore());
			
			ImageLoaderFactory.createImageLoader((ListActivity)context).setTask(game.getHomeIcon(),homeIcon);
			ImageLoaderFactory.createImageLoader((ListActivity)context).setTask(game.getGuestIcon(),guestIcon);
			ImageLoaderFactory.createImageLoader((ListActivity)context).go();
			
		}
		chkList.put(position, v);
		
		return v; 
	}
}
