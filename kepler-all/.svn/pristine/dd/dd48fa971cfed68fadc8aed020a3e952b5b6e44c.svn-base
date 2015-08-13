package com.kepler.zookeeper.processor;

import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * @author kim 2015年7月9日
 */
abstract public class Processor {

	private final StateMatch match;

	public Processor(KeeperState state, EventType event) {
		super();
		this.match = new StateMatch(state, event);
	}

	public StateMatch match() {
		return this.match;
	}

	abstract public void process(String path);

	public static class StateMatch {

		private final KeeperState state;

		private final EventType event;

		public StateMatch(KeeperState state, EventType event) {
			super();
			this.state = state;
			this.event = event;
		}

		public int hashCode() {
			return this.state.hashCode() ^ this.event.hashCode();
		}

		public boolean equals(Object ob) {
			// Not null point security
			StateMatch matcher = StateMatch.class.cast(ob);
			return this.state.equals(matcher.state) && this.event.equals(matcher.event);
		}
	}
}
