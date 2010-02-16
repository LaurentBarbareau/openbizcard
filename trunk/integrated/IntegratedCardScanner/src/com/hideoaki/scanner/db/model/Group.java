package com.hideoaki.scanner.db.model;

public class Group {
    private Long id ;
	private String name;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Group() {
		id = -1L;
	}

	public Group(String name) {
		id = -1L;
		this.name = name;
	}
	public void copy(Group group){
		this.id = group.id;
		this.name = group.name;
	}
}
