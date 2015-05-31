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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
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

import angularBeans.api.AngularBean;
import angularBeans.api.NGApp;
import angularBeans.api.NGModel;
import angularBeans.api.NGModules;
import angularBeans.api.NGPostConstruct;
import angularBeans.api.NGReturn;
import angularBeans.api.NGSubmit;
import angularBeans.context.BeanLocator;
import angularBeans.context.NGSessionScopeContext;
import angularBeans.context.NGSessionScoped;
import angularBeans.io.ByteArrayCache;
import angularBeans.io.Call;
import angularBeans.io.FileUpload;
import angularBeans.io.FileUploadHandler;
import angularBeans.io.LobWrapper;
import angularBeans.ngservices.NGExtention;
import angularBeans.realtime.RealTime;
import angularBeans.util.AngularBeansUtil;
import angularBeans.util.CurrentNGSession;
import angularBeans.util.ModelQueryFactory;
import angularBeans.util.NGBean;
import angularBeans.validation.BeanValidationProcessor;

@SessionScoped
public class ModuleGenerator implements Serializable {

	private String UID;;

	@Inject
	ModelQueryFactory modelQueryFactory;

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
		//NGSessionScopeContext.setCurrentContext(UID);
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
	@AngularBean
	@Any
	Instance<Object> beans;

	@Inject
	@Any
	@NGExtention
	Instance<Object> ext;

	@Inject
	 FileUploadHandler uploadHandler;

	@Inject
	BeanValidationProcessor validationAdapter;



	@Inject
	 CurrentNGSession ngSession;

	private StringWriter writer;

	private HttpServletRequest request;

	private String contextPath;

	public void getScript(StringWriter writer) {

		NGSessionScopeContext.setCurrentContext(UID);

		ngSession.setSessionId(UID);

		contextPath = (request.getServletContext().getContextPath());

		this.writer = writer;

		String appName = null;

		Class appClass = null;
		for (Object ap : app) {
			ap.toString();

			appClass = ap.getClass();
			if (appClass.isAnnotationPresent(Named.class)) {
				appName = ap.getClass().getAnnotation(Named.class).value();
			}

			if ((appName == null) || (appName.length() < 1)) {

				appName = util.getBeanName(ap.getClass());
			}
		}

		writer.write("var angularBeans={ "

				+ "bind:function(scope,service,modelName){"
				+ "var members={};"

				+ "scope[service.serviceID]=service;"

				+ " scope.$watch((service.serviceID+'.'+modelName), function (newVal, oldVal, scope) {"
				+ "		    if(newVal) { "

				+ "		      scope[modelName] = newVal;"
				+ "	    }"
				+ "		  });"

				+ "}"
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

				+ " };");

		writer.write("var app=angular.module('" + appName + "', [");

		if (appClass.isAnnotationPresent(NGModules.class)) {

			NGModules ngModAnno = (NGModules) appClass
					.getAnnotation(NGModules.class);
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

		for (String model : modelQueryFactory.getRootScope().getProperties()) {

			writer.write("$rootScope." + model + " = "
					+ util.getJson(modelQueryFactory.getRootScope().getProperty(model))
					+ ";");

		}

		writer.write("});");

		for (Object bean : beans) {

			NGBean mb = new NGBean(bean);

			// writer.write(";angular.module('"+appName+"')");
			writer.write(";app.factory('" + mb.getName() + "',function "
					+ mb.getName() + "(");

			generateBean(mb, contextPath);

			writer.write(");\n");
		}

		validationAdapter.build(writer);

		for (Object extention : ext) {

			Method m;
			try {
				extention.toString();
				m = extention.getClass().getMethod("render");
				writer.write(m.invoke(extention) + ";");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void generateBean(NGBean bean, String contextPath) {

		Object reference = locator.lookup(bean.getName(), UID);

		Class<? extends Object> clazz = bean.getTargetClass();

		Method[] methods = clazz.getDeclaredMethods();
		Object o = reference;

		modelQueryFactory.addQuery(clazz);

		// writer.write("['$rootScope','$scope','$http','$location','logger','responseHandler','RTSrvc',function");

		writer.write("$rootScope, $http, $location,logger,responseHandler,$q");

		writer.write(",RTSrvc");
		writer.write("){\n");

		// writer.write("var deffered = $q.defer();");
		writer.write("var " + bean.getName() + "={serviceID:'" + bean.getName()
				+ "'};");// ,scopes:[]};");

		writer.write("\nvar rpath='" + contextPath
				+ "/http/invoke/service/';\n");

		// String defaultChannel = clazz.getSimpleName();

		// writer.write("\nRTSrvc.subscribe("+bean.getName()+",'" +
		// defaultChannel + "');");
		// if (clazz.isAnnotationPresent(Subscribe.class)) {
		// String[] channels = ((Subscribe) clazz
		// .getAnnotation(Subscribe.class)).channels();
		//
		// for (String channel : channels) {
		//
		// writer.write("RTSrvc.subscribe($scope,'" + channel + "');");
		// }
		// }

		List<Method> getters = new ArrayList<Method>();

		for (Method m : methods) {
			if (util.isGetter(m)) {
				if (m.isAnnotationPresent(NGModel.class)) {
					getters.add(m);
				}
			}
		}

		for (Method get : getters) {
			Object result = null;

			String getter = get.getName();

			String modelName = util.obtainFieldNameFromAccessor(getter);

			if (get.getReturnType().equals(LobWrapper.class)) {

				String uid = String.valueOf(UUID.randomUUID());
				cache.getCache().put(uid, new Call(o, get));
				result = "lob/" + uid;

				writer.write(bean.getName() + "." + modelName + "='" + result
						+ "';");
				continue;

			}

			validationAdapter.processBeanValidationParsing(get);

			Method m;

			try {

				m = o.getClass().getMethod((getter));

				result = m.invoke(o);

				if ((result == null && (m.getReturnType().equals(String.class))))
					result = "";

				if (result == null)
					continue;

				Class<? extends Object> resultClazz = result.getClass();

				if (!resultClazz.isPrimitive()) {

					result = util.getJson(result);

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

			writer.write(bean.getName() + "." + modelName + "=" + result + ";");

		}

		for (Method m : methods) {
			if ((!util.isSetter(m)) && (!util.isGetter(m))) {

				// String csModel = null;
				String[] csUpdates = null;
				Set<Method> setters = new HashSet<Method>();

				String httpMethod = "get";

				if (m.isAnnotationPresent(FileUpload.class)) {

					String uploadPath = ((FileUpload) m
							.getAnnotation(FileUpload.class)).path();

					Call call = new Call(o, m);

					uploadHandler.getUploadsActions().put(uploadPath, call);
				}

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

						pushScope(methods, setters);

					} else {

						for (String model : models) {

							for (Method md : methods) {

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

				writer.write(bean.getName() + "." + m.getName() + "= function(");
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

						writer.write(argsString.substring(0,
								argsString.length() - 1));

					}
				}
				// ------------------------------------------------
				// -------------------------------

				writer.write(") {");

				writer.write("var mainReturn={data:{}};");
				writer.write("var params={sessionUID:$rootScope.sessionUID};");
				addParams(bean, setters, m, args);

				if (m.isAnnotationPresent(RealTime.class)) {

					writer.write("return RTSrvc.call(" + bean.getName() + ",'"
							+ bean.getName() + "." + m.getName() + "',params");

					writer.write(").then(function(response) {\n");

					writer.write("var msg=(response);");

					// writer.write("console.log((response));");

					// writer.write("var callers=RTSrvc.getCallers();");

					writer.write("mainReturn.data= responseHandler.handleResponse(msg,"
							+ bean.getName() + ",true);");

					// writer.write("deffered.resolve();");

					writer.write("return mainReturn.data;"); // }");

					writer.write("} ,function(response){return $q.reject(response.data);});");

				} else {

					writer.write("\n  return $http." + httpMethod + "(rpath+'"
							+ bean.getName() + "/" + m.getName() + "/json");

					if (httpMethod.equals("post")) {
						writer.write("',params");
					} else {
						// encodeURI
						String paramsQuery = ("?params='+encodeURIComponent(angular.toJson(params))");

						writer.write(paramsQuery);
					}

					writer.write(").then(function(response) {\n");

					writer.write("var msg=response.data;");
					// writer.write("var callers=RTSrvc.getCallers();");

					writer.write("mainReturn.data= responseHandler.handleResponse(msg,"
							+ bean.getName() + ",true);");

					// writer.write("deffered.resolve();");

					writer.write("return mainReturn.data;"); // }");

					writer.write("} ,function(response){return $q.reject(response.data);});");

				}

				writer.write("};");

				if (m.isAnnotationPresent(NGPostConstruct.class)) {

					writer.write(bean.getName() + "." + m.getName() + "();\n");
				}
			}
		}
		writer.write("return " + bean.getName() + ";} \n");

	}

	private void pushScope(Method[] methods, Set<Method> setters) {
		for (Method md : methods) {

			if (util.isSetter(md)) {
				setters.add(md);
			}
		}
	}

	private void addParams(NGBean bean, Set<Method> setters, Method m,
			Type[] args) {

		for (Method setter : setters) {

			String name = util.obtainFieldNameFromAccessor(setter.getName());
			writer.write("params['" + name + "']=" + bean.getName() + "."
					+ name + ";");

		}

		// ---------------------------------
		// handle args

		if (args.length > 0) {
			String argsString = "";
			for (int i = 0; i < args.length; i++) {

				argsString += "arg" + i + ",";

			}

			argsString = argsString.substring(0, argsString.length() - 1);

			writer.write("params['args']=[" + argsString + "];\n");

		}
		// }

	}

	public void setHTTPRequest(HttpServletRequest request) {

		request.getSession().setAttribute(util.NG_SESSION_ATTRIBUTE_NAME, UID);

		this.request = request;

	}

	public HttpServletRequest getRequest() {
		return request;
	}
}