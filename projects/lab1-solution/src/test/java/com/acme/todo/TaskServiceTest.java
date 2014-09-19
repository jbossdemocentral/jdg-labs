package com.acme.todo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.infinispan.Cache;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.acme.todo.model.Task;
import com.acme.todo.model.User;

@RunWith(Arquillian.class)
public class TaskServiceTest {
	
	Logger log = Logger.getLogger(this.getClass().getName());

	@Inject
	private TaskService taskservice;
	
	@Inject
	UserService userService;
	
	@PersistenceContext
    EntityManager em;
	
	@Inject
	Cache<String, User> cache;
	
	
	@Deployment
	public static WebArchive createDeployment() {

		return ShrinkWrap
				.create(WebArchive.class, "todo-test.war")
				.addClass(Config.class)
				.addClass(Task.class)
				.addClass(TaskService.class)
				.addClass(User.class)
				.addClass(DefaultUser.class)
				.addClass(UserService.class)
				.addAsResource("import.sql")
				.addAsResource("META-INF/persistence.xml",
						"META-INF/persistence.xml")
			    .addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-deployment-structure.xml"))
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Test
	@InSequence(1)
	public void should_be_deployed() {
		Assert.assertNotNull(taskservice);
	}

	@Test
	@InSequence(2)
	public void testRetrivingTasks() {
		Collection<Task> tasks = taskservice.findAll();
		Assert.assertNotNull(tasks);
	}

	@Test
	@InSequence(3)
	public void testInsertTask() {
		int orgsize = taskservice.findAll().size();
		Task task = new Task();
		task.setTitle("This is a test task");
		task.setCreatedOn(new Date());
		
		taskservice.insert(task);
		Assert.assertEquals(orgsize+1, taskservice.findAll().size());
		
		taskservice.delete(task);
		Assert.assertEquals(orgsize, taskservice.findAll().size());
	}

	@Test
	@InSequence(4)
	public void testUpdateTask() {
		int orgsize = taskservice.findAll().size();
		Task task = new Task();
		task.setTitle("This is the second test task");
		task.setCreatedOn(new Date());
		taskservice.insert(task);
		Assert.assertEquals(orgsize+1, taskservice.findAll().size());

		log.info("###### Inserted task with id " + task.getId());
		task.setDone(true);
		task.setCompletedOn(new Date());
		taskservice.update(task);
		Assert.assertEquals(orgsize+1, taskservice.findAll().size());
		Assert.assertNotNull(task.getCompletedOn());
		Assert.assertEquals(true,task.isDone());
		
		taskservice.delete(task);
		Assert.assertEquals(orgsize, taskservice.findAll().size());
	}
	
	@Test
	@InSequence(5)
	public void testReadPerformance() {
	
		
		// Create 500 tasks
		for (int i = 0; i < 500; i++) {
			User user = new User();
			user.setUsername("testuser" + i);
			List<Task> taskList = new ArrayList<Task>();
			taskList.add(generateTestTasks("Some data may be used in a confirmatory way, typically to verify ...t",true));
			taskList.add(generateTestTasks("program or function that aids the tester",true));
			taskList.add(generateTestTasks("family of test techniques that focus on the test data",true));
			taskList.add(generateTestTasks("Software testing is an important part of the Software Development Life Cycle",true));
			user.setTasks(taskList);
			userService.createUser(user);
		
		}
		
		Random r = new Random(System.currentTimeMillis());
		long startTime = System.currentTimeMillis();
	
		//Execute 1000 reads
		for (int i = 0; i < 1000; i++) {
			cache.get("testuser" + r.nextInt(500));
			
		}
		long stopTime = System.currentTimeMillis();
		
		log.info("#### Executeing 1000 reads took " + (stopTime-startTime) + " ms");
		
		Assert.assertTrue((stopTime-startTime)<400);
	}
	
	private Task generateTestTasks(String title, boolean done) {
		Task task = new Task();
		task.setTitle(title);
		if(done) {
			task.setCompletedOn(new Date());
			task.setDone(true);
		}
		return task;
	}

}
