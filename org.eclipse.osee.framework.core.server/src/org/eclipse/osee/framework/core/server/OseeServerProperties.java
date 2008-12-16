/*
 * Created on Nov 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.server;

import java.io.File;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class OseeServerProperties {

   public static final String CHECK_TAG_QUEUE_ON_START_UP = "osee.check.tag.queue.on.startup";
   private static boolean wasBinaryDataChecked = false;

   private OseeServerProperties() {
      super();
   }

   /**
    * Get location for OSEE application server binary data
    * 
    * @return OSEE application server binary data path
    */
   public static String getOseeApplicationServerData() {
      String toReturn = OseeProperties.getOseeApplicationServerData();
      if (!wasBinaryDataChecked) {
         File file = new File(toReturn);
         if (file.exists()) {
            OseeLog.log(CoreServerActivator.class, Level.INFO, String.format("Application Server Data: [%s]", toReturn));
         } else {
            OseeLog.log(CoreServerActivator.class, Level.WARNING, String.format(
                  "Application Server Data: [%s] does not exist and will be created", toReturn));
         }
         wasBinaryDataChecked = true;
      }
      return toReturn;
   }

   /**
    * Retrieve the application server port
    * 
    * @return the application server port
    */
   public static int getOseeApplicationServerPort() {
      return OseeProperties.getOseeApplicationServerPort();
   }

   /**
    * Check Tag Queue on start up. Entries found in the tag queue are tagged by the server on start up.
    * 
    * @return whether tag queue should be checked upon server start-up.
    */
   public static boolean isCheckTagQueueOnStartupAllowed() {
      return Boolean.valueOf(System.getProperty(CHECK_TAG_QUEUE_ON_START_UP, "false"));
   }
}
