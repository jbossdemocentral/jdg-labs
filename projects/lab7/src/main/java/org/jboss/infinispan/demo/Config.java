package org.jboss.infinispan.demo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.infinispan.Cache;
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
	@ApplicationScoped
	public RemoteCache<Long, Task> getRemoteCache() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.addServer().host("localhost").port(11322);
		return new RemoteCacheManager(builder.build(), true).getCache("tasks");
	}
	
	
	/**
	 * NOTE: We need an Advanced Cache since we are going to run map reduce functions against it later.
	 * 
	 * @return org.infinispan.AdvancedCache<Long, String>
	 */
	@Produces
	@ApplicationScoped
	public org.infinispan.AdvancedCache<Long, String> getLocalRequestCache() {
		org.infinispan.Cache<Long,String> basicCache = getLocalCacheManager().getCache("client-request-cache",true);
		return basicCache.getAdvancedCache();
	}
	
	/**
	 * FIXME: Use org.infinispan.configuration.global.GlobalConfiguration and
	 *  org.infinispan.configuration.cache.Configuration to create a
	 * 	org.infinispan.manager.DefaultCacheManager (which is an implementation of EmbeddedCacheManager)
	 * 
	 * NOTE: You will have to use full namespace for the org.inifinispan.configuration.cache.ConfigurationBuilder
	 *  since we already import org.infinispan.client.hotrod.configuration.ConfigurationBuilder
	 *  
	 * @return org.infinispan.manager.EmbeddedCacheManager
	 */
	private org.infinispan.manager.EmbeddedCacheManager getLocalCacheManager() {
		throw new RuntimeException("The metod Config.getLocalCacheManager() is not implemented yet");
	}

}
