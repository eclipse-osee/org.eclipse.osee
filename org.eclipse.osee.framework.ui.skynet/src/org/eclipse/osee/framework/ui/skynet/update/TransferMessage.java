/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.update;

/**
 * @author Jeff C. Phillips
 *
 */
public class TransferMessage {
   public enum Type{ERROR, INFO};
   
   private Type type;
   private String message;
   
   
   public TransferMessage(Type type, String message) {
      super();
      this.type = type;
      this.message = message;
   }
   
   /**
    * @return the type
    */
   public Type getType() {
      return type;
   }


   /**
    * @return the message
    */
   public String getMessage() {
      return message;
   }
}
