package com.acme.todo;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.acme.todo.model.Task;
import com.acme.todo.model.User;

/**
 * This class is used to query, insert or update Task object.
 * @author tqvarnst
 *
 */
@Stateless
public class TaskService {

	@PersistenceContext
    EntityManager em;
	
	@Inject
	@DefaultUser
	@SessionScoped
	User currentUser;
	
	@Inject
	UserService userService;
	
	
	
	
	//FIXME: Inject Cache<Long,Task> object

	Logger log = Logger.getLogger(this.getClass().getName());
	
	/**
	 * This methods return all cache entries 
	 * @return
	 * 
	 * FIXME: Replace implementation with Cache.values()
	 */
	public Collection<Task> findAll() {
		List<Task> tasks = currentUser.getTasks();
		Collections.sort(tasks, new Comparator<Task>() {
			@Override
			public int compare(Task o1, Task o2) {
				return o1.getCreatedOn().compareTo(o2.getCreatedOn());
			}});
        return tasks;
	}

	/**
	 * This method persists a new Task instance
	 * @param task
	 * 
	 * FIXME: Add implementation to also update the Cache with the new object
	 */
	public void insert(Task task) {
		if(task.getCreatedOn()==null) {
			task.setCreatedOn(new Date());
		}
		em.persist(task);
		currentUser.getTasks().add(task);
	}


	/**
	 * This method persists an existing Task instance
	 * @param task
	 * 
	 * FIXME: Add implementation to also update the Object in the Cache
	 */
	public void update(Task task) {
		em.merge(em.getReference(Task.class, task.getId()));
	}
	
	/**
	 * This method deletes an Task from the persitance store
	 * @param task
	 * 
	 * FIXME: Add implementation to also delete the object from the Cache
	 */
	public void delete(Task task) {
		//Note object may be detached so we need to tell it to remove based on reference
		currentUser.getTasks().remove(task);
//		currentUser.getTasks().remove(task);
//		em.merge(currentUser);
		
		
	}
	
	
	/**
	 * This method is called after construction of this SLSB.
	 * 
	 * FIXME: Replace implementation to read existing Tasks from the database and add them to the cache
	 */
	@PostConstruct
	public void startup() {
	}

}
