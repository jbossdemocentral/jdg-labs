package org.jboss.infinispan.demo;

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
import org.jboss.infinispan.demo.model.Task;

/**
 * This is Class will be used to configure JDG Cache
 * @author tqvarnst
 * 
 * FIXME: Remove configuration as to which fields to index.
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
			mapping.entity(Task.class).indexed().providedId()
			      .property("title", ElementType.METHOD).field();
			 
			Properties properties = new Properties();
			properties.put(org.hibernate.search.Environment.MODEL_MAPPING, mapping);
			properties.put("default.directory_provider", "ram");
		
			
			Configuration loc = new ConfigurationBuilder().jmxStatistics()
					.enable() // Enable JMX statistics
					.eviction().strategy(EvictionStrategy.NONE) // Do not evic objects
					.indexing()
						.enable()
						.indexLocalOnly(false)
						.withProperties(properties)
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
