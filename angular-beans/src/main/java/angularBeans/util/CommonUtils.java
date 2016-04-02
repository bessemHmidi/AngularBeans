/* AngularBeans, CDI-AngularJS bridge Copyright (c) 2014, Bessem Hmidi. or third-party contributors as indicated by
 * the @author tags or express copyright attribution statements applied by the authors. This copyrighted material is
 * made available to anyone wishing to use, modify, copy, or redistribute it subject to the terms and conditions of the
 * GNU Lesser General Public License, as published by the Free Software Foundation. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. */

package angularBeans.util;

import static angularBeans.util.Accessor.GET;
import static angularBeans.util.Accessor.IS;
import static angularBeans.util.Accessor.SET;
import static angularBeans.util.Constants.THREE;
import static angularBeans.util.Constants.TWO;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

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
	public static final Map<String, Class> beanNamesHolder = new HashMap<>();

	public static String getBeanName(Class targetClass) {

		if (targetClass.isAnnotationPresent(Named.class)) {
			Named named = (Named) targetClass.getAnnotation(Named.class);
			return named.value();
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
			return IS.prefix() + name;
		return GET.prefix() + name;
	}

	public static String obtainSetter(Field field) {
		String name = field.getName();
		name = name.substring(0, 1).toUpperCase() + name.substring(1);

		return SET.prefix() + name;
	}

	public static JsonElement parse(String message) {

		if (!message.startsWith("{")) {
			return new JsonPrimitive(message);
		}
		JsonParser parser = new JsonParser();
		return parser.parse(message);
	}

	public static Object convertFromString(String value, Class type) {

		if (isNullOrEmpty(value) || type.equals(byte[].class) || type.equals(Byte[].class)) {
			return null;
		}
		if (type.equals(int.class) || type.equals(Integer.class)) {
			return Integer.parseInt(value);
		}
		if (type.equals(float.class) || type.equals(Float.class)) {
			return Float.parseFloat(value);
		}
		if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			return Boolean.parseBoolean(value);
		}
		if (type.equals(double.class) || type.equals(Double.class)) {
			return Double.parseDouble(value);
		}
		if (type.equals(byte.class) || type.equals(Byte.class)) {
			return Byte.parseByte(value);
		}
		if (type.equals(long.class) || type.equals(Long.class)) {
			return Long.parseLong(value);
		}
		if (type.equals(short.class) || type.equals(Short.class)) {
			return Short.parseShort(value);
		}

		return type.cast(value);
	}

	public static boolean isSetter(Method m) {

		return m.getName().startsWith(SET.prefix()) && m.getReturnType().equals(void.class)
				&& (m.getParameterTypes().length > 0 && m.getParameterTypes().length < TWO);

	}

	public static boolean isGetter(Method m) {
		return
		// TODO clean up dirty boolean
		((m.getParameterTypes().length == 0) && ((m.getName().startsWith(GET.prefix()))
				|| (((m.getReturnType().equals(boolean.class)) || (m.getReturnType().equals(Boolean.class)))
						&& (m.getName().startsWith(IS.prefix())))))
				&& (!(

		m.getReturnType().equals(Void.class) || (m.getReturnType().equals(void.class))
				|| m.isAnnotationPresent(RealTime.class) || m.isAnnotationPresent(Get.class)
				|| m.isAnnotationPresent(Post.class) || m.isAnnotationPresent(Put.class)
				|| m.isAnnotationPresent(Delete.class) || m.isAnnotationPresent(CORS.class)));
	}

	public static boolean hasSetter(Class clazz, String name) {

		String setterName = SET.prefix() + name.substring(0, 1).toUpperCase() + name.substring(1);

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
		if (getterName.startsWith(IS.prefix()))
			index = TWO;

		String fieldName = getterName.substring(index);
		fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);

		return fieldName;
	}

	/**
	 * check is the parameter is null or empty.
	 * 
	 * @param <T>
	 *            class type of tested objects.
	 * @param o
	 *            parameter to check.
	 * @return true is the parameter is null or empty, false otherwise.
	 */
	public static <T> Boolean isNullOrEmpty(final T o) {
		if (o == null) {
			return true;
		}
		if (o instanceof String) {
			return "".equals(((String) o).trim());
		}
		if (o.getClass().isArray()) {
			return Arrays.asList((Object[]) o).isEmpty();
		}
		if (o instanceof Collection<?>) {
			return ((Collection<?>) o).isEmpty();
		}
		return false;
	}

	/**
	 * check is the parameter (byte array) is null or empty.
	 * 
	 * @param o
	 *            byte[] parameter to check.
	 * @return true is the parameter is null or empty, false otherwise.
	 */
	public static Boolean isNullOrEmpty(final byte[] o) {
		if (o == null) {
			return true;
		}
		return o.length == 0;
	}
}
