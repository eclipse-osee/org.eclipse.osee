package org.eclipse.osee.framework.servlet.log;

import java.util.logging.Level;

public class Logger implements ILogger {

   public void log(Level level, String message) {
      System.out.println(message);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.servlet.log.ILogger#log(java.util.logging.Level, java.lang.String, java.lang.Throwable)
    */
   @Override
   public void log(Level level, String message, Throwable ex) {
      System.out.println(message);
   }

}
