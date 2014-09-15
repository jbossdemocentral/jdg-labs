package org.jboss.infinispan.demo.rest;

import java.util.Collection;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.infinispan.Cache;
import org.jboss.infinispan.demo.RequestCache;
import org.jboss.infinispan.demo.TaskService;
import org.jboss.infinispan.demo.model.Task;

/**
 * 
 */
@SuppressWarnings("deprecation")
@Stateless
@Path("/tasks")
public class TaskEndpoint {

	@Inject
	TaskService taskService;
	

	Logger log = Logger.getLogger(this.getClass().getName());


	/**
	 * FIXME: Add request logging by adding user-agent Header to a request cache
	 * @param task
	 * @param headers
	 * @return
	 */
	@POST
	@Consumes("application/json")
	public Response create(Task task, @Context HttpHeaders headers) {
//		requestCache.putAsync(System.nanoTime(), headers.getRequestHeader("user-agent").get(0));
		taskService.insert(task);
		return Response.created(
				UriBuilder.fromResource(TaskEndpoint.class)
						.path(String.valueOf(task.getId())).build()).build();
	}

	/**
	 * FIXME: Add request logging by adding user-agent Header to a request cache
	 * @param headers
	 * @return
	 */
	@GET
	@Produces("application/json")
	public Collection<Task> listAll(@Context HttpHeaders headers) {
//		requestCache.putAsync(System.nanoTime(), headers.getRequestHeader("user-agent").get(0));
		return taskService.findAll();
	}

	/**
	 * FIXME: Add request logging by adding user-agent Header to a request cache
	 * @param value
	 * @param headers
	 * @return
	 */
	@GET
	@Produces("application/json")
	@Path("/filter/{value}")
	public Collection<Task> filter(@PathParam("value") String value,
			@Context HttpHeaders headers) {
//		requestCache.putAsync(System.nanoTime(), headers.getRequestHeader("user-agent").get(0));
		return taskService.filter(value);
	}

	/**
	 * FIXME: Add request logging by adding user-agent Header to a request cache
	 * @param id
	 * @param task
	 * @param headers
	 * @return
	 */
	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response update(@PathParam("id") Long id, Task task,
			@Context HttpHeaders headers) {
//		requestCache.putAsync(System.nanoTime(), headers.getRequestHeader("user-agent").get(0));
		taskService.update(task);
		return Response.noContent().build();
	}
	
}