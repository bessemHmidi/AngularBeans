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

import angularBeans.api.NGReturn;
import angularBeans.context.NGSessionScopeContext;
import angularBeans.io.LobWrapper;
import angularBeans.log.LogMessage;
import angularBeans.log.NGLogger;
import angularBeans.util.AngularBeansUtil;
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

	public synchronized void wsInvoke(Object controller, String methodName,
			JsonObject params, WSocketEvent event, long reqID, String UID) {

		NGSessionScopeContext.setCurrentContext(UID);

		Map<String, Object> returns = new HashMap<String, Object>();
		Object mainReturn = null;

		returns.put("isRPC", true);
		//returns.put("reqId", reqID);

		returns.put("reqId", controller.getClass().getSimpleName());

		Method methodToInvoke = null;
		
		try {
			try {
				methodToInvoke = controller.getClass().getMethod(methodName,
						WSocketEvent.class);
				
				if (!util.isGetter(methodToInvoke)) {
					// if(util.isSetter(m)){
					update(controller, params);
					// }

				}
					mainReturn = methodToInvoke.invoke(controller, event);
				
			} catch (NoSuchMethodException e) {
				try {

					
					
					JsonElement argsElem = params.get("args");

					
					
					if (argsElem != null) {

						JsonArray args = params.get("args").getAsJsonArray();

						Map<String, Class> builtInMap = new HashMap<String, Class>();
						builtInMap.put("int", Integer.TYPE);
						builtInMap.put("long", Long.TYPE);
						builtInMap.put("double", Double.TYPE);
						builtInMap.put("float", Float.TYPE);
						builtInMap.put("bool", Boolean.TYPE);
						builtInMap.put("char", Character.TYPE);
						builtInMap.put("byte", Byte.TYPE);
						// builtInMap("void", Void.TYPE );
						builtInMap.put("short", Short.TYPE);

						for (Method mt : controller.getClass().getMethods()) {

							if (mt.getName().equals(methodName)) {

								Type[] parameters = mt.getParameterTypes();

							if (parameters.length == args.size()) {

									List<Object> argsValues = new ArrayList<Object>();

									for (int i = 0; i < parameters.length; i++) {

										// System.out.println(parameters[i].toString());
										//

										Class typeClass = null;
										String typeString = ((parameters[i]).toString());

										if (typeString.startsWith("class")) {
											typeString = typeString.substring(6);
											try {
												
												typeClass = Class.forName(typeString);
											} catch (Exception e2) {
												e2.printStackTrace();
											}
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

									methodToInvoke = mt;
									
									if (!util.isGetter(methodToInvoke)) {
										// if(util.isSetter(m)){
										update(controller, params);
										// }

									}
									mainReturn = methodToInvoke.invoke(controller, argsValues.toArray());
									
									
                                   
								}

							}

						}

					}else{
						
					
						methodToInvoke = controller.getClass().getMethod(methodName);
						
						if (!util.isGetter(methodToInvoke)) {
							// if(util.isSetter(m)){
							update(controller, params);
							// }

						}
						
						mainReturn = methodToInvoke.invoke(controller);
					}
					

					
					
				} catch (NoSuchMethodException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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

			//	System.out.println("* "+ngReturn.model());
				returns.put(ngReturn.model(), mainReturn);
			//	System.out.println("--> "+mainReturn);
				
				for (String up : ngReturn.updates()) {

					String getterName = "get"
							+ up.substring(0, 1).toUpperCase()
							+ up.substring(1);
					Method getter = null;

					getter = controller.getClass().getMethod(getterName);

					
					
					result = getter.invoke(controller);
				
					
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
			
			
			
//			System.out.println(returns);
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

		try {

			genericInvoke(o, method, params, returns);

			
		
			
		} catch (Exception e) {
			// fire(e);
			e.printStackTrace();
		}

		return returns;
	}

	private void genericInvoke(Object controller, String methodName,
			JsonObject params, List<Object> returns) throws SecurityException,
			ClassNotFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException {

		Object mainReturn = null;

		Method m = null;

		JsonElement argsElem = params.get("args");

		if (argsElem != null) {

			JsonArray args = params.get("args").getAsJsonArray();

			Map<String, Class> builtInMap = new HashMap<String, Class>();
			builtInMap.put("int", Integer.TYPE);
			builtInMap.put("long", Long.TYPE);
			builtInMap.put("double", Double.TYPE);
			builtInMap.put("float", Float.TYPE);
			builtInMap.put("bool", Boolean.TYPE);
			builtInMap.put("char", Character.TYPE);
			builtInMap.put("byte", Byte.TYPE);
			// builtInMap("void", Void.TYPE );
			builtInMap.put("short", Short.TYPE);

			for (Method mt : controller.getClass().getMethods()) {

				if (mt.getName().equals(methodName)) {

					Type[] parameters = mt.getParameterTypes();

					if (parameters.length == args.size()) {

						List<Object> argsValues = new ArrayList<Object>();

						for (int i = 0; i < parameters.length; i++) {

							// System.out.println(parameters[i].toString());
							//

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
							update(controller, params);
							// }

						}
						mainReturn = m.invoke(controller, argsValues.toArray());

					}

				}

			}

		}
		// --------------------------------------------------

		else {

			m = controller.getClass().getMethod(methodName);

			if (!util.isGetter(m)) {
				// if(util.isSetter(m)){
				update(controller, params);
				// }

			}

			mainReturn = m.invoke(controller);

		}

		LinkedList<LogMessage> logs = new LinkedList<LogMessage>();
		returns.add(logs);

		logs.addAll(logger.getLogPool());
		logger.getLogPool().clear();

		returns.add(mainReturn);

		if (m.isAnnotationPresent(NGReturn.class)) {

			NGReturn ngReturn = m.getAnnotation(NGReturn.class);

			for (String up : ngReturn.updates()) {

				String getterName = "get" + up.substring(0, 1).toUpperCase()
						+ up.substring(1);
				Method getter = null;
				try {
					getter = controller.getClass().getMethod(getterName);
				} catch (NoSuchMethodException e) {
					getter = controller.getClass().getMethod(
							(getterName.replace("get", "is")));
				}

				Object result = getter.invoke(controller);
				returns.add(result);

			}

		}
	
	
	}

	private void update(Object o, JsonObject params) {

		if (params != null) {

			// boolean firstIn = false;

			for (Map.Entry<String, JsonElement> entry : params.entrySet()) {

				JsonElement value = entry.getValue();
				String name = entry.getKey();

				if ((name.equals("sessionUID"))||(name.equals("args"))) {
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

	private Object deserialise(Class clazz, JsonElement element) {
		Object elem;
		GsonBuilder builder = new GsonBuilder();

		builder.registerTypeAdapter(LobWrapper.class,
				new JsonDeserializer<LobWrapper>() {

					@Override
					public LobWrapper deserialize(JsonElement json,
							Type typeOfT, JsonDeserializationContext context)
							throws JsonParseException {
						// System.out.println("TADA.......");
						return null;
					}
				});

		Gson gson = builder.create();

		elem = gson.fromJson(element, clazz);
		return elem;
	}

}
