package com.tssoft.one.webservice.model;

import java.util.ArrayList;
import java.util.List;

public class GameEvent {
	public String eventType;
	public String description;

	public GameEvent(String eventType, String description) {
		this.eventType = eventType;
		this.description = description;
	}
}
