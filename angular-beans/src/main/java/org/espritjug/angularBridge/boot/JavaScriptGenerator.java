package org.espritjug.angularBridge.boot;

import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.espritjug.angularBridge.BridgeExtention;
import org.espritjug.angularBridge.Util;
import org.espritjug.angularBridge.api.NGApp;
import org.espritjug.angularBridge.api.NGController;
import org.espritjug.angularBridge.api.NGRedirect;
import org.espritjug.angularBridge.api.NGReturn;
import org.espritjug.angularBridge.api.NGSubmit;
import org.espritjug.angularBridge.context.BeanLocator;
import org.espritjug.angularBridge.context.NGSessionScopeContext;
import org.espritjug.angularBridge.log.NGLogger;
import org.espritjug.angularBridge.util.NGControllerBean;
import org.espritjug.angularBridge.validation.BeanValidationProcessor;
import org.espritjug.angularBridge.wsocket.WebSocket;
import org.espritjug.angularBridge.wsocket.annotations.Subscribe;

@SessionScoped
public class JavaScriptGenerator implements Serializable {

	private String UID;;

	public JavaScriptGenerator() {

	}

	public static ThreadLocal<Integer> value = new ThreadLocal<Integer>();

	@PostConstruct
	public void init() {

		UID = (Util.generateUID());

	}

	public synchronized String getUID() {
		return UID;
	}

	@Inject
	BeanLocator locator;

	@Inject
	NGLogger logger;

	@Inject
	@NGController
	@Any
	Instance<Object> controllers;

	@Inject
	@Any
	@NGApp
	Instance<Object> app;

	@Inject
	@Any
	@BridgeExtention
	Instance<Object> ext;

	@Inject
	BeanValidationProcessor validationAdapter;

	private StringWriter writer;

	private HttpServletRequest request;

	public void getScript(StringWriter writer) {

		//NGSessionScopeContext.changeHolder(UID);

		// beanManager.(HttpConversationContext.class).get();

		this.writer = writer;

		String appName = null;

		boolean isModule = false;
		for (Object ap : app) {
			ap.toString();
			isModule = true;
			if (ap.getClass().isAnnotationPresent(Named.class)) {
				appName = ap.getClass().getAnnotation(Named.class).value();
			}

			if ((appName == null) || (appName.length() < 1)) {

				appName = Util.getBeanName(ap.getClass());
			}
		}

		if (isModule) {

			writer.write("var app=angular.module('" + appName + "', [])");

			writer.write(".run(function($rootScope) {$rootScope.sessionUID = \""
					+ UID + "\";})");

		}

		// writer.write(TemplatesDirectives.getTemplatesDirectives());
		for (Object controller : controllers) {

			NGControllerBean mb = new NGControllerBean(controller);

			if (isModule) {
				writer.write(";app.controller('" + mb.getName() + "',");
			}

			generateController(mb, isModule);

			if (isModule)
				writer.write(");\n");
		}

		validationAdapter.build(writer);

		for (Object extention : ext) {

			Method m;
			try {
				extention.toString();
				m = extention.getClass().getMethod("render");
				writer.write(m.invoke(extention) + ";");
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
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

	public void generateController(NGControllerBean ngController,
			boolean isModule) {

		Object reference = locator.lookup(ngController.getName(), UID);

		Class<? extends Object> clazz = ngController.getTargetClass();

		Method[] methods = clazz.getDeclaredMethods();
		Object o = reference;

		if (isModule) {
			writer.write("function");

		} else {
			writer.write("function " + ngController.getName());
		}

		writer.write("($rootScope,$scope, $http, $location,logger");

		writer.write(",wsocketRPC){\n");

		if (clazz.isAnnotationPresent(Subscribe.class)) {
			String[] channels = ((Subscribe) clazz
					.getAnnotation(Subscribe.class)).channels();
			for (String channel : channels) {

				writer.write("wsocketRPC.subscribe($scope,'" + channel + "');");
			}
		}

		List<Method> getters = new ArrayList<Method>();

		for (Method m : methods) {
			if ((m.getName().startsWith("get"))
					|| (m.getName().startsWith("is"))) {
				getters.add(m);
			}
		}

		for (Method get : getters) {

			String getter = get.getName();

			String modelName = Util.obtainFieldNameFromAccessor(getter);

			// TODO validation parts

			validationAdapter.processBeanValidationParsing(get);

			Object result = null;

			Method m;

			try {

				m = o.getClass().getMethod((getter));

				result = m.invoke(o);

				if (result == null)
					continue;

				Class<? extends Object> resultClazz = result.getClass();

				if (!resultClazz.isPrimitive()) {

					result = Util.getJson(result);

				}

			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
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

			writer.write("$scope." + modelName + "=" + result + ";");

		}

		for (Method m : methods) {

			String csModel = null;
			String[] csUpdates = null;
			Set<Method> setters = new HashSet<Method>();

			if (m.isAnnotationPresent(NGReturn.class)) {
				NGReturn returns = m.getAnnotation(NGReturn.class);
				csModel = returns.model();
				csUpdates = returns.updates();
			}

			if (m.isAnnotationPresent(NGSubmit.class)
					|| m.isAnnotationPresent(NGRedirect.class)) {

				if (m.isAnnotationPresent(NGSubmit.class)) {
					
					
					
					String[] models = m.getAnnotation(NGSubmit.class)
							.updateModels();

					if (models.length == 0||models==null) {

					
						for (Method md : methods) {
							String methodName = md.getName();
							if (methodName.startsWith("set")) {
								setters.add(md);
							}
						}

					} else {

						for (String model : models) {

							for (Method md : methods) {
								String methodName = md.getName();
								if (methodName.startsWith("set")) {

									String modelName = Util
											.obtainFieldNameFromAccessor(methodName);
									if (modelName.equals(model)) {
										setters.add(md);
									}

								}

							}

						}
					}
				}

				writer.write("\n $scope." + m.getName() + "= function() {");

				writer.write("var params={sessionUID:'" + UID + "'};");
				addParams(setters, m);

				if (m.isAnnotationPresent(WebSocket.class)) {

					writer.write("wsocketRPC.call($scope,'"
							+ ngController.getName() + "." + m.getName()
							+ "',params);");

				} else {
					writer.write("\n  $http.get('./rest/invoke/service/"
							+ ngController.getName() + "/" + m.getName()
							+ "/json");

					writer.write("?params='+encodeURI(JSON.stringify(params))");

					writer.write(").\n success(function(data) {\n");

					if (m.isAnnotationPresent(NGRedirect.class)) {
						writer.write("window.location = data[1];\n });");
					}

					else {
						if (csModel != null) {

							writer.write("$scope." + csModel + " = data[1];");

						}

						if (csUpdates != null) {
							for (int i = 0; i < csUpdates.length; i++)
								writer.write("$scope." + csUpdates[i]
										+ " = data[" + (i + 2) + "];\n");

						}

						writer.write("logger.log(data[0]);");

						writer.write("\n });");
					}
				}

				writer.write("};");

			}

		}

		writer.write("\n} \n");

	}

	private void addParams(Set<Method> setters, Method m) {
		if (m.isAnnotationPresent(NGSubmit.class)) {

			for (Method setter : setters) {

				String name = setter.getName();
				name = name.substring(3, 4).toLowerCase() + name.substring(4);

				writer.write("params['" + name + "']=$scope." + name + ";");

			}

		}

	}

	public void setHTTPRequest(HttpServletRequest request) {

		request.getSession().setAttribute(Util.NG_SESSION_ATTRIBUTE_NAME, UID);

		this.request = request;

	}

	public HttpServletRequest getRequest() {
		return request;
	}
}