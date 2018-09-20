package angularBeans.util;

import java.io.Serializable;

/**
 * Implements the base class for conversion byte[] in json format
 * <p>
 * 
 * @author Osni Marin
 **/
@SuppressWarnings("serial")
public class NGBytesBase implements Serializable {

   protected NGBytesBase(byte[] bytes) {
      this.bytes = bytes;
   }

   public NGBytesBase(String type, byte[] bytes) {
      this.bytes = bytes;
   }

   private byte[] bytes = null;

   public byte[] getBytes() {
      return bytes;
   }

   public void setBytes(byte[] bytes) {
      this.bytes = bytes;
   }

   public int size() {
      if (this.bytes != null) {
         return this.bytes.length;
      }
      return 0;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }
}