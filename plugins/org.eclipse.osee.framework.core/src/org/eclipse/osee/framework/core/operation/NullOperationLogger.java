/*
 * Created on Feb 21, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.operation;

public class NullOperationLogger extends OperationLogger {
   private final static OperationLogger singleton = new NullOperationLogger();

   private NullOperationLogger() {
      // singleton so prevent external construction
   }

   @Override
   public void log(String... row) {
      // no implementation since this is a null logger 
   }

   public static final OperationLogger getSingleton() {
      return singleton;
   }
}