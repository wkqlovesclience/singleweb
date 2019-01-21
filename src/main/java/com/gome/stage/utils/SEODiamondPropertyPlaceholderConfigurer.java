package com.gome.stage.utils;


import com.gome.stage.config.diamond.StageDiamondPropertyAware;
import com.gome.stage.logger.GomeLogger;
import com.gome.stage.logger.GomeLoggerFactory;
import com.gome.stage.properties.PropertiesAware;
import com.taobao.diamond.manager.DiamondManager;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;

public class SEODiamondPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements ApplicationContextAware, ManagerListener {
    private static GomeLogger logger = GomeLoggerFactory.getLogger(SEODiamondPropertyPlaceholderConfigurer.class);
    private String group;
    private String dataId;
    private long timeout = 5000L;
    private DiamondManager diamondManager;
    private final Properties properties = new Properties();
    private ApplicationContext applicationContext;
    private final Set<String> propertyAwaresBeanNames = new HashSet();

    public SEODiamondPropertyPlaceholderConfigurer() {
    }
    //2
    protected void fetchPropertiesFromDiamondServer() throws IllegalStateException {
        this.diamondManager = new DefaultDiamondManager(this.group, this.dataId, this);
        String availableConfInfo = this.diamondManager.getAvailableConfigureInfomation(this.timeout);
        logger.info("diamond配置信息初始化.");
        System.out.println("diamond配置信息初始化.");
        this.processRecvConfigInfo(availableConfInfo);
    }
    //3
    private void processRecvConfigInfo(String availableConfInfo) {
        ByteArrayInputStream input = null;
        logger.info("diamondprocessRecvConfigInfo++========");
        System.out.println("diamondprocessRecvConfigInfo++========");
        try {
            Properties variables = new Properties();
            input = new ByteArrayInputStream(availableConfInfo.getBytes("UTF-8"));
            variables.load(input);
            this.properties.clear();
            this.properties.putAll(variables);
            this.setProperties(this.properties);
        } catch (UnsupportedEncodingException var9) {
            logger.error("获取diamond配置信息出错", var9);
            throw new RuntimeException(var9);
        } catch (IOException var10) {
            logger.error("获取diamond配置信息出错", var10);
            throw new RuntimeException(var10);
        } catch (RuntimeException var11) {
            logger.error("获取diamond配置信息出错", var11);
            throw var11;
        } finally {
            IOUtils.closeQuietly(input);
        }

    }
    /**
     *
     * Description:   diamond配置一旦更新，5s后自动刷新properties配置
     *
     * @param:
     * @return: void
     * @auther: qinruixin-ds
     * @date: 2018/11/19 16:31
     */
    public void receiveConfigInfo(String configInfo) {
        logger.info("diamond配置信息发生变更.");
        System.out.println("diamond配置信息发生变更");
        try {
            this.processRecvConfigInfo(configInfo);
            this.notifyPropertyModified(this.properties);
        } catch (RuntimeException var3) {
            logger.error("接收配置处理失败!", var3);
        }
        SEOPropertiesUtils.setProperties(this.properties);
        System.out.println("更新propertiesutils中的properties");
    }

    private synchronized void notifyPropertyModified(Properties properties) {
        Iterator itr = this.propertyAwaresBeanNames.iterator();
        System.out.println("diamondnotifyPropertyModified++========");
        logger.info("diamondnotifyPropertyModified++========");
        while(itr.hasNext()) {
            String beanName = (String)itr.next();
            Object bean = this.applicationContext.getBean(beanName);
            if (StageDiamondPropertyAware.class.isAssignableFrom(bean.getClass())) {
                StageDiamondPropertyAware aware = (StageDiamondPropertyAware)bean;

                try {
                    aware.setDiamondProperties(properties);
                } catch (RuntimeException var7) {
                    logger.error(String.format("更新属性失败(beanName= %s)!", beanName));
                }
            }

            if (PropertiesAware.class.isAssignableFrom(bean.getClass())) {
                PropertiesAware aware = (PropertiesAware)bean;

                try {
                    aware.setProperties(properties);
                } catch (RuntimeException var8) {
                    logger.error(String.format("更新属性失败(beanName= %s)!", beanName));
                }
            }
        }

    }
     //1
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.info("diamondnotifypostProcessBeanFactory++========");
        System.out.println("diamondnotifypostProcessBeanFactory++========");
        this.fetchPropertiesFromDiamondServer();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry)beanFactory;
        String[] beanNameArray = registry.getBeanDefinitionNames();

        for(int i = 0; i < beanNameArray.length; ++i) {
            String beanName = beanNameArray[i];
            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            String beanClassName = beanDef.getBeanClassName();
            Class beanClass = null;

            try {
                beanClass = cl.loadClass(beanClassName);
            } catch (Exception var11) {
                continue;
            }

            MutablePropertyValues mpv;
            if (StageDiamondPropertyAware.class.isAssignableFrom(beanClass)) {
                this.propertyAwaresBeanNames.add(beanName);
                mpv = beanDef.getPropertyValues();
                mpv.addPropertyValue("diamondProperties", this.properties);
            }

            if (PropertiesAware.class.isAssignableFrom(beanClass)) {
                this.propertyAwaresBeanNames.add(beanName);
                mpv = beanDef.getPropertyValues();
                mpv.addPropertyValue("properties", this.properties);
            }
        }

        super.postProcessBeanFactory(beanFactory);
        SEOPropertiesUtils.setProperties(this.properties);
        System.out.println("初始化propertiesutils中的properties");
    }

    public Executor getExecutor() {
        return null;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDataId() {
        return this.dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
