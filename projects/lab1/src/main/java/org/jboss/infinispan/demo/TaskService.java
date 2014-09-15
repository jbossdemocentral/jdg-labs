package org.jboss.infinispan.demo;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
	
	//FIXME: Inject Cache<Long,Task> object

	Logger log = Logger.getLogger(this.getClass().getName());
	
	/**
	 * This methods return all cache entries 
	 * @return
	 * 
	 * FIXME: Replace implementation with Cache.values()
	 */
	public Collection<Task> findAll() {
		log.info("### Querying the database for tasks!!!!");
		final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
		
        Root<Task> root = criteriaQuery.from(Task.class);
        criteriaQuery.select(root);
        return em.createQuery(criteriaQuery).getResultList();
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
	}


	/**
	 * This method persists an existing Task instance
	 * @param task
	 * 
	 * FIXME: Add implementation to also update the Object in the Cache
	 */
	public void update(Task task) {
		em.merge(task);
	}
	
	/**
	 * This method deletes an Task from the persitance store
	 * @param task
	 * 
	 * FIXME: Add implementation to also delete the object from the Cache
	 */
	public void delete(Task task) {
		//Note object may be detached so we need to tell it to remove based on reference
		em.remove(em.getReference(task.getClass(),task.getId()));
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
