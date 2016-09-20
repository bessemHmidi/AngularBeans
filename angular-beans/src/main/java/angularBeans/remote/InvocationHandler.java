/* AngularBeans, CDI-AngularJS bridge Copyright (c) 2014, Bessem Hmidi. or third-party contributors as indicated by
 * the @author tags or express copyright attribution statements applied by the authors. This copyrighted material is
 * made available to anyone wishing to use, modify, copy, or redistribute it subject to the terms and conditions of the
 * GNU Lesser General Public License, as published by the Free Software Foundation. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. */

/**
 * @author Bessem Hmidi
 */
package angularBeans.remote;

import static angularBeans.util.Accessors.*;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import angularBeans.api.NGPostConstruct;
import angularBeans.api.NGReturn;
import angularBeans.context.BeanLocator;
import angularBeans.context.NGSessionScopeContext;
import angularBeans.io.ByteArrayCache;
import angularBeans.io.LobWrapper;
import angularBeans.log.NGLogger;
import angularBeans.log.NGLogger.Level;
import angularBeans.util.AngularBeansUtils;
import angularBeans.util.CommonUtils;
import angularBeans.util.ModelQueryFactory;
import angularBeans.util.ModelQueryImpl;

/**
 * AngularBeans RPC main handler.
 * 
 * @author Bassem Hmidi
 */

@SuppressWarnings("serial")
@ApplicationScoped
public class InvocationHandler implements Serializable {

	@Inject
	ByteArrayCache cache;

	@Inject
	NGLogger logger;

	@Inject
	AngularBeansUtils util;

	@Inject
	BeanLocator locator;

	@Inject
	ModelQueryFactory modelQueryFactory;

	static final Map<String, Class> builtInMap = new HashMap<>();

	static {

		builtInMap.put("int", Integer.TYPE);
		builtInMap.put("long", Long.TYPE);
		builtInMap.put("double", Double.TYPE);
		builtInMap.put("float", Float.TYPE);
		builtInMap.put("boolean", Boolean.TYPE);
		builtInMap.put("char", Character.TYPE);
		builtInMap.put("byte", Byte.TYPE);
		builtInMap.put("short", Short.TYPE);

	}

	public void realTimeInvoke(Object ServiceToInvoque, String methodName, JsonObject params,
			RealTimeDataReceivedEvent event, long reqID, String UID) {

		NGSessionScopeContext.setCurrentContext(UID);
		Map<String, Object> returns = new HashMap<>();
		returns.put("isRT", true);

		try {
			genericInvoke(ServiceToInvoque, methodName, params, returns, reqID, UID,null);

			if (returns.get("mainReturn") != null) {

				event.getConnection().write(util.getJson(returns), false);
			}
		} catch (SecurityException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException e) {

			e.printStackTrace();
		}

	}

	public Object invoke(Object o, String method, JsonObject params, String UID,HttpServletRequest request) {

		NGSessionScopeContext.setCurrentContext(UID);

		Map<String, Object> returns = new HashMap<>();

		try {

			returns.put("isRT", false);
			genericInvoke(o, method, params, returns, 0, UID,request);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return returns;
	}

	private void genericInvoke(Object service, String methodName, JsonObject params, Map<String, Object> returns,
			long reqID, String UID,HttpServletRequest request)

					throws SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException,
					InvocationTargetException, NoSuchMethodException {

		
		
		
		Object mainReturn = null;
		Method m = null;
		JsonElement argsElem = params.get("args");

		if (reqID > 0) {
			returns.put("reqId", reqID);
		}
		if (argsElem != null) {

			JsonArray args = params.get("args").getAsJsonArray();

			
			for (Method mt : service.getClass().getMethods()) {

				if (mt.getName().equals(methodName) && !Modifier.isVolatile(mt.getModifiers())) {
					m=mt;
					Type[] parameters = m.getGenericParameterTypes();

					if (parameters.length == args.size()) {

						List<Object> argsValues = new ArrayList<>();

						for (int i = 0; i < parameters.length; i++) {

							JsonElement element = args.get(i);

							if (element.isJsonPrimitive()) {

								Class<?> clazz = null;

								String typeString = ((parameters[i]).toString());
								if (typeString.startsWith("interface")) {
									clazz =  Class.forName(typeString.substring(10));
								} else if (typeString.startsWith("class")) {
									clazz =  Class.forName(typeString.substring(6));
								} else {
									clazz = builtInMap.get(typeString);
								}

								String val = element.getAsString();

								argsValues.add(CommonUtils.convertFromString(val, clazz));

							} else if (element.isJsonArray()) {

								JsonArray arr = element.getAsJsonArray();

								argsValues.add(util.deserialise(parameters[i], arr));

							} else {

								argsValues.add(util.deserialise(parameters[i], element));

							}
						}

						if (!CommonUtils.isGetter(mt)) {
							update(service, params);
						}


						try {
							mainReturn = mt.invoke(service, argsValues.toArray());
						} catch (Exception e) {
							handleException(mt, e);
							e.printStackTrace();
						}
					}
				}	
			}
		} else {
			
			
			for (Method mt : service.getClass().getMethods()) {

				if (mt.getName().equals(methodName) && !Modifier.isVolatile(mt.getModifiers())) {

					Type[] parameters = mt.getParameterTypes();

					// handling methods that took HttpServletRequest as parameter					
					if(parameters.length!=1){
						 if (!CommonUtils.isGetter(m)) {
								update(service, params);
							}
							mainReturn = mt.invoke(service);
					 }
			
				}}
		}

		ModelQueryImpl qImpl = (ModelQueryImpl) modelQueryFactory.get(service.getClass());

		Map<String, Object> scMap = new HashMap<>(qImpl.getData());

		returns.putAll(scMap);

		qImpl.getData().clear();

		if (!modelQueryFactory.getRootScope().getRootScopeMap().isEmpty()) {
			returns.put("rootScope", new HashMap<>(modelQueryFactory.getRootScope().getRootScopeMap()));
			modelQueryFactory.getRootScope().getRootScopeMap().clear();
		}

		String[] updates = null;

		if (m != null && m.isAnnotationPresent(NGReturn.class)) {

			if (mainReturn == null)
				mainReturn = "";

			NGReturn ngReturn = m.getAnnotation(NGReturn.class);
			updates = ngReturn.updates();

			if (ngReturn.model().length() > 0) {
				returns.put(ngReturn.model(), mainReturn);
				Map<String, String> binding = new HashMap<>();

				binding.put("boundTo", ngReturn.model());

				mainReturn = binding;
			}
		}

		if (m != null && m.isAnnotationPresent(NGPostConstruct.class)) {
			NGPostConstruct ngPostConstruct = m.getAnnotation(NGPostConstruct.class);
			updates = ngPostConstruct.updates();

		}

		if (updates != null) {
			if ((updates.length == 1) && (updates[0].equals("*"))) {

				List<String> upd = new ArrayList<>();
				for (Method met : service.getClass().getDeclaredMethods()) {

					if (CommonUtils.isGetter(met)) {

						String fieldName = (met.getName()).substring(3);
						String firstCar = fieldName.substring(0, 1);
						upd.add((firstCar.toLowerCase() + fieldName.substring(1)));

					}
				}

				updates = new String[upd.size()];

				for (int i = 0; i < upd.size(); i++) {
					updates[i] = upd.get(i);
				}
			}
		}

		if (updates != null) {
			for (String up : updates) {

				String getterName = GETTER_PREFIX + up.substring(0, 1).toUpperCase() + up.substring(1);
				Method getter;
				try {
					getter = service.getClass().getMethod(getterName);
				} catch (NoSuchMethodException e) {
					getter = service.getClass().getMethod((getterName.replace(GETTER_PREFIX, BOOLEAN_GETTER_PREFIX)));
				}

				Object result = getter.invoke(service);
				returns.put(up, result);

			}
		}

		returns.put("mainReturn", mainReturn);

		if (!logger.getLogPool().isEmpty()) {
			returns.put("log", logger.getLogPool().toArray());
			logger.getLogPool().clear();
		}
	}

	private void handleException(Method m, Exception e) {
		Throwable cause = e.getCause();

		String exceptionString = m.getName() + " -->" + cause.getClass().getName();

		if (cause.getMessage() != null) {
			exceptionString += " " + cause.getMessage();
		}

		logger.log(Level.ERROR, exceptionString);

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
						getName = CommonUtils.obtainGetter(o.getClass().getDeclaredField(name));

						Method getter = o.getClass().getMethod(getName);

						Object subObj = getter.invoke(o);

						// logger.log(Level.INFO, "#entring sub object "+name);
						update(subObj, value.getAsJsonObject());

					} catch (NoSuchFieldException | SecurityException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {

						e.printStackTrace();
					}

				}
				// ------------------------------------
				if (value.isJsonArray()) {

					try {
						String getter = CommonUtils.obtainGetter(o.getClass().getDeclaredField(name));

						Method get = o.getClass().getDeclaredMethod(getter);

						Type type = get.getGenericReturnType();
						ParameterizedType pt = (ParameterizedType) type;
						Type actType = pt.getActualTypeArguments()[0];

						String className = actType.toString();

						className = className.substring(className.indexOf("class") + 6);
						Class clazz = Class.forName(className);

						JsonArray array = value.getAsJsonArray();

						Collection collection = (Collection) get.invoke(o);
						Object elem;
						for (JsonElement element : array) {
							if (element.isJsonPrimitive()) {
								JsonPrimitive primitive = element.getAsJsonPrimitive();

								elem = element;
								if (primitive.isBoolean())
									elem = primitive.getAsBoolean();
								if (primitive.isString()) {
									elem = primitive.getAsString();
								}
								if (primitive.isNumber())
									elem = primitive.isNumber();

							} else {

								elem = util.deserialise(clazz, element);
							}

							try {

								if (collection instanceof List) {

									if (collection.contains(elem))
										collection.remove(elem);
								}

								collection.add(elem);
							} catch (UnsupportedOperationException e) {
								Logger.getLogger("AngularBeans").log(java.util.logging.Level.WARNING,
										"trying to modify an immutable collection : " + name);
							}

						}

					} catch (Exception e) {
						e.printStackTrace();

					}

				}

				// ------------------------------------------
				if (value.isJsonPrimitive() && (!name.equals("setSessionUID"))) {
					try {

						if (!CommonUtils.hasSetter(o.getClass(), name)) {
							continue;
						}
						name = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);

						Class type = null;
						for (Method set : o.getClass().getDeclaredMethods()) {
							if (CommonUtils.isSetter(set)) {
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

							param = CommonUtils.convertFromString(value.getAsString(), type);

						}

						o.getClass().getMethod(name, type).invoke(o, param);

					} catch (Exception e) {
						e.printStackTrace();

					}
				}

			}
		}

	}

}
