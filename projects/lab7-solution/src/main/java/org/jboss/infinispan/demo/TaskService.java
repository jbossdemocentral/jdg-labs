package org.jboss.infinispan.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.infinispan.demo.model.Task;

@Stateless
public class TaskService {
	
	@Inject
	RemoteCache<Long, Task> cache;
	
	Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * This methods should return all cache entries, currently contains mockup code. 
	 * @return
	 */
	public Collection<Task> findAll() {
		return cache.getBulk().values();
	}
	

	/**
	 * This method persists a new Task instance
	 * @param task
	 * 
	 */
	public void insert(Task task) {
		if(task.getCreatedOn()==null)
			task.setCreatedOn(new Date());
		task.setId(System.nanoTime());
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
