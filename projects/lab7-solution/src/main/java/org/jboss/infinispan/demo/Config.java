package org.jboss.infinispan.demo;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.infinispan.demo.model.Task;

/**
 * This class produces configured cache objects via CDI
 *  
 * @author tqvarnst
 *
 */
public class Config {

	
	/**
	 * 
	 * @return org.infinispan.client.hotrod.RemoteCache<Long, Task>
	 */
	@Produces
	public RemoteCache<Long, Task> getRemoteCache() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.addServer()
			.host("localhost").port(11322)
			.security()
	        .authentication()
	            .enable()
	            .serverName("tasks")
	            .saslMechanism("DIGEST-MD5")
	            .callbackHandler(new LoginHandler("thomas", "thomas-123".toCharArray(), "ApplicationRealm"));
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
	 * DONE: Use org.infinispan.configuration.global.GlobalConfiguration and
	 *  org.infinispan.configuration.cache.Configuration to create a
	 * 	org.infinispan.manager.DefaultCacheManager (which is an implementation of EmbeddedCacheManager)
	 * 
	 * NOTE: You will have to use full namespace for the org.inifinispan.configuration.cache.ConfigurationBuilder
	 *  since we already import org.infinispan.client.hotrod.configuration.ConfigurationBuilder
	 *  
	 * @return org.infinispan.manager.EmbeddedCacheManager
	 */
	private EmbeddedCacheManager getLocalCacheManager() {
		GlobalConfiguration glob = new GlobalConfigurationBuilder()
			.globalJmxStatistics().allowDuplicateDomains(true).enable().build();
	
		org.infinispan.configuration.cache.Configuration loc = new org.infinispan.configuration.cache.ConfigurationBuilder()
			.expiration().lifespan(1,TimeUnit.DAYS)
			.build();
		
		return new DefaultCacheManager(glob, loc, true);
	}

}
