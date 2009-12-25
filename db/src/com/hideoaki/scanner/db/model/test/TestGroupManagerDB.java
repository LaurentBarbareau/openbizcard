package com.hideoaki.scanner.db.model.test;

import java.util.List;

import junit.framework.TestCase;

import com.hideoaki.scanner.db.manager.GroupDBManager;
import com.hideoaki.scanner.db.model.Group;

public class TestGroupManagerDB extends TestCase {
	public static final String DEFAULT_TEST_CARD_FILE = "defaulttestcard.csv";

	public TestGroupManagerDB(String name) {
		super(name);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestGroupManagerDB.class);
	}
	public static void testAddDatabase() throws Exception {
		Group group = new Group("Group");
		List<Group> groups = GroupDBManager.loadDBGroup();
		int before = groups.size();
		GroupDBManager.addGroup(group);
		groups =  GroupDBManager.loadDBGroup();
		assertEquals(before + 1, groups.size());
	}
	public static void testDeleteFromDatabase() throws Exception {
		List<Group> newGroups = GroupDBManager.loadDBGroup();
		int before = newGroups.size();
		GroupDBManager.deleteGroup(newGroups.get(newGroups.size() - 1).getId());
		newGroups = GroupDBManager.loadDBGroup();
		assertEquals(before - 1, newGroups.size());
	}

	public static void testEditFromDatabase() throws Exception {
		Group card = GroupDBManager.getDBGroupById(6L);
		card.setName("Oak");
		GroupDBManager.editGroup(card);
	}
}
