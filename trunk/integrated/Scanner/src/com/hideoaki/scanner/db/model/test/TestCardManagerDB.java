package com.hideoaki.scanner.db.model.test;

import java.util.List;

import com.hideoaki.scanner.db.manager.CardDBManager;
import com.hideoaki.scanner.db.model.Card;
import com.hideoaki.scanner.db.model.Group;
import com.hideoaki.scanner.db.utils.Privacy;
import com.hideoaki.scanner.db.utils.ScannerDBException;

import junit.framework.TestCase;

public class TestCardManagerDB extends TestCase {
	public static final String DEFAULT_TEST_CARD_FILE = "defaulttestcard.csv";

	public TestCardManagerDB(String name) {
		super(name);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestCardManagerDB.class);
	}

	public static void testAddFromDatabase() throws Exception {

		Card card1 = new Card("krissada3", "chalermsook", "Project LEader",
				"hideoaki@gmail.com", "Crie Company Limited",
				"http://www.hideoaki.com", "\"400/107 \' Soi", "Bangkok",
				"ไทย", "d", "a", "025894821", "ssss", "0805511559", "aa",
				"sss", "sss", new Group("Test"), Privacy.GROUP);
		List<Card> newCards = CardDBManager.loadDBCard();
		int before = newCards.size();
		CardDBManager.addCard(card1);
		newCards =  CardDBManager.loadDBCard();
		assertEquals(before + 1, newCards.size());
	}

	public static void testDeleteFromDatabase() throws Exception {
		List<Card> newCards = CardDBManager.loadDBCard();
		int before = newCards.size();
		CardDBManager.deleteCard(newCards.get(newCards.size() - 1).getId());
		newCards = CardDBManager.loadDBCard();
		assertEquals(before - 1, newCards.size());
	}

	public static void testEditFromDatabase() throws Exception {
		Card card = CardDBManager.getDBCardById(6);
		card.setFirstName("Oak");
		CardDBManager.editCard(card);
	}
}
