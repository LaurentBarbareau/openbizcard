package com.tssoft.one.webservice.model;

import java.util.ArrayList;

public class Game {
	private String id = "";
	private String gameMinute = "";
	private String condition = ""; // notstarted
	private String perioidType = "";
	private String gameType = ""; // / soccer
	private String homeScore = "";
	private String guestScore = "";
	private String homeHalfScore = "";
	private String guestHalfScore = "";
	private String penaltyHomeScore = "";
	private String penaltyGuestScore = "";
	private String homeIcon = "";
	private String guestIcon = "";
	private String startTime = "";
	private String homeTeam = "";
	private String guestTeam = "";
	private String gameDate = "";
	private String hasEvent = "";
	public ArrayList<GameEvent> guestEvents = new ArrayList<GameEvent>();
	public ArrayList<GameEvent> homeEvents= new ArrayList<GameEvent>();
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGameMinute() {
		return gameMinute;
	}

	public void setGameMinute(String gameMinute) {
		this.gameMinute = gameMinute;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getPerioidType() {
		return perioidType;
	}

	public void setPerioidType(String perioidTime) {
		this.perioidType = perioidTime;
	}

	public String getGameType() {
		return gameType;
	}

	public void setGameType(String gameType) {
		this.gameType = gameType;
	}

	public String getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(String homeScore) {
		this.homeScore = homeScore;
	}

	public String getGuestScore() {
		return guestScore;
	}

	public void setGuestScore(String guestScore) {
		this.guestScore = guestScore;
	}

	public String getHomeHalfScore() {
		return homeHalfScore;
	}

	public void setHomeHalfScore(String homeHalfScore) {
		this.homeHalfScore = homeHalfScore;
	}

	public String getGuestHalfScore() {
		return guestHalfScore;
	}

	public void setGuestHalfScore(String guestHalfScore) {
		this.guestHalfScore = guestHalfScore;
	}

	public String getPenaltyHomeScore() {
		return penaltyHomeScore;
	}

	public void setPenaltyHomeScore(String penaltyHomeScore) {
		this.penaltyHomeScore = penaltyHomeScore;
	}

	public String getPenaltyGuestScore() {
		return penaltyGuestScore;
	}

	public void setPenaltyGuestScore(String penaltyGuestScore) {
		this.penaltyGuestScore = penaltyGuestScore;
	}

	public String getHomeIcon() {
		return homeIcon;
	}

	public void setHomeIcon(String homeIcon) {
		this.homeIcon = homeIcon;
	}

	public String getGuestIcon() {
		return guestIcon;
	}

	public void setGuestIcon(String guestIcon) {
		this.guestIcon = guestIcon;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}

	public String getGuestTeam() {
		return guestTeam;
	}

	public void setGuestTeam(String guestTeam) {
		this.guestTeam = guestTeam;
	}

	public String getGameDate() {
		return gameDate;
	}

	public void setGameDate(String gameDate) {
		this.gameDate = gameDate;
	}

	public String getHasEvent() {
		return hasEvent;
	}

	public void setHasEvent(String hasEvent) {
		this.hasEvent = hasEvent;
	}

	public Game(String id, String gameMinute, String condition,
			String perioidType, String gameType, String homeScore,
			String guestScore, String homeHalfScore, String guestHalfScore,
			String penaltyHomeScore, String penaltyGuestScore, String homeIcon,
			String guestIcon, String startTime, String homeTeam,
			String guestTeam, String gameDate, String hasEvent) {
		if(condition.equals("Active")){
			startTime = gameMinute;
		}
		if(condition.equals("Ended")){
			startTime = "Ended";
		}
		this.id = id;
		this.gameMinute = gameMinute;
		this.condition = condition;
		this.perioidType = perioidType;
		this.gameType = gameType;
		this.homeScore = homeScore;
		this.guestScore = guestScore;
		this.homeHalfScore = homeHalfScore;
		this.guestHalfScore = guestHalfScore;
		this.penaltyHomeScore = penaltyHomeScore;
		this.penaltyGuestScore = penaltyGuestScore;
		this.homeIcon = homeIcon;
		this.guestIcon = guestIcon;
		this.startTime = startTime;
		this.homeTeam = homeTeam;
		this.guestTeam = guestTeam;
		this.gameDate = gameDate;
		this.hasEvent = hasEvent;
	}
	public boolean isEnded(){
		if(condition.equals("Ended")){
			return true;
		}
		else {
			return false;
		}
	}
}
