# JDG + EAP Lab 2 Guide
This explains the steps for lab 2, either follow them step-by-step or if you 
feel adventurous try to accomplish goals without the help of the step-by-step guide.

## Background 
In Lab 1 we implemented a side cache using JDG to speed up reads, but master 
data store is still the database. So far however the data access is only a using
the common CRUD (Create, Read, Update and Delete) operations. Since JDG is a 
key/value store these operations are easy to implement. 

A competing vendor that has a similar task management solution released a new feature where users can filter their tasks. Something our customers has been requesting for a while. Our marketing director demands that we ASAP add this feature. An external consultant are hired and to implement this feature, but since he wasn't familiar with JDG he implemented the filter solution using JPA query. This has once again put more load on the database and performance has degraded.

JDG has very advanced querying capabilities in library mode (remote is still tech preview)

## Use-case
You are tasked to rewrite the filter implementation using queries in JDG instead of JPA queries. However the Task data model is used in the native mobile application and since it will take a while before we can update the mobile application you are not allowed to change the org.jboss.infinspan.demo.model.Task class.

## Objectives
Your task in Lab 2 re-implement the filtering method, but using JDG Queries. 
The UI and REST methods are already implemented.

Basically you should replace the DB Query with a JDG Query and you will have to 
do this without modifying the org.jboss.infinspan.demo.model.Task class. 

To to this we need to do the following:

1. Add developer dependencies:
	Update the pom.xml and add developer dependency to infinispan-query
2. Add runtime dependencies
	Update jboss-deployment-structure.xml to add runtime dependency to infinispan-query
3. Update configuration
	Enable indexing in the API Configuration. Hint [See the Infinispan Query Index](http://red.ht/1nSniBo)
	  - The index should only be persisted in RAM
	  - Since we will later deploy this on mulitple EAP instances we need to allow for shared indexes.
	  - The index should be based on the `title` field from `org.jboss.infinspan.demo.model.Task`
4. Write the implementation to Query JDG
	Replace the implementation of `TaskSerivce.filter(String)` to query JDG instead of DB

## Step-by-Step

### Add developer dependencies

1. Open pom.xml
1. Add the followwing dependency

		<dependency>
			<groupId>org.infinispan</groupId>
			<artifactId>infinispan-query</artifactId>
			<scope>provided</scope>
		</dependency>
		
  
### Add runtime dependencies

1. Open `src/main/webapp/WEB-INF/jboss-deployment-structure.xml` 
1. Add `org.infinispan.query` module. The content of the file should look like this:

		<jboss-deployment-structure>
			<deployment>
				<dependencies>
					<module name="org.infinispan" slot="jdg-6.3" services="import"/>
					<module name="org.infinispan.cdi" slot="jdg-6.3" meta-inf="import"/>
					<module name="org.infinispan.query" slot="jdg-6.3" services="import"/>
				</dependencies>
			</deployment>
		</jboss-deployment-structure>

1. After saving It's recommended to run the JUnit test to verify that everything deploys fine.

### Save Tasks so that we can query them
In order for us to create queries we need to flaten our object model. Since we are still using a database we do not want to destroy the complex hibernate model yet, so instead we will save the Tasks nativly using *username_id* as unique key.

This has already been implemented for you but review the code in `src/main/java/com/acme/todo/TaskService.java`. Basically every time a Task is inserted/updated or deleted we do the same to a Task instance in the cache. 


### Update the configuration

1. Open `src/main/java/com/acme/todo/Config.java`
1. After the global configuration we need to create a `SearchMapping` object that tells JDG how to index `Task` objects 

		SearchMapping mapping = new SearchMapping();
			mapping
				.entity(Task.class).indexed().providedId()
					.property("title", ElementType.METHOD).field()
					.property("owner", ElementType.METHOD).indexEmbedded().depth(1).prefix("owner_")
				.entity(User.class)
					.property("username", ElementType.METHOD).containedIn();
	
			Properties properties = new Properties();
			properties.put(org.hibernate.search.Environment.MODEL_MAPPING, mapping);
			properties.put("default.directory_provider", "ram");
			properties.put("default.exclusive_index_use", "true");
			properties.put("default.indexmanager", "near-real-time");
			
1. Now we can enable the index on the Configuration by adding `.indexing().enable().withProperties(properties)` to the fluid API before `.build()`

		.indexing().enable().withProperties(properties)

1. The config class should now look like this, when you are done:

		package com.acme.todo;

		import java.lang.annotation.ElementType;
		import java.util.Properties;

		import javax.annotation.PreDestroy;
		import javax.enterprise.context.ApplicationScoped;
		import javax.enterprise.inject.Default;
		import javax.enterprise.inject.Produces;

		import org.apache.lucene.analysis.standard.StandardAnalyzer;
		import org.apache.solr.analysis.LowerCaseFilterFactory;
		import org.apache.solr.analysis.NGramFilterFactory;
		import org.apache.solr.analysis.StandardTokenizerFactory;
		import org.hibernate.search.cfg.SearchMapping;
		import org.infinispan.configuration.cache.Configuration;
		import org.infinispan.configuration.cache.ConfigurationBuilder;
		import org.infinispan.configuration.global.GlobalConfiguration;
		import org.infinispan.configuration.global.GlobalConfigurationBuilder;
		import org.infinispan.eviction.EvictionStrategy;
		import org.infinispan.manager.DefaultCacheManager;
		import org.infinispan.manager.EmbeddedCacheManager;
		import org.infinispan.transaction.LockingMode;
		import org.infinispan.transaction.TransactionMode;

		import com.acme.todo.model.Task;
		import com.acme.todo.model.User;

		/**
		 * This is Class will be used to configure JDG Cache
		 * @author tqvarnst
		 * 
		 * DONE: Add implementation that Produces configuration for the default cache
		 *
		 */
		public class Config {

			private EmbeddedCacheManager manager;

			@Produces
			@ApplicationScoped
			@Default
			public EmbeddedCacheManager defaultEmbeddedCacheConfiguration() {
				if (manager == null) {
					GlobalConfiguration glob = new GlobalConfigurationBuilder()
							.globalJmxStatistics().allowDuplicateDomains(true).enable() // This
							// method enables the jmx statistics of the global
							// configuration and allows for duplicate JMX domains
							.build();
			
					SearchMapping mapping = new SearchMapping();
					mapping
						.entity(Task.class).indexed().providedId()
							.property("title", ElementType.METHOD).field()
							.property("owner", ElementType.METHOD).indexEmbedded().depth(1).prefix("owner_")
						.entity(User.class)
							.property("username", ElementType.METHOD).containedIn();
			
					Properties properties = new Properties();
					properties.put(org.hibernate.search.Environment.MODEL_MAPPING, mapping);
					properties.put("default.directory_provider", "ram");
					properties.put("default.exclusive_index_use", "true");
					properties.put("default.indexmanager", "near-real-time");
			
			
			
					Configuration loc = new ConfigurationBuilder().jmxStatistics()
							.enable() // Enable JMX statistics
							.eviction().strategy(EvictionStrategy.NONE) // Do not evic objects
							.transaction().transactionMode(TransactionMode.TRANSACTIONAL).lockingMode(LockingMode.OPTIMISTIC)
							.indexing().enable().withProperties(properties).indexLocalOnly(true)
							.build();
			
					manager = new DefaultCacheManager(glob, loc, true);
				}
				return manager;
			}

			@PreDestroy
			public void cleanUp() {
				manager.stop();
				manager = null;
			}
		}
		
		
 

### Write the implementation to Query JDG

1. Open `src/main/java/com/acme/todo/TaskService.java`
1. Navigate to the `filter(String)` method and delete the current implementation
1. First we need to create a search string out of the filter value that the user supplies. We will use wildcard '*' before and after and search useing lower case which is the default tokenizer.
	
		String searchStr = String.format("*%s*", value.toLowerCase());
		
1. In order create QueryBuilder and run that query we need a `SearchMangaer` object. We can get that by calling `Search.getSearchManager(cache)`
		
		SearchManager sm = Search.getSearchManager(taskCache);
		
	You also need to import:
		
		import org.infinispan.query.SearchManager;
		
	
1. To create a `QueryBuilder` object we can then get a `SearchFactory` from the `SearchManager` and call `buildQueryBuilder().forEntity(Task.class).get()` on it.
		
		QueryBuilder qb = sm.getSearchFactory().buildQueryBuilder().forEntity(Task.class).get();
		
	You also need to import:
		
		import org.hibernate.search.query.dsl.QueryBuilder; 
		
1. Now we can create a `Query` object from the `QueryBuilder` using the fluid api to specify which Field to match etc. (For more information on see section _[JDG Query Guide](http://red.ht/1obXvd1)_. 

		Query q = qb.bool()
			.must(qb.keyword().wildcard().onField("title").matching(searchStr).createQuery())
			.must(qb.keyword().onFields("owner_username").ignoreAnalyzer().matching(userService.getUsernameOfCurrentUser()).createQuery())
			.createQuery();
	
	The query uses combines two different queries where both _must_ be valid for a Task to return. 
	
	You also need to import
	
		import org.infinispan.query.Search;

1. We can now get a `CacheQuery` object by using the `SearchManager.getQuery(...)` method.
		
		CacheQuery cq = sm.getQuery(q, Task.class);
	
	You also need to import
	
		import org.infinispan.query.CacheQuery;
		
1. The `CacheQuery` extends `Iterable<Object>` directly, but since we are expecting a `Collection<Task>` to return we will have to call `CacheQuery.list()` to get a `List<Object>` back. This will now have to be cast to typed Collection using double Casting.

		return (Collection<Task>)(List)cq.list();
		
	Note that since we are using a QueryBuilder specifically for Task.class we can safely do this cast.

1. The filter method should look something like this when you are done:

		/**
		 * 
		 * @param value
		 * @return
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Collection<Task> filter(String value) {		
			String searchStr = String.format("*%s*", value.toLowerCase());
			SearchManager sm = Search.getSearchManager(taskCache);
			QueryBuilder qb = sm.getSearchFactory().buildQueryBuilder().forEntity(Task.class).get();
			Query q = qb.bool()
					.must(qb.keyword().wildcard().onField("title").matching(searchStr).createQuery())
					.must(qb.keyword().onFields("owner_username").ignoreAnalyzer().matching(userService.getUsernameOfCurrentUser()).createQuery())
					.createQuery();
			CacheQuery cq = sm.getQuery(q, Task.class);
			return (Collection<Task>)(List)cq.list();
		}
### Test and deploy
Now you are almost finished with Lab 2, you should run the Arquillian tests and then deploy the application.

 
	  
			