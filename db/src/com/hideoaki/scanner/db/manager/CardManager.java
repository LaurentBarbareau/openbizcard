package com.hideoaki.scanner.db.manager;

import hello.Message;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.hideoaki.scanner.db.model.Card;
import com.hideoaki.scanner.db.model.Group;
import com.hideoaki.scanner.db.utils.Privacy;

public class CardManager {
	   public static void main(String[] args) {

	        // Start EntityManagerFactory
	        EntityManagerFactory emf =
	                Persistence.createEntityManagerFactory("openscanner");

	        // First unit of work
	        EntityManager em = emf.createEntityManager();
	        EntityTransaction tx = em.getTransaction();
	        tx.begin();

	        Card card1 = new Card("krissada2", "chalermsook", "Project LEader",
					"hideoaki@gmail.com", "Crie Company Limited",
					"http://www.hideoaki.com", "\"400/107 \' Soi", "Bangkok",
					"ไทย", "d", "a", "025894821", "ssss", "0805511559", "aa",
					"sss", "sss", new Group("Test"), Privacy.GROUP);
	        em.persist(card1);

	        tx.commit();
	        em.close();

	    }
	   public static Card searchCard(String searchKey){
		   return null;
	   }
}
