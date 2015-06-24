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
package angularBeans.boot;

import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import angularBeans.api.NGApp;
import angularBeans.api.NGModules;
import angularBeans.api.NGPostConstruct;
import angularBeans.api.NGReturn;
import angularBeans.api.NGSubmit;
import angularBeans.context.BeanLocator;
import angularBeans.context.NGSessionScopeContext;
import angularBeans.io.ByteArrayCache;
import angularBeans.io.Call;
import angularBeans.io.FileUpload;
import angularBeans.io.FileUploadHandler;
import angularBeans.io.LobWrapper;
import angularBeans.ngservices.NGService;
import angularBeans.realtime.RealTime;
import angularBeans.util.AngularBeansUtil;
import angularBeans.util.CurrentNGSession;
import angularBeans.util.NGBean;
import angularBeans.validation.BeanValidationProcessor;

@SessionScoped
public class ModuleGenerator implements Serializable {

	final String angularBeanMainFunction = "var angularBeans={ "

			+ "bind:function(scope,service,modelsName){"
			// + "var members={};"

			+ "scope[service.serviceID]=service;"

			+ "for (i in modelsName){"

			+ "modelsName[i]=service.serviceID+'.'+modelsName[i];"
			+ "}"

			+ "scope.$watch(angular.toJson(modelsName).split('\\\"').join(''), function (newValue, oldValue) {"

			+ "for (i in modelsName){"
			+ "scope[modelsName[i].split(service.serviceID+'.')[1]]=newValue[i];} "

			+ "}, true);"

			+ "}"

			+ ",addMethod :function(object,name,fn){"

			+ "if (object['$ab_fn_cache']==null){object['$ab_fn_cache']=[]; }"

			+ "  if((object['$ab_fn_cache'][name])==undefined){object['$ab_fn_cache'][name]=[];}"

			+ " var index= object['$ab_fn_cache'][name].length;"

			+ "object['$ab_fn_cache'][name][index]=fn;"

			+ "object[name]=function  (){"

			+ "  for (index in object['$ab_fn_cache'][name]){"

			+ "      var actf=object['$ab_fn_cache'][name][index];"

			+ "     if(arguments.length==actf.length){"

			+ "        return actf.apply(object,arguments);"

			+ "      } }};"

			+ "}"

			// *
			+ ",isIn:function(array,elem){var found=false;"
			+ "for(item in array){"
			+ "if(this.isSame(array[item],elem)){found =true;break;}"
			+ "}"

			+ "return found;}"

			+ ",isSame:function(item1,item2){"

			+ "var same=true;"

			+ "for(prop in item1){"

			+ "if(prop=='$$hashKey'){continue;}"
			+ "if (typeof item1[prop] == 'string' || item1[prop] instanceof String){if(item1[prop].startsWith('lob/')){continue;}}"

			+ "if(!(angular.toJson(item1[prop])==angular.toJson(item2[prop]))){same=false;}"

			+ "}"

			+ "return same;}"

			+ " };";

	private String UID;

	@Inject
	AngularBeansUtil util;

	public ModuleGenerator() {
		if (this.getClass().equals(ModuleGenerator.class)) {
			UID = String.valueOf(UUID.randomUUID());
		}
		NGSessionScopeContext.setCurrentContext(UID);
	}

	@PostConstruct
	public void init() {
		// NGSessionScopeContext.setCurrentContext(UID);
	}

	public synchronized String getUID() {
		return UID;
	}

	@Inject
	ByteArrayCache cache;

	@Inject
	@Any
	@NGApp
	Instance<Object> app;

	@Inject
	BeanLocator locator;

	@Inject
	transient FileUploadHandler uploadHandler;

	@Inject
	BeanValidationProcessor validationAdapter;

	@Inject
	transient CurrentNGSession ngSession;

	private StringWriter writer;
	private HttpServletRequest request;
	private String contextPath;

	public void getScript(StringWriter writer) {
		NGSessionScopeContext.setCurrentContext(UID);
		ngSession.setSessionId(UID);
		contextPath = (request.getServletContext().getContextPath());
		this.writer = writer;
		String appName = null;
		Class<? extends Object> appClass = null;
		for (Object ap : app) {

			appClass = ap.getClass();
			if (appClass.isAnnotationPresent(Named.class)) {
				appName = ap.getClass().getAnnotation(Named.class).value();
			}

			if ((appName == null) || (appName.length() < 1)) {

				appName = util.getBeanName(ap.getClass());
			}
		}

		writer.write(angularBeanMainFunction);

		writer.write("var app=angular.module('" + appName + "', [");

		if (appClass.isAnnotationPresent(NGModules.class)) {

			NGModules ngModAnno = appClass.getAnnotation(NGModules.class);
			String[] modules = ngModAnno.value();
			String modulesPart = "";
			for (String module : modules) {
				modulesPart += ("'" + module + "',");
			}
			modulesPart = modulesPart.substring(0, modulesPart.length() - 1);
			writer.write(modulesPart);
		}

		writer.write("])");

		writer.write(".run(function($rootScope) {$rootScope.sessionUID = \""
				+ UID + "\";");

		writer.write("});");

		for (NGBean mb : BeanRegistry.getInstance().getAngularBeans()) {

			// locator.lookup(mb.getName(), UID);

			writer.write(";app.factory('" + mb.getName() + "',function "
					+ mb.getName() + "(");

			generateBean(mb, contextPath);

			writer.write(");\n");
		}

		validationAdapter.build(writer);

		for (NGService extention : BeanRegistry.getInstance().getExtentions()) {

			extention.setGenerator(this);
			Method m;
			try {
				m = extention.getClass().getMethod("render");
				writer.write(m.invoke(extention) + ";");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public void generateBean(NGBean bean, String contextPath) {

		Class<? extends Object> clazz = bean.getTargetClass();

		Method[] methods = bean.getMethods();

		// writer.write("['$rootScope','$scope','$http','$location','logger','responseHandler','RTSrvc',function");

		writer.write("$rootScope, $http, $location,logger,responseHandler,$q");

		writer.write(",RTSrvc");
		writer.write("){\n");

		// writer.write("var deffered = $q.defer();");
		writer.write("var " + bean.getName() + "={serviceID:'" + bean.getName()
				+ "'};");// ,scopes:[]};");

		writer.write("\nvar rpath='" + contextPath
				+ "/http/invoke/service/';\n");

		Object reference = locator.lookup(bean.getName(), UID);

		for (Method get : bean.getters()) {
			Object result = null;

			String getter = get.getName();

			String modelName = util.obtainFieldNameFromAccessor(getter);

			if (get.getReturnType().equals(LobWrapper.class)) {

				String uid = String.valueOf(UUID.randomUUID());
				cache.getCache().put(uid, new Call(reference, get));
				result = "lob/" + uid;

				writer.write(bean.getName() + "." + modelName + "='" + result
						+ "';");
				continue;

			}

			validationAdapter.processBeanValidationParsing(get);

			Method m;

			try {

				m = bean.getTargetClass().getMethod((getter));

				result = m.invoke(reference);

				if ((result == null && (m.getReturnType().equals(String.class))))
					result = "";

				if (result == null)
					continue;
				Class<? extends Object> resultClazz = result.getClass();
				if (!resultClazz.isPrimitive()) {
					result = util.getJson(result);
				}

			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			writer.write(bean.getName() + "." + modelName + "=" + result + ";");

		}

		for (Method m : bean.getMethods()) {
			if (m.isAnnotationPresent(FileUpload.class)) {

				String uploadPath = m.getAnnotation(FileUpload.class).path();

				Call call = new Call(reference, m);

				uploadHandler.getUploadsActions().put(uploadPath, call);
			}
		}

		writer.write(generateStaticPart(bean).toString());

		// for (Method m : bean.getMethods()) {
		//
		// }
		writer.write("return " + bean.getName() + ";} \n");
	}

	private static Map<Class, StringBuffer> cachedStaticParts = new HashMap<Class, StringBuffer>();

	private synchronized StringBuffer generateStaticPart(NGBean bean) {

		StringBuffer cachedStaticPart = new StringBuffer();
		if (cachedStaticParts.containsKey(bean.getTargetClass())) {
			return cachedStaticParts.get(bean.getTargetClass());
		}

		Method[] nativesMethods = Object.class.getMethods();

		for (Method m : bean.getMethods()) {

			boolean isNative = false;
			for (Method nativeMethod : nativesMethods) {
				if (nativeMethod.equals(m))
					isNative = true;
			}

			if (isNative)
				continue;

			if ((!util.isSetter(m)) && (!util.isGetter(m))) {

				// String csModel = null;
				String[] csUpdates = null;
				Set<Method> setters = new HashSet<Method>();

				String httpMethod = "get";

				if (m.isAnnotationPresent(GET.class)) {
					httpMethod = "get";
				}

				if (m.isAnnotationPresent(POST.class)) {
					httpMethod = "post";
				}

				if (m.isAnnotationPresent(DELETE.class)) {
					httpMethod = "delete";
				}

				if (m.isAnnotationPresent(PUT.class)) {
					httpMethod = "put";
				}

				if (m.isAnnotationPresent(NGReturn.class)) {
					NGReturn returns = m.getAnnotation(NGReturn.class);
					csUpdates = returns.updates();
				}

				// if (m.isAnnotationPresent(NGSubmit.class)
				// || m.isAnnotationPresent(NGRedirect.class)) {

				if (m.isAnnotationPresent(NGSubmit.class)) {

					String[] models = m.getAnnotation(NGSubmit.class)
							.backEndModels();

					if (models.length == 1 && models[0].equals("*")) {

						pushScope(bean.getMethods(), setters);

					} else {

						for (String model : models) {

							for (Method md : bean.getMethods()) {

								if (util.isSetter(md)) {
									String methodName = md.getName();
									String modelName = util
											.obtainFieldNameFromAccessor(methodName);
									if (modelName.equals(model)) {
										setters.add(md);
									}

								}

							}
						}
					}
				}
				// else {
				// pushScope(methods, setters);
				// }

				//

				cachedStaticPart.append("angularBeans.addMethod("
						+ bean.getName() + ",'" + m.getName() + "',function(");

				// writer.write(bean.getName() + "." + m.getName() +
				// "= function(");

				// ---------------------------------------------

				// Handle args
				// ---------------------------------------------
				Type[] args = m.getParameterTypes();

				if (!m.isAnnotationPresent(FileUpload.class)) {

					if (args.length > 0) {
						String argsString = "";
						for (int i = 0; i < args.length; i++) {

							argsString += ("arg" + i + ",");

						}

						cachedStaticPart.append(argsString.substring(0,
								argsString.length() - 1));

					}
				}
				// ------------------------------------------------
				// -------------------------------

				cachedStaticPart
						.append(") {")

						.append("var mainReturn={data:{}};")
						.append("var params={sessionUID:$rootScope.sessionUID};");

				cachedStaticPart.append(addParams(bean, setters, m, args));

				if (m.isAnnotationPresent(RealTime.class)) {

					cachedStaticPart.append("return RTSrvc.call("
							+ bean.getName() + ",'" + bean.getName() + "."
							+ m.getName() + "',params");

					cachedStaticPart.append(").then(function(response) {\n");

					cachedStaticPart.append("var msg=(response);");

					cachedStaticPart
							.append("mainReturn.data= responseHandler.handleResponse(msg,"
									+ bean.getName() + ",true);");

					cachedStaticPart.append("return mainReturn.data;"); // }");

					cachedStaticPart
							.append("} ,function(response){return $q.reject(response.data);});");

				} else {

					cachedStaticPart.append("\n  return $http." + httpMethod
							+ "(rpath+'" + bean.getName() + "/" + m.getName()
							+ "/json");

					if (httpMethod.equals("post")) {
						cachedStaticPart.append("',params");
					} else {
						// encodeURI
						String paramsQuery = ("?params='+encodeURIComponent(angular.toJson(params))");

						cachedStaticPart.append(paramsQuery);
					}

					cachedStaticPart.append(").then(function(response) {\n");

					cachedStaticPart.append("var msg=response.data;");
					// writer.write("var callers=RTSrvc.getCallers();");

					cachedStaticPart
							.append("mainReturn.data= responseHandler.handleResponse(msg,"
									+ bean.getName() + ",true);");

					// writer.write("deffered.resolve();");

					cachedStaticPart.append("return mainReturn.data;"); // }");

					cachedStaticPart
							.append("} ,function(response){return $q.reject(response.data);});");

				}

				cachedStaticPart.append("});");

				if ((!util.isSetter(m)) && (!util.isGetter(m))) {
					if (m.isAnnotationPresent(NGPostConstruct.class)) {

						cachedStaticPart.append(bean.getName() + "."
								+ m.getName() + "();\n");
					}
				}
			}
		}

		cachedStaticParts.put(bean.getClass(), cachedStaticPart);
		return cachedStaticPart;

	}

	private void pushScope(Method[] methods, Set<Method> setters) {
		for (Method md : methods) {

			if (util.isSetter(md)) {
				setters.add(md);
			}
		}
	}

	private StringBuffer addParams(NGBean bean, Set<Method> setters, Method m,
			Type[] args) {

		StringBuffer sb = new StringBuffer();

		for (Method setter : setters) {

			String name = util.obtainFieldNameFromAccessor(setter.getName());
			sb.append("params['" + name + "']=" + bean.getName() + "." + name
					+ ";");
		}

		if (args.length > 0) {
			String argsString = "";
			for (int i = 0; i < args.length; i++) {
				argsString += "arg" + i + ",";
			}
			argsString = argsString.substring(0, argsString.length() - 1);
			sb.append("params['args']=[" + argsString + "];\n");
		}
		return sb;
	}

	public void setHTTPRequest(HttpServletRequest request) {
		request.getSession().setAttribute(util.NG_SESSION_ATTRIBUTE_NAME, UID);
		this.request = request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -9146331095657429874L;
}