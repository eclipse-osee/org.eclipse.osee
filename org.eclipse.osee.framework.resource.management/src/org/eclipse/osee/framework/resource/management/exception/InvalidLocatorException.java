/*
 * Created on Apr 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.management.exception;

/**
 * @author Roberto E. Escobar
 */
public class InvalidLocatorException extends Exception {

   private static final long serialVersionUID = -1291325728313575694L;

   public InvalidLocatorException() {
      super();
   }

   public InvalidLocatorException(String message, Throwable cause) {
      super(message, cause);
   }

   public InvalidLocatorException(String message) {
      super(message);
   }

   public InvalidLocatorException(Throwable cause) {
      super(cause);
   }

}
