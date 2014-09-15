package org.jboss.infinispan.demo;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.infinispan.Cache;
import org.jboss.infinispan.demo.model.Task;

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
	 * FIXME: The current implementation is database query, replace it with a JDG query instead
	 */
	public Collection<Task> filter(String input) {
		log.info("### Querying the database for filtered tasks!!!!");
		final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
		
        Root<Task> root = criteriaQuery.from(Task.class);
        criteriaQuery.where(
        		criteriaBuilder.like(
        				criteriaBuilder.upper(root.get("title").as(String.class)), 
        				"%" + input.toUpperCase() + "%"));
        return em.createQuery(criteriaQuery).getResultList();
	}

	/**
	 * This method persists a new Task instance
	 * @param task
	 * 
	 */
	public void insert(Task task) {
		if(task.getCreatedOn()==null) {
			task.setCreatedOn(new Date());
		}
		em.persist(task);
		cache.put(task.getId(),task);
	}


	/**
	 * This method persists an existing Task instance
	 * @param task
	 * 
	 */
	public void update(Task task) {
		Task newTask = em.merge(task);
		em.detach(newTask);
		cache.replace(task.getId(),newTask);
	}
	
	/**
	 * This method deletes an Task from the persistence store
	 * @param task
	 * 
	 */
	public void delete(Task task) {
		//Note object may be detached so we need to tell it to remove based on reference
		em.remove(em.getReference(task.getClass(),task.getId()));
		cache.remove(task.getId());
	}
	
	
	/**
	 * This method is called after construction of this SLSB.
	 * 
	 */
	@PostConstruct
	public void startup() {
		
		log.info("### Querying the database for tasks!!!!");
		final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		final CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
	
		Root<Task> root = criteriaQuery.from(Task.class);
		criteriaQuery.select(root);
		Collection<Task> resultList = em.createQuery(criteriaQuery).getResultList();
		
		for (Task task : resultList) {
			this.insert(task);
		}
		
	}
	
}
