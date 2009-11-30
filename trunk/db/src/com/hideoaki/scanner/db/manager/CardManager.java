package com.hideoaki.scanner.db.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.hideoaki.scanner.db.model.Card;
import com.hideoaki.scanner.db.model.Group;
import com.hideoaki.scanner.db.utils.Privacy;

public class CardManager {
	static EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("openscanner");
	public static final String SQL_SELECT_CARD_BY_ID = "select c from Card c order by c.id asc";
	public static final String SQL_SELECT_CARD_BY_SEARCHKEY = "SELECT c from Card c WHERE c.firstName like :searchKey OR lastName like :searchKey order by c.id asc";

	public static void main(String[] args) {
		Card card1 = new Card("krissada3", "chalermsook", "Project LEader",
				"hideoaki@gmail.com", "Crie Company Limited",
				"http://www.hideoaki.com", "\"400/107 \' Soi", "Bangkok",
				"ไทย", "d", "a", "025894821", "ssss", "0805511559", "aa",
				"sss", "sss", new Group("Test"), Privacy.GROUP);
		// addCard(card1);
		// Card c = getCardById(2);
		List<Card> cards = searchCardDB("chalerm");
		System.out.println(cards.size());
		closeEntityManagerFactory();
	}

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

	public static Card getCardById(long id) {
		Card retCard = null;
		// Start EntityManagerFactory
		// First unit of work
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		List<Card> cards = em.createQuery(SQL_SELECT_CARD_BY_ID)
				.getResultList();
		if (cards.size() > 0) {
			retCard = cards.get(0);
		}
		em.close();
		// emf.close();
		return retCard;
	}

	public static void closeEntityManagerFactory() {
		emf.close();
	}
}
