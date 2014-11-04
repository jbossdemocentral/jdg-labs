# JDG + EAP Lab 4 Guide
This explains the steps for lab 4, either follow them step-by-step or if you 
feel adventurous try to accomplish goals without the help of the step-by-step guide.

## Background 
The sales account manager for Acme Inc from the RDBMS vendor (Cleora) had a meeting with the CIO of Acme this week. Because Acme Inc used JDG to improve performance instead of purchasing more DB licenses the sales account manager of Cleora decided to try to make up for the lost sales, by raising the price on the licenses that Acme are currently using. The discussion has been harsh and the CIO are really angry at the sales account manager from Cleora. At a similar meeting with the Red Hat Sales team with the CIO the Red Hat Solutions Architect (who the CIO really trusts for advices) suggested that Acme removes the database from the application an instead starts using JDG as the primary data store.


## Use-case
Rewrite the application to only use JDG library mode, configure a file store and configure cluster.

 

## These are the main tasks of lab 3
To save time we have prepared the code by removing references to JPA and implementing a flatter datamodel. 

1. Remove JPA code from Task and TaskService. __Already DONE!!!__, just review the code
2. Configure a file store (using SingleFileStore)
3. Configure the cache for clustering

## Step-by-Step

1. Review the code. Compare TaskService, UserService, User, Task in __lab4__ with __lab3__.
	
1. Configure the file cache store by opening `src/main/java/com/acme/todo/Config.java` and add the following to Configuration builder:

		.persistence()
			.addSingleFileStore()
				.location(System.getProperty("jboss.home.dir") + "/cache-store")
				.fetchPersistentState(true)
				.ignoreModifications(true)
				.shared(false)
				.preload(false)
				.async()
					.enable()
					.threadPoolSize(500)
					.flushLockTimeout(1)
					.modificationQueueSize(1024)
					.shutdownTimeout(25000)
					
6. Run the JUnit test to verify that your changes works. 
7. Add Clustering using CacheMode DIST_ASYNC with 2 owners to Configuration builder.
		
		...
		Configuration loc = new ConfigurationBuilder().jmxStatistics()
			.enable() // Enable JMX statistics
			.clustering().cacheMode(CacheMode.DIST_ASYNC) 
			.hash().numOwners(1)
		...

	Normally we would configure 2 or more owners, but since we later will start two nodes have two nodes with two owners are essentially the same as running in replicated mode.
	
8. Configure the transport for the cluster by adding `jgroups-udp.xml` to the `GlobalConfigurationBuilder`


		GlobalConfiguration glob = new GlobalConfigurationBuilder()
			.clusteredDefault()
			.transport().addProperty("configurationFile", "jgroups-udp.xml")
			.globalJmxStatistics().allowDuplicateDomains(true).enable()
			.build();
			
9. Run the JUnit test again to verify your changes

10. Setup a EAP nodes using clustered JDG

		$ sh init-lab.sh --lab=4
		
11. Start the first node using

		$ target/node1/jboss-eap-6.3/bin/standalone.sh -Djgroups.bind_addr=0.0.0.0 -Djboss.node.name=jdg-1
		
12. Start the second node using port offset

		$ target/node2/jboss-eap-6.3/bin/standalone.sh -Djgroups.bind_addr=0.0.0.0 -Djboss.node.name=jdg-2 -Djboss.socket.binding.port-offset=100 
		
13. Deploy the application and test that everything works as before.

		$ cd projects/lab4
		$ mvn clean package 
		$ mvn jboss-as:deploy
		$ mvn jboss-as:deploy -Djboss-as.port=10099
		
14. Open two browser windows, one to [http://localhost:8080/todo](http://localhost:8080/todo) and another to [http://localhost:8180/todo](http://localhost:8180/todo). Verify that you can add content in one window and that they appear when you reload the other window.

15. Congratulation you are finished with lab 4