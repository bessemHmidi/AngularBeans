/* AngularBeans, CDI-AngularJS bridge Copyright (c) 2014, Bessem Hmidi. or third-party contributors as indicated by
 * the @author tags or express copyright attribution statements applied by the authors. This copyrighted material is
 * made available to anyone wishing to use, modify, copy, or redistribute it subject to the terms and conditions of the
 * GNU Lesser General Public License, as published by the Free Software Foundation. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. */
package angularBeans.util;

import static angularBeans.util.Accessor.GET;
import static angularBeans.util.Accessor.IS;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import angularBeans.events.NGEvent;
import angularBeans.io.ByteArrayCache;
import angularBeans.io.Call;
import angularBeans.io.LobWrapper;

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

	private transient Gson mainSerializer;
	private String contextPath;

	public void initJsonSerialiser() {

		GsonBuilder builder = new GsonBuilder().serializeNulls();

		builder.registerTypeAdapter(LobWrapper.class, new LobWrapperJsonAdapter(cache));

		builder.registerTypeAdapter(byte[].class, new ByteArrayJsonAdapter(cache, contextPath));

		builder.registerTypeAdapter(LobWrapper.class, new JsonDeserializer<LobWrapper>() {

			@Override
			public LobWrapper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

				return null;
			}

		});

		mainSerializer = builder.create();

	}

	public String getJson(Object object) {

		if (mainSerializer == null || object == null) {
			return null;
		}
		return mainSerializer.toJson(object);
	}

	public void setContextPath(String contextPath) {

		this.contextPath = contextPath;
		initJsonSerialiser();

	}

	public Object deserialise(Class clazz, JsonElement element) {

		return mainSerializer.fromJson(element, clazz);
	}

	public Object convertEvent(NGEvent event) throws ClassNotFoundException {

		JsonElement element = CommonUtils.parse(event.getData());

		JsonElement data;
		Class javaClass;

		try {
			data = element.getAsJsonObject();

			javaClass = Class.forName(event.getDataClass());
		} catch (Exception e) {
			data = element.getAsJsonPrimitive();
			if (event.getDataClass() == null) {
				event.setDataClass("String");
			}
			javaClass = Class.forName("java.lang." + event.getDataClass());

		}

		Object o;
		if (javaClass.equals(String.class)) {
			o = data.toString().substring(1, data.toString().length() - 1);
		} else {
			o = deserialise(javaClass, data);
		}
		return o;
	}

}

class LobWrapperJsonAdapter implements JsonSerializer<LobWrapper> {

	Object container;
	ByteArrayCache cache;

	public LobWrapperJsonAdapter(ByteArrayCache cache) {

		this.cache = cache;
	}

	@Override
	public JsonElement serialize(LobWrapper src, Type typeOfSrc, JsonSerializationContext context) {

		LobWrapper lobWrapper = src;

		container = lobWrapper.getOwner();
		String id = "";
		Class clazz = container.getClass();

		for (Method m : clazz.getMethods()) {
			// TODO to many nested statement
			if ((m.getName().startsWith(GET.prefix()) || m.getName().startsWith(IS.prefix()))
					&& m.getReturnType().equals(LobWrapper.class)) {
				try {

					Call lobSource = new Call(container, m);

					if (!cache.getCache().containsValue(lobSource)) {
						id = String.valueOf(UUID.randomUUID());
						cache.getCache().put(id, lobSource);
					} else {
						for (String idf : cache.getCache().keySet()) {
							Call ls = cache.getCache().get(idf);
							if (ls.equals(lobSource)) {
								id = idf;
								break;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return new JsonPrimitive("lob/" + id + "?" + Calendar.getInstance().getTimeInMillis());
	}
}

class ByteArrayJsonAdapter implements JsonSerializer<byte[]> {

	ByteArrayCache cache;
	String contextPath;

	public ByteArrayJsonAdapter(ByteArrayCache cache, String contextPath) {
		this.contextPath = contextPath;
		this.cache = cache;
	}

	@Override
	public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {

		String id = String.valueOf(UUID.randomUUID());
		cache.getTempCache().put(id, src);

		String result = contextPath + "lob/" + id + "?" + Calendar.getInstance().getTimeInMillis();

		return new JsonPrimitive(result);
	}

}
