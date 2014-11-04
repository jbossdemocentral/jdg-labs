package com.acme.todo;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.Cache;

import com.acme.todo.model.Task;
import com.acme.todo.model.User;

@Stateless
public class UserService {

	Logger log = Logger.getLogger(this.getClass().getName());

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
			user = createUser(username);
		}
		return user;
	}
	
	public String getUsernameOfCurrentUser() {
		String username = sessionContext.getCallerPrincipal().getName();
		return (username==null || "".equals(username))?"testUser":username;
	}
	

	/**
	 * This method will create a User in the cache with the username provided as
	 * principal
	 * 
	 * @param username
	 * @return
	 */
	private User createUser(String username) {
		log.info("Creating a user with username " + username);
		User user = new User();
		user.setUsername(username);
		cache.put(username,user);
		return user;
	}
}
