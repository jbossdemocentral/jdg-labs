package com.acme.todo.rest;

import java.util.Collection;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.acme.todo.UserService;
import com.acme.todo.model.User;

/**
 * 
 */
@Stateless
@Path("/admin")
public class AdminEndpoint {

	Logger log = Logger.getLogger(this.getClass().getName());

	@Inject
	UserService userService;
	
	@GET
	@Path("/user/search/{searchStr}")
	@Produces("application/json")
	public Collection<User> searchUser(@PathParam("searchStr") String searchStr) {
		log.info("Searching for User with search string " + searchStr);
		return userService.searchUser(searchStr);
	}
	
	@GET
	@Produces("application/json")
	public String sayHi() {
		return String.format("Hi %s!", userService.getCurrentUser().getUsername());
	}
}