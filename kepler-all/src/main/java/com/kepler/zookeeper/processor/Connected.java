package com.kepler.zookeeper.processor;

import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import com.kepler.zookeeper.ZkConnection;

/**
 * @author kim 2015年7月9日
 */
public class Connected extends Processor {

	private final ZkConnection connection;

	public Connected(ZkConnection connection) {
		super(KeeperState.SyncConnected, EventType.None);
		this.connection = connection;
	}

	public void process(String path) {
		this.connection.activate();
	}
}
