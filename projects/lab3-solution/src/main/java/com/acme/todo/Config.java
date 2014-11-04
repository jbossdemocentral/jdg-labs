package com.acme.todo;

import java.lang.annotation.ElementType;
import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

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
			
			Properties properties = new Properties();
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
