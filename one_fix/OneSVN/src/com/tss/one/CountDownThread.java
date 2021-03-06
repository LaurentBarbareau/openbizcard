package com.tss.one;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import android.graphics.Typeface;
import android.widget.TextView;

import com.tssoft.one.utils.Utils;
import com.tssoft.one.webservice.WebServiceReaderScoreBoard;

public class CountDownThread extends TimerTask{
	private TextView secTv;
	private TextView timeTv;
	private TextView dayTv;
	private static int startCount;
	public int sec;
	private ScoreBoard scoreBoard;
	private SimpleDateFormat oriFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ssZ");
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	private SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM/yyyy");
	private Typeface face;
	private String timeString = "";
	private String dayString = "";
	
	private float textSize = 0;
	
	public CountDownThread(ScoreBoard sb,int s){
		super();
		secTv = (TextView)sb.findViewById(R.id.score_board_sec);
		timeTv = (TextView)sb.findViewById(R.id.score_board_next);
		dayTv = (TextView)sb.findViewById(R.id.score_board_date);
		face = Typeface.createFromAsset(sb.getAssets(),"fonts/Arial.ttf");
		startCount = s;
		sec = s;
		scoreBoard = sb;
		textSize = secTv.getTextSize()-2;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (sec<=0){
			Date dateDisplay = null;
			String date = WebServiceReaderScoreBoard.getCurrentDate();
			date = date.replace("T", "");
			try {
				dateDisplay = oriFormat.parse(date);
			} catch (ParseException e) {				
				e.printStackTrace();
			} 
			if(dateDisplay!=null){
				dayString = dayFormat.format(dateDisplay);
				timeString = timeFormat.format(dateDisplay);
				sec = startCount;
				scoreBoard.runOnUiThread(updateDisplay);
//				scoreBoard.updateScore();
			} 
		}else{
			sec--;
			scoreBoard.runOnUiThread(updateDisplay);
		}
	}
	
	private Runnable updateDisplay = new Runnable(){
		public void run(){
			secTv.setTypeface(face);
			secTv.setText(sec+"");
//			secTv.setTextSize(textSize);
			
			timeTv.setTypeface(face);
			timeTv.setText(timeString);
//			timeTv.setTextSize(textSize);
			
			dayTv.setTypeface(face);
			dayTv.setTag(dayString);
//			dayTv.setTextSize(textSize);
			
			// update time
			TextView timeTv = (TextView) scoreBoard.findViewById(R.id.score_board_next);
			timeTv.setText(Utils.getCurrentHour()+":"+Utils.getCurrentMinute());							
		}
	};
}
