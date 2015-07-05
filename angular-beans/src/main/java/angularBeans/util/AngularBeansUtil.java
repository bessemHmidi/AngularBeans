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

/**
 @author Bessem Hmidi
 */
package angularBeans.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import angularBeans.io.ByteArrayCache;
import angularBeans.io.Call;
import angularBeans.io.LobWrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@ApplicationScoped
public class AngularBeansUtil implements Serializable {

	public static final String NG_SESSION_ATTRIBUTE_NAME = "NG_SESSION_ID";

	@Inject
	private CurrentNGSession currentSession;

	public String getCurrentSessionId() {
		return currentSession.getSessionId();
	}

	@Inject
	ByteArrayCache cache;

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

	public static Map<String, Class> beanNamesHolder = new HashMap<String, Class>();

	public static String obtainGetter(Field field) {
		String name = field.getName();
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		if (field.getType().equals(Boolean.class)
				|| field.getType().equals(boolean.class))
			return "is" + name;
		return "get" + name;
	}

	public static String obtainSetter(Field field) {
		String name = field.getName();
		name = name.substring(0, 1).toUpperCase() + name.substring(1);

		return "set" + name;
	}

	@PostConstruct
	public void init() {

		// builder.registerTypeAdapter(byte[].class, new
		// JsonSerializer<Object>() {
		//
		// @Override
		// public JsonElement serialize(Object src, Type typeOfSrc,
		// JsonSerializationContext context) {
		//
		// for (String key : cache.getCache().keySet()) {
		// if (cache.getCache().get(key).equals(src)) {
		// return new JsonPrimitive("lob" + key);
		// }
		// }
		//
		// return new JsonPrimitive("hoho");
		// }
		// });

	}

	public String getJson(Object object) {

		if (object instanceof Properties) {
			return new Gson().toJson(object);
		}

		GsonBuilder builder = new GsonBuilder().serializeNulls();

		if (object == null) {
			return new GsonBuilder().serializeNulls().create().toJson(null);

		}

		// Class clazz = object.getClass();

		builder.registerTypeAdapter(LobWrapper.class,
				new LobWrapperJsonAdapter(cache));

		builder.registerTypeAdapter(byte[].class, new ByteArrayJsonAdapter(
				cache, contextPath));

		Gson gson = builder.create();

		return gson.toJson(object);

	}

	public static String obtainFieldNameFromAccessor(String methodName) {
		int index = 3;
		if (methodName.startsWith("is"))
			index = 2;
		String fieldName = methodName.substring(index);

		fieldName = fieldName.substring(0, 1).toLowerCase()
				+ fieldName.substring(1);

		return fieldName;
	}

	public static JsonObject parse(String message) {

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(message);

		return element.getAsJsonObject();
	}

	public Object convertFromString(String value, Class type) {

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

	public boolean isSetter(Method m) {

		return m.getName().startsWith("set")
				&& m.getReturnType().equals(void.class)
				&& (m.getParameterTypes().length > 0 && m.getParameterTypes().length < 2);

	}

	public static boolean isGetter(Method m) {
		return (

		(m.getParameterTypes().length == 0) && ((m.getName().startsWith("get")) || (((m
				.getReturnType().equals(boolean.class)) || (m.getReturnType()
				.equals(Boolean.class))) && (m.getName().startsWith("is")))));

	}

	public boolean hasSetter(Class clazz, String name) {

		String setterName = "set" + name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		setterName = setterName.trim();

		for (Method m : clazz.getDeclaredMethods()) {

			if (m.getName().equals(setterName)) {
				return true;
			}
		}

		return false;
	}

	private String contextPath;

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;

	}
	
	public Object deserialise(Class clazz, JsonElement element) {
		Object elem;
		GsonBuilder builder = new GsonBuilder();

		builder.registerTypeAdapter(LobWrapper.class,
				new JsonDeserializer<LobWrapper>() {

					@Override
					public LobWrapper deserialize(JsonElement json,
							Type typeOfT, JsonDeserializationContext context)
							throws JsonParseException {

						return null;
					}
				});

		

		Gson gson = builder.create();

		elem = gson.fromJson(element, clazz);
		return elem;
	}

}

class LobWrapperJsonAdapter implements JsonSerializer<LobWrapper> {

	Object container;
	ByteArrayCache cache;

	public LobWrapperJsonAdapter(ByteArrayCache cache) {

		this.cache = cache;
	}

	public JsonElement serialize(LobWrapper src, Type typeOfSrc,
			JsonSerializationContext context) {

		LobWrapper lobWrapper = (LobWrapper) src;

		container = lobWrapper.getOwner();
		String id = "";
		Class clazz = container.getClass();

		for (Method m : clazz.getMethods()) {

			if (m.getName().startsWith("get") || m.getName().startsWith("is")) {
				if (m.getReturnType().equals(LobWrapper.class)) {
					String field = AngularBeansUtil
							.obtainFieldNameFromAccessor(m.getName());

					try {

						Call lobSource = new Call(container, m);

						if (!cache.getCache().containsValue(lobSource)) {
							id = String.valueOf(UUID.randomUUID());
							cache.getCache().put(id, lobSource);
						} else {
							for (String idf : (cache.getCache().keySet())) {
								Call ls = cache.getCache().get(idf);
								if (ls.equals(lobSource)) {
									id = idf;
									// cache.getCache().remove(idf);
									// id = String.valueOf(UUID.randomUUID());
									// cache.getCache().put(id, lobSource);

									break;
								}
							}
							continue;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

					// else{
					// return context.serialize(src);
					// }

				}
			}

		}

		return new JsonPrimitive("lob/" + id + "?"
				+ Calendar.getInstance().getTimeInMillis());
	}


}

class ByteArrayJsonAdapter implements JsonSerializer<byte[]> {

	ByteArrayCache cache;

	String contextPath;

	public ByteArrayJsonAdapter(ByteArrayCache cache, String contextPath) {
		this.contextPath = contextPath;
		this.cache = cache;
	}

	public JsonElement serialize(byte[] src, Type typeOfSrc,
			JsonSerializationContext context) {

		String id = String.valueOf(UUID.randomUUID());
		cache.getTempCache().put(id, src);

		String result = contextPath + "lob/" + id + "?"
				+ Calendar.getInstance().getTimeInMillis();

		return new JsonPrimitive(result);
	}

	
	
}
