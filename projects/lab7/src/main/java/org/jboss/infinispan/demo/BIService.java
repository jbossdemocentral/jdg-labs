package org.jboss.infinispan.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.ejb.Stateless;

@Stateless
public class BIService {
	
	@SuppressWarnings("unused")
	private String[] fakeUserAgents = new String[] { 
				"Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.78.2 (KHTML, like Gecko) Version/7.0.6 Safari/537.78.2",
				"Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543 Safari/419.3",
				"Mozilla/5.0 (compatible; MSIE 10.6; Windows NT 6.1; Trident/5.0; InfoPath.2; SLCC1; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET CLR 2.0.50727) 3gpp-gba UNTRUSTED/1.0"
			};
	
	Map<String, Integer> browserMap = new HashMap<String, Integer>();
	Map<String, Integer> osMap = new HashMap<String, Integer>();

	
	
	Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * FIXME: Replace implementation with a MapReduce function
	 * @return
	 */
	public Map<String,Integer> getRequestStatiscsPerOs() {
		if(osMap.size()==0)
			this.generateTestData();
		return osMap;
	}
	
	/**
	 * FIXME: Replace implementation with a MapReduce function
	 * @return
	 */
	public Map<String,Integer> getRequestStatiscsPerBrowser() {
		if(browserMap.size()==0)
			this.generateTestData();
		return browserMap;
	}
	
	/**
	 * FIXME: Replace this implementation with code that adds 500 fake user agent strings into the cache
	 */
	public void generateTestData() {
		Random random = new Random(System.currentTimeMillis());
		browserMap.put("Safari", random.nextInt(500));
		browserMap.put("Googel Chrome", random.nextInt(500));
		browserMap.put("Internet Explorer", random.nextInt(500));
		osMap.put("iPhone", random.nextInt(500));
		osMap.put("Microsoft", random.nextInt(500));
		osMap.put("Android", random.nextInt(500));
		osMap.put("Windows", random.nextInt(500));
		
	}
}
