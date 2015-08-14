package com.kepler.collector;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.kepler.annotation.Version;
import com.kepler.config.PropertiesUtils;
import com.kepler.host.Host;
import com.kepler.host.impl.DefaultHost;
import com.kepler.management.invoker.Feeder;
import com.kepler.management.search.Exported;
import com.kepler.management.search.impl.PIDHost;
import com.kepler.mongo.Dictionary;
import com.kepler.mongo.MongoConfig;
import com.kepler.mongo.impl.MongoUtils;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author kim 2015年7月22日
 */
@Version("0.0.1")
public class InvokerHandler implements Feeder, Exported {

	private final static Integer LIMIT_INVOKER = Integer.valueOf(PropertiesUtils.get(InvokerHandler.class.getName().toLowerCase() + ".limit", "5"));

	private final MongoConfig invoker;

	public InvokerHandler(MongoConfig invoker) {
		super();
		this.invoker = invoker;
	}

	@Override
	// db.invoker.ensureIndex({"host_local":1, "pid":1, "minute":1})
	public void feed(Host local, Collection<Host> invokers) {
		this.invoker.collection().update(BasicDBObjectBuilder.start().add(Dictionary.FIELD_HOST_LOCAL, local.getAsString()).add(Dictionary.FIELD_HOST_LOCAL_PID, local.pid()).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).get(), BasicDBObjectBuilder.start("$set", BasicDBObjectBuilder.start().add(Dictionary.FIELD_HOST_LOCAL, local.host()).add(Dictionary.FIELD_HOST_LOCAL_PID, local.pid()).add(Dictionary.FIELD_HOST_LOCAL_TAG, local.tag()).add(Dictionary.FIELD_HOST_LOCAL_GROUP, local.group()).add(Dictionary.FIELD_HOSTS, new Hosts(invokers)).add(Dictionary.FIELD_MINUTE, TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)).get()).get(), true, false);
	}

	@Override
	// db.invoker.ensureIndex({"hosts":1, "minute":1})
	public Collection<Host> exported(String host) {
		return new SortedExported(this.invoker.collection().find(BasicDBObjectBuilder.start().add(Dictionary.FIELD_HOSTS, host).add(Dictionary.FIELD_MINUTE, BasicDBObjectBuilder.start("$gte", TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - InvokerHandler.LIMIT_INVOKER).get()).get())).exported();
	}

	private class Hosts extends HashSet<String> {

		private static final long serialVersionUID = 1L;

		public Hosts(Collection<Host> hosts) {
			for (Host each : hosts) {
				super.add(each.getAsString());
			}
		}
	}

	private class SortedExported {

		private final TreeSet<Host> exported = new TreeSet<Host>();

		public SortedExported(DBCursor cur) {
			try (DBCursor cursor = cur) {
				while (cursor.hasNext()) {
					DBObject db = cursor.next();
					this.exported.add(new PIDHost(DefaultHost.valueOf(MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL), MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL_GROUP), MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL_TAG), MongoUtils.asString(db, Dictionary.FIELD_HOST_LOCAL_PID))));
				}
			}
		}

		public Collection<Host> exported() {
			return this.exported;
		}
	}
}
