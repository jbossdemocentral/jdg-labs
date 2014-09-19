# JDG + EAP Lab 7 Guide
This explains the steps for lab 6, either follow them step-by-step or if you 
feel adventurous try to accomplish goals without the help of the step-by-step guide.

## Background 
myTODO application is a success, but we don't know much about our users. The marketing department has expressed requirements for tracking if users are using Computers orTablets, which OS they are using and which browsers are more popular.

## Use-case
We will implement a solution to store user information. To minimize any impact to performance user information should be stored unstructured. Via Map/Reduce pattern we can structure the data and make use of it using reporting tools. The user information is captured from the User-Agent HTTP header that browser typically prodvide.

## These are the main tasks of lab 7

1. Create a local library mode cache together with the RemoteCache
1. Extend the REST layer to store the request data unstructured in a local cache
2. Provide a BiSerivce (Business Intelligence Service) class that can structure the data and return data summarized views of the data.
3. Provide a BiEndpoint (REST Service) to enable UI to access the BiService

## Step-by-Step

1. Open lab7 project in JBoss Developer Studion
1. Open `Config.java` and fix the getLocalCacheManager which should look like this:
	
		private EmbeddedCacheManager getLocalCacheManager() {
			GlobalConfiguration glob = new GlobalConfigurationBuilder()
				.globalJmxStatistics().allowDuplicateDomains(true).enable().build();

			org.infinispan.configuration.cache.Configuration loc = new org.infinispan.configuration.cache.ConfigurationBuilder()
				.expiration().lifespan(1,TimeUnit.DAYS)
				.build();
	
			return new DefaultCacheManager(glob, loc, true);
		}

1. Open `TaskEndpoint` and do the following changes

	Inject the request cache like this:
		
		@Inject private Cache<Long, String> requestCache;

	Add the following line to all REST operations/methods
	
		requestCache.putAsync(System.nanoTime(), headers.getRequestHeader("user-agent").get(0));
		
1. Open `BIService.java` and do the following changes

	Change the implementation of `getRequestStatiscsPerOs()` method. 
	
		public Map<String,Integer> getRequestStatiscsPerOs() {
			return new MapReduceTask<Long, String, String, Integer>(requestCache.getAdvancedCache())
					.mappedWith(new UserOSCountMapper())
					.reducedWith(new CountReducer())
					.execute();	
		}
	
	Change the implementation of `getRequestStatiscsPerBrowser()` method.
	
		public Map<String,Integer> getRequestStatiscsPerBrowser() {
			return new MapReduceTask<Long, String, String, Integer>(requestCache.getAdvancedCache())
					.mappedWith(new UserBrowserVendorCountMapper())
					.reducedWith(new CountReducer())
					.execute();	
		}

1. Investigate and try to understand what is mappenign in the mapping classes `UserOSCountMapper` and `UserBrowserVendorCountMapper`
1. Open `CountReducer.java` and add the implementation like below:
	 	
		@Override
		public Integer reduce(String reducedKey, Iterator<Integer> iter) {
			int sum = 0;
			while (iter.hasNext()) {
				Integer i = (Integer) iter.next();
				sum += i;
			}
			return sum;
		}

1. Open `TaskServiceTest.java` and uncomment Test 6 
1. Run the JUnit test
1. Deploy the application using the following command from projects/lab7 dir
		
		$ mvn clean package jboss-as:deploy
		
1. **Fill out the lab evaluation**
1. Congratulations you are done with all the labs.
