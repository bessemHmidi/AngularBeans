package angularBeans.util;

/**
 * Implements the type of class that will be used to maintain compatibility with LOB format implemented by AngularBeans
 * <p>
 * 
 * @author Osni Marin
 **/
@SuppressWarnings("serial")
public class NGLob extends NGBytesBase {

   public NGLob(byte[] bytes) {
      super(bytes);
   }
}