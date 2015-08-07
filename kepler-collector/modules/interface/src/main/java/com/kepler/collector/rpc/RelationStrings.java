package com.kepler.collector.rpc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kim 2015年8月7日
 */
public class RelationStrings extends ArrayList<String> {

	private static final long serialVersionUID = 1L;

	public RelationStrings(List<Object> relations) {
		for (Object each : relations) {
			super.add(each.toString());
		}
	}
}