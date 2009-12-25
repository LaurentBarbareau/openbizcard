package com.hideoaki.scanner.db.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.hideoaki.scanner.db.model.Group;

public class GroupDBManager {
	public static final String SQL_SELECT_GROUP = "SELECT g from Group g";

	public static List<Group> loadDBGroup() {
		EntityManager em = CardDBManager.emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Query query = em.createQuery(SQL_SELECT_GROUP);
		List<Group> cards = query.getResultList();
		em.close();
		return cards;
	}

	public static void addGroup(Group user) {
		// Start EntityManagerFactory
		// First unit of work
		EntityManager em = CardDBManager.emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(user);
		tx.commit();
		em.close();
		// emf.close();
	}

	public static void editGroup(Group group) {
		// Start EntityManagerFactory
		// First unit of work
		EntityManager em = CardDBManager.emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Group c = em.find(Group.class, group.getId());
		c.copy(group);
		tx.commit();
		em.close();
		// emf.close();
	}

	public static void deleteGroup(long id) {
		// Start EntityManagerFactory
		// First unit of work
		EntityManager em = CardDBManager.emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Group c = em.find(Group.class, id);
		em.remove(c);
		tx.commit();
		em.close();
		// emf.close();
	}
}
