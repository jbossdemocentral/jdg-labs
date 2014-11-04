package com.acme.todo;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.infinispan.Cache;

import com.acme.todo.model.Task;
import com.acme.todo.model.User;

@Stateless
public class UserService {

	Logger log = Logger.getLogger(this.getClass().getName());

	@PersistenceContext
	EntityManager em;

	@Resource(mappedName = "java:comp/EJBContext")
	protected SessionContext sessionContext;
	
	/**
	 * DONE: Inject a cache object
	 */
	@Inject Cache<String, User> cache;
	
	@Inject Cache<String, Task> taskCache;

	/**
	 * This method returns the current user according to the caller principals.
	 * 
	 * DONE: Before getting using from the database try getting him from the cache
	 * 
	 * @return
	 */
	public User getCurrentUser() {
		String username = sessionContext.getCallerPrincipal().getName();
		User user = cache.get(username);

		if (user == null) {
			user = getUserFromUsername(username);
			if (user == null) {
				user = createUser(username);
			}
			cache.put(username,user);
			populateSearchTasks(user);
		}
		return user;
	}
	
	public String getUsernameOfCurrentUser() {
		return sessionContext.getCallerPrincipal().getName();
	}
	
	public void populateSearchTasks(User user) {
		for(Task task : user.getTasks()) {
			taskCache.put(String.format("%s#%d", task.getOwner(), task.getId()), task);
		}
		
	}

	/**
	 * This method will create a database record with the username provided as
	 * principal
	 * 
	 * @param username
	 * @return
	 */
	private User createUser(String username) {
		log.info("Creating a user with username " + username);
		User user = new User();
		user.setUsername(username);
		em.persist(user);
		return user;
	}

	/**
	 * This method will get the user object from the database. If no user exists
	 * null is returned.
	 * 
	 * @param username
	 * @return
	 */
	private User getUserFromUsername(String username) {
		log.info("Getting user " + username + " from the database.");
		return em.find(User.class, username);
	}

}
