package com.kepler.mongo;

import java.util.List;

import com.mongodb.AggregationOutput;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

/**
 * 
 * @author kim 2014年1月22日
 */
public interface MongoOperation {

	public WriteResult save(DBObject entity);

	public WriteResult save(DBObject entity, WriteConcern concern);

	public WriteResult update(DBObject query, DBObject entity);

	public WriteResult update(DBObject query, DBObject entity, boolean upsert, boolean batch);

	public WriteResult update(DBObject query, DBObject entity, boolean upsert, boolean batch, WriteConcern concern);

	public WriteResult remove(DBObject query);

	public WriteResult remove(DBObject query, WriteConcern concern);

	public DBCursor find(DBObject query);

	public DBCursor find(DBObject query, DBObject filter);

	public DBObject findOne(DBObject query);

	public DBObject findOne(DBObject query, DBObject filter);

	public DBObject findAndModify(DBObject query, DBObject entity);

	public DBObject findAndModify(DBObject query, DBObject fields, DBObject sort, boolean remove, DBObject update, boolean returnNew, boolean upsert);

	public AggregationOutput aggregate(DBObject... ops);
	
	public List<Object> distinct(String key, DBObject query);
}