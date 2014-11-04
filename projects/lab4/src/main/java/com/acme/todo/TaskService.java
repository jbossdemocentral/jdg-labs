package com.acme.todo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;

import com.acme.todo.model.Task;
import com.acme.todo.model.User;

/**
 * This class is used to query, insert or update Task object.
 * 
 * @author tqvarnst
 * 
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TaskService {

	@Inject
	UserService userService;

	@Inject
	Cache<String, Task> taskCache;

	Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * This methods should return the users all tasks code.
	 * 
	 * Note: It's not recommended to get all the cache values and
	 * filer them since a cache might contain allot of values. However in Lab4
	 * we want to show replication in a nice way so instead of making the object
	 * model more complex we use this technique.
	 * 
	 * @return
	 */
	public List<Task> findAll() {
		User user = userService.getCurrentUser();
		Collection<Task> values = taskCache.values();
		List<Task> tasks = new ArrayList<Task>();
		for (Object obj : values) {
			if (obj instanceof Task) {
				Task task = (Task) obj;
				if (task.getOwner().equals(user)) {
					tasks.add(task);
				}
			}
		}
		return tasks;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<Task> filter(String value) {
		User user = userService.getCurrentUser();
		String searchStr = String.format("*%s*", value.toLowerCase());
		SearchManager sm = Search.getSearchManager(taskCache);
		QueryBuilder qb = sm.getSearchFactory().buildQueryBuilder()
				.forEntity(Task.class).get();
		Query q = qb
				.bool()
				.must(qb.keyword().wildcard().onField("title")
						.matching(searchStr).createQuery())
				.must(qb.keyword().onFields("owner_username").ignoreAnalyzer()
						.matching(user.getUsername()).createQuery())
				.createQuery();
		CacheQuery cq = sm.getQuery(q, Task.class);
		return (Collection<Task>) (List) cq.list();
	}

	/**
	 * This method persists a new Task instance
	 * 
	 * @param task
	 * 
	 *            DONE: Insert the new object into the cache as well
	 */
	public void insert(Task task) {
		User user = userService.getCurrentUser();
		if (task.getCreatedOn() == null) {
			task.setCreatedOn(new Date());
		}
		task.setId(System.nanoTime()); // Generates a unique id
		task.setOwner(userService.getCurrentUser());
		taskCache.putIfAbsent(
				String.format("%s#%d", user.getUsername(), task.getId()), task);
	}

	/**
	 * This method persists an existing Task instance
	 * 
	 * @param task
	 * 
	 *            DONE: Add implementation to also update the task object in the
	 *            cache Cache
	 */
	public void update(Task task) {
		User user = userService.getCurrentUser();
		taskCache.put(String.format("%s#%d", user.getUsername(), task.getId()),
				task);
	}

	/**
	 * This method deletes an Task from the persistence store
	 * 
	 * @param taskId
	 */
	public void delete(Long taskId) {
		User user = userService.getCurrentUser();
		Task fakeTask = new Task();
		fakeTask.setId(taskId);
		taskCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES)
				.remove(String.format("%s#%d", user.getUsername(), taskId));
	}

}
