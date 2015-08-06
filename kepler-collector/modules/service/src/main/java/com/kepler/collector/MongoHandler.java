package com.kepler.collector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.kepler.collector.rpc.Condition;
import com.kepler.collector.rpc.Conditions;
import com.kepler.collector.rpc.impl.GroupCondition;
import com.kepler.collector.rpc.impl.GroupHost;
import com.kepler.collector.runtime.State;
import com.kepler.config.PropertiesUtils;
import com.kepler.mongo.Dictionary;
import com.kepler.mongo.MongoConfig;
import com.kepler.mongo.impl.MongoUtils;
import com.kepler.service.annotation.Version;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author kim 2015年7月22日
 */
@Version("0.0.1")
public class MongoHandler implements Note, History {

	private final static Integer HISTORY = Integer.valueOf(PropertiesUtils.get(MongoHandler.class.getName().toLowerCase() + ".history", "2"));

	private final MongoConfig config;

	public MongoHandler(MongoConfig host) {
		super();
		this.config = host;
	}

	@Override
	public void state(Collection<State> states) {
	}

	@Override
	public void condition(Collection<Conditions> conditions) {
		for (Conditions each : conditions) {
			for (Condition condition : each.conditions()) {
				// Servce ,Version, Method, TargetHost union index
				this.config.collection().update(BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, each.service()).add(Dictionary.FIELD_VERSION, each.version()).add(Dictionary.FIELD_METHOD, each.method()).add(Dictionary.FIELD_HOST_TARGET, condition.target().getAsString()).add(Dictionary.FIELD_HOST_SOURCE, condition.source().getAsString()).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).get(), BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, each.service()).add(Dictionary.FIELD_VERSION, each.version()).add(Dictionary.FIELD_HOST_SOURCE, condition.source().getAsString()).add(Dictionary.FIELD_HOST_SOURCE_GROUP, condition.source().group()).add(Dictionary.FIELD_HOST_TARGET, condition.target().getAsString()).add(Dictionary.FIELD_HOST_TARGET_GROUP, condition.target().group()).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).add(Dictionary.FIELD_RTT, condition.rtt()).add(Dictionary.FIELD_TIMEOUT, condition.timeout()).add(Dictionary.FIELD_TOTAL, condition.total()).add(Dictionary.FIELD_EXCEPTION, condition.exception()).get(), true, false);
			}
		}
	}

	public Collection<Condition> last(String service, String version, String host) {
		Group group = new Group();
		try (DBCursor cursor = this.config.collection().find(BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, service).add(Dictionary.FIELD_VERSION, version).add(Dictionary.FIELD_MINUTE, BasicDBObjectBuilder.start("$gte", TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - MongoHandler.HISTORY).get()).add(Dictionary.FIELD_HOST_TARGET, host).get())) {
			while (cursor.hasNext()) {
				this.group(group, cursor.next());
			}
		}
		return group.conditions();
	}

	private void group(Group group, DBObject db) {
		group.put(new GroupHost(MongoUtils.asString(db, Dictionary.FIELD_HOST_SOURCE_GROUP), MongoUtils.asString(db, Dictionary.FIELD_HOST_SOURCE)), new GroupHost(MongoUtils.asString(db, Dictionary.FIELD_HOST_TARGET_GROUP), MongoUtils.asString(db, Dictionary.FIELD_HOST_TARGET)), MongoUtils.asLong(db, Dictionary.FIELD_RTT), MongoUtils.asLong(db, Dictionary.FIELD_TOTAL), MongoUtils.asLong(db, Dictionary.FIELD_TIMEOUT), MongoUtils.asLong(db, Dictionary.FIELD_EXCEPTION));
	}

	private class Group {

		private final Map<String, Condition> conditions = new HashMap<String, Condition>();

		public void put(GroupHost source, GroupHost target, long rtt, long total, long timeout, long exception) {
			GroupCondition average = GroupCondition.class.cast(this.conditions.get(source));
			this.conditions.put(source.getHost(), (average = (average != null ? average : new GroupCondition())).put(source, target, rtt, total, timeout, exception));
		}

		public Collection<Condition> conditions() {
			return this.conditions.values();
		}
	}
}
