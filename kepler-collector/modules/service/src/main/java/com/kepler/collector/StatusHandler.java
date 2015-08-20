package com.kepler.collector;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.kepler.annotation.Service;
import com.kepler.config.PropertiesUtils;
import com.kepler.host.Host;
import com.kepler.management.search.Properties;
import com.kepler.management.status.Feeder;
import com.kepler.mongo.Dictionary;
import com.kepler.mongo.MongoConfig;
import com.kepler.mongo.impl.MongoUtils;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author kim 2015年8月12日
 */
@Service(version = "0.0.1", autowired = true)
public class StatusHandler implements Feeder, Properties {

	private final static Integer LIMIT_STATUS = Integer.valueOf(PropertiesUtils.get(StatusHandler.class.getName().toLowerCase() + ".limit", "5"));

	private final static DBObject FILTER = BasicDBObjectBuilder.start(Dictionary.FIELD_MINUTE, -1).get();

	private final MongoConfig status;

	public StatusHandler(MongoConfig status) {
		super();
		this.status = status;
	}

	@Override
	// db.status.ensureIndex({"host_local":1, "minute":1})
	public void feed(Host local, Map<String, Object> status) {
		this.status.collection().update(BasicDBObjectBuilder.start().add(Dictionary.FIELD_HOST_LOCAL, local.getAsString()).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).get(), BasicDBObjectBuilder.start("$set", BasicDBObjectBuilder.start().add(Dictionary.FIELD_HOST_LOCAL, local.getAsString()).add(Dictionary.FIELD_HOST_LOCAL_TAG, local.tag()).add(Dictionary.FIELD_HOST_LOCAL_PID, local.pid()).add(Dictionary.FIELD_HOST_LOCAL_GROUP, local.group()).add(Dictionary.FIELD_STATUS, status).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).get()).get(), true, false);
	}

	@Override
	public Map<String, String> properties(String host) {
		// 指定时间范围内的最后1条
		return new SortedProperties(this.status.collection().find(BasicDBObjectBuilder.start().add(Dictionary.FIELD_HOST_LOCAL, host).add(Dictionary.FIELD_MINUTE, BasicDBObjectBuilder.start("$gte", TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - StatusHandler.LIMIT_STATUS).get()).get()).sort(StatusHandler.FILTER).limit(1)).runtimes();
	}

	private class SortedProperties {

		private final Map<String, String> properties = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

		public SortedProperties(DBCursor cursor) {
			this.properties.putAll(MongoUtils.<String> asMap(MongoUtils.asDBObject(cursor.hasNext() ? cursor.next() : null, Dictionary.FIELD_STATUS)));
		}

		public Map<String, String> runtimes() {
			return this.properties;
		}
	}
}
