package com.hideoaki.scanner.db.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.hideoaki.scanner.db.model.User;

public class UserDBManager {
	public static final String SQL_SELECT_USER = "SELECT u from User where u.username = :userName and u.password = :password";
	
	public static User login(String username, String password) {
		EntityManager em = CardDBManager.emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Query query = em.createQuery(SQL_SELECT_USER);
		query.setParameter("userName", username);
		query.setParameter("password", password);
		List<User> users = query.getResultList();
		em.close();
		if (users.size() > 0) {
			users.get(0);
		}
		
		return null;
	}
	public static void addUser(User user) {
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
	public static void editCard(User user) {
		// Start EntityManagerFactory
		// First unit of work
		EntityManager em = CardDBManager.emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		User c  =  em.find(User.class, user.getId());
		c.copy(user);
		tx.commit();
		em.close();
		// emf.close();
	}
	public static void deleteUser(long id) {
		// Start EntityManagerFactory
		// First unit of work
		EntityManager em = CardDBManager.emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		User c = em.find(User.class, id);
		em.remove(c);
		tx.commit();
		em.close();
		// emf.close();
	}
}
