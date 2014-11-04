package com.acme.todo;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.infinispan.Cache;

import com.acme.todo.model.Task;
import com.acme.todo.model.User;

@Startup
@Singleton
public class StartupBean {
	
	@Inject Cache<String, Task> taskCache;

	@PostConstruct
	public void startup() {
		String username = "defaultUser";
		User user = new User();
		user.setUsername(username);
		Task t1 = new Task();
		t1.setId(1);
		t1.setTitle("Dummy task 1");
		t1.setCreatedOn(new Date());
		t1.setOwner(user);
		taskCache.putIfAbsent(String.format("%s#%d", username, t1.getId()), t1);
	}
}
