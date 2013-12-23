package org.necros.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestDataBinder;

public class RequestUtils {
	private static final Logger logger = LoggerFactory.getLogger(RequestUtils.class);
	
	private Map<String, ObjectFactory<? extends Object>> beanFactories;
	private ClassCache classCache;
	
	public Object requestToObject(HttpServletRequest req, String clazz) {
		Object obj = clazzToInstance(clazz);
		if (obj == null) {
			obj = requestToMap(req);
		} else {
			assignRequestToObject(req, obj);
		}
		return obj;
	}

	public String requestBody(HttpServletRequest req) throws IOException {
		BufferedReader r = req.getReader();
		StringWriter w = new StringWriter();
		char[] buff = new char[4096];
		int cnt = 0;
		while ((cnt = r.read(buff, 0, buff.length)) > 0) {
			w.write(buff, 0, cnt);
		}
		return w.toString();
	}

	public void assignRequestToObject(HttpServletRequest req, Object obj) {
		ServletRequestDataBinder binder = new ServletRequestDataBinder(obj);
		initBinder(binder, req);
		binder.bind(req);
	}
	
	public String absolutePath(HttpServletRequest req, String relativePath) {
		return absoluteContextPath(req) + relativePath;
	}
	
	public static String absoluteContextPath(HttpServletRequest req) {
		int p = req.getLocalPort();
		return req.getScheme() + "://" + req.getLocalAddr() + (p == 80 ? "" : (":" + p)) + req.getContextPath();
	}

	private void initBinder(ServletRequestDataBinder binder, HttpServletRequest req) {
		ConversionService cs = (ConversionService) req.getAttribute(ConversionService.class.getName());
		if (cs == null) {
			logger.warn("Conversion service not found.");
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Conversion from string to date: " + cs.canConvert(String.class, Date.class));
			}
		}
		binder.setConversionService(cs);
	}

	private Object clazzToInstance(String clazz) {
		logger.debug("Initializing class instance: " + clazz);
		if (!StringUtils.hasText(clazz))
			return null;
		if (beanFactories != null && !beanFactories.isEmpty()) {
			ObjectFactory<? extends Object> factory = beanFactories.get(clazz);
			if (factory != null) return factory.getObject();
		}
		try {
			Class<? extends Object> cls = classCache.get(clazz);
			if (cls == null) {
				return null;
			}
			return cls.newInstance();
		} catch (Throwable t) {
			logger.error("Error creating bean instance.", t);
			return null;
		}
	}

	public Object requestToMap(HttpServletRequest req) {
		Map<String, Object> r = new HashMap<String, Object>();
		for (Entry<String, String[]> e: req.getParameterMap().entrySet()) {
			String k = e.getKey();
			String[] v = e.getValue();
			if (v != null)
				r.put(k, StringUtils.arrayToCommaDelimitedString(v));
		}
		return r;
	}

	public ClassCache getClassCache() {
		return classCache;
	}

	public void setClassCache(ClassCache classCache) {
		this.classCache = classCache;
	}
}
