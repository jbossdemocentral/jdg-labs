package org.jboss.infinispan.demo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.jboss.infinispan.demo.model.Task;

public class Config {

	@Inject
	private RemoteCacheManager cacheManager;

	@Produces
	@ApplicationScoped
	public RemoteCacheManager getRemoteCacheManager() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.addServer().host("localhost").port(11322);
		return new RemoteCacheManager(builder.build(), true);
	}

	@Produces
	public RemoteCache<Long, Task> getRemoteCache() {
		return cacheManager.getCache("tasks");
	}

}
