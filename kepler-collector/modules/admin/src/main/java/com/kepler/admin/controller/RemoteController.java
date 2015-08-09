package com.kepler.admin.controller;

import java.util.Collection;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kepler.management.transfer.History;
import com.kepler.management.transfer.Relations;
import com.kepler.management.transfer.Transfers;

/**
 * @author kim 2015年8月8日
 */
@Controller
public class RemoteController {

	private final Relations relations;

	private final History history;

	public RemoteController(Relations relations, History history) {
		super();
		this.relations = relations;
		this.history = history;
	}

	@RequestMapping(value = "/relations", method = RequestMethod.GET)
	@ResponseBody
	public Collection<String> relations(String path, String host) throws Exception {
		return this.relations.relations(path.substring(0, path.lastIndexOf("/")).replaceAll("/", "."), path.substring(path.lastIndexOf("/") + 1), host);
	}

	@RequestMapping(value = "/history", method = RequestMethod.GET)
	@ResponseBody
	public Collection<Transfers> history(String path, String host) throws Exception {
		return this.history.history(path.substring(0, path.lastIndexOf("/")).replaceAll("/", "."), path.substring(path.lastIndexOf("/") + 1), host);
	}
}
