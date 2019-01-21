package com.gome.stage.utils;

import org.apache.commons.lang3.StringUtils;


/**
 * 基本数据类型转换
 * 
 * @author Ellison
 * 
 */
public class BasicDataTypeConverter  {

	public <T> String convert(T obj) {
		if (null == obj) {
			return null;
		} else if (String.class == obj.getClass()) {
			return (String) obj;
		} else if (Integer.class == obj.getClass() || int.class == obj.getClass() || Double.class == obj.getClass() || double.class == obj.getClass()
				|| Float.class == obj.getClass() || float.class == obj.getClass() || Long.class == obj.getClass() || long.class == obj.getClass()
				|| Boolean.class == obj.getClass() || boolean.class == obj.getClass()) {
			return String.valueOf(obj);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T convert(Class<T> objType, String value) {
		if(StringUtils.isNotBlank(value)){
			if (String.class == objType) {
				return (T) value;
			} else if (Integer.class == objType || int.class == objType) {
				return (T) new Integer(value);
			} else if (Double.class == objType || double.class == objType) {
				return (T) new Double(value);
			} else if (Float.class == objType || float.class == objType) {
				return (T) new Float(value);
			} else if (Boolean.class == objType || boolean.class == objType) {
				return (T) new Boolean(value);
			} else if (Long.class == objType || long.class == objType) {
				return (T) new Long(value);
			}
		}

		return null;
	}

	public <T> boolean isConvert(Class<T> objType) {
		if (String.class == objType || Integer.class == objType || int.class == objType || Double.class == objType || double.class == objType
				|| Float.class == objType || float.class == objType || Boolean.class == objType || boolean.class == objType || Long.class == objType || long.class == objType) {
			return true;
		}
		return false;
	}

}
