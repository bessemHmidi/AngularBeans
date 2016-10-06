/* AngularBeans, CDI-AngularJS bridge Copyright (c) 2014, Bessem Hmidi. or third-party contributors as indicated by
 * the @author tags or express copyright attribution statements applied by the authors. This copyrighted material is
 * made available to anyone wishing to use, modify, copy, or redistribute it subject to the terms and conditions of the
 * GNU Lesser General Public License, as published by the Free Software Foundation. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. */
package angularBeans.util;

import static angularBeans.util.Accessors.BOOLEAN_GETTER_PREFIX;
import static angularBeans.util.Accessors.GETTER_PREFIX;
import static angularBeans.util.Accessors.SETTER_PREFIX;

import java.beans.Introspector;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import org.boon.Pair;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import angularBeans.api.CORS;
import angularBeans.api.NGParamCast;
import angularBeans.api.http.Delete;
import angularBeans.api.http.Get;
import angularBeans.api.http.Post;
import angularBeans.api.http.Put;
import angularBeans.realtime.RealTime;

/**
 * @author Bessem Hmidi
 * @author Aymen Naili
 */
public abstract class CommonUtils {

   private static final String BASE64_MARK = ";base64,";
   private static final String DATA_MARK = "data:";

   /**
    * used to obtain a bean java class from a bean name.
    */
   public static final Map<String, Class> beanNamesHolder = new HashMap<>();

   public static String getBeanName(Class targetClass) {

      if (targetClass.isAnnotationPresent(Named.class)) {
         Named named = (Named) targetClass.getAnnotation(Named.class);
         if (!named.value().isEmpty()) {
            return named.value();
         }
      }

      String name = Introspector.decapitalize(targetClass.getSimpleName());
      beanNamesHolder.put(name, targetClass);
      return name;
   }

   public static String obtainGetter(Field field) {
      String name = capitalize(field.getName());
      if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
         return BOOLEAN_GETTER_PREFIX + name;
      } else {
         return GETTER_PREFIX + name;
      }
   }

   public static String obtainSetter(Field field) {
      String name = capitalize(field.getName());
      return SETTER_PREFIX + name;
   }

   public static JsonElement parse(String message) {

      if (!message.startsWith("{")) {
         return new JsonPrimitive(message);
      }
      JsonParser parser = new JsonParser();
      return parser.parse(message);
   }

   /**
    * Create a wrapper object for one of the primitive Java types from a string. Basically it calls
    * {@code x.parseX(value)} after checking for {@code null} and empty argument.
    * <p>
    * For a {@code null} or empty value, {@code null} is returned.
    * 
    * @param value
    *           String to convert
    * @param type
    *           Type to convert to.
    * @return Instance of the corresponding wrapper class or {@code null}
    * @throws ArithmeticException
    *            if {@code value} cannot be parsed
    * @throws IllegalArgumentException
    *            if {@code value} is not one of: primitive type, wrapper type, String, collection
    */
   public static Object convertFromString(String value, Class type) {

      if (isNullOrEmpty(value) || type.equals(byte[].class) || type.equals(Byte[].class)) {
         return null;
      }

      if (String.class.equals(type)) {
         return value;
      }

      if (type.equals(int.class) || type.equals(Integer.class)) {
         return Integer.parseInt(value);
      }
      if (type.equals(float.class) || type.equals(Float.class)) {
         return Float.parseFloat(value);
      }
      if (type.equals(boolean.class) || type.equals(Boolean.class)) {
         return Boolean.parseBoolean(value);
      }
      if (type.equals(double.class) || type.equals(Double.class)) {
         return Double.parseDouble(value);
      }
      if (type.equals(byte.class) || type.equals(Byte.class)) {
         return Byte.parseByte(value);
      }
      if (type.equals(long.class) || type.equals(Long.class)) {
         return Long.parseLong(value);
      }
      if (type.equals(short.class) || type.equals(Short.class)) {
         return Short.parseShort(value);
      }

      throw new IllegalArgumentException("unknown primitive type :" + type.getCanonicalName());

   }

   public static boolean isSetter(Method m) {
      if (m == null) {
         return false;
      }

      return m.getName().startsWith(SETTER_PREFIX) && returnsVoid(m) && hasOneParameter(m);
   }

   public static boolean isGetter(Method m) {
      if (m == null) {
         return false;
      }
      if (returnsVoid(m)) {
         return false;
      }
      if (isHttpAnnotated(m) || m.isAnnotationPresent(RealTime.class) || m.isAnnotationPresent(CORS.class)) {
         return false;
      }
      return hasNoParameters(m) && m.getName().startsWith(GETTER_PREFIX) || returnsBoolean(m) && m.getName().startsWith(BOOLEAN_GETTER_PREFIX);
   }

   public static boolean hasSetter(Class clazz, String fieldName) {

      String setterName = SETTER_PREFIX + capitalize(fieldName);

      setterName = setterName.trim();
      for (Method m: clazz.getDeclaredMethods()) {

         if (m.getName().equals(setterName) && isSetter(m)) {
            return true;
         }
      }
      return false;
   }

   public static String obtainFieldNameFromAccessor(String methodName) {
      String fieldName;
      if (methodName.startsWith(GETTER_PREFIX) || methodName.startsWith(SETTER_PREFIX)) {
         fieldName = methodName.substring(3);
      } else if (methodName.startsWith(BOOLEAN_GETTER_PREFIX)) {
         fieldName = methodName.substring(2);
      } else {
         throw new IllegalArgumentException("Unable to obtain field name from method '" + methodName + "'.");
      }

      return Introspector.decapitalize(fieldName);
   }

   private static boolean isHttpAnnotated(Method m) {
      if (m == null) {
         return false;
      }

      return m.isAnnotationPresent(Get.class) || m.isAnnotationPresent(Post.class) || m.isAnnotationPresent(Put.class) || m.isAnnotationPresent(Delete.class);
   }

   private static boolean returnsBoolean(Method m) {
      return m.getReturnType().equals(boolean.class) || m.getReturnType().equals(Boolean.class);
   }

   private static boolean returnsVoid(Method m) {
      return m.getReturnType().equals(Void.class) || (m.getReturnType().equals(void.class));
   }

   private static boolean hasNoParameters(Method m) {
      return m.getParameterTypes().length == 0;
   }

   private static boolean hasOneParameter(Method m) {
      return m.getParameterTypes().length == 1;
   }

   private static String capitalize(String name) {
      return name.substring(0, 1).toUpperCase() + name.substring(1);
   }

   /**
    * Check if the parameter is null or empty. If {@code o} is neither a String, an array or a
    * Collection it is regarded as non-empty.
    *
    * @param <T>
    *           class type of tested object.
    * @param o
    *           parameter to check.
    * @return true if the parameter is {@code null} or empty, false otherwise.
    */
   private static <T> Boolean isNullOrEmpty(final T o) {
      if (o == null) {
         return true;
      }
      if (o instanceof String) {
         return "".equals(((String) o).trim());
      }
      if (o.getClass().isArray()) {
         return Array.getLength(o) == 0;
      }
      if (o instanceof Collection<?>) {
         return ((Collection<?>) o).isEmpty();
      }
      return false;
   }

   /**
    * Get class method in accordance with the parameters informed
    * 
    * @param clazz
    *           Source class
    * @param methodName
    *           Method name
    * @param paramSize
    *           Number of parameters for the method to be caught
    * @return Method Class method
    */
   public static Method getMethod(Class<?> clazz, String methodName, int paramSize) {
      for (Method mt: clazz.getMethods()) {
         if (mt.getName().equals(methodName) && mt.getGenericParameterTypes().length == paramSize && !Modifier.isVolatile(mt.getModifiers())) {
            return mt;
         }
      }
      return null;
   }

   /**
    * Returns the Map with the names and indexes defined in NGParamCast
    * 
    * @param m
    *           Method to parse
    * @return Map with indexes and names of types
    */
   public static Pair<Boolean, Map<Integer, String>> getParamCastMap(Method m) {
      if (m.isAnnotationPresent(NGParamCast.class)) {
         Map<Integer, String> mParam = new HashMap<>();

         NGParamCast ngCast = m.getAnnotation(NGParamCast.class);
         boolean required = ngCast.required();

         String[] params = ngCast.param();

         if (params != null && params.length > 0) {
            for (String param: params) {
               param = param.trim();

               if (param.contains("{") && param.contains("}")) {
                  int idxIni = param.indexOf("{");
                  String paramName = param.substring(0, idxIni).trim();
                  String[] paramIndex = param.substring(idxIni + 1, param.indexOf("}")).split(",");

                  for (String idx: paramIndex) {
                     mParam.put(Integer.parseInt(idx.trim()), paramName);
                  }
               } else {
                  if (param.length() > 0) {
                     mParam.put(0, param);
                  }
               }
            }
         }
         return new Pair<>(required, mParam);
      }
      return null;
   }

   /**
    * 
    * Returns the type of NGParamType defined in the implemented class
    * 
    * @param service
    *           Class instantiated
    * @param paramName
    *           Parameter name
    * @param required
    *           Parameter required or not
    * @return Parameter type instantiated in NGParamType
    */
   public static Type getParamType(Object service, String paramName, boolean required) {
      if (paramName != null && paramName.length() > 0) {
         try {
            Field field = service.getClass().getDeclaredField(paramName);
            field.setAccessible(true);
            return ((NGParamType<?>) field.get(service)).getType();
         }
         catch (Exception e) {
            if (required) {
               e.printStackTrace();
            }
         }
      }

      return null;
   }

   /**
    * Returns the primitive base type of json
    * 
    * @param element
    *           JSON element
    * @return Primitive class of element
    */
   public static Class getPrimitiveClass(JsonElement element) {
      if (element.getAsJsonPrimitive().isBoolean()) {
         return Boolean.class;
      }

      if (element.getAsJsonPrimitive().isNumber()) {
         if (element.getAsString().contains(".")) {
            return Double.class;
         }
         return Long.class;
      }

      return String.class;
   }

   /***
    * Returns the type of bind to convert byte[] - LOB (current format) / BASE64
    *
    * @return String "LOB" or "BASE64"
    */
   public static String getBytesArrayBind() {
      String bytesArrayBind = Constants.LOB_BIND;
      if (NGConfiguration.getProperty("BYTES_ARRAY_BIND") != null) {
         if (NGConfiguration.getProperty("BYTES_ARRAY_BIND").trim().equals(Constants.BASE64_BIND)) {
            bytesArrayBind = Constants.BASE64_BIND;
         }
      }
      return bytesArrayBind;
   }

   /**
    * Returns JsonPrimitive with base64 javascript pattern
    * 
    * @param type
    *           Type of data
    * @param bytes
    *           array of bytes to convert
    * @return String json element in base64 javascript pattern
    */
   public static JsonPrimitive getBase64Json(String type, byte[] bytes) {
      if (bytes != null && bytes.length > 0) {
         if (type == null || type.trim().length() == 0) {
            type = NGBase64.FORM_DATA_TYPE;
         }

         try {
            return new JsonPrimitive(DATA_MARK + type + BASE64_MARK + Base64.getEncoder().encodeToString(bytes).trim());
         }
         catch (Exception e) {}

      }
      return null;
   }

   /**
    * Returns array of bytes
    * 
    * @param element
    *           JsonPrimitive with base64 javascript pattern
    * @return Array of bytes
    */
   public static byte[] getBytesFromJson(JsonElement element) {
      String value = element.getAsString();
      if (value != null && value.trim().length() > 0) {
         try {
            if (value.contains(DATA_MARK) && value.contains(BASE64_MARK)) {
               value = value.substring(value.indexOf(BASE64_MARK) + BASE64_MARK.length());
            }
            if (value.length() > 0) {
               return Base64.getDecoder().decode(value);
            }
         }
         catch (Exception e) {}
      }
      return null;
   }
}