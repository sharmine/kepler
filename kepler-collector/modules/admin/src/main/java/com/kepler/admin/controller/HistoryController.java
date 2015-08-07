package com.kepler.admin.controller;

import java.util.Collection;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kepler.collector.rpc.Notes;
import com.kepler.collector.rpc.History;

/**
 * @author kim 2015年7月25日
 */
@Controller
@RequestMapping(value = "/history")
public class HistoryController {

	private History history;

	public HistoryController(History history) {
		super();
		this.history = history;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Collection<Notes> last(String path, String host) throws Exception {
		return this.history.history(path.substring(0, path.lastIndexOf("/")).replaceAll("/", "."), path.substring(path.lastIndexOf("/") + 1), host);
	}
}
