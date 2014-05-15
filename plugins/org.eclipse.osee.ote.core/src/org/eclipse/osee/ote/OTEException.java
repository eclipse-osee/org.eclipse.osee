package org.eclipse.osee.ote;


public class OTEException extends RuntimeException {

   private static final long serialVersionUID = 8430838531056913404L;

   public OTEException(String format, Throwable t) {
      super(format, t);
   }

   public OTEException(String string) {
      super(string);
   }

   public OTEException(Throwable e) {
      super(e);
   }


}
