package com.acme.todo;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.acme.todo.model.User;

@Stateless
public class UserService {

	public static final String DEFAULT_USERNAME = "defaultUser";

	Logger log = Logger.getLogger(this.getClass().getName());

	@PersistenceContext
	EntityManager em;


	/**
	 * This method shoudl return the current user but is currently hard wired to
	 * return the default user the for labs, please note this behavior is
	 * specific to the JDG labs. In a real application the user would probably
	 * be retrieved from the user credentials, but since this this app does not
	 * provide authentication we use a default user
	 * 
	 * @return
	 */
	public User getCurrentUser() {
		return this.getDefaultUser();
	}

	/**
	 * This method returns a default user for labs, please note this behavior is
	 * specific to the JDG labs. In a real application userid would probably be
	 * retrieved from the user credentials, but since this this app does not
	 * provide authentication we use a default user
	 * 
	 * @return
	 */
	private User getDefaultUser() {
		User defaultUser = getUserFromUsername(DEFAULT_USERNAME);
		if (defaultUser == null) {
			log.info("Default user doesn't exists. Creating default user");
			defaultUser = createDefaultUser();
		}
		return defaultUser;
	}
	
	private User createDefaultUser() {
		log.info("### Creating a default user");
		User defaultUser = new User();
		defaultUser.setUsername(DEFAULT_USERNAME);
		em.persist(defaultUser);
		return defaultUser;
	}

	private User getUserFromUsername(String id) {
		log.info("Getting user from the database");
		return em.find(User.class, id);
	}

}
