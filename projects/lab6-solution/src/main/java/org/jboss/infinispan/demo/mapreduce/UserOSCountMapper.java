package org.jboss.infinispan.demo.mapreduce;

import java.io.Serializable;

import org.infinispan.distexec.mapreduce.Collector;
import org.infinispan.distexec.mapreduce.Mapper;

public class UserOSCountMapper implements
		Mapper<Long, String, String, Integer>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5989618131097142749L;

	@Override
	public void map(Long key, String value, Collector<String, Integer> collector) {
		if (value!=null) {
			if (value.contains("Android")) {
				collector.emit("Android", 1);
			} else if (value.contains("iPhone")) {
				collector.emit("iPhone", 1);
			} else if (value.contains("Macintosh")) {
				collector.emit("Macintosh", 1);
			} else if (value.contains("Windows")) {
				collector.emit("Windows", 1);
			} else {
				collector.emit("Unknown", 1);
			}
		}
		
	}

}
