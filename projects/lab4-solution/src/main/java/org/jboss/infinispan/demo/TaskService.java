package org.jboss.infinispan.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.infinispan.Cache;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;
import org.jboss.infinispan.demo.model.Task;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TaskService {
	
	@Inject
	Cache<Long,Task> cache;
	
	Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * This methods should return all cache entries, currently contains mockup code. 
	 * @return
	 */
	public Collection<Task> findAll() {
		return cache.values();
	}
	
	/**
	 * This method filters task based on the input
	 * @param input - string to filter on
	 * @return
	 * 
	 */
	public Collection<Task> filter(String input) {
		SearchManager sm = Search.getSearchManager(cache);
		QueryBuilder qb = sm.buildQueryBuilderForClass(Task.class).get();
		Query q = qb.keyword().onField("title").matching(input).createQuery();
		CacheQuery cq = sm.getQuery(q, Task.class);
		List<Task> tasks = new ArrayList<Task>();
		for (Object object : cq) {
			tasks.add((Task) object);
		}
		return tasks;
	}

	/**
	 * This method persists a new Task instance
	 * 
	 * NOTE: We use System.nanoTime() to create unique ids for the tasks, but please be aware that 
	 * using System.nanoTime() is not recommended and it does NOT guarantee unique id's. Why  we use it here
	 * is because we have a very simplified domain model and normally we would probably connect tasks to a
	 * User object in which case generatign unique id's us much better.
	 * 
	 * @param task
	 * 
	 */
	public void insert(Task task) {
		if(task.getCreatedOn()==null)
			task.setCreatedOn(new Date());
		task.setId(new Long(System.nanoTime())); //Not recommended!!!!
		cache.put(task.getId(),task);
	}


	/**
	 * This method persists an existing Task instance
	 * @param task
	 * 
	 */
	public void update(Task task) {
		cache.replace(task.getId(),task);
	}
	
	/**
	 * This method deletes an Task from the persistence store
	 * @param task
	 * 
	 */
	public void delete(Task task) {
		//Note object may be detached so we need to tell it to remove based on reference
		cache.remove(task.getId());
	}
	
	
	/**
	 * This method is called after construction of this SLSB.
	 * 
	 */
	@PostConstruct
	public void startup() {
		
	}
	
}
