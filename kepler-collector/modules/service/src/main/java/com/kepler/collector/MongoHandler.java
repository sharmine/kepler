package com.kepler.collector;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.kepler.collector.rpc.Condition;
import com.kepler.collector.rpc.Conditions;
import com.kepler.collector.rpc.History;
import com.kepler.collector.rpc.Relation;
import com.kepler.collector.rpc.RelationStrings;
import com.kepler.collector.rpc.Relations;
import com.kepler.collector.rpc.impl.DefaultRelations;
import com.kepler.collector.rpc.impl.Group;
import com.kepler.collector.rpc.impl.GroupConditions;
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
public class MongoHandler implements Audience, History, Relation {

	private final static Integer LIMIT_HISTORY = Integer.valueOf(PropertiesUtils.get(MongoHandler.class.getName().toLowerCase() + ".history", "5"));

	private final static Integer LIMIT_RELATION = Integer.valueOf(PropertiesUtils.get(MongoHandler.class.getName().toLowerCase() + ".relation", "1440"));

	private final MongoConfig config;

	public MongoHandler(MongoConfig config) {
		super();
		this.config = config;
	}

	@Override
	public void put(Collection<Conditions> conditions) {
		for (Conditions each : conditions) {
			for (Condition condition : each.conditions()) {
				this.config.collection().update(BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, each.service()).add(Dictionary.FIELD_VERSION, each.version()).add(Dictionary.FIELD_METHOD, each.method()).add(Dictionary.FIELD_HOST_SOURCE, condition.local().getAsString()).add(Dictionary.FIELD_HOST_TARGET, condition.host().getAsString()).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).get(), BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, each.service()).add(Dictionary.FIELD_VERSION, each.version()).add(Dictionary.FIELD_METHOD, each.method()).add(Dictionary.FIELD_HOST_SOURCE, condition.local().getAsString()).add(Dictionary.FIELD_HOST_SOURCE_GROUP, condition.local().group()).add(Dictionary.FIELD_HOST_TARGET, condition.host().getAsString()).add(Dictionary.FIELD_HOST_TARGET_GROUP, condition.host().group()).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).add(Dictionary.FIELD_RTT, condition.rtt()).add(Dictionary.FIELD_TIMEOUT, condition.timeout()).add(Dictionary.FIELD_TOTAL, condition.total()).add(Dictionary.FIELD_EXCEPTION, condition.exception()).get(), true, false);
			}
		}
	}

	public Relations relation(String host) {
		return new DefaultRelations(this.relations(host, Dictionary.FIELD_HOST_SOURCE, Dictionary.FIELD_HOST_TARGET_GROUP), this.relations(host, Dictionary.FIELD_HOST_TARGET, Dictionary.FIELD_HOST_SOURCE_GROUP));
	}

	public Collection<Conditions> history(String service, String version, String host) {
		return new Conditionses(service, version, this.config.collection().find(BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, service).add(Dictionary.FIELD_VERSION, version).add(Dictionary.FIELD_HOST_TARGET, host).add(Dictionary.FIELD_MINUTE, BasicDBObjectBuilder.start("$gte", TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - MongoHandler.LIMIT_HISTORY).get()).get())).values();
	}

	private List<String> relations(String host, String field, String group) {
		return new RelationStrings(this.config.collection().distinct(group, BasicDBObjectBuilder.start().add(field, host).add(Dictionary.FIELD_MINUTE, BasicDBObjectBuilder.start("$gte", TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - MongoHandler.LIMIT_RELATION).get()).get()));
	}

	private class Conditionses extends HashMap<String, Conditions> {

		private static final long serialVersionUID = 1L;

		public Conditionses(String service, String version, DBCursor cur) {
			try (DBCursor cursor = cur) {
				while (cursor.hasNext()) {
					this.each(service, version, cursor.next());
				}
			}
		}

		private void each(String service, String version, DBObject db) {
			GroupConditions conditions = GroupConditions.class.cast(super.get(MongoUtils.asString(db, Dictionary.FIELD_METHOD)));
			super.put(MongoUtils.asString(db, Dictionary.FIELD_METHOD), (conditions = (conditions != null ? conditions : new GroupConditions(MongoHandler.LIMIT_HISTORY, service, version, MongoUtils.asString(db, Dictionary.FIELD_METHOD)).put(new Group(MongoUtils.asString(db, Dictionary.FIELD_HOST_SOURCE_GROUP), MongoUtils.asString(db, Dictionary.FIELD_HOST_SOURCE)), new Group(MongoUtils.asString(db, Dictionary.FIELD_HOST_TARGET_GROUP), MongoUtils.asString(db, Dictionary.FIELD_HOST_TARGET)), MongoUtils.asLong(db, Dictionary.FIELD_RTT), MongoUtils.asLong(db, Dictionary.FIELD_TOTAL), MongoUtils.asLong(db, Dictionary.FIELD_TIMEOUT), MongoUtils.asLong(db, Dictionary.FIELD_EXCEPTION)))));
		}
	}
}
