/*
 * Created on Aug 3, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.client.internal;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.OseeLog;

abstract class OseeServer {

   private boolean isAlive;
   private String message = "";
   private final String name;
   private Exception exception;
   private Level level = Level.INFO;

   public OseeServer(String serverName) {
      this.name = serverName;
   }

   public void set(Level level, Exception ex, String message) {
      this.level = level;
      this.message = message;
      this.exception = ex;
   }

   public String getName() {
      return name;
   }

   public void setAlive(boolean isServerAlive) {
      this.isAlive = isServerAlive;
   }

   public Exception getException() {
      return exception;
   }

   public Level getLevel() {
      return level;
   }

   public void reset() {
      message = null;
      level = null;
      exception = null;
      isAlive = false;
   }

   public String getMessage() {
      return message;
   }

   public boolean isAlive() {
      return isAlive;
   }

   public String report() {
      OseeLog.reportStatus(new BaseStatus(getName(), level, message, getException()));
      OseeLog.log(OseeApplicationServer.class, level, message, getException());
      return String.format("%s: %s %s", level, message,
         (getException() != null ? "[" + getException().getLocalizedMessage() + "]" : ""));
   }
}
