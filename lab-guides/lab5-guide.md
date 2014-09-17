# JDG + EAP Lab 5 Guide
This explains the steps for lab 5, either follow them step-by-step or if you 
feel adventurous try to accomplish goals without the help of the step-by-step guide.

## Background 
The TODO application is a huge success and over 1 million users are today using it to synchronise the task list between different devices. Even though each entry are typically small the data is growing fast. By mid next year numbers of users and expected to grow to 5 millions. However the success gives new challenges. The memory used to store tasks is growing fast.

### Memory usage today in the future
The average length of a title is 20 characters (about 80 bytes). Together with two Date objects (32 bytes each) and the other fields we can assume that each Task object will take about 200 bytes of memory. The average number of tasks per user is 50. 

Today the effective data storage used (data without meta-data) is about 10 GB of data [1 million x 50 tasks per user x 200 bytes per task / (1024^3)].

Mid next year the prediction is that we will have to be able to store 50 GB of data in the grid. 

Given that TODO application is using distributed with 2 owners per object, the used storage is probably closer to 25 GB today and 125 GB mid next year.

The TODO application today runs on 4 mid sized virtual servers with 10 GB allocated to JVM on each host. 

To meet the predicted increase we have a accouple of options:

1. Increase the number of server 5 times
3. Layer our solution by moving to client/server mode for the JDG cache allowing applications for more effective JVM memory management. (Decouple data from application)

## These are the main tasks of lab 3

1. List pros and cons with different solutions to the memory issue.
2. Setup a standalone node of JDG
3. Rewrite the application to use HotRod client instead of native client.
 
## Step-by-Step
1. Discussion pros and cons with different options for solution to the memory usage issue
2. Setup of a standalone node, but running the init-lab.sh for lab5

		$ ./init-lab.sh --lab=5
		
3. Configure the cache in the JDG Server, Open `target/jboss-datagrid-6.3.0-server/standalone/configuration/standalone.xml` and add the following to the `infinispan:server:core` subsystem inside the `cache-container` named `local`:

		<local-cache name="tasks" start="EAGER">
			<locking isolation="NONE" acquire-timeout="30000" concurrency-level="1000" striping="false"/>
			<transaction mode="NONE"/>
		</local-cache>
		
4. Start the JDG server (and EAP if not already started)

		$ target/jboss-datagrid-6.3.0-server/bin/standalone.sh -Djboss.socket.binding.port-offset=100
		$ target/jboss-eap-6.3/bin/standalone.sh 

5. Setup development and runtime dependencies by adding `infinispan-client-hotrod` and `infinispan-commons` as dependencies in pom.xml

	_Note:_ Because of a known bug for HotRod modules we will note use the EAP modules for HotRod. Instead we are going to ship the libraries in WEB-INF/lib by setting scope compile instead of scope provided.
	
6. Write CDI producers in the `org.jboss.infinispan.demo.Config`:

		@Produces
		public RemoteCache<Long, Task> getRemoteCache() {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.addServer().host("localhost").port(11322);
			return new RemoteCacheManager(builder.build(), true).getCache("tasks");
		}

7. Implement the following methods and field in the TaskService class

	Field cache should look like this
	
		@Inject
		RemoteCache<Long, Task> cache;
	
	TaskService.findAll(), should look like this
		
		public Collection<Task> findAll() {
			return cache.getBulk().values();
		}
	
	TaskService.insert(Task), should look like this
	
		public void insert(Task task) {
			if(task.getCreatedOn()==null) {
				task.setCreatedOn(new Date());
			}
			int nextKey = cache.size() + 1;
			task.setId(new Long(nextKey));
			cache.putIfAbsent(task.getId(), task);
		}
		
	TaskService.update(Task), should look like this
	
		public void update(Task task) {
			cache.replace(task.getId(), task);			
		}
	
	TaskService.delete(Task), should look like this
	
		public void delete(Task task) {
			this.delete(task.getId());
		}
	
		
9. Run the arquillian tests to verify that the application.
10. Deploy the application.

	$ mvn clean package jboss-as:deploy
	
11. Verify that everything works by opening a browser to [http://localhost:8080/todo](http://localhost:8080/todo)





