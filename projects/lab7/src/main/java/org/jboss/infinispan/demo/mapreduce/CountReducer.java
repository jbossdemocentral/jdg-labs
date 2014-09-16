package org.jboss.infinispan.demo.mapreduce;

import java.io.Serializable;
import java.util.Iterator;

import org.infinispan.distexec.mapreduce.Reducer;

public class CountReducer implements Reducer<String, Integer>,
		Serializable {

	private static final long serialVersionUID = 5918721993899089700L;

	/**
	 * FIXME: Add implementation to summarize the reduced keys
	 */
	@Override
	public Integer reduce(String reducedKey, Iterator<Integer> iter) {
		return 0;
	}

}
