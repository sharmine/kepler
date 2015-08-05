package com.kepler.zookeeper.processor;

import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import com.kepler.router.RouteChange;
import com.kepler.router.RouteChanges;

/**
 * @author kim 2015年7月9日
 */
public class Changed extends Processor {

	private final RouteChanges changes;

	public Changed(RouteChanges changes) {
		super(KeeperState.SyncConnected, EventType.NodeChildrenChanged);
		this.changes = changes;
	}

	@Override
	public void process(String path) {
		this.changes.put(new DefaultChange(path));
	}

	private class DefaultChange implements RouteChange {

		private final String path;

		public DefaultChange(String path) {
			super();
			this.path = path;
		}

		public String path() {
			return this.path;
		}

		@Override
		public Action action() {
			return Action.PUSH;
		}
	}
}
