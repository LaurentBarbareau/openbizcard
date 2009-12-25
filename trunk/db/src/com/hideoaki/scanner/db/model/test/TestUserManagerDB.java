package com.hideoaki.scanner.db.model.test;

import junit.framework.TestCase;

public class TestUserManagerDB extends TestCase {
	public static final String DEFAULT_TEST_CARD_FILE = "defaulttestcard.csv";

	public TestUserManagerDB(String name) {
		super(name);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestUserManagerDB.class);
	}

	
}
