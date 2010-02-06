package com.tssoft.one.webservice.model;

import java.util.ArrayList;
import java.util.List;

public class Table {
	public String name;
	public List<Team> teams = new ArrayList<Team>();

	public Table(String subject, List<Team> team) {
		this.name = subject;
		this.teams = team;
	}
}
