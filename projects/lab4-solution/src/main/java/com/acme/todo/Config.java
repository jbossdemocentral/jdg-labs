package com.acme.todo;

import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;

/**
 * This is Class will be used to configure JDG Cache
 * @author tqvarnst
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
				.clusteredDefault()
				.transport().addProperty("configurationFile", "mytodos-jgroups-udp.xml")
				.globalJmxStatistics().allowDuplicateDomains(true).enable()
				.build();
			
			Properties properties = new Properties();
			properties.put("default.directory_provider", "ram");
			properties.put("default.indexmanager", "near-real-time");
			
			
			Configuration loc = new ConfigurationBuilder().jmxStatistics()
				.enable() // Enable JMX statistics
				.clustering().cacheMode(CacheMode.REPL_SYNC)
				.eviction().strategy(EvictionStrategy.NONE) // Do not evic objects
				.transaction().transactionMode(TransactionMode.TRANSACTIONAL).lockingMode(LockingMode.OPTIMISTIC)
				.indexing().enable().withProperties(properties).indexLocalOnly(true)
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
