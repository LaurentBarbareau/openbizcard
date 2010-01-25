package com.tssoft.one.webservice.model;

import java.util.ArrayList;
import java.util.List;

public class GameBySubject {
	public String subject;
	public List<Game> games = new ArrayList<Game>();

	public GameBySubject(String subject, List<Game> games) {
		this.subject = subject;
		this.games = games;
	}
}
