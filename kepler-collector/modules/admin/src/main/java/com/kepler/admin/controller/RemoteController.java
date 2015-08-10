package com.kepler.admin.controller;

import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kepler.management.search.RoundTrip;
import com.kepler.management.search.Exported;
import com.kepler.management.search.Runtime;
import com.kepler.management.transfer.Transfers;

/**
 * @author kim 2015年8月8日
 */
@Controller
public class RemoteController {

	private final Runtime runtime;

	private final Exported exported;

	private final RoundTrip roundtrip;

	public RemoteController(Runtime runtime, Exported exported, RoundTrip roundtrip) {
		super();
		this.runtime = runtime;
		this.exported = exported;
		this.roundtrip = roundtrip;
	}

	@RequestMapping(value = "/exported", method = RequestMethod.GET)
	@ResponseBody
	public Collection<String> relations(String path, String host) throws Exception {
		return this.exported.exported(path.substring(0, path.lastIndexOf("/")).replaceAll("/", "."), path.substring(path.lastIndexOf("/") + 1), host);
	}

	@RequestMapping(value = "/roundtrip", method = RequestMethod.GET)
	@ResponseBody
	public Collection<Transfers> history(String path, String host) throws Exception {
		return this.roundtrip.roundtrip(path.substring(0, path.lastIndexOf("/")).replaceAll("/", "."), path.substring(path.lastIndexOf("/") + 1), host);
	}

	@RequestMapping(value = "/runtime", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> properties(String host) throws Exception {
		return this.runtime.runtime(host);
	}
}
