package com.acme.todo;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.infinispan.Cache;

import com.acme.todo.model.User;

@Stateless
public class UserService {

	public static final String DEFAULT_USERNAME = "defaultUser";

	Logger log = Logger.getLogger(this.getClass().getName());

	@PersistenceContext
	EntityManager em;
	
	@Inject
	Cache<String, User> cache;

	/**
	 * This metod produces a default user for labs, please note this behavior is
	 * specfic to the JDG labs. In a real application userid would probably be
	 * retrieved from the user creditials, but since this this app does not
	 * provide authentication we use a default user
	 * 
	 * @return
	 */
	@Produces
	@DefaultUser
	public User getDefaultUser() {
		// First try to find a user in the database

		log.info("### Getting default user from the database");
		User defaultUser = cache.get(DEFAULT_USERNAME);
		
		if (defaultUser==null) {
			defaultUser = getUserFromUsername(DEFAULT_USERNAME);
			// if defaultUser is still null it doesn't exists so we create one
			if (defaultUser == null) {
				defaultUser = createDefaultUser();
			}
			cache.put(defaultUser.getUsername(), defaultUser);
		}
		return defaultUser;
	}

	public User createDefaultUser() {
		log.info("### Creating a default user");
		User defaultUser = new User();
		defaultUser.setUsername(DEFAULT_USERNAME);
		em.persist(defaultUser);
		return defaultUser;
	}
	
	public User getUserFromUsername(String id) {
		return em.find(User.class, id);
	}
	
	public void createUser(User user) {
		em.persist(user);
		cache.put(user.getUsername(),user);
	}

}
