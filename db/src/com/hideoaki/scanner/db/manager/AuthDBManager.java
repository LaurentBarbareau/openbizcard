package com.hideoaki.scanner.db.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.hideoaki.scanner.db.model.User;

public class AuthDBManager {
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
}
