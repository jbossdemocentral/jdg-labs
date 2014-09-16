package org.jboss.infinispan.demo.rest;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.jboss.infinispan.demo.TaskService;
import org.jboss.infinispan.demo.model.Task;

/**
 * 
 */
@Stateless
@Path("/tasks")
public class TaskEndpoint
{

   @Inject
   TaskService taskService;
	
   @POST
   @Consumes("application/json")
   public Response create(Task task)
   {
      taskService.insert(task);
      return Response.created(UriBuilder.fromResource(TaskEndpoint.class).path(String.valueOf(task.getId())).build()).build();
   }

   @GET
   @Produces("application/json")
   public Collection<Task> listAll()
   {
      return taskService.findAll(); 
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(@PathParam("id") Long id,Task task)
   {
	  taskService.update(task);
      return Response.noContent().build();
   }
}