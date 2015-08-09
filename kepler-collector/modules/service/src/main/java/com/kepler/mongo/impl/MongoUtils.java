package com.kepler.mongo.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kepler.org.apache.commons.lang.ArrayUtils;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * MongoDB取值器
 * 
 * @author kim 2014年2月23日
 */
public class MongoUtils {

	private final static List<?> LIST = Collections.unmodifiableList(new ArrayList<Object>());

	private final static Map<String, Object> MAP = Collections.unmodifiableMap(new HashMap<String, Object>());

	public static String asString(DBObject db, String key) {
		return MongoUtils.asString(db, key, null);
	}

	public static String asString(DBObject db, String key, String def) {
		Object value = MongoUtils.as(db, key);
		return value != null ? value.toString() : def;
	}

	@SuppressWarnings("unchecked")
	public static String[] asStrings(DBObject db, String key) {
		List<String> value = List.class.cast(MongoUtils.as(db, key));
		return value != null ? value.toArray(new String[] {}) : null;
	}

	public static String[] asStrings(DBObject db, String key, String[] def) {
		String[] value = MongoUtils.asStrings(db, key);
		return value != null ? value : def;
	}

	public static int asInt(DBObject db, String key, int def) {
		Object value = MongoUtils.as(db, key);
		return value != null ? Integer.class.cast(MongoUtils.as(db, key)) : def;
	}

	public static int[] asInts(DBObject db, String key) {
		List<?> value = MongoUtils.asList(db, key);
		return value != null ? ArrayUtils.toPrimitive(value.toArray(new Integer[] {})) : null;
	}

	public static long asLong(DBObject db, String key) {
		Object value = MongoUtils.as(db, key);
		return value != null ? Long.parseLong(value.toString()) : null;
	}

	public static double asDouble(DBObject db, String key, double def) {
		Object value = MongoUtils.as(db, key);
		return value != null ? Double.parseDouble(value.toString()) : def;
	}

	public static double[] asDoubles(DBObject db, String key) {
		List<?> value = MongoUtils.asList(db, key);
		return value != null ? ArrayUtils.toPrimitive(value.toArray(new Double[] {})) : null;
	}

	public static boolean asBoolean(DBObject db, String key) {
		return MongoUtils.asBoolean(db, key, Boolean.FALSE);
	}

	public static boolean asBoolean(DBObject db, String key, boolean def) {
		Object value = MongoUtils.as(db, key);
		return value != null ? Boolean.class.cast(value) : def;
	}

	public static DBObject asDBObject(DBObject db, String key) {
		Object value = MongoUtils.as(db, key);
		return value != null ? DBObject.class.cast(value) : null;
	}

	public static Object as(DBObject db, String key) {
		Object value = db != null ? db.get(key) : null;
		return value != null ? value : null;
	}

	public static <T> T as(DBObject db, String key, Class<T> clazz) {
		Object value = MongoUtils.as(db, key);
		return value != null ? clazz.cast(value) : null;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> asList(DBObject db, String key) {
		Object value = MongoUtils.as(db, key);
		return value != null ? List.class.cast(value) : MongoUtils.LIST;
	}

	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> asMap(DBObject db) {
		return db != null ? db.toMap() : MongoUtils.MAP;
	}

	public static boolean effect(WriteResult result) {
		return result.getN() != 0;
	}
}