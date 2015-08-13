package com.kepler.connection.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 黏包 (@see io.netty.handler.codec.LengthFieldBasedFrameDecoder)
 * 
 * @author kim 2015年7月8日
 */
public enum CodecHeader {

	ONE, TWO, FOUR, EIGHT;

	private final static Map<CodecHeader, Integer> MAPPING = new ConcurrentHashMap<CodecHeader, Integer>();

	static {
		MAPPING.put(ONE, 1);
		MAPPING.put(TWO, 2);
		MAPPING.put(FOUR, 4);
		MAPPING.put(EIGHT, 8);
	}

	public final static int DEFAULT = FOUR.code();

	public int code() {
		return MAPPING.get(this).intValue();
	}
}
