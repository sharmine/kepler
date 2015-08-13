package com.kepler.collector;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.kepler.annotation.Version;
import com.kepler.config.PropertiesUtils;
import com.kepler.management.search.RoundTrip;
import com.kepler.management.search.impl.Group;
import com.kepler.management.search.impl.GroupTransfers;
import com.kepler.management.transfer.Feeder;
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
public class TransferHandler implements Feeder, RoundTrip {

	private final static Integer LIMIT_TRANSFER = Integer.valueOf(PropertiesUtils.get(TransferHandler.class.getName().toLowerCase() + ".limit", "5"));

	private final MongoConfig transfers;

	public TransferHandler(MongoConfig transfers) {
		super();
		this.transfers = transfers;
	}

	@Override
	// db.transfer.ensureIndex({"servce":1, "version":1, "minute":1, "host_target":1, "method":1, "host_local":1})
	public void feed(Collection<Transfers> transfers) {
		for (Transfers each : transfers) {
			for (Transfer transfer : each.transfers()) {
				// Query: minute, service, version, host_target, method, host_local
				this.transfers.collection().update(BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, each.service()).add(Dictionary.FIELD_VERSION, each.version()).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).add(Dictionary.FIELD_HOST_TARGET, transfer.target().getAsString()).add(Dictionary.FIELD_METHOD, each.method()).add(Dictionary.FIELD_HOST_LOCAL, transfer.local().getAsString()).get(), BasicDBObjectBuilder.start("$set", BasicDBObjectBuilder.start(Dictionary.FIELD_SERVICE, each.service()).add(Dictionary.FIELD_VERSION, each.version()).add(Dictionary.FIELD_METHOD, each.method()).add(Dictionary.FIELD_HOST_LOCAL, transfer.local().getAsString()).add(Dictionary.FIELD_HOST_LOCAL_TAG, transfer.local().tag()).add(Dictionary.FIELD_HOST_LOCAL_PID, transfer.local().pid()).add(Dictionary.FIELD_HOST_LOCAL_GROUP, transfer.local().group()).add(Dictionary.FIELD_HOST_TARGET, transfer.target().getAsString()).add(Dictionary.FIELD_HOST_TARGET_PID, transfer.target().pid()).add(Dictionary.FIELD_HOST_TARGET_PID, transfer.target().pid()).add(Dictionary.FIELD_HOST_TARGET_GROUP, transfer.target().group()).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).add(Dictionary.FIELD_RTT, transfer.rtt()).add(Dictionary.FIELD_TIMEOUT, transfer.timeout()).add(Dictionary.FIELD_TOTAL, transfer.total()).add(Dictionary.FIELD_EXCEPTION, transfer.exception()).get()).get(), true, false);
			}
		}
	}

	public Collection<Transfers> roundtrip(String service, String version, String host) {
		// 指定时间范围内的平均值
		return new MethodTransfers(service, version, this.transfers.collection().find(BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, service).add(Dictionary.FIELD_VERSION, version).add(Dictionary.FIELD_MINUTE, BasicDBObjectBuilder.start("$gte", TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - TransferHandler.LIMIT_TRANSFER).get()).add(Dictionary.FIELD_HOST_TARGET, host).get())).transfers();
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
			GroupTransfers transfers = GroupTransfers.class.cast(this.transfers.get(MongoUtils.asString(db, Dictionary.FIELD_METHOD), MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL)));
			this.transfers.put(MongoUtils.asString(db, Dictionary.FIELD_METHOD), MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL), (transfers = (transfers != null ? transfers : new GroupTransfers(TransferHandler.LIMIT_TRANSFER, service, version, MongoUtils.asString(db, Dictionary.FIELD_METHOD)).put(new Group(MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL_GROUP), MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL)), new Group(MongoUtils.asString(db, Dictionary.FIELD_HOST_TARGET_GROUP), MongoUtils.asString(db, Dictionary.FIELD_HOST_TARGET)), MongoUtils.asLong(db, Dictionary.FIELD_RTT), MongoUtils.asLong(db, Dictionary.FIELD_TOTAL), MongoUtils.asLong(db, Dictionary.FIELD_TIMEOUT), MongoUtils.asLong(db, Dictionary.FIELD_EXCEPTION)))));
		}

		@SuppressWarnings("unchecked")
		public Collection<Transfers> transfers() {
			return this.transfers.values();
		}
	}
}
