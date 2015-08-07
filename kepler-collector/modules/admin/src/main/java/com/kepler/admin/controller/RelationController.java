package com.kepler.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kepler.collector.rpc.Relation;
import com.kepler.collector.rpc.Relations;

/**
 * @author kim 2015年8月6日
 */
@Controller
@RequestMapping(value = "/relations")
public class RelationController {

	private Relation relations;

	public RelationController(Relation relations) {
		super();
		this.relations = relations;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Relations importeds(String host) throws Exception {
		return this.relations.relation(host);
	}
}
