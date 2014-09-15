package org.jboss.infinispan.demo;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.infinispan.demo.model.Task;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TaskServiceTest {
	
	
	static Logger log = Logger.getLogger(TaskServiceTest.class.getName());
	

	@Inject
	private TaskService taskservice;
	
	
	/**
	 * 
	 * @return
	 * 
	 * DONE: Uncomment Maven resolver and the addAs Library
	 * 
	 * Note: Because of issue ISPN4468 (https://issues.jboss.org/browse/ISPN-4468) we cannot use HotRod Clients as a module and there for we need to import these in the deployment
	 */
	@Deployment
	public static WebArchive createDeployment() {
		File[] jars = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile();
		
		return ShrinkWrap
				.create(WebArchive.class, "todo-test.war")
				.addClass(Config.class)
				.addClass(Task.class)
				.addClass(TaskService.class)
				.addAsLibraries(jars)
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
		int currentSize = taskservice.findAll().size();
		
		Task task = new Task();
		task.setTitle("This is a test task");
		task.setCreatedOn(new Date());
		taskservice.insert(task);
		Collection<Task> tasks = taskservice.findAll();
		Assert.assertEquals(currentSize+1, tasks.size());		
		
		// Clean up
		taskservice.delete(task);
		tasks = taskservice.findAll();
		Assert.assertEquals(currentSize, tasks.size());
	}

	@Test
	@InSequence(4)
	public void testUpdateTask() {
		int currentSize = taskservice.findAll().size();
		
		// Insert a task
		Task task = new Task();
		task.setTitle("THIS IS A TEST TASK QWERTY!123456");
		task.setCreatedOn(new Date());
		taskservice.insert(task);

		//Collect the tasks
		Collection<Task> tasks = taskservice.findAll();
		Assert.assertEquals(currentSize+1,tasks.size());
		for (Task listTask : tasks) {
			if("THIS IS A TEST TASK QWERTY!123456".equals(listTask.getTitle())) {
				listTask.setDone(true);
				listTask.setCompletedOn(new Date());
				taskservice.update(listTask);
			}
		}
		
		// Make sure that the update hasen't changed the size
		tasks = taskservice.findAll();
		Assert.assertEquals(currentSize+1,tasks.size());
		
		//Make sure that the task has been updated
		for (Task listTask : tasks) {
			if("THIS IS A TEST TASK QWERTY!123456".equals(listTask.getTitle())) {
				Assert.assertNotNull(listTask.getCompletedOn());
				Assert.assertEquals(true,listTask.isDone());
				
				// Clean up
				taskservice.delete(listTask);
				tasks = taskservice.findAll();
				Assert.assertEquals(currentSize, tasks.size());
				
			}
		}
	}
}
