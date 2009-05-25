/*
 * Created on May 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.support.test.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;

/**
 * @author Donald G. Dunne
 */
public class TestUtil {

   public static String DEMO_CODE_TEAM_WORKFLOW_ARTIFACT = "Demo Code Team Workflow";
   public static String DEMO_REQ_TEAM_WORKFLOW_ARTIFACT = "Demo Req Team Workflow";
   public static String DEMO_TEST_TEAM_WORKFLOW_ARTIFACT = "Demo Test Team Workflow";
   public static Collection<String> ignoreLogging = Arrays.asList("No image was defined for art type");

   public static boolean isProductionDb() throws OseeCoreException {
      return ClientSessionManager.isProductionDataStore();
   }

   public static boolean isDemoDb() throws OseeCoreException {
      return ClientSessionManager.getAuthenticationProtocols().contains("demo");
   }

   public static void sleep(long milliseconds) throws Exception {
      System.out.println("Sleeping " + milliseconds);
      Thread.sleep(milliseconds);
      System.out.println("Awake");
   }

   public static SevereLoggingMonitor severeLoggingStart() throws Exception {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      return monitorLog;
   }

   public static void severeLoggingEnd(SevereLoggingMonitor monitorLog) throws Exception {
      OseeLog.unregisterLoggerListener(monitorLog);
      Collection<IHealthStatus> healthStatuses = monitorLog.getSevereLogs();
      int numExceptions = 0;
      if (healthStatuses.size() > 0) {
         for (IHealthStatus status : healthStatuses) {
            if (status.getLevel() != Level.INFO) {
               boolean ignoreIt = false;
               for (String str : ignoreLogging) {
                  if (status.getMessage().startsWith(str)) ignoreIt = true;
               }
               if (ignoreIt) continue;
               if (status.getException() != null) {
                  StringBuilder sb = new StringBuilder();
                  exceptionToString(status.getException(), sb);
                  System.err.println(sb.toString());
               } else {
                  System.err.println(status.getMessage());
               }
            }
         }
         if (numExceptions > 0) {
            throw new OseeStateException("SevereLoggingMonitor found " + numExceptions + " exceptions!");
         }
      }
   }

   private static void exceptionToString(Throwable ex, StringBuilder sb) {
      if (ex == null) {
         sb.append("Exception == null; can't display stack");
         return;
      }
      sb.append(ex.getMessage() + "\n");
      StackTraceElement st[] = ex.getStackTrace();
      for (int i = 0; i < st.length; i++) {
         StackTraceElement ste = st[i];
         sb.append("   at " + ste.toString() + "\n");
      }
      Throwable cause = ex.getCause();
      if (cause != null) {
         sb.append("   caused by ");
         exceptionToString(cause, sb);
      }
   }

}
