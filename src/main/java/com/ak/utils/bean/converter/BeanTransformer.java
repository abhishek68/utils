package com.ak.utils.bean.converter;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;

public class BeanTransformer {

	private static Map<String, Map<String, String>> classPropertyMap = new HashMap<>();

	static {
		BeanUtilsBean.getInstance().getPropertyUtils().addBeanIntrospector(new BooleanIntrospector());
	}

	public static <S, D> void addClassPropertyMap(Class<S> klass1, Class<D> klass2,
			Map<String, String> klass1ToKlass2FieldMap) {
		classPropertyMap.put(createKey(klass1, klass2), klass1ToKlass2FieldMap);
		registerConverter(klass1, klass2);
	}

	public static <T> T convert(Object source, Class<T> destinationType)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
		T destination = destinationType.newInstance();
		convert(source, destination);
		return destination;
	}
	
	public static void convert(Object source, Object destination)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String key = createKey(source.getClass(), destination.getClass());
		if (classPropertyMap.containsKey(key)) {
			Map<String, String> fieldMap = classPropertyMap.get(key);
			if (fieldMap != null && !fieldMap.isEmpty()) {
				for (Entry<String, String> entry : fieldMap.entrySet()) {
					Object value = PropertyUtils.getProperty(source, entry.getKey());
					BeanUtils.setProperty(destination, entry.getValue(), value);
				}
			}
			return;
		}

		key = createKey(destination.getClass(), source.getClass());
		if (classPropertyMap.containsKey(key)) {
			Map<String, String> fieldMap = classPropertyMap.get(key);
			if (fieldMap != null && !fieldMap.isEmpty()) {
				for (Entry<String, String> entry : fieldMap.entrySet()) {
					Object value = PropertyUtils.getProperty(source, entry.getValue());
					BeanUtils.setProperty(destination, entry.getKey(), value);
				}
			}
			return;
		}

		BeanUtils.copyProperties(destination, source);

	}

	private static <S, D> void registerConverter(Class<S> klass1, Class<D> klass2) {
		BeanUtilsBean.getInstance().getConvertUtils().register(new Converter() {

			@Override
			public <T> T convert(Class<T> type, Object value) {
				T newInstance;
				try {
					newInstance = type.newInstance();
					BeanTransformer.convert(value, newInstance);
					return newInstance;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

			}
		}, klass1);

		BeanUtilsBean.getInstance().getConvertUtils().register(new Converter() {

			@Override
			public <T> T convert(Class<T> type, Object value) {
				T newInstance;
				try {
					newInstance = type.newInstance();
					BeanTransformer.convert(value, newInstance);
					return newInstance;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

			}
		}, klass2);

	}

	private static <S, D> String createKey(Class<S> klass1, Class<D> klass2) {
		return klass1.getName() + "$" + klass2.getName();
	}

}