package angularBeans.util;

/**
 * Implements the type of class that will be used to maintain compatibility with BASE64 (javascript
 * binary pattern) format implemented by AngularBeans
 * <p>
 * 
 * @author Osni Marin
 **/
@SuppressWarnings("serial")
public class NGBase64 extends NGBytesBase {

   public static final String PNG_TYPE = "image/png";
   public static final String GIF_TYPE = "image/gif";
   public static final String BMP_TYPE = "image/bmp";
   public static final String JPG_TYPE = "image/jpg";

   public static final String PDF_TYPE = "application/pdf";
   
   public static final String JAVASCRIPT_TYPE = "text/javascript";
   public static final String HTML_TYPE = "text/html";
   
   public static final String FORM_DATA_TYPE = "multipart/form-data";
   public static final String FORM_URL_ENCODED_TYPE = "application/x-www-form-urlencoded";
   public static final String TEXT_PLAIN_TYPE = "text/plain";

   private String type = FORM_DATA_TYPE;

   public NGBase64(byte[] bytes) {
      super(bytes);
   }

   public NGBase64(String type, byte[] bytes) {
      super(bytes);

      this.type = type;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }
}