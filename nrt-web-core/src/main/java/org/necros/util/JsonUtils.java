package org.necros.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {
	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	
	private ObjectMapper mapper = new ObjectMapper();
	private ClassCache classCache;
	
	public String encode(Object obj) throws IOException {
		StringWriter w = new StringWriter();
		encode(obj, w);
		return w.toString();
	}
	
	public void encode(Object obj, Writer w) throws IOException {
		mapper.writeValue(w, obj);
	}
	
	public Object decode(String className, String src) throws IOException {
		Class<? extends Object> clazz = classCache.get(className);
		if (clazz == null) {
			logger.warn("Class for name [" + className + "] not found.");
			return null;
		}
		return mapper.readValue(src, clazz);
	}

	public Object decode(String className, Reader r) throws IOException {
		Class<? extends Object> clazz = classCache.get(className);
		if (clazz == null) {
			logger.warn("Class for name [" + className + "] not found.");
			return null;
		}
		return mapper.readValue(r, clazz);
	}

	public ClassCache getClassCache() {
		return classCache;
	}

	public void setClassCache(ClassCache classCache) {
		this.classCache = classCache;
	}
}
