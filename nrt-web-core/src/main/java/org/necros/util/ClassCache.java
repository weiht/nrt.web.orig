package org.necros.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassCache {
	private static final Logger logger = LoggerFactory.getLogger(ClassCache.class);
	
	private Map<String, Class<? extends Object>> cachedClasses = new HashMap<String, Class<? extends Object>>();

	public Class<? extends Object> get(String className) {
		Class<? extends Object> clazz = cachedClasses.get(className);
		if (clazz == null) {
			clazz = add(className);
		}
		return clazz;
	}

	private Class<? extends Object> add(String className) {
		Class<? extends Object> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (Exception ex) {
			logger.error("", ex);
		}
		//Won't load a second time while load fails.
		cachedClasses.put(className, clazz);
		return clazz;
	}
}