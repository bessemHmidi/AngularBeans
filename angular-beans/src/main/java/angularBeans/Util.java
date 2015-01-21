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
package angularBeans;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Named;
import javax.json.Json;




import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Util {
 
	
	public static final String NG_SESSION_ATTRIBUTE_NAME="NG_SESSION_ID";
	
	public static String generateUID(){
		return String.valueOf(UUID.randomUUID());
	}
	public static String getBeanName(Class targetClass) {

		
//		Class beanClass=null;
//		String proxyClassName = proxyClazz.getCanonicalName();
//		int startIndex=proxyClassName.lastIndexOf(".");
//		int endIndex=proxyClassName.indexOf("$");
//		
//		String beanClassName=proxyClassName.substring(startIndex,endIndex);
//		
//		beanClassName = firstCar + beanClassName.substring(1);
//		
//		beanClass=Class.forName(beanClassName);
//		
		
		
		if(targetClass.isAnnotationPresent(Named.class)){
			Named named=(Named) targetClass.getAnnotation(Named.class);
			return (named.value());
		}

		
		
		
		String name=targetClass.getSimpleName();
		
		String firstCar = name.substring(0, 1).toLowerCase();

		name = firstCar + name.substring(1);

		beanNamesHolder.put(name, targetClass);
		
		return name;
	}
	
	public static Map<String, Class> beanNamesHolder=new HashMap<String, Class>();

	public static String obtainGetter(Field field) {
		String name = field.getName();
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		if (field.getType().equals(Boolean.class)
				|| field.getType().equals(boolean.class))
			return "is" + name;
		return "get" + name;
	}
	
//	public static String obtainSetter(Field field) {
//		String name = field.getName();
//		name = name.substring(0, 1).toUpperCase() + name.substring(1);
//	
//		return "set" + name;
//	}
	
	
	public static String getJson(Object object){
		
//		JsonObject o=Json.createObjectBuilder().add("toot", "ahla")
//		.build();
		
		
		
		Gson gs=new Gson();
		
		return gs.toJson(object,object.getClass());
	}
	public static String obtainFieldNameFromAccessor(String methodName) {
		int index=3;
		if(methodName.startsWith("is"))index=2;
		String fieldName = methodName.substring(index);
		
		fieldName = fieldName.substring(0, 1)
				.toLowerCase()
				+ fieldName.substring(1);

		return fieldName;
	}
	public static JsonObject parse(
			String message) {
		JsonParser parser = new JsonParser(); 
		JsonElement element = parser.parse(message);

		JsonObject jObj = element.getAsJsonObject();
		return jObj;
	}
	
	
	
	
	public static  Object convertFromString(String value, Class type) {

		Object param = null;

		// NPE

		if (type.equals(int.class) || type.equals(Integer.class)) {
			param = Integer.parseInt(value);
			return param;
		}

		if (type.equals(float.class) || type.equals(Float.class)) {
			param = Float.parseFloat(value);
			return param;
		}

		if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			param = Boolean.parseBoolean(value);
			return param;
		} 
		
		if (type.equals(double.class) || type.equals(Double.class)) {
			param = Double.parseDouble(value);
			return param;
		} 
		
		if (type.equals(float.class) || type.equals(Float.class)) {
			param = Float.parseFloat(value);
			return param;
		} 
		
		
		if (type.equals(byte.class) || type.equals(Byte.class)) {
			param = Byte.parseByte(value);
			return param;
		} 
		
		
		if (type.equals(long.class) || type.equals(Long.class)) {
			param = Long.parseLong(value);
			return param;
		} 
		
		
		if (type.equals(short.class) || type.equals(Short.class)) {
			param = Short.parseShort(value);
			return param;
		} 
		
		
		else {

			param = type.cast(value);
		}
		return param;

	}
	
}
