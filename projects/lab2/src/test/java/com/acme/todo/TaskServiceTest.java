package com.acme.todo;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.acme.todo.model.Task;
import com.acme.todo.model.User;

@RunWith(Arquillian.class)
public class TaskServiceTest {
	
	Logger log = Logger.getLogger(this.getClass().getName());

	@Inject
	private TaskService taskservice;
	
	
	@PersistenceContext
    EntityManager em;
	
//	@Inject
//	Cache<String, User> cache;
	
	@Inject
	TaskService taskService;
	
	
	@Deployment
	public static WebArchive createDeployment() {

		return ShrinkWrap
				.create(WebArchive.class, "todo-test.war")
				.addClass(Config.class)
				.addClass(Task.class)
				.addClass(TaskService.class)
				.addClass(User.class)
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
		
		Task task2 = new Task();
		task2.setTitle("This is another test task");
		task2.setCreatedOn(new Date());
		
		taskservice.insert(task2);
		Assert.assertEquals(orgsize+2, taskservice.findAll().size());
		
		taskservice.delete(task.getId());
		taskservice.delete(task2.getId());
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
		Date taskUpdatedDate = new Date();
		task.setCompletedOn(taskUpdatedDate);
		taskservice.update(task);	
		
		for (Task listTask : taskservice.findAll()) {
			if("This is the second test task".equals(listTask.getTitle())) {
				Assert.assertEquals(true,listTask.isDone());
				Assert.assertNotNull(listTask.getCompletedOn());
			}
		}
		taskservice.delete(task.getId());
		Assert.assertEquals(orgsize, taskservice.findAll().size());
	}
	
	@Test
	@InSequence(5)
	public void testFilterTask() {
		Task t1 = new Task();
		t1.setTitle("Complete the labs in JDG + EAP");
		t1.setCreatedOn(new Date());
		taskservice.insert(t1);
		
		Task t2 = new Task();
		t2.setTitle("Sell more JDG");
		t2.setCreatedOn(new Date());
		taskservice.insert(t2);
		
		Task t3 = new Task();
		t3.setTitle("Send feedback on the training to tqvarnst@redhat.com");
		t3.setCreatedOn(new Date());
		taskservice.insert(t3);
		
		
		Task t4 = new Task();
		t4.setTitle("Send feedback to sme-jdg to the developers how great JDG is");
		t4.setCreatedOn(new Date());
		taskservice.insert(t4);
		
		Task t5 = new Task();
		t5.setTitle("Sell more EAP");
		t5.setCreatedOn(new Date());
		taskservice.insert(t5);
		
		
		Collection<Task> tasks = taskservice.filter("EAP");
		Assert.assertEquals(2, tasks.size());
		tasks = taskservice.filter("SELL");
		Assert.assertEquals(2, tasks.size());
		tasks = taskservice.filter("FeedBack");
		Assert.assertEquals(2, tasks.size());
		
		taskservice.delete(t1.getId());
		taskservice.delete(t2.getId());
		taskservice.delete(t3.getId());
		taskservice.delete(t4.getId());
		taskservice.delete(t5.getId());
	}
	
	@Test
	@Ignore
	@InSequence(6)
	public void testReadPerformance() {
		
		int numberOfTasks = 100;
		int numberOfReads = 1000;
		int timeAllowed = 400;
		

		//Creating tasks
		for (int i = 0; i < numberOfTasks; i++) {
			Task task = new Task();
			task.setTitle("This is the " + i + " test task");
			task.setCreatedOn(new Date());
			taskservice.insert(task);
		}
		long startTime = System.currentTimeMillis();
		//Executing reads
		for (int i = 0; i < numberOfReads; i++) {
			taskservice.findAll();
		}
		long stopTime = System.currentTimeMillis();
		String assertMsg = String.format("Failed to execute %s reads within %s ms. Actual executiong time was %d ms",numberOfReads,timeAllowed,(stopTime - startTime));
		String logMsg = String.format("#### Executing %s reads tool %d ms",numberOfReads,(stopTime - startTime));
		log.info(logMsg);
		Assert.assertTrue(assertMsg,(stopTime - startTime) < timeAllowed);
	}
}
