package com.kepler.admin.controller;

import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kepler.host.Host;
import com.kepler.management.search.Exported;
import com.kepler.management.search.Properties;
import com.kepler.management.search.RoundTrip;
import com.kepler.management.transfer.Transfers;

/**
 * @author kim 2015年8月8日
 */
@Controller
public class RemoteController {

	private final Exported exported;

	private final RoundTrip roundtrip;

	private final Properties properties;

	public RemoteController(Exported exported, RoundTrip roundtrip, Properties properties) {
		super();
		this.exported = exported;
		this.roundtrip = roundtrip;
		this.properties = properties;
	}

	@RequestMapping(value = "/roundtrip", method = RequestMethod.GET)
	@ResponseBody
	public Collection<Transfers> roundtrip(String path, String host) throws Exception {
		return this.roundtrip.roundtrip(path.substring(0, path.lastIndexOf("/")).replaceAll("/", "."), path.substring(path.lastIndexOf("/") + 1), host);
	}

	@RequestMapping(value = "/properties", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> properties(String host) throws Exception {
		return this.properties.properties(host);
	}

	@RequestMapping(value = "/exported", method = RequestMethod.GET)
	@ResponseBody
	public Collection<Host> exported(String host) throws Exception {
		return this.exported.exported(host);
	}
}
