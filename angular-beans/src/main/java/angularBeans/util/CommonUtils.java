/*
 * AngularBeans, CDI-AngularJS bridge 
 *
 * Copyright (c) 2014, Bessem Hmidi. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 */

package angularBeans.util;

import static angularBeans.util.Constants.GET;
import static angularBeans.util.Constants.IS;
import static angularBeans.util.Constants.SET;
import static angularBeans.util.Constants.THREE;
import static angularBeans.util.Constants.TWO;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.EMPTY_MAP;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import angularBeans.api.CORS;
import angularBeans.api.http.Delete;
import angularBeans.api.http.Get;
import angularBeans.api.http.Post;
import angularBeans.api.http.Put;
import angularBeans.realtime.RealTime;

/**
 * @author Bessem Hmidi
 */

public abstract class CommonUtils {

	/**
	 * used to obtain a bean java class from a bean name.
	 */
	public final static Map<String, Class> beanNamesHolder = new HashMap<>();

	public static String getBeanName(Class targetClass) {

		if (targetClass.isAnnotationPresent(Named.class)) {
			Named named = (Named) targetClass.getAnnotation(Named.class);
			return (named.value());
		}

		String name = targetClass.getSimpleName();

		String firstCar = name.substring(0, 1).toLowerCase();

		name = firstCar + name.substring(1);

		beanNamesHolder.put(name, targetClass);

		return name;
	}

	public static String obtainGetter(Field field) {
		String name = field.getName();
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class))
			return IS + name;
		return GET + name;
	}

	public static String obtainSetter(Field field) {
		String name = field.getName();
		name = name.substring(0, 1).toUpperCase() + name.substring(1);

		return SET + name;
	}

	public static JsonElement parse(String message) {

		if (!message.startsWith("{")) {
			return new JsonPrimitive(message);
		}
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(message);

		return element;
	}

	public static Object convertFromString(String value, Class type) {

		if (value == null){
			return null;
		}
		Object param;
		
		if (type.equals(int.class) || type.equals(Integer.class)) {
			param = Integer.parseInt(value);
			return param;
		}

		if (type.equals(float.class) || type.equals(Float.class)) {
			param = Float.parseFloat(value);
			return param;
		}

		if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			param = Boolean.parseBoolean(value);
			return param;
		}

		if (type.equals(double.class) || type.equals(Double.class)) {
			param = Double.parseDouble(value);
			return param;
		}

		if (type.equals(float.class) || type.equals(Float.class)) {
			param = Float.parseFloat(value);
			return param;
		}

		if (type.equals(byte.class) || type.equals(Byte.class)) {
			param = Byte.parseByte(value);
			return param;
		}

		if (type.equals(long.class) || type.equals(Long.class)) {
			param = Long.parseLong(value);
			return param;
		}

		if (type.equals(short.class) || type.equals(Short.class)) {
			param = Short.parseShort(value);
			return param;
		}

		if (type.equals(byte[].class) || type.equals(Byte[].class)) {
			param = null;
			return param;
		}

		else {

			param = type.cast(value);

		}
		return param;

	}
	
	public static boolean isSetter(Method m) {

		return m.getName().startsWith(SET) && m.getReturnType().equals(void.class)
				&& (m.getParameterTypes().length > 0 && m.getParameterTypes().length < 2);

	}

	public static boolean isGetter(Method m) {
		return (
		//TODO clean up dirty boolean
		((m.getParameterTypes().length == 0) && ((m.getName().startsWith(GET))
				|| (((m.getReturnType().equals(boolean.class)) || (m.getReturnType().equals(Boolean.class)))
						&& (m.getName().startsWith(IS)))))
				&& (!(

		m.getReturnType().equals(Void.class) || (m.getReturnType().equals(void.class))
				|| m.isAnnotationPresent(RealTime.class) || m.isAnnotationPresent(Get.class)
				|| m.isAnnotationPresent(Post.class) || m.isAnnotationPresent(Put.class)
				|| m.isAnnotationPresent(Delete.class)
				|| m.isAnnotationPresent(CORS.class)
		))

		);
	}

	public static boolean hasSetter(Class clazz, String name) {

		String setterName = SET
				+ name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		
		setterName = setterName.trim();
		for (Method m : clazz.getDeclaredMethods()) {

			if (m.getName().equals(setterName) && (isSetter(m))) {
				return true;
			}
		}
		return false;
	}

	public static String obtainFieldNameFromAccessor(String getterName) {
		int index = THREE;		
		if (getterName.startsWith(IS))
			index = TWO;
		
		String fieldName = getterName.substring(index);
		fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);

		return fieldName;
	}
	
	public static boolean isNullOrEmpty(Object o){
		//TODO gotta complete this dammy method
		if (o == null){
			return true;
		}
		if (o instanceof String){
			return Strings.isNullOrEmpty((String) o);
		}
		if (o instanceof Collections){
			return ((Collections) o).equals(EMPTY_LIST)
					|| ((Collections) o).equals(EMPTY_MAP)
					|| ((Collections) o).equals(Collections.EMPTY_SET);
		}
		return false;
		
	}

}
