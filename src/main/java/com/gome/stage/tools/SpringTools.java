package com.gome.stage.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringTools implements ApplicationContextAware {
	protected static Logger log = LoggerFactory.getLogger(SpringTools.class);

	private static final SpringTools instance = new SpringTools();
	private ApplicationContext applicationContext;

	private SpringTools() {
		if (instance != null) {
			throw new IllegalStateException();
		}
	}

	public static SpringTools getInstance() {
		return instance;
	}

	public static ApplicationContext getContext() {
		return SpringTools.getInstance().getApplicationContext();
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

}