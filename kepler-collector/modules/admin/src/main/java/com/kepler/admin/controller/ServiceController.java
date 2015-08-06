package com.kepler.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kepler.admin.service.Node;
import com.kepler.admin.service.Paths;
import com.kepler.admin.service.ServiceContext;

/**
 * @author kim 2014年7月12日
 */
@Controller
@RequestMapping(value = "/service")
public class ServiceController {

	private final ServiceContext context;

	public ServiceController(ServiceContext context) {
		super();
		this.context = context;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Paths path(String path) throws Exception {
		return this.context.path(path);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Node del(String path) throws Exception {
		return this.context.del(path);
	}
}
