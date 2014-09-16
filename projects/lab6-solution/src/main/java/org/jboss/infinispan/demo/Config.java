package org.jboss.infinispan.demo;

import javax.enterprise.inject.Produces;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.jboss.infinispan.demo.model.Task;

/**
 * This class produces configured cache objects via CDI
 *  
 * @author tqvarnst
 *
 */
public class Config {

	
	/**
	 * DONE: Add a default Producer for org.infinispan.client.hotrod.RemoteCache<Long, Task> 
	 * 		  using org.infinispan.client.hotrod.configuration.ConfigurationBuilder
	 * 		  and org.infinispan.client.hotrod.RemoteCacheManager
	 * 
	 * @return org.infinispan.client.hotrod.RemoteCache<Long, Task>
	 */
	@Produces
	public RemoteCache<Long, Task> getRemoteCache() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.addServer().host("localhost").port(11322);
		return new RemoteCacheManager(builder.build(), true).getCache("tasks");
	}

}
