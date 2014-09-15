package org.jboss.infinispan.demo;

import java.util.Collection;
import java.util.logging.Logger;

import javax.ejb.Stateless;

import org.jboss.infinispan.demo.model.Task;

/**
 * This is a mockup class for the TaskService, as part of the lab you are expected to write the implemenation 
 * @author tqvarnst
 * 
 */
@Stateless
public class TaskService {
	
	static Logger log = Logger.getLogger(TaskService.class.getName());

	//FIXME: 1. Inject a RemoteCache object (needs to be produced in the Config class)
	
	/**
	 * This methods should return all cache entries
	 * @return
	 * 
	 * FIXME: query the cache for all keys and then iterate over the keys into a ArrayList and return the list
	 */
	public Collection<Task> findAll() {
		
		throw new RuntimeException("The metod TaskService.findAll() is not implemented yet");
	}
	
	/**
	 * This method should insert a new task object into the grid. You will have to create a unique id for the task.
	 * @param task
	 * 
	 * FIXME: Create a unique id by querying for the size of the cache (RemoteCache.size()) and increment by one.
	 * FIXME: Put the object into the grid via RemoteCache.putIfAbsent(Object key, Object value) 
	 * 
	 * NOTE: Using the Id field from the model as key is actually a rest from the JPA model, a better way would be to create a unique id baste on for example user, title and creation date)
	 */
	public void insert(Task task) {
		throw new RuntimeException("The metod TaskService.findAll(Task) is not implemented yet");
	}

	/**
	 * This method should update an existing task. Please note that since this is REST application there are no notion of user transaction and the latest update even if it based on a earlier version will override intermediate versions. 
	 * One way to solve this would be to store the hotrod version of the object in the actual object and use RemoteCache.replaceWithVersion(...) and handle a failed update in the UI, but for this application we are happy with overriding.
	 * @param task
	 * 
	 * FIXME: Replace the implementation with RemoteCache.put(Long, Task)
	 */
	public void update(Task task) {
		throw new RuntimeException("The metod TaskService.findAll() is not implemented yet");
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
	 * FIXME: Replace the implementation with RemoteCache.remove(Long)
	 * 
	 */
	public void delete(Long id) {
		throw new RuntimeException("The metod TaskService.findAll() is not implemented yet");
	}
	
	
}
