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

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.websocket.EncodeException;

import angularBeans.AngularBeansUtil;
import angularBeans.LobWrapper;
import angularBeans.api.NGReturn;
import angularBeans.context.NGSessionScopeContext;
import angularBeans.log.LogMessage;
import angularBeans.log.NGLogger;
import angularBeans.log.NGLogger.Level;
import angularBeans.wsocket.WSocketClient;
import angularBeans.wsocket.WSocketEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

@Dependent
public class RemoteInvoker implements Serializable {

	@Inject
	WSocketClient holder;

	@Inject
	NGLogger logger;

	@Inject
	AngularBeansUtil util;

	public synchronized void wsInvoke(Object o, String method,
			JsonObject params, WSocketEvent event, long reqID, String UID) {

		NGSessionScopeContext.setCurrentContext(UID);

		Map<String, Object> returns = new HashMap<String, Object>();
		Object mainReturn = null;

		returns.put("isRPC", true);
		returns.put("reqId", reqID);

		Method methodToInvoke = null;
		boolean injectEvent = false;
		try {
			try {
				methodToInvoke = o.getClass().getMethod(method,
						WSocketEvent.class);
				injectEvent = true;
			} catch (NoSuchMethodException e) {
				try {
					methodToInvoke = o.getClass().getMethod(method);
				} catch (NoSuchMethodException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			if ((!methodToInvoke.getName().startsWith("get"))
					&& (!(methodToInvoke.getName().startsWith("is")))) {
				update(o, params);
			}

			if (injectEvent) {
				mainReturn = methodToInvoke.invoke(o, event);
			} else {
				mainReturn = methodToInvoke.invoke(o);
			}
		} catch (SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// event.setSession(holder.getSession());
		returns.put("log", logger.getLogPool().toArray());
		logger.getLogPool().clear();

		if (methodToInvoke.isAnnotationPresent(NGReturn.class)) {
			Object result = null;
			try {
				NGReturn ngReturn = methodToInvoke
						.getAnnotation(NGReturn.class);

				returns.put(ngReturn.model(), mainReturn);
				for (String up : ngReturn.updates()) {

					String getterName = "get"
							+ up.substring(0, 1).toUpperCase()
							+ up.substring(1);
					Method getter = null;

					getter = o.getClass().getMethod(getterName);

					result = getter.invoke(o);
					returns.put(up, result);
				}
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		try {
			event.getSession().getBasicRemote()
					.sendObject(util.getJson(returns));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized Object invoke(Object o, String method,
			JsonObject params, String UID) {

		NGSessionScopeContext.setCurrentContext(UID);

		List<Object> returns = new ArrayList<Object>();
		Object mainReturn = null;
		try {

			Method m = o.getClass().getMethod(method);

			if (!util.isGetter(m)) {
				// if(util.isSetter(m)){
				update(o, params);
				// }

			}

			mainReturn = m.invoke(o);
			LinkedList<LogMessage> logs = new LinkedList<LogMessage>();
			returns.add(logs);

			logs.addAll(logger.getLogPool());
			logger.getLogPool().clear();

			returns.add(mainReturn);

			if (m.isAnnotationPresent(NGReturn.class)) {

				NGReturn ngReturn = m.getAnnotation(NGReturn.class);

				for (String up : ngReturn.updates()) {

					String getterName = "get"
							+ up.substring(0, 1).toUpperCase()
							+ up.substring(1);
					Method getter = null;
					try {
						getter = o.getClass().getMethod(getterName);
					} catch (NoSuchMethodException e) {
						getter = o.getClass().getMethod(
								(getterName.replace("get", "is")));
					}

					Object result = getter.invoke(o);
					returns.add(result);

				}

			}

		} catch (Exception e) {
			// fire(e);
			e.printStackTrace();
		}

		return returns;
	}

	private void update(Object o, JsonObject params) {

		if (params != null) {

			boolean firstIn = false;

			for (Map.Entry<String, JsonElement> entry : params.entrySet()) {

				JsonElement value = entry.getValue();
				String name = entry.getKey();

				if (name.equals("sessionUID")) {
					continue;
				}

				if ((value.isJsonObject()) && (!value.isJsonNull())) {

					String getName;
					try {
						getName = util.obtainGetter(o.getClass()
								.getDeclaredField(name));

						Object subObj = o.getClass().getMethod(getName)
								.invoke(o);

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
					// System.out.println("RAW "+value);
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
								// System.out.println("primitive"+primitive);
								elem = element;
								if (primitive.isBoolean())
									elem = primitive.getAsBoolean();
								if (primitive.isString()) {
									elem = primitive.getAsString();
								}
								if (primitive.isNumber())
									elem = primitive.getAsNumber();

							} else {
								// System.out.println(clazz);
								GsonBuilder builder = new GsonBuilder();

								builder.registerTypeAdapter(LobWrapper.class,
										new JsonDeserializer<LobWrapper>() {

											@Override
											public LobWrapper deserialize(
													JsonElement json,
													Type typeOfT,
													JsonDeserializationContext context)
													throws JsonParseException {
												// System.out.println("TADA.......");
												return null;
											}
										});

								Gson gson = builder.create();

								elem = gson.fromJson(element, clazz);

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
								Logger.getLogger("AngularBeans")
								.log(java.util.logging.Level.WARNING,"trying to modify an immutable collection : "+name);
							}

						}

					} catch (Exception e) {
						e.printStackTrace();
						// System.out.println(value);
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

}
