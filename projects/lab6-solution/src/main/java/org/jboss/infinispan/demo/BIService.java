package org.jboss.infinispan.demo;

import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.distexec.mapreduce.MapReduceTask;
import org.jboss.infinispan.demo.mapreduce.UserBrowserVendorCountMapper;
import org.jboss.infinispan.demo.mapreduce.UserOSCountMapper;
import org.jboss.infinispan.demo.mapreduce.CountReducer;

@Stateless
public class BIService {
	
	private String[] fakeUserAgents = new String[] { 
				"Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.78.2 (KHTML, like Gecko) Version/7.0.6 Safari/537.78.2",
				"Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543 Safari/419.3",
				"Mozilla/5.0 (compatible; MSIE 10.6; Windows NT 6.1; Trident/5.0; InfoPath.2; SLCC1; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET CLR 2.0.50727) 3gpp-gba UNTRUSTED/1.0"
			};
	
	
	@Inject
	@RequestCache
	Cache<Long,String> requestCache;
	
	Logger log = Logger.getLogger(this.getClass().getName());

	public Map<String,Integer> getRequestStatiscsPerOs() {
		
		
		return new MapReduceTask<Long, String, String, Integer>(requestCache.getAdvancedCache())
				.mappedWith(new UserOSCountMapper())
				.reducedWith(new CountReducer())
				.execute();	
	}
	
	public Map<String,Integer> getRequestStatiscsPerBrowser() {
		
		
		return new MapReduceTask<Long, String, String, Integer>(requestCache.getAdvancedCache())
				.mappedWith(new UserBrowserVendorCountMapper())
				.reducedWith(new CountReducer())
				.execute();	
	}
	
	
	public void generateTestData() {
		Random random = new Random(System.currentTimeMillis());
		
		for(int i=0;i<5000;i++) {
			int agent = random.nextInt(fakeUserAgents.length);
			requestCache.put(new Long(i), fakeUserAgents[agent] );
		}
	}
}
