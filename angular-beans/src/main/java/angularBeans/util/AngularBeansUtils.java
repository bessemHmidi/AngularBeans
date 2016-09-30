/* AngularBeans, CDI-AngularJS bridge Copyright (c) 2014, Bessem Hmidi. or third-party contributors as indicated by
 * the @author tags or express copyright attribution statements applied by the authors. This copyrighted material is
 * made available to anyone wishing to use, modify, copy, or redistribute it subject to the terms and conditions of the
 * GNU Lesser General Public License, as published by the Free Software Foundation. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. */
package angularBeans.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
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
		
		builder.setExclusionStrategies(NGConfiguration.getGsonExclusionStrategy());

		builder.registerTypeAdapter(LobWrapper.class, new LobWrapperJsonAdapter(cache));

		builder.registerTypeAdapter(byte[].class, new ByteArrayJsonAdapter(cache, contextPath));

		builder.registerTypeAdapter(LobWrapper.class, new JsonDeserializer<LobWrapper>() {

			@Override
			public LobWrapper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

				return null;
			}

		});
		
		if (NGConfiguration.getProperty("DATE_PATTERN") != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(NGConfiguration.getProperty("DATE_PATTERN"));

			if (dateFormat != null && NGConfiguration.getProperty("TIME_ZONE") != null) {
				dateFormat.setTimeZone(TimeZone.getTimeZone(NGConfiguration.getProperty("DATE_PATTERN")));
			}

			builder.registerTypeAdapter(java.sql.Date.class, new JsonSerializer<java.sql.Date>(){

				@Override
				public JsonElement serialize(java.sql.Date src, Type typeOfSrc, JsonSerializationContext context) {

					if (src != null) {
					   Calendar cal = Calendar.getInstance();
					   cal.setTime(src);
					   cal.set(Calendar.HOUR_OF_DAY, 0);
					   cal.set(Calendar.MINUTE, 0);
					   cal.set(Calendar.SECOND, 0);
					   cal.set(Calendar.MILLISECOND, 0);

					   return new JsonPrimitive(dateFormat.format(cal.getTime()));
				   }
				   return null;
				}
			});

			builder.registerTypeAdapter(java.sql.Date.class, new JsonDeserializer<java.sql.Date>(){

				@Override
				public java.sql.Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

				    try {
					   Calendar cal = Calendar.getInstance();
					   cal.setTime(dateFormat.parse(json.getAsString()));
					   cal.set(Calendar.HOUR_OF_DAY, 0);
					   cal.set(Calendar.MINUTE, 0);
					   cal.set(Calendar.SECOND, 0);
					   cal.set(Calendar.MILLISECOND, 0);

					   return new java.sql.Date(cal.getTime().getTime());
				    } catch (Exception e) {}

				    return null;
				}

			});

			builder.registerTypeAdapter(java.sql.Time.class, new JsonSerializer<java.sql.Time>(){

				@Override
				public JsonElement serialize(java.sql.Time src, Type typeOfSrc, JsonSerializationContext context) {
				    if (src != null) {
					   Calendar cal = Calendar.getInstance();
					   cal.setTime(src);

					   return new JsonPrimitive(dateFormat.format(cal.getTime()));
				    }
				    return null;
				}

			});

			builder.registerTypeAdapter(java.sql.Time.class, new JsonDeserializer<java.sql.Time>(){

				@Override
				public java.sql.Time deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

				    try {
					   Calendar cal = Calendar.getInstance();
					   cal.setTime(dateFormat.parse(json.getAsString()));

					   return new java.sql.Time(cal.getTime().getTime());
				    } catch (Exception e) {}

				    return null;
				}

			});

			builder.registerTypeAdapter(java.sql.Timestamp.class, new JsonSerializer<java.sql.Timestamp>(){

				@Override
				public JsonElement serialize(java.sql.Timestamp src, Type typeOfSrc, JsonSerializationContext context) {
				    if (src != null) {
					   Calendar cal = Calendar.getInstance();
					   cal.setTime(src);

					   return new JsonPrimitive(dateFormat.format(cal.getTime()));
				    }
				    return null;
				}

			});

			builder.registerTypeAdapter(java.sql.Timestamp.class, new JsonDeserializer<java.sql.Timestamp>(){

				@Override
				public java.sql.Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

				    try {
					   Calendar cal = Calendar.getInstance();
					   cal.setTime(dateFormat.parse(json.getAsString()));

					   return new java.sql.Timestamp(cal.getTime().getTime());
				    } catch (Exception e) {}

				    return null;
				}

			});

			builder.registerTypeAdapter(java.util.Date.class, new JsonSerializer<java.util.Date>(){

				@Override
				public JsonElement serialize(java.util.Date src, Type typeOfSrc, JsonSerializationContext context) {
				   return src == null ? null : new JsonPrimitive(dateFormat.format(src));
				}

			 });

			builder.registerTypeAdapter(java.util.Date.class, new JsonDeserializer<java.util.Date>(){

				@Override
				public java.util.Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

				    try {
					   return dateFormat.parse(json.getAsString());
				    } catch (Exception e) {}

				    return null;
				}

			});
		}

		mainSerializer = builder.create();

	}

	public String getJson(Object object) {		
		if (object == null) {
			return null;
		}
		
		if(mainSerializer == null){
			initJsonSerialiser();
		}

		return mainSerializer.toJson(object);
	}

	public void setContextPath(String contextPath) {

		this.contextPath = contextPath;
		initJsonSerialiser();

	}

	public Object deserialise(Type type, JsonElement element) {
		if(mainSerializer == null){
			initJsonSerialiser();
		}
		
		return mainSerializer.fromJson(element, type);
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
			if (CommonUtils.isGetter(m) && m.getReturnType().equals(LobWrapper.class) && !Modifier.isVolatile(m.getModifiers())) {
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
