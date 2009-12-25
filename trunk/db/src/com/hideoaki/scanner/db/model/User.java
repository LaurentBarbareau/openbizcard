package com.hideoaki.scanner.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Users")
public class User {
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	private String username;
	private String password;
	private int role;

	public User() {
		id = -1L;
	}

	public User(String username, String password, int role) {
		id = -1L;
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}
	public void copy(User user) {
		this.id = user.id;
		this.username = user.username;
		this.password = user.password;
		this.role = user.role;
	}
}
