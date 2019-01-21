package com.gome.stage.diamond;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.gome.stage.logger.GomeLogger;
import com.gome.stage.logger.GomeLoggerFactory;
import com.gome.stage.properties.PropertiesAware;
import com.taobao.diamond.manager.DiamondManager;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;

/**
 * 用于运行时从diamond接收配置属性.
 * 
 * @author liuyangming
 */
public class StageDiamondPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements
		ApplicationContextAware, ManagerListener {
	private static GomeLogger logger = GomeLoggerFactory.getLogger(StageDiamondPropertyPlaceholderConfigurer.class);

	private String group;
	private String dataId;
	private long timeout = 5000L;
	private DiamondManager diamondManager;
	private final Properties properties = new Properties();
	private ApplicationContext applicationContext;
	private final Set<String> propertyAwaresBeanNames = new HashSet<String>();

	/**
	 * 从diamond服务器中获取配置信息.
	 */
	protected void fetchPropertiesFromDiamondServer() throws IllegalStateException {
		this.diamondManager = new DefaultDiamondManager(group, dataId, this);
		String availableConfInfo = this.diamondManager.getAvailableConfigureInfomation(this.timeout);

		logger.info("diamond配置信息初始化.");
		this.processRecvConfigInfo(availableConfInfo);
	}

	private void processRecvConfigInfo(String availableConfInfo) {
		InputStream input = null;
		try {
			Properties variables = new Properties();
			input = new ByteArrayInputStream(availableConfInfo.getBytes("UTF-8"));
			variables.load(input);
			this.properties.clear();
			this.properties.putAll(variables);
			this.setProperties(this.properties);
		} catch (UnsupportedEncodingException ex) {
			logger.error("获取diamond配置信息出错", ex);
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			logger.error("获取diamond配置信息出错", ex);
			throw new RuntimeException(ex);
		} catch (RuntimeException ex) {
			logger.error("获取diamond配置信息出错", ex);
			throw ex;
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	public void receiveConfigInfo(String configInfo) {
		logger.info("diamond配置信息发生变更.");
		try {
			this.processRecvConfigInfo(configInfo);
			this.notifyPropertyModified(this.properties);
		} catch (RuntimeException ex) {
			logger.error("接收配置处理失败!", ex);
		}
	}

	private synchronized void notifyPropertyModified(Properties properties) {
		Iterator<String> itr = this.propertyAwaresBeanNames.iterator();
		while (itr.hasNext()) {
			String beanName = itr.next();
			Object bean = this.applicationContext.getBean(beanName);
			if (StageDiamondPropertyAware.class.isAssignableFrom(bean.getClass())) {
				StageDiamondPropertyAware aware = (StageDiamondPropertyAware) bean;
				try {
					aware.setDiamondProperties(properties);
				} catch (RuntimeException rex) {
					logger.error(String.format("更新属性失败(beanName= %s)!", beanName));
				}
			}
			if (PropertiesAware.class.isAssignableFrom(bean.getClass())) {
				PropertiesAware aware = (PropertiesAware) bean;
				try {
					aware.setProperties(properties);
				} catch (RuntimeException rex) {
					logger.error(String.format("更新属性失败(beanName= %s)!", beanName));
				}
			}
		}
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		this.fetchPropertiesFromDiamondServer();

		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
		String[] beanNameArray = registry.getBeanDefinitionNames();

		for (int i = 0; i < beanNameArray.length; i++) {
			String beanName = beanNameArray[i];
			BeanDefinition beanDef = registry.getBeanDefinition(beanName);

			String beanClassName = beanDef.getBeanClassName();
			Class<?> beanClass = null;
			try {
				beanClass = cl.loadClass(beanClassName);
			} catch (Exception ex) {
				continue;
			}

			if (StageDiamondPropertyAware.class.isAssignableFrom(beanClass)) {
				this.propertyAwaresBeanNames.add(beanName);
				MutablePropertyValues mpv = beanDef.getPropertyValues();
				mpv.addPropertyValue(StageDiamondPropertyAware.FIELD_DIAMOND_PROPERTIES, this.properties);
			}
			if (PropertiesAware.class.isAssignableFrom(beanClass)) {
				this.propertyAwaresBeanNames.add(beanName);
				MutablePropertyValues mpv = beanDef.getPropertyValues();
				mpv.addPropertyValue(PropertiesAware.FIELD_PROPERTIES, this.properties);
			}

		}

		super.postProcessBeanFactory(beanFactory);

	}

	public Executor getExecutor() {
		return null;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
