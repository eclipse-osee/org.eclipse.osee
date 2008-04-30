/*
 * Created on Apr 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management.exception;

/**
 * @author Roberto E. Escobar
 */
public class MalformedLocatorException extends Exception {

   private static final long serialVersionUID = -7595802736847790150L;

   public MalformedLocatorException() {
      super();
   }

   /**
    * @param message
    * @param cause
    */
   public MalformedLocatorException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * @param message
    */
   public MalformedLocatorException(String message) {
      super(message);
   }

   /**
    * @param cause
    */
   public MalformedLocatorException(Throwable cause) {
      super(cause);
   }

}
