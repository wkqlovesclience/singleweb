package com.gome.stage.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.locks.Lock;

/**
 * Created by wangshuai-ds on 2015/12/21.
 */
public class FileConfigUtil {
	private static Logger logger = Logger.getLogger(FileConfigUtil.class);
	private final static Lock lock = new java.util.concurrent.locks.ReentrantLock();
	private String url;
	private String filePath;

	public FileConfigUtil() {
		
	}

	/**
	 * 
	 * @param filePath
	 */
	public FileConfigUtil(String filePath) {
		this.filePath = filePath;
	}

	/**
	 */
	public void readPropertFile() {
		File file = new File(filePath);
		if (file.exists()) {
			lock.lock();
			String line = "";
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				while ((line = br.readLine()) != null) {
					if (!line.contains("#")) {
						url = line.trim();
						break;
					}
				}
				br.close();
				fr.close();
			} catch (Exception e) {
				logger.error("load.diamond.domain error:" + e.getMessage(), e);
			} finally {
				lock.unlock();
			}
		} else {
			logger.error("config " + filePath + " not exist!");
		}
	}

	/**
	 * 
	 * @return
	 */
	public synchronized String getValue() {
		this.readPropertFile();
		if (StringUtils.isBlank(url)) {
			return "";
		}
		return getUrl();
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
