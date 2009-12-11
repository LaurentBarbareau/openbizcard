package com.hideoaki.scanner.db.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.hideoaki.scanner.db.model.Card;
import com.hideoaki.scanner.db.model.Group;
import com.hideoaki.scanner.db.utils.Privacy;
import com.hideoaki.scanner.db.utils.ScannerDBException;

public class CardLocalManager {
	static EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("openscanner");
	public static final String SQL_SELECT_CARD_BY_ID = "select c from Card c order by c.id asc";
	public static final String SQL_SELECT_CARD_BY_SEARCHKEY = "SELECT c from Card c WHERE c.firstName like :searchKey OR lastName like :searchKey order by c.id asc";
	public static final String SQL_SELECT_CARD = "SELECT c from Card c";

	public static void closeEntityManagerFactory() {
		emf.close();
	}

	public static ArrayList<Card> loadLocalCard(String pathToCSV)
			throws ScannerDBException {
		return Card.loadLocalCard(pathToCSV);
	}

	public static void saveLocalCard(List<Card> cards, String pathToCSV)
			throws ScannerDBException {
		Card.saveLocalCard(cards, pathToCSV);
	}

	public static List<Card> addLocalCard(Card card, String pathToCSV)
			throws ScannerDBException {
		return Card.addLocalCard(card, pathToCSV);
	}

	public static List<Card> deleteLocalCard(long id, String pathToCSV)
			throws ScannerDBException {
		return Card.deleteLocalCard(id, pathToCSV);
	}

	public static List<Card> editLocalCard(Card card, String pathToCSV)
			throws ScannerDBException {
		return Card.editLocalCard(card, pathToCSV);
	}
}
