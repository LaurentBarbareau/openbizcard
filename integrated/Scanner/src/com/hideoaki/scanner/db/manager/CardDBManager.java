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

public class CardDBManager {
	static EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("openscanner");
	public static final String SQL_SELECT_CARD_BY_ID = "select c from Card c order by c.id asc";
	public static final String SQL_SELECT_CARD_BY_SEARCHKEY = "SELECT c from Card c WHERE c.firstName like :searchKey OR lastName like :searchKey order by c.id asc";
	public static final String SQL_SELECT_CARD = "SELECT c from Card c";

	public static void main(String[] args) {
                		Card card12 = new Card("krissada3", "chalermsook", "Project LEader",
				"hideoaki@gmail.com", "Crie Company Limited",
				"http://www.hideoaki.com", "\"400/107 \' Soi", "Bangkok",
				"test", "d", "a", "025894821", "ssss", "0805511559", "aa",
				"sss", "sss", new Group("Test"), Privacy.GROUP);
		//List<Card> newCards = CardDBManager.loadDBCard();
		//int before = newCards.size();
		CardDBManager.addCard(card12);
		//newCards =  CardDBManager.loadDBCard();
//		Card card5 = new Card("krissada13", "chalermsook", "Project LEader",
//				"hideoaki@gmail.com", "Crie Company Limited",
//				"http://www.hideoaki.com", "\"400/107 \' Soi", "Bangkok",
//				"ไทย", "d", "a", "025894821", "ssss", "0805511559", "aa",
//				"sss", "sss", new Group("Test"), Privacy.GROUP);
		// addCard(card1);
		// Card c = getCardById(2);
		// List<Card> cards = searchCardDB("chalerm");
//		List<Card> cards = loadDBCard();
//		Card card0 = cards.get(0);
//		card0.setAddress("Change Address");
//		Card card1 = cards.get(1);
//		card1.setFirstName("hideoak2i2");
//		Card card2 = cards.get(2);
//		card2.setLastName("hideo2");
//		// cards.clear();
//		cards.add(card5);
//		System.out.println(cards.size());
//		saveDBAllCard(cards);
//		closeEntityManagerFactory();
	}

	/*
	 * For DB CArds
	 */
	public static List<Card> searchCardDB(String searchKey) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Query query = em.createQuery(SQL_SELECT_CARD_BY_SEARCHKEY);
		query.setParameter("searchKey", "%" + searchKey + "%");
		List<Card> cards = query.getResultList();
		em.close();
		return cards;
	}

	public static List<Card> loadDBCard() {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Query query = em.createQuery(SQL_SELECT_CARD);
		List<Card> cards = query.getResultList();
		em.close();
		return cards;
	}

	public static void saveDBAllCard(List<Card> cards) {
		// Start EntityManagerFactory
		// First unit of work
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		for (Iterator iterator = cards.iterator(); iterator.hasNext();) {
			Card card = (Card) iterator.next();
			if (card.getId() == -1) {
				em.persist(card);
			} else {
				Card oldCard = em.find(Card.class, card.getId());
				oldCard.copy(card);
			}
			// em.persist(card);
		}
		tx.commit();
		em.close();
		// emf.close();
	}

	public static void addCard(Card card) {
		// Start EntityManagerFactory
		// First unit of work
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(card);
		tx.commit();
		em.close();
		// emf.close();
	}
	public static void editCard(Card card) {
		// Start EntityManagerFactory
		// First unit of work
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Card c  =  em.find(Card.class, card.getId());
		c.copy(card);
		tx.commit();
		em.close();
		// emf.close();
	}
	public static void deleteCard(long id) {
		// Start EntityManagerFactory
		// First unit of work
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Card c = em.find(Card.class, id);
		em.remove(c);
		tx.commit();
		em.close();
		// emf.close();
	}
	public static Card getDBCardById(long id) {
		Card retCard = null;
		// Start EntityManagerFactory
		// First unit of work
		EntityManager em = emf.createEntityManager();
		retCard = em.find(Card.class, id);
		em.close();
		// emf.close();
		return retCard;
	}

	public static void closeEntityManagerFactory() {
		emf.close();
	}

}
