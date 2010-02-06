package com.tssoftgroup.tmobile.model;

import java.util.Vector;

public class ProjectInfo implements TitleDescriptionObj {
	private String projectName = "";
	private String projectDesc= "";

	private String id= "";
	private Vector users = new Vector();

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Vector getUsers() {
		return users;
	}

	public void setUsers(Vector users) {
		this.users = users;
	}

	public String getProjectDesc() {
		return projectDesc;
	}

	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}

	public String getDescription() {
		return projectDesc;
	}

	public String getTitle() {
		return projectName;
	}

	public boolean containKey(String key) {
		key = key.toLowerCase();
		if (projectName.toLowerCase().indexOf(key) >= 0) {
			return true;
		} else {
			return false;
		}
	}
}
