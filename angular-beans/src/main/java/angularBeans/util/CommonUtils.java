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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import angularBeans.api.CORS;
import angularBeans.realtime.RealTime;

/**
 * @author Bessem Hmidi
 */

public abstract class CommonUtils {

	public static final String NG_SESSION_ATTRIBUTE_NAME = "NG_SESSION_ID";

	/**
	 * used to obtain a bean java class from a bean name.
	 */
	public final static Map<String, Class> beanNamesHolder = new HashMap<String, Class>();

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
			return "is" + name;
		return "get" + name;
	}

	public static String obtainSetter(Field field) {
		String name = field.getName();
		name = name.substring(0, 1).toUpperCase() + name.substring(1);

		return "set" + name;
	}

	public static JsonElement parse(String message) {

		if (!message.startsWith("{")) {
			return new JsonPrimitive(message);
		}

		// JsonReader reader = new JsonReader(new StringReader(message));
		// reader.setLenient(true);

		JsonParser parser = new JsonParser();

		JsonElement element = parser.parse(message);

		return element;
	}

	public static Object convertFromString(String value, Class type) {

		Object param = null;

		// NPE

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

	// public static List<Method> obtainSetters(Class clazz){
	//
	// //Method[] methods=clazz.getDeclaredMethods()
	//
	// }

	public static boolean isSetter(Method m) {

		return m.getName().startsWith("set") && m.getReturnType().equals(void.class)
				&& (m.getParameterTypes().length > 0 && m.getParameterTypes().length < 2);

	}

	public static boolean isGetter(Method m) {
		return (

		(
				(m.getParameterTypes().length == 0) && ((m.getName().startsWith("get"))
				|| (((m.getReturnType().equals(boolean.class)) || (m.getReturnType().equals(Boolean.class)))
						&& (m.getName().startsWith("is"))))
						)
				&& (!(

						m.getReturnType().equals(Void.class) ||
		m.isAnnotationPresent(RealTime.class) || m.isAnnotationPresent(GET.class) || m.isAnnotationPresent(POST.class)
				|| m.isAnnotationPresent(PUT.class) || m.isAnnotationPresent(DELETE.class)
				|| m.isAnnotationPresent(OPTIONS.class) || m.isAnnotationPresent(HEAD.class)
				|| m.isAnnotationPresent(CORS.class)

		))

		);

	}

	public static boolean hasSetter(Class clazz, String name) {

		String setterName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
		setterName = setterName.trim();

		for (Method m : clazz.getDeclaredMethods()) {

			if (m.getName().equals(setterName) && (isSetter(m))) {
				return true;
			}
		}

		return false;
	}

	public static String obtainFieldNameFromAccessor(String getterName) {
		int index = 3;
		if (getterName.startsWith("is"))
			index = 2;
		String fieldName = getterName.substring(index);

		fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);

		return fieldName;
	}

}
