package com.acme.todo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.infinispan.Cache;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;

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
		}
		return user;
	}
	
	/**
	 * This method returns a List of user where username or email matches the search string
	 * 
	 * @param searchStr
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<User> searchUser(String searchStr) {
		searchStr = String.format("*%s*", searchStr.toLowerCase());
		SearchManager sm = Search.getSearchManager(cache);
		QueryBuilder qb = sm.getSearchFactory().buildQueryBuilder().forEntity(User.class).get();
		Query q = qb.bool()
				.should(qb.keyword().wildcard().onField("username").matching(searchStr).createQuery())
				.should(qb.keyword().wildcard().onField("email").matching(searchStr).createQuery())
				.createQuery();
		CacheQuery cq = sm.getQuery(q, User.class);
		return (List<User>)(List)cq.list();
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
