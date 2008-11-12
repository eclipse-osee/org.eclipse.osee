package org.eclipse.osee.framework.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SevereLoggingMonitor implements ILoggerListener {

   private List<IHealthStatus> status = new ArrayList<IHealthStatus>();

   @Override
   public void log(String loggerName, Level level, String message, Throwable th) {
      status.add(new BaseStatus(loggerName, level, message, th));
   }

   public List<IHealthStatus> getSevereLogs() {
      return status;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(status.size());
      sb.append(" Severe logs captured.\n");
      for (IHealthStatus health : status) {
         sb.append(health.getException().getMessage());
         sb.append("\n");
      }
      return sb.toString();
   }

}
