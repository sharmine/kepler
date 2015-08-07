package com.kepler.collector;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.kepler.collector.rpc.Note;
import com.kepler.collector.rpc.Notes;
import com.kepler.collector.rpc.History;
import com.kepler.collector.rpc.impl.Group;
import com.kepler.collector.rpc.impl.GroupNotes;
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
public class MongoHandler implements Feeder, History {

	private final static Integer LIMIT_HISTORY = Integer.valueOf(PropertiesUtils.get(MongoHandler.class.getName().toLowerCase() + ".history", "5"));

	private final MongoConfig config;

	public MongoHandler(MongoConfig config) {
		super();
		this.config = config;
	}

	@Override
	public void feed(Collection<Notes> notes) {
		for (Notes each : notes) {
			for (Note note : each.notes()) {
				this.config.collection().update(BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, each.service()).add(Dictionary.FIELD_VERSION, each.version()).add(Dictionary.FIELD_METHOD, each.method()).add(Dictionary.FIELD_HOST_LOCAL, note.local().getAsString()).add(Dictionary.FIELD_HOST_TARGET, note.target().getAsString()).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).get(), BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, each.service()).add(Dictionary.FIELD_VERSION, each.version()).add(Dictionary.FIELD_METHOD, each.method()).add(Dictionary.FIELD_HOST_LOCAL, note.local().getAsString()).add(Dictionary.FIELD_HOST_LOCAL_GROUP, note.local().group()).add(Dictionary.FIELD_HOST_TARGET, note.target().getAsString()).add(Dictionary.FIELD_HOST_TARGET_GROUP, note.target().group()).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).add(Dictionary.FIELD_RTT, note.rtt()).add(Dictionary.FIELD_TIMEOUT, note.timeout()).add(Dictionary.FIELD_TOTAL, note.total()).add(Dictionary.FIELD_EXCEPTION, note.exception()).get(), true, false);
			}
		}
	}

	public Collection<Notes> history(String service, String version, String target) {
		return new MethodNotes(service, version, this.config.collection().find(BasicDBObjectBuilder.start().add(Dictionary.FIELD_SERVICE, service).add(Dictionary.FIELD_VERSION, version).add(Dictionary.FIELD_HOST_TARGET, target).add(Dictionary.FIELD_MINUTE, BasicDBObjectBuilder.start("$gte", TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - MongoHandler.LIMIT_HISTORY).get()).get())).values();
	}

	private class MethodNotes extends HashMap<String, Notes> {

		private static final long serialVersionUID = 1L;

		public MethodNotes(String service, String version, DBCursor cur) {
			try (DBCursor cursor = cur) {
				while (cursor.hasNext()) {
					this.each(service, version, cursor.next());
				}
			}
		}

		private void each(String service, String version, DBObject db) {
			GroupNotes notes = GroupNotes.class.cast(super.get(MongoUtils.asString(db, Dictionary.FIELD_METHOD)));
			super.put(MongoUtils.asString(db, Dictionary.FIELD_METHOD), (notes = (notes != null ? notes : new GroupNotes(MongoHandler.LIMIT_HISTORY, service, version, MongoUtils.asString(db, Dictionary.FIELD_METHOD)).put(new Group(MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL_GROUP), MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL)), new Group(MongoUtils.asString(db, Dictionary.FIELD_HOST_TARGET_GROUP), MongoUtils.asString(db, Dictionary.FIELD_HOST_TARGET)), MongoUtils.asLong(db, Dictionary.FIELD_RTT), MongoUtils.asLong(db, Dictionary.FIELD_TOTAL), MongoUtils.asLong(db, Dictionary.FIELD_TIMEOUT), MongoUtils.asLong(db, Dictionary.FIELD_EXCEPTION)))));
		}
	}
}
