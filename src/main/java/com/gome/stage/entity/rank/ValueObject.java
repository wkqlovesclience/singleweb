/**
 * @author tianyu-ds
 * @date 2014-2-20
 * @project_name topic_trunk
 * @company �����������޹�˾
 * @desc ��Object�Ĳ��ַ�����������д������һЩObject���������磺finalize��notify��notifyAll��wait��
 */
package com.gome.stage.entity.rank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ValueObject implements Serializable, Cloneable
{
	private static final long serialVersionUID = 1595642780315275502L;
	
	private static final Logger logger = LoggerFactory.getLogger(ValueObject.class);
	
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		
		ValueObject object = new ValueObject();
		return object;	
				
	}

	/**
	 * @return boolean������
	 * @desc ��дObject���е�equals����
	 */
	public boolean equals(Object obj)
	{
		if(null == obj) return false;
		if (this == obj) return true;

		if (this.getClass() == obj.getClass())
		{
			List<Field> fields = this.getFields(this.getClass());
			for (Field field : fields)
			{
				if (field.getType().isArray())
				{
					if (this.getAccessibleClass().contains(
							field.getType().getComponentType()))
					{
						Object[] thisFieldValue = (Object[]) this
								.getFieldValue(field, this);
						Object[] objFieldValue = (Object[]) this.getFieldValue(
								field, obj);
						if (!Arrays.equals(thisFieldValue, objFieldValue)) { return false; }
					}
				}
				else
				{
					if (this.getAccessibleClass().contains(field.getType()))
					{
						Object thisFieldValue = this.getFieldValue(field, this);
						Object objFieldValue = this.getFieldValue(field, obj);
						if (((thisFieldValue == null) && (objFieldValue != null))
								|| ((thisFieldValue != null) && (objFieldValue == null))
								|| ((thisFieldValue != null) && (!thisFieldValue.equals(objFieldValue)))) { return false; }
					}
				}
			}
			return true;
		}

		return false;
	}

	/**
	 * @return integer
	 * @desc ��дObject���е�hashcode����
	 */
	public int hashCode()
	{
		int result = 17;

		List<Field> fields = this.getFields(this.getClass());
		for (Field field : fields)
		{
			if (field.getType().isArray())
			{
				if (this.getAccessibleClass().contains(
						field.getType().getComponentType()))
				{
					Object[] fieldValue = (Object[]) this.getFieldValue(field,
							this);
					result = 37 * result + Arrays.hashCode(fieldValue);
				}
			}
			else
			{
				if (this.getAccessibleClass().contains(field.getType()))
				{
					Object fieldValue = this.getFieldValue(field, this);
					result = 37
							* result
							+ ((fieldValue == null) ? 0 : fieldValue.hashCode());
				}
			}
		}

		return result;
	}

	/**
	 * @return String
	 * @desc ��дObject���е�toString����
	 */
	public String toString()
	{
		StringBuilder result = new StringBuilder();

		result.append(this.getClass().getSimpleName());
		result.append(": (");
		List<Field> fields = getFields(this.getClass());
		for (Field f : fields)
		{
			if (!Modifier.isStatic(f.getModifiers()))
			{
				f.setAccessible(true);
				result.append(f.getName());
				result.append("=");
				try
				{
					if (f.getType().isArray())
					{
						if (this.getAccessibleClass().contains(
								f.getType().getComponentType()))
						{
							result.append((f.get(this) == null) ? "null"
									: Arrays.toString((Object[]) f.get(this)));
						}
						else
						{
							result.append((f.get(this) == null) ? "null" : f
									.getType().getName());
						}
					}
					else
					{
						if (this.getAccessibleClass().contains(f.getType()))
						{
							result.append((f.get(this) == null) ? "null" : f
									.get(this));
						}
						else
						{
							result.append((f.get(this) == null) ? "null" : f
									.getType().getName());
						}
					}
				}
				catch (IllegalAccessException e)
				{
					logger.error(e.getMessage(), e);
				}
				result.append(", ");
			}
		}
		if (!fields.isEmpty())
		{
			result.delete(result.length() - 2, result.length());
		}
		result.append(")");

		return result.toString();
	}

	/**
	 * @return List<Field>
	 * @desc ��ȡһ�����е����ж���ı�����������ı���
	 */
	private List<Field> getFields(Class<?> clazz)
	{
		List<Field> result = new LinkedList<Field>();

		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null)
		{
			result.addAll(getFields(superClass));
		}

		Field[] selfFields = clazz.getDeclaredFields();
		for (Field f : selfFields)
		{
			result.add(f);
		}

		return Collections.unmodifiableList(result);
	}

	/**
	 * @desc ��ȡ�������п����õ�������
	 * @return Set�����а������е�java�������ͣ�Ŀ�ľ���Ϊ�����������ȡ���г�Ա����
	 */
	private Set<Class<?>> getAccessibleClass()
	{
		Set<Class<?>> result = new HashSet<Class<?>>();

		result.add(boolean.class);
		result.add(byte.class);
		result.add(char.class);
		result.add(double.class);
		result.add(float.class);
		result.add(int.class);
		result.add(long.class);
		result.add(short.class);
		result.add(Boolean.class);
		result.add(Byte.class);
		result.add(Character.class);
		result.add(Double.class);
		result.add(Float.class);
		result.add(Integer.class);
		result.add(Long.class);
		result.add(Short.class);
		result.add(String.class);
		result.add(java.math.BigDecimal.class);
		result.add(java.math.BigInteger.class);
		result.add(Date.class);
		result.add(java.sql.Date.class);
		result.add(java.sql.Time.class);
		result.add(java.sql.Timestamp.class);

		return result;
	}

	/**
	 * @desc ��ȡ�������п����õ�������
	 * @return Set�����а������е�java��������
	 */
	private Object getFieldValue(Field field, Object obj)
	{
		Object result = null;

		field.setAccessible(true);
		try
		{
			result = field.get(obj);
		}
		catch (IllegalAccessException e)
		{
			logger.error(e.getMessage(), e);
		}

		return result;
	}
}