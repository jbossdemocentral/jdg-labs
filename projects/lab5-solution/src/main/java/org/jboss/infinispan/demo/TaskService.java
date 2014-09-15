package org.jboss.infinispan.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.infinispan.demo.model.Task;


@Stateless 
public class TaskService {
	
	static Logger log = Logger.getLogger(TaskService.class.getName());

	//DONE: 1. Inject a RemoteCache object (needs to be produced in the Config class)
	@Inject
	RemoteCache<Long, Task> cache;

	/**
	 * This methods should return all cache entries
	 * @return
	 * 
	 * DONE: query the cache for all keys and then iterate over the keys and place the values into a ArrayList and return the list
	 */
	public Collection<Task> findAll() {
		Set<Long> keySet = cache.keySet();
		ArrayList<Task> tasks = new ArrayList<Task>();
		for (Long key : keySet) {
			Task task = cache.get(key);
			tasks.add(task);
		}
		return tasks;
	}
	
	/**
	 * This method should insert a new task object into the grid. You will have to create a unique id for the task.
	 * @param task
	 * 
	 * DONE: If the new task doesn't have a createdOn date (UI issue) we add the current date of now
	 * DONE: Create a unique id by querying for the size of the cache (RemoteCache.size()) and increment by one.
	 * DONE: Put the object into the grid via RemoteCache.putIfAbsent(Object key, Object value)
	 * 
	 * NOTE: Using the Id field from the model as key is actually a rest from the JPA model, a better way would be to create a unique id baste on for example user, title and creation date)
	 */
	public void insert(Task task) {
		if(task.getCreatedOn()==null) {
			task.setCreatedOn(new Date());
		}
		int nextKey = cache.size() + 1;
		task.setId(new Long(nextKey));
		cache.putIfAbsent(task.getId(), task);
	}

	/**
	 * This method should update an existing task. Please note that since this is REST application there are no notion of user transaction and the latest update even if it based on a earlier version will override intermediate versions. 
	 * One way to solve this would be to store the hotrod version of the object in the actual object and use RemoteCache.replaceWithVersion(...) and handle a failed update in the UI, but for this application we are happy with overriding.
	 * @param task
	 * 
	 * DONE: Replace the implementation with RemoteCache.put(Long, Task)
	 */	
	public void update(Task task) {
		cache.put(task.getId(), task);
			
	}
	
	/**
	 * This method deletes an object from the cache
	 * @param task
	 * @see TaskService.delete(Long)
	 */
	public void delete(Task task) {
		this.delete(task.getId());
	}
	
	/**
	 * This method deletes an object from the cache
	 * Since we now (or will sone) have a permanent store we need to implement a delete method for the tests to be able to delete any object they add
	 * @param id
	 * 
	 * DONE: Replace the implementation with RemoteCache.remove(Long)
	 * 
	 */
	public void delete(Long id) {
		cache.remove(id);
	}
	
	
}
