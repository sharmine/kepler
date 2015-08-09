package com.kepler.collector;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.kepler.config.PropertiesUtils;
import com.kepler.host.Host;
import com.kepler.management.transfer.History;
import com.kepler.management.transfer.Relations;
import com.kepler.management.transfer.Transfer;
import com.kepler.management.transfer.Transfers;
import com.kepler.management.transfer.impl.Group;
import com.kepler.management.transfer.impl.GroupTransfers;
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
public class MongoHandler implements com.kepler.management.transfer.Feeder, com.kepler.management.status.Feeder, History, Relations {

	private final static Integer LIMIT_HISTORY = Integer.valueOf(PropertiesUtils.get(MongoHandler.class.getName().toLowerCase() + ".history", "5"));

	private final static Integer LIMIT_RELATIONS = Integer.valueOf(PropertiesUtils.get(MongoHandler.class.getName().toLowerCase() + ".relations", "5"));

	private final MongoConfig config;

	public MongoHandler(MongoConfig config) {
		super();
		this.config = config;
	}

	@Override
	public void feed(Collection<Transfers> transfers) {
		for (Transfers each : transfers) {
			for (Transfer transfer : each.transfer()) {
				MongoHandler.this.config.collection().update(BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, each.service()).add(Dictionary.FIELD_VERSION, each.version()).add(Dictionary.FIELD_METHOD, each.method()).add(Dictionary.FIELD_HOST_LOCAL, transfer.local().getAsString()).add(Dictionary.FIELD_HOST_TARGET, transfer.target().getAsString()).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).get(), BasicDBObjectBuilder.start("$set", BasicDBObjectBuilder.start(Dictionary.FIELD_SERVICE, each.service()).add(Dictionary.FIELD_VERSION, each.version()).add(Dictionary.FIELD_METHOD, each.method()).add(Dictionary.FIELD_HOST_LOCAL, transfer.local().getAsString()).add(Dictionary.FIELD_HOST_LOCAL_GROUP, transfer.local().group()).add(Dictionary.FIELD_HOST_TARGET, transfer.target().getAsString()).add(Dictionary.FIELD_HOST_TARGET_GROUP, transfer.target().group()).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).add(Dictionary.FIELD_RTT, transfer.rtt()).add(Dictionary.FIELD_TIMEOUT, transfer.timeout()).add(Dictionary.FIELD_TOTAL, transfer.total()).add(Dictionary.FIELD_EXCEPTION, transfer.exception()).get()).get(), true, false);
			}
		}
	}

	@Override
	public void feed(Host local, Map<String, Object> status) {
		int i = 1;
	}

	public Collection<Transfers> history(String service, String version, String target) {
		return new MethodTransfers(service, version, this.config.collection().find(BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, service).add(Dictionary.FIELD_VERSION, version).add(Dictionary.FIELD_HOST_TARGET, target).add(Dictionary.FIELD_MINUTE, BasicDBObjectBuilder.start("$gte", TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - MongoHandler.LIMIT_HISTORY).get()).get())).values();
	}

	@Override
	public Collection<String> relations(String service, String version, String target) {
		return new Exporteds(this.config.collection().find(BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, service).add(Dictionary.FIELD_VERSION, version).add(Dictionary.FIELD_HOST_TARGET, target).add(Dictionary.FIELD_MINUTE, BasicDBObjectBuilder.start("$gte", TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - MongoHandler.LIMIT_RELATIONS).get()).get()));
	}

	private class Exporteds extends HashSet<String> {

		private static final long serialVersionUID = 1L;

		public Exporteds(DBCursor cur) {
			try (DBCursor cursor = cur) {
				while (cursor.hasNext()) {
					DBObject db = cursor.next();
					super.add(MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL_GROUP) + " / " + MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL));
				}
			}
		}
	}

	private class MethodTransfers extends HashMap<String, Transfers> {

		private static final long serialVersionUID = 1L;

		public MethodTransfers(String service, String version, DBCursor cur) {
			try (DBCursor cursor = cur) {
				while (cursor.hasNext()) {
					this.each(service, version, cursor.next());
				}
			}
		}

		private void each(String service, String version, DBObject db) {
			GroupTransfers transfers = GroupTransfers.class.cast(super.get(MongoUtils.asString(db, Dictionary.FIELD_METHOD)));
			super.put(MongoUtils.asString(db, Dictionary.FIELD_METHOD), (transfers = (transfers != null ? transfers : new GroupTransfers(MongoHandler.LIMIT_HISTORY, service, version, MongoUtils.asString(db, Dictionary.FIELD_METHOD)).put(new Group(MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL_GROUP), MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL)), new Group(MongoUtils.asString(db, Dictionary.FIELD_HOST_TARGET_GROUP), MongoUtils.asString(db, Dictionary.FIELD_HOST_TARGET)), MongoUtils.asLong(db, Dictionary.FIELD_RTT), MongoUtils.asLong(db, Dictionary.FIELD_TOTAL), MongoUtils.asLong(db, Dictionary.FIELD_TIMEOUT), MongoUtils.asLong(db, Dictionary.FIELD_EXCEPTION)))));
		}
	}
}
