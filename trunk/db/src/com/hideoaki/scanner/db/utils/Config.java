package com.hideoaki.scanner.db.utils;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

public class Config {
	public static final String CONFIG_FILE_NAME = "config.properties";
	public static final String USERNAME_PROP = "username";
	public static final String PASSWORD_PROP = "password";
	public static final String REMEMBERPASS_PROP = "rememberpass";
	private String username = "";
	private String password = "";
	private boolean rememberPassword = true;

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

	public boolean isRememberPassword() {
		return rememberPassword;
	}

	public void setRememberPassword(boolean rememberPassword) {
		this.rememberPassword = rememberPassword;
	}

	public static Config loadConfig() throws ScannerDBException {
		try {
			Config config = new Config();
			Properties prop = new Properties();
			FileReader reader = new FileReader(CONFIG_FILE_NAME);
			prop.load(reader);
			config.username = prop.getProperty(USERNAME_PROP);
			config.password = prop.getProperty(PASSWORD_PROP);
			config.rememberPassword = Boolean.getBoolean(prop.getProperty(REMEMBERPASS_PROP) );
			return config;
		} catch (IOException e) {
			throw new ScannerDBException(e);
		}
		
	}

	public void saveConfig() throws ScannerDBException {
		try {
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(CONFIG_FILE_NAME));
			Properties prop = new Properties();
			prop.setProperty(USERNAME_PROP, username);
			prop.setProperty(PASSWORD_PROP, password);
			prop.setProperty(REMEMBERPASS_PROP, String
					.valueOf(rememberPassword));
			prop.store(writer, "");
		} catch (IOException e) {
			throw new ScannerDBException(e);
		}
	}

	public static void main(String args[]) {
		try {
			Config cfg = Config.loadConfig();
			System.out.println("First Username "  + cfg.getUsername());
			System.out.println("First Password "  + cfg.getPassword());
			System.out.println("First Remember "  + cfg.isRememberPassword());
			
			cfg.username = "Oak";
			cfg.password = "Surname";
			cfg.rememberPassword = false;
			
			cfg.saveConfig();
		} catch (ScannerDBException e) {
			e.printStackTrace();
		}
	}
}
