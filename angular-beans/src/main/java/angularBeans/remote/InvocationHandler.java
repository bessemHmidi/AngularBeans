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
package angularBeans.remote;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.swing.text.rtf.RTFEditorKit;

import angularBeans.api.AngularBean;
import angularBeans.api.NGPostConstruct;
import angularBeans.api.NGReturn;
import angularBeans.context.NGSessionScopeContext;
import angularBeans.context.NGSessionScoped;
import angularBeans.io.LobWrapper;
import angularBeans.log.NGLogger;
import angularBeans.util.AngularBeansUtil;
import angularBeans.util.ModelQueryFactory;
import angularBeans.util.ModelQueryImpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

@ApplicationScoped
public class InvocationHandler implements Serializable {

//	@Inject
//	RealTimeClient holder;

	@Inject
	NGLogger logger;

	@Inject
	AngularBeansUtil util;

	@Inject
	ModelQueryFactory modelQueryFactory;

	Map<String, Class> builtInMap = new HashMap<String, Class>();

	@PostConstruct
	public void init() {
		builtInMap.put("int", Integer.TYPE);
		builtInMap.put("long", Long.TYPE);
		builtInMap.put("double", Double.TYPE);
		builtInMap.put("float", Float.TYPE);
		builtInMap.put("bool", Boolean.TYPE);
		builtInMap.put("char", Character.TYPE);
		builtInMap.put("byte", Byte.TYPE);
		// builtInMap("void", Void.TYPE );
		builtInMap.put("short", Short.TYPE);
	}

	public synchronized void realTimeInvoke(Object ServiceToInvoque,
			String methodName, JsonObject params, RealTimeDataReceiveEvent event,
			long reqID, String UID) {

		NGSessionScopeContext.setCurrentContext(UID);

		Map<String, Object> returns = new HashMap<String, Object>();
		// Object mainReturn = null;

		returns.put("isRT", true);

		try {
			genericInvoke(ServiceToInvoque, methodName, params, returns,reqID);
			
			if(returns.get("mainReturn")!=null)
			{
			event.getConnection().write(util.getJson(returns));
		    }
		} catch (SecurityException | ClassNotFoundException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//if(returns.get("mainReturn")==null){returns =null;}

	}

	public synchronized Object invoke(Object o, String method,
			JsonObject params, String UID) {

		NGSessionScopeContext.setCurrentContext(UID);

		Map<String, Object> returns = new HashMap<String, Object>();

		try {

			returns.put("isRT", false);
			genericInvoke(o, method, params, returns,0);

		} catch (Exception e) {
			// fire(e);
			e.printStackTrace();
		}

		
		
		return returns;
	}

	private void genericInvoke(Object service, String methodName,
			JsonObject params, Map<String, Object> returns,long reqID)
			
			throws SecurityException, ClassNotFoundException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException {

		Object mainReturn = null;

		Method m = null;

		JsonElement argsElem = params.get("args");

		// returns.put("isRT", false);
		
		if(reqID>0){
		returns.put("reqId", reqID);
		}
		if (argsElem != null) {

			JsonArray args = params.get("args").getAsJsonArray();

			for (Method mt : service.getClass().getMethods()) {

				if (mt.getName().equals(methodName)) {

					Type[] parameters = mt.getParameterTypes();

					if (parameters.length == args.size()) {

						List<Object> argsValues = new ArrayList<Object>();

						for (int i = 0; i < parameters.length; i++) {

							Class typeClass = null;
							String typeString = ((parameters[i]).toString());

							if (typeString.startsWith("class")) {
								typeString = typeString.substring(6);
								typeClass = Class.forName(typeString);
							}

							else {

								typeClass = builtInMap.get(typeString);
							}

							JsonElement element = args.get(i);

							if (element.isJsonPrimitive()) {
								String val = element.getAsString();
								argsValues.add(util.convertFromString(val,
										typeClass));

							} else {
								argsValues.add(deserialise(typeClass, element));
							}

						}

						m = mt;

						if (!util.isGetter(m)) {
							// if(util.isSetter(m)){
							update(service, params);
							// }

						}
						mainReturn = m.invoke(service, argsValues.toArray());

					}

				}

			}

		}
		// --------------------------------------------------

		else {

			m = service.getClass().getMethod(methodName);

			if (!util.isGetter(m)) {
				// if(util.isSetter(m)){
				update(service, params);
				// }

			}

			mainReturn = m.invoke(service);

		}

		if (!logger.getLogPool().isEmpty()) {
			returns.put("log", logger.getLogPool().toArray());
			logger.getLogPool().clear();
		}

		String[] updates = null;

//		if ((m.isAnnotationPresent(NGReturn.class))
//				|| (m.isAnnotationPresent(NGPostConstruct.class))
//		) {

			if (m.isAnnotationPresent(NGReturn.class)) {
				NGReturn ngReturn = m.getAnnotation(NGReturn.class);
				updates = ngReturn.updates();

				if(ngReturn.model().length()>0){
				 returns.put(ngReturn.model(), mainReturn);
				 Map<String, String> binding=new HashMap<String, String>();
				 
				 binding.put("boundTo", ngReturn.model());
				 
				 mainReturn=binding;
				}
			}

		

			if (m.isAnnotationPresent(NGPostConstruct.class)) {
				NGPostConstruct ngPostConstruct = m
						.getAnnotation(NGPostConstruct.class);
				updates = ngPostConstruct.updates();

			}

			if (updates != null) {
				if ((updates.length == 1) && (updates[0].equals("*"))) {

					List<String> upd = new ArrayList<String>();
					for (Method met : service.getClass()
							.getDeclaredMethods()) {

						if (util.isGetter(met)) {

							String fieldName = (met.getName()).substring(3);
							String firstCar = fieldName.substring(0, 1);
							upd.add((firstCar.toLowerCase() + fieldName
									.substring(1)));

						}
					}

					updates = new String[upd.size()];

					for (int i = 0; i < upd.size(); i++) {
						updates[i] = upd.get(i);
					}

				}
			}
			
			if(updates!=null){
			for (String up : updates) {

				String getterName = "get" + up.substring(0, 1).toUpperCase()
						+ up.substring(1);
				Method getter = null;
				try {
					getter = service.getClass().getMethod(getterName);
				} catch (NoSuchMethodException e) {
					getter = service.getClass().getMethod(
							(getterName.replace("get", "is")));
				}

				Object result = getter.invoke(service);
				returns.put(up, result);

			}}

			// 1

			ModelQueryImpl qImpl= (ModelQueryImpl)modelQueryFactory.get(service.getClass());
			
			Map<String, Object> scMap = new HashMap<String, Object>(
					(qImpl).getData());

			returns.putAll(scMap);

			(qImpl).getData().clear();

			if (!modelQueryFactory.getRootScope().getRootScopeMap().isEmpty()) {
				returns.put("rootScope", new HashMap<String, Object>(modelQueryFactory
						.getRootScope().getRootScopeMap()));
				modelQueryFactory.getRootScope().getRootScopeMap().clear();
			}


	//	}

		
		//if (m.isAnnotationPresent(NGReturn.class)) {

			
			
			
			returns.put("mainReturn", mainReturn);
		//}
	
		

			
	}

	private void update(Object o, JsonObject params) {

		if (params != null) {

			// boolean firstIn = false;

			for (Map.Entry<String, JsonElement> entry : params.entrySet()) {

				JsonElement value = entry.getValue();
				String name = entry.getKey();

				if ((name.equals("sessionUID")) || (name.equals("args"))) {
					continue;
				}

				if ((value.isJsonObject()) && (!value.isJsonNull())) {

					String getName;
					try {
						getName = util.obtainGetter(o.getClass()
								.getDeclaredField(name));

						Method getter = o.getClass().getMethod(getName);

						Object subObj = getter.invoke(o);

						// logger.log(Level.INFO, "#entring sub object "+name);
						update(subObj, value.getAsJsonObject());

					} catch (NoSuchFieldException | SecurityException
							| IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException e) {

						e.printStackTrace();
					}

				}
				// ------------------------------------
				if (value.isJsonArray()) {

					try {
						String getter = util.obtainGetter(o.getClass()
								.getDeclaredField(name));

						Method get = o.getClass().getDeclaredMethod(getter);

						Type type = get.getGenericReturnType();
						ParameterizedType pt = (ParameterizedType) type;
						Type actType = pt.getActualTypeArguments()[0];

						Class collectionClazz = get.getReturnType();

						String className = actType.toString();

						className = className.substring(className
								.indexOf("class") + 6);
						Class clazz = Class.forName(className);

						JsonArray array = value.getAsJsonArray();

						Collection collection = (Collection) get.invoke(o);
						Object elem = null;
						for (JsonElement element : array) {
							if (element.isJsonPrimitive()) {
								JsonPrimitive primitive = element
										.getAsJsonPrimitive();

								elem = element;
								if (primitive.isBoolean())
									elem = primitive.getAsBoolean();
								if (primitive.isString()) {
									elem = primitive.getAsString();
								}
								if (primitive.isNumber())
									elem = primitive.getAsNumber();

							} else {

								elem = deserialise(clazz, element);

							}

							// if (collection instanceof AbstractCollection) {
							//
							// ArrayList list=new ArrayList();
							// list.addAll(collection);
							// collection=list;
							// }

							try {

								if (collection instanceof List) {

									if (collection.contains(elem))
										collection.remove(elem);
								}

								collection.add(elem);
							} catch (UnsupportedOperationException e) {
								Logger.getLogger("AngularBeans").log(
										java.util.logging.Level.WARNING,
										"trying to modify an immutable collection : "
												+ name);
							}

						}

					} catch (Exception e) {
						e.printStackTrace();

					}

				}

				// ------------------------------------------
				if (value.isJsonPrimitive() && (!name.equals("setSessionUID"))) {
					try {

						if (!util.hasSetter(o.getClass(), name)) {
							continue;
						}
						name = "set" + name.substring(0, 1).toUpperCase()
								+ name.substring(1);

						Class type = null;
						for (Method set : o.getClass().getDeclaredMethods()) {
							if (util.isSetter(set)) {
								if (set.getName().equals(name)) {
									Class<?>[] pType = set.getParameterTypes();

									type = pType[0];
									break;

								}
							}

						}

						if (type.equals(LobWrapper.class))
							continue;

						Object param = null;
						if ((params.entrySet().size() >= 1) && (type != null)) {

							param = util.convertFromString(value.getAsString(),
									type);

						}

						o.getClass().getMethod(name, type).invoke(o, param);

					} catch (Exception e) {
						// fire(e);
						e.printStackTrace();

					}
				}

			}
		}

	}

	private Object deserialise(Class clazz, JsonElement element) {
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
