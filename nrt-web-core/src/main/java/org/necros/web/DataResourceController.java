package org.necros.web;

import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import org.necros.data.MapDAO;
import org.necros.data.MetaClassManager;
import org.necros.data.MetaClass;
import org.necros.data.MetaPackageManager;
import org.necros.data.MetaPackage;
import org.necros.data.MetaDataAccessException;

@Controller
public class DataResourceController {
	private static final Logger logger = LoggerFactory.getLogger(DataResourceController.class);

	private static final String MAP_PREFIX = "/entities";
	private static final String ENTITY_PREFIX = "/{entity}";
	private static final String ID_PREFIX = "/{id}";
	private static final String PKG_PREFIX = "/package";
	private static final String PKG_LIST_PREFIX = "/packages";
	private static final String CLS_PREFIX = "/class";
	private static final String CLS_LIST_PREFIX = "/classes";
	private static final String PATH_PREFIX = "/{path:.+}";

//	@Resource(name="mapDAO")
	private MapDAO dao;
	@Resource(name="metaClassManager")
	private MetaClassManager metaClassManager;
	@Resource(name="metaPackageManager")
	private MetaPackageManager metaPackageManager;

	@RequestMapping(value=PKG_LIST_PREFIX, method=RequestMethod.GET)
	public @ResponseBody List<MetaPackage> rootPackages(
		HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			return metaPackageManager.root();
		} catch (Exception ex) {
			logger.warn("Error retrieving root packges: \n {}", ex);
			throw new ServletException(ex);
		}
	}

	@RequestMapping(value=PKG_LIST_PREFIX + PATH_PREFIX, method=RequestMethod.GET)
	public @ResponseBody List<MetaPackage> childPackages(
		@PathVariable("path") String path,
		HttpServletRequest request, HttpServletResponse response) throws ServletException {
		logger.debug("Retrieving children of [{}]...", path);
		try {
			return !StringUtils.hasText(path) ? metaPackageManager.root() : metaPackageManager.children(path);
		} catch (Exception ex) {
			logger.warn("Error retrieving packges: {}\n {}", path, ex);
			throw new ServletException(ex);
		}
	}

	@RequestMapping(value=PKG_PREFIX, method=RequestMethod.POST)
	public @ResponseBody MetaPackage addPackage(
		@RequestBody MetaPackage pkg,
		HttpServletRequest request, HttpServletResponse response)
		throws MetaDataAccessException {
		try {
			return metaPackageManager.add(pkg);
		} catch (MetaDataAccessException e) {
			logger.error("Error saving package: {}\n{}", pkg, e);
			throw e;
		}
	}

	@RequestMapping(value=PKG_PREFIX, method=RequestMethod.PUT)
	public @ResponseBody MetaPackage modifyPackage(
		@RequestBody MetaPackage pkg,
		HttpServletRequest request, HttpServletResponse response)
		throws MetaDataAccessException {
		try {
			return metaPackageManager.updateDescription(pkg);
		} catch (MetaDataAccessException e) {
			logger.error("Error saving package: {}\n{}", pkg, e);
			throw e;
		}
	}

	@RequestMapping(value=PKG_PREFIX + PATH_PREFIX, method=RequestMethod.DELETE)
	public @ResponseBody MetaPackage removePackage(
		@PathVariable("path") String path,
		HttpServletRequest request, HttpServletResponse response)
		throws MetaDataAccessException {
		try {
			return metaPackageManager.remove(path);
		} catch (MetaDataAccessException e) {
			logger.error("Error saving package: {}\n{}", path, e);
			throw e;
		}
	}

	@RequestMapping(value=PKG_PREFIX + PATH_PREFIX, method=RequestMethod.GET)
	public @ResponseBody MetaPackage getPackage(
		@PathVariable("path") String path,
		HttpServletRequest request, HttpServletResponse response) {
		return metaPackageManager.get(path);
	}

	@RequestMapping(value=CLS_LIST_PREFIX, method=RequestMethod.GET)
	public @ResponseBody List<MetaClass> rootClasses(
		HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			return metaClassManager.all(null);
		} catch (Exception ex) {
			logger.warn("Error retrieving root classes: \n {}", ex);
			throw new ServletException(ex);
		}
	}

	@RequestMapping(value=CLS_LIST_PREFIX + PATH_PREFIX, method=RequestMethod.GET)
	public @ResponseBody List<MetaClass> childClasses(
		@PathVariable("path") String path,
		HttpServletRequest request, HttpServletResponse response) throws ServletException {
		logger.debug("Retrieving child classes of [{}]...", path);
		try {
			return !StringUtils.hasText(path) ? metaClassManager.all(null) : metaClassManager.all(path);
		} catch (Exception ex) {
			logger.warn("Error retrieving classes: {}\n {}", path, ex);
			throw new ServletException(ex);
		}
	}

	@RequestMapping(value=CLS_PREFIX, method=RequestMethod.POST)
	public @ResponseBody MetaClass addClass(
		@RequestBody MetaClass cls,
		HttpServletRequest request, HttpServletResponse response)
		throws MetaDataAccessException {
		try {
			return metaClassManager.add(cls);
		} catch (MetaDataAccessException e) {
			logger.error("Error saving class: {}\n{}", cls, e);
			throw e;
		}
	}

	@RequestMapping(value=CLS_PREFIX, method=RequestMethod.PUT)
	public @ResponseBody MetaClass modifyClass(
		@RequestBody MetaClass cls,
		HttpServletRequest request, HttpServletResponse response)
		throws MetaDataAccessException {
		try {
			return metaClassManager.updateInfo(cls);
		} catch (MetaDataAccessException e) {
			logger.error("Error saving class: {}\n{}", cls, e);
			throw e;
		}
	}

	@RequestMapping(value=CLS_PREFIX + ID_PREFIX, method=RequestMethod.DELETE)
	public @ResponseBody MetaClass removeClass(
		@PathVariable("id") String id,
		HttpServletRequest request, HttpServletResponse response)
		throws MetaDataAccessException {
		try {
			return metaClassManager.remove(id);
		} catch (MetaDataAccessException e) {
			logger.error("Error saving package: {}\n{}", id, e);
			throw e;
		}
	}

	@RequestMapping(value=CLS_PREFIX + ID_PREFIX, method=RequestMethod.GET)
	public @ResponseBody MetaClass getClass(
		@PathVariable("id") String id,
		HttpServletRequest request, HttpServletResponse response) {
		return metaClassManager.get(id);
	}

	@RequestMapping(value=MAP_PREFIX + ENTITY_PREFIX + ID_PREFIX, method=RequestMethod.GET)
	public @ResponseBody Map<String, Object> getById(
			@PathVariable("entity") String entityName,
			@PathVariable("id") String id,
			HttpServletRequest request, HttpServletResponse response) {
		if (!StringUtils.hasText(entityName) || !StringUtils.hasText(id)) {
			return null;
		}
		return dao.get(id);
	}
}
