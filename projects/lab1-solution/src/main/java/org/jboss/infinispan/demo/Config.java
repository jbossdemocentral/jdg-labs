package org.jboss.infinispan.demo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * This is Class will be used to configure JDG Cache
 * @author tqvarnst
 */
public class Config {
    @Produces
    @ApplicationScoped
    @Default
    public EmbeddedCacheManager defaultEmbeddedCacheConfiguration() {
        GlobalConfiguration glob = new GlobalConfigurationBuilder()
                .globalJmxStatistics().allowDuplicateDomains(true).enable() // This
                // method enables the jmx statistics of the global
                // configuration and allows for duplicate JMX domains
                .build();
        Configuration loc = new ConfigurationBuilder().jmxStatistics()
                .enable() // Enable JMX statistics
                .eviction().strategy(EvictionStrategy.NONE) // Do not evic objects
                .build();
        return new DefaultCacheManager(glob, loc, true);
    }
}
