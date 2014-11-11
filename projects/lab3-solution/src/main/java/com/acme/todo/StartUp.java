package com.acme.todo;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.infinispan.Cache;

import com.acme.todo.model.User;

@Startup
@Singleton
public class StartUp {

	Logger log = Logger.getLogger(this.getClass().getName());

	@Inject Cache<String, User> cache;
	
	@PersistenceContext
	EntityManager em;
	
	@PostConstruct
	private void atStartup() {
		List<User> users = em.createQuery("select e from User e", User.class).getResultList();
		for (User user : users) {
			cache.putIfAbsent(user.getUsername(), user);
		}
	}
}
