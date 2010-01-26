package com.tss.one;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import android.widget.TextView;

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
	private String timeString = "";
	private String dayString = "";
	
	
	public CountDownThread(ScoreBoard sb,int s){
		super();
		secTv = (TextView)sb.findViewById(R.id.score_board_sec);
		timeTv = (TextView)sb.findViewById(R.id.score_board_next);
		dayTv = (TextView)sb.findViewById(R.id.score_board_date);
		startCount = s;
		sec = s;
		scoreBoard = sb;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (sec<=0){
			Date dateDisplay = null;
			String date = WebServiceReaderScoreBoard.getCurrentDate();
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
			} 
			scoreBoard.updateScore();			
		}else{
			sec--;
			scoreBoard.runOnUiThread(updateDisplay);
		}
	}
	
	private Runnable updateDisplay = new Runnable(){
		public void run(){
			secTv.setText(sec+"");
			timeTv.setText(timeString);
			dayTv.setTag(dayString);
		}
	};
}
