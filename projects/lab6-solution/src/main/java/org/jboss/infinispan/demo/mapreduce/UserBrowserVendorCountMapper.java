package org.jboss.infinispan.demo.mapreduce;

import java.io.Serializable;

import org.infinispan.distexec.mapreduce.Collector;
import org.infinispan.distexec.mapreduce.Mapper;

public class UserBrowserVendorCountMapper implements
		Mapper<Long, String, String, Integer>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5989618131097142749L;

	@Override
	public void map(Long key, String value, Collector<String, Integer> collector) {
		if (value!=null) {
			if (value.contains("Chrome")) {
				collector.emit("Googel Chrome", 1);
			} else if (value.contains("Safari")) {
				collector.emit("Safari", 1);
			} else if (value.contains("MSIE")) {
				collector.emit("Internet Explorer", 1);
			} else {
				collector.emit("Unknown", 1);
			}
		}
	}

}
