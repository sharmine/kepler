package com.kepler.collector;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.kepler.annotation.Version;
import com.kepler.config.PropertiesUtils;
import com.kepler.host.Host;
import com.kepler.management.search.Exported;
import com.kepler.management.search.RoundTrip;
import com.kepler.management.search.Runtime;
import com.kepler.management.search.impl.Group;
import com.kepler.management.search.impl.GroupTransfers;
import com.kepler.management.transfer.Transfer;
import com.kepler.management.transfer.Transfers;
import com.kepler.mongo.Dictionary;
import com.kepler.mongo.MongoConfig;
import com.kepler.mongo.impl.MongoUtils;
import com.kepler.org.apache.commons.collections.map.MultiKeyMap;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author kim 2015年7月22日
 */
@Version("0.0.1")
public class MongoHandler implements com.kepler.management.transfer.Feeder, com.kepler.management.status.Feeder, RoundTrip, Runtime, Exported {

	private final static Integer LIMIT_ROUNDTRIP = Integer.valueOf(PropertiesUtils.get(MongoHandler.class.getName().toLowerCase() + ".roundtrip", "5"));

	private final static Integer LIMIT_EXPORTED = Integer.valueOf(PropertiesUtils.get(MongoHandler.class.getName().toLowerCase() + ".expored", "5"));

	private final static Integer LIMIT_RUNTIME = Integer.valueOf(PropertiesUtils.get(MongoHandler.class.getName().toLowerCase() + ".runtime", "5"));

	private final static DBObject FILTER = BasicDBObjectBuilder.start(Dictionary.FIELD_MINUTE, -1).get();

	private final MongoConfig transfers;

	private final MongoConfig status;

	public MongoHandler(MongoConfig transfers, MongoConfig status) {
		super();
		this.transfers = transfers;
		this.status = status;
	}

	@Override
	// db.transfer.ensureIndex({"servce":1,"version":1,"host_target":1,"minute":1,"method":1,"host_local":1})
	public void feed(Collection<Transfers> transfers) {
		for (Transfers each : transfers) {
			for (Transfer transfer : each.transfer()) {
				this.transfers.collection().update(BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, each.service()).add(Dictionary.FIELD_VERSION, each.version()).add(Dictionary.FIELD_HOST_TARGET, transfer.target().getAsString()).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).add(Dictionary.FIELD_METHOD, each.method()).add(Dictionary.FIELD_HOST_LOCAL, transfer.local().getAsString()).get(), BasicDBObjectBuilder.start("$set", BasicDBObjectBuilder.start(Dictionary.FIELD_SERVICE, each.service()).add(Dictionary.FIELD_VERSION, each.version()).add(Dictionary.FIELD_METHOD, each.method()).add(Dictionary.FIELD_HOST_LOCAL, transfer.local().getAsString()).add(Dictionary.FIELD_HOST_LOCAL_GROUP, transfer.local().group()).add(Dictionary.FIELD_HOST_TARGET, transfer.target().getAsString()).add(Dictionary.FIELD_HOST_TARGET_GROUP, transfer.target().group()).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).add(Dictionary.FIELD_RTT, transfer.rtt()).add(Dictionary.FIELD_TIMEOUT, transfer.timeout()).add(Dictionary.FIELD_TOTAL, transfer.total()).add(Dictionary.FIELD_EXCEPTION, transfer.exception()).get()).get(), true, false);
			}
		}
	}

	@Override
	// db.status.ensureIndex({"host_local":1,"minute":1})
	public void feed(Host local, Map<String, Object> status) {
		this.status.collection().update(BasicDBObjectBuilder.start().add(Dictionary.FIELD_HOST_LOCAL, local.getAsString()).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).get(), BasicDBObjectBuilder.start("$set", BasicDBObjectBuilder.start().add(Dictionary.FIELD_HOST_LOCAL, local.getAsString()).add(Dictionary.FIELD_HOST_LOCAL_GROUP, local.group()).add(Dictionary.FIELD_STATUS, status).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).get()).get(), true, false);
	}

	@Override
	public Map<String, String> runtime(String host) {
		DBCursor cursor = this.status.collection().find(BasicDBObjectBuilder.start().add(Dictionary.FIELD_HOST_LOCAL, host).add(Dictionary.FIELD_MINUTE, BasicDBObjectBuilder.start("$gte", TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - MongoHandler.LIMIT_RUNTIME).get()).get()).sort(MongoHandler.FILTER).limit(1);
		return MongoUtils.asMap(MongoUtils.asDBObject(cursor.hasNext() ? cursor.next() : null, Dictionary.FIELD_STATUS));
	}

	public Collection<Transfers> roundtrip(String service, String version, String host) {
		return new MethodTransfers(service, version, this.transfers.collection().find(BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, service).add(Dictionary.FIELD_VERSION, version).add(Dictionary.FIELD_HOST_TARGET, host).add(Dictionary.FIELD_MINUTE, BasicDBObjectBuilder.start("$gte", TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - MongoHandler.LIMIT_ROUNDTRIP).get()).get())).transfers();
	}

	@Override
	public Collection<String> exported(String service, String version, String host) {
		return new Exporteds(this.transfers.collection().find(BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, service).add(Dictionary.FIELD_VERSION, version).add(Dictionary.FIELD_HOST_TARGET, host).add(Dictionary.FIELD_MINUTE, BasicDBObjectBuilder.start("$gte", TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - MongoHandler.LIMIT_EXPORTED).get()).get())).exported();
	}

	private class Exporteds {

		private final Set<String> exported = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

		public Exporteds(DBCursor cur) {
			try (DBCursor cursor = cur) {
				while (cursor.hasNext()) {
					DBObject db = cursor.next();
					this.exported.add(MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL_GROUP) + " / " + MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL));
				}
			}
		}

		public Set<String> exported() {
			return this.exported;
		}
	}

	private class MethodTransfers {

		private final MultiKeyMap transfers = new MultiKeyMap();

		public MethodTransfers(String service, String version, DBCursor cur) {
			try (DBCursor cursor = cur) {
				while (cursor.hasNext()) {
					this.each(service, version, cursor.next());
				}
			}
		}

		private void each(String service, String version, DBObject db) {
			GroupTransfers transfers = GroupTransfers.class.cast(this.transfers.get(MongoUtils.asString(db, Dictionary.FIELD_METHOD)));
			this.transfers.put(MongoUtils.asString(db, Dictionary.FIELD_METHOD), MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL), (transfers = (transfers != null ? transfers : new GroupTransfers(MongoHandler.LIMIT_ROUNDTRIP, service, version, MongoUtils.asString(db, Dictionary.FIELD_METHOD)).put(new Group(MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL_GROUP), MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL)), new Group(MongoUtils.asString(db, Dictionary.FIELD_HOST_TARGET_GROUP), MongoUtils.asString(db, Dictionary.FIELD_HOST_TARGET)), MongoUtils.asLong(db, Dictionary.FIELD_RTT), MongoUtils.asLong(db, Dictionary.FIELD_TOTAL), MongoUtils.asLong(db, Dictionary.FIELD_TIMEOUT), MongoUtils.asLong(db, Dictionary.FIELD_EXCEPTION)))));
		}

		@SuppressWarnings("unchecked")
		public Collection<Transfers> transfers() {
			return this.transfers.values();
		}
	}
}
