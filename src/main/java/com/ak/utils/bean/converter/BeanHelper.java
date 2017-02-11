package com.ak.utils.bean.converter;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author abhishek Kumar
 *
 */
public class BeanHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BeanHelper.class);

	/**
	 * map to store class properties mapping
	 */
	private static Map<String, Map<String, String>> classPropertyMap = new HashMap<>();

	static {
		/**
		 * Add BooleanIntrospector to handle method start with "is"
		 */
		BeanUtilsBean.getInstance().getPropertyUtils().addBeanIntrospector(new BooleanIntrospector());
	}

	/**
	 * 
	 * @param klass1
	 * @param klass2
	 * @param klass1ToKlass2FieldMap
	 */
	public static <S, D> void addClassPropertyMap(Class<S> klass1, Class<D> klass2,
			Map<String, String> klass1ToKlass2FieldMap) {
		LOGGER.debug("Adding field mapping for "+klass1.getName()+" and "+klass2.getName());
		classPropertyMap.put(createKey(klass1, klass2), klass1ToKlass2FieldMap);
		registerConverter(klass1, klass2);
	}

	/**
	 * 
	 * @param source
	 * @param destinationType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 */
	public static <T> T convert(Object source, Class<T> destinationType)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
		T destination = destinationType.newInstance();
		convert(source, destination);
		return destination;
	}
	
	/**
	 * 
	 * @param source
	 * @param destination
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static void convert(Object source, Object destination)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String key = createKey(source.getClass(), destination.getClass());
		if (classPropertyMap.containsKey(key)) {
			LOGGER.debug("Found properties mapping");
			Map<String, String> fieldMap = classPropertyMap.get(key);
			if (fieldMap != null && !fieldMap.isEmpty()) {
				for (Entry<String, String> entry : fieldMap.entrySet()) {
					LOGGER.debug("Copy "+entry.getKey() + " of "+ source.getClass().getName()+ " to "+entry.getValue()+" from "+destination.getClass().getName());
					Object value = PropertyUtils.getProperty(source, entry.getKey());
					BeanUtils.setProperty(destination, entry.getValue(), value);
				}
			}
			return;
		}

		key = createKey(destination.getClass(), source.getClass());
		if (classPropertyMap.containsKey(key)) {
			LOGGER.debug("Found properties mapping");
			Map<String, String> fieldMap = classPropertyMap.get(key);
			if (fieldMap != null && !fieldMap.isEmpty()) {
				for (Entry<String, String> entry : fieldMap.entrySet()) {
					LOGGER.debug("Copy "+entry.getValue() + " of "+ source.getClass().getName()+ " to "+entry.getKey()+" from "+destination.getClass().getName());
					Object value = PropertyUtils.getProperty(source, entry.getValue());
					BeanUtils.setProperty(destination, entry.getKey(), value);
				}
			}
			return;
		}

		LOGGER.debug("No properties mapping found, hence using BeanUtills.copyProperties");
		BeanUtils.copyProperties(destination, source);

	}

	/**
	 * Register converters
	 * 
	 * @param klass1
	 * @param klass2
	 */
	private static <S, D> void registerConverter(Class<S> klass1, Class<D> klass2) {
		BeanUtilsBean.getInstance().getConvertUtils().register(new Converter() {

			@Override
			public <T> T convert(Class<T> type, Object value) {
				T newInstance;
				try {
					newInstance = type.newInstance();
					BeanHelper.convert(value, newInstance);
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
					BeanHelper.convert(value, newInstance);
					return newInstance;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

			}
		}, klass2);

	}

	/**
	 * 
	 * @param klass1
	 * @param klass2
	 * @return key to store field mapping 
	 */
	private static <S, D> String createKey(Class<S> klass1, Class<D> klass2) {
		return klass1.getName() + "$" + klass2.getName();
	}

}