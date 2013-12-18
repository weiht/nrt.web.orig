package org.necros.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

@Controller
public class VelocityController {
	@RequestMapping(value="/**")
	public String doVelocity(HttpServletRequest request) {
		String path = (String) request.getAttribute( HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE );
		request.setAttribute("request", request);
		return path;
	}
}
