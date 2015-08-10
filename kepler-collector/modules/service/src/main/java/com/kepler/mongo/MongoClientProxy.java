package com.kepler.mongo;

import java.net.UnknownHostException;

import com.kepler.config.PropertiesUtils;
import com.mongodb.MongoClient;

/**
 * @author kim 2015年8月10日
 */
public class MongoClientProxy extends MongoClient {

	private final static String ADDRESS = PropertiesUtils.get(MongoClientProxy.class.getName().toLowerCase() + ".address");

	public MongoClientProxy() throws UnknownHostException {
		super(MongoClientProxy.ADDRESS);
	}
}
