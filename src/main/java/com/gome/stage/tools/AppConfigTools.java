package com.gome.stage.tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.gome.frontSe.comm.BaseBean;

public class AppConfigTools {
	protected static Logger logger = LoggerFactory.getLogger(AppConfigTools.class);

	private static final AppConfigTools instance = new AppConfigTools();
	private Map<String, String> variables = null; // new HashMap<String, String>();

	private Resource location;

	private AppConfigTools() {
		if (instance != null) {
			throw new IllegalStateException();
		}
	}

	public static AppConfigTools getInstance() {
		return instance;
	}

	public synchronized void initIfNecessary() {
		if (this.variables == null) {
			try {
				Properties props = new Properties();
				props.load(this.location.getInputStream());

				this.variables = new HashMap<String, String>();

				Iterator<Map.Entry<Object, Object>> itr = props.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry<Object, Object> entry = itr.next();
					Object key = entry.getKey();
					Object val = entry.getValue();

					boolean keyValid = key != null && String.class.isInstance(key);
					boolean valValid = val == null || String.class.isInstance(val);
					if (keyValid && valValid) {
						this.variables.put((String) key, (String) val);
					}

				}

			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}

	}

	private Map<String, String> getConfigInfo() {
		if (this.variables == null) {
			this.initIfNecessary();
		}
		return this.variables;
	}

	public static void setAppConfig(BaseBean bean) {
		Map<String, String> map = getAppConfig();
		bean.setStoreConfiguration(map);
	}

	public static Map<String, String> getAppConfig() {
		return getInstance().getConfigInfo();
	}

	public Resource getLocation() {
		return location;
	}

	public void setLocation(Resource location) {
		this.location = location;
	}

}
