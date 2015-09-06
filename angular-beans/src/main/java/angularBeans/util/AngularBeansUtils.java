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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import angularBeans.io.ByteArrayCache;
import angularBeans.io.Call;
import angularBeans.io.LobWrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * utility class for AngularBeans
 * 
 * @author Bassem Hmidi
 *
 */

@SuppressWarnings("serial")
@ApplicationScoped
public class AngularBeansUtils implements Serializable {

	@Inject
	ByteArrayCache cache;

	private Gson mainSerializer;

	public void initJsonSerialiser() {

		GsonBuilder builder = new GsonBuilder().serializeNulls();

		builder.registerTypeAdapter(LobWrapper.class,
				new LobWrapperJsonAdapter(cache));

		builder.registerTypeAdapter(byte[].class, new ByteArrayJsonAdapter(
				cache, contextPath));

		builder.registerTypeAdapter(LobWrapper.class,
				new JsonDeserializer<LobWrapper>() {

					@Override
					public LobWrapper deserialize(JsonElement json,
							Type typeOfT, JsonDeserializationContext context)
							throws JsonParseException {

						return null;
					}

				});

		mainSerializer = builder.create();

	}

	public String getJson(Object object) {

		if (mainSerializer == null) {
			// if (object instanceof Properties) {
			// return new Gson().toJson(object);
			// }

			if (object == null) {
				mainSerializer.toJson(null);

			}

		}

		return mainSerializer.toJson(object);

	}

	private String contextPath;

	public void setContextPath(String contextPath) {

		this.contextPath = contextPath;
		initJsonSerialiser();

	}

	public Object deserialise(Class clazz, JsonElement element) {

		Object o = mainSerializer.fromJson(element, clazz);
		Field[] fields = clazz.getFields();

		for (Field f : fields) {

			if (f.getType() == LobWrapper.class) {

				String setterName = CommonUtils.obtainSetter(f);

				Method setterMethod;
				try {
					setterMethod = clazz
							.getMethod(setterName, LobWrapper.class);
					setterMethod.invoke(o, null);

				} catch (NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		return mainSerializer.fromJson(element, clazz);
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
					String field = CommonUtils.obtainFieldNameFromAccessor(m
							.getName());

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
