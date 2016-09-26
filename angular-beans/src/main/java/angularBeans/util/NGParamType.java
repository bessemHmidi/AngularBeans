package angularBeans.util;

import com.google.common.reflect.TypeToken;

/**
 * Implements the class type that will be used to make the Cast in JSON defined  in abstract class or interface
 * <p>
 * 
 * Ex. 
 * 
 * ...
 * @Inject
 * ModelQuery modelQuery;
 *
 * NGParamType<?> ngEntity = new NGParamType<ImplementedBean>(){};
 * ...
 * 
 * @author Osni Marin
 **/
public class NGParamType<T>extends TypeToken<T> {}